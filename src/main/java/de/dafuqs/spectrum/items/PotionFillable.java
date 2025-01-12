package de.dafuqs.spectrum.items;

import de.dafuqs.spectrum.energy.InkCost;
import de.dafuqs.spectrum.energy.InkPoweredStatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Items with this interface should support Potion nbt like
 * set via PotionUtil.setCustomPotionEffects
 * Think Custom Potions or Trinkets
 */
public interface PotionFillable {
	
	int maxEffectCount();
	
	int maxEffectAmplifier();
	
	// used for calculating the items cost to apply a certain effect
	// calculated once and then stored in the items nbt for quick lookup and nicer modifiability
	// via commands or special loot (so ones found in dungeon chests can be cheaper!)
	default long adjustFinalCostFor(@NotNull InkPoweredStatusEffectInstance instance) {
		return (long) Math.pow(instance.getInkCost().getCost(), 1 + instance.getStatusEffectInstance().getAmplifier());
	}
	
	// saving
	default void addOrUpgradeEffects(ItemStack potionFillableStack, List<InkPoweredStatusEffectInstance> newEffects) {
		if (!isFull(potionFillableStack)) {
			List<InkPoweredStatusEffectInstance> existingEffects = InkPoweredStatusEffectInstance.getEffects(potionFillableStack);
			int maxCount = maxEffectCount();
			int maxAmplifier = maxEffectAmplifier();
			for (InkPoweredStatusEffectInstance newEffect : newEffects) {
				StatusEffectInstance statusEffectInstance = newEffect.getStatusEffectInstance();
				if (statusEffectInstance.getAmplifier() > maxAmplifier) {
					statusEffectInstance = new StatusEffectInstance(statusEffectInstance.getEffectType(), statusEffectInstance.getDuration(), maxAmplifier, statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles());
				}
				if (existingEffects.size() == maxCount) {
					break;
				}
				
				// calculate the final cost of this effect and add it
				InkCost adjustedCost = new InkCost(newEffect.getInkCost().getColor(), adjustFinalCostFor(newEffect));
				InkPoweredStatusEffectInstance modifiedInstance = new InkPoweredStatusEffectInstance(statusEffectInstance, adjustedCost);
				existingEffects.add(modifiedInstance);
			}
			
			InkPoweredStatusEffectInstance.setEffects(potionFillableStack, existingEffects);
		}
	}
	
	default boolean isFull(ItemStack itemStack) {
		return InkPoweredStatusEffectInstance.getEffects(itemStack).size() >= maxEffectCount();
	}
	
	default boolean isAtLeastPartiallyFilled(ItemStack itemStack) {
		return InkPoweredStatusEffectInstance.getEffects(itemStack).size() > 0;
	}
	
	default void removeEffects(ItemStack itemStack) {
		itemStack.removeSubNbt("InkPoweredStatusEffects");
	}
	
	default void appendPotionFillableTooltip(ItemStack stack, List<Text> tooltip, MutableText attributeModifierText, boolean showDuration) {
		List<InkPoweredStatusEffectInstance> effects = InkPoweredStatusEffectInstance.getEffects(stack);
		InkPoweredStatusEffectInstance.buildTooltip(tooltip, effects, attributeModifierText, showDuration);
		
		int maxEffectCount = maxEffectCount();
		if (effects.size() < maxEffectCount) {
			if (maxEffectCount == 1) {
				tooltip.add(Text.translatable("item.spectrum.potion_pendant.tooltip_not_full_one"));
			} else {
				tooltip.add(Text.translatable("item.spectrum.potion_pendant.tooltip_not_full_count", maxEffectCount));
			}
			tooltip.add(Text.translatable("item.spectrum.potion_pendant.tooltip_max_level").append(Text.translatable("enchantment.level." + (maxEffectAmplifier() + 1))));
		}
	}
	
}