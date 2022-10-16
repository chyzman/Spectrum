package de.dafuqs.spectrum.registries;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class SpectrumFoodComponents {
	
	public static final FoodComponent MOONSTRUCK_NECTAR = new FoodComponent.Builder().hunger(2).saturationModifier(0.2F)
			.statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2), 1.0F).build();
	
	public static final FoodComponent JADE_JELLY = new FoodComponent.Builder().hunger(4).saturationModifier(0.75F).snack()
			.statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 2), 0.2F).build();
	
	public static final FoodComponent STAR_COOKIE = new FoodComponent.Builder()
			.hunger(3).saturationModifier(0.25F).alwaysEdible().snack().build();
	
	public static final FoodComponent ENCHANTED_STAR_COOKIE = new FoodComponent.Builder()
			.hunger(20).saturationModifier(1.0F).alwaysEdible().snack()
			.statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 20), 1.0F).build();
	
	public static final FoodComponent JARAMEL = new FoodComponent.Builder().hunger(1).saturationModifier(0.25F).snack()
			.statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600, 2), 1.0F)
			.statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600, 1), 1.0F).build();
	
	public static final FoodComponent LUCKY_ROLL = new FoodComponent.Builder().hunger(4).saturationModifier(0.25F)
			.statusEffect(new StatusEffectInstance(SpectrumStatusEffects.ANOTHER_ROLL, 1200), 1.0F).build();
	
	public static final FoodComponent GLISTERING_JELLY_TEA = new FoodComponent.Builder().hunger(4).saturationModifier(0.75F)
			.statusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 1200), 1.0F).build();
	
	public static final FoodComponent RESTORATION_TEA = new FoodComponent.Builder().hunger(2).saturationModifier(0.5F)
			.statusEffect(new StatusEffectInstance(SpectrumStatusEffects.IMMUNITY, 1200), 1.0F).build();
	
	public static final FoodComponent INFUSED_BEVERAGE = new FoodComponent.Builder().hunger(2).saturationModifier(0.2F).snack().build();
	
	public static final FoodComponent SUSPICIOUS_BREW = new FoodComponent.Builder().hunger(2).saturationModifier(0.4F).snack().build();
	
	public static final FoodComponent JADE_WINE = new FoodComponent.Builder().hunger(2).saturationModifier(0.4F).snack().build();
	
	public static final FoodComponent PURE_ALCOHOL = new FoodComponent.Builder()
			.hunger(2).saturationModifier(0.2F)
			.alwaysEdible().snack()
			.statusEffect(new StatusEffectInstance(StatusEffects.POISON, 20 * 15, 4), 1.0F)
			.statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20 * 30, 2), 1.0F)
			.statusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 60, 2), 1.0F)
			.build();
	
	public static final FoodComponent KIMCHI = new FoodComponent.Builder().hunger(6).saturationModifier(0.6F).build();
	
}