package de.dafuqs.spectrum.energy.storage;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.energy.color.CMYKColor;
import de.dafuqs.spectrum.energy.color.PigmentColors;
import de.dafuqs.spectrum.helpers.Support;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PigmentPaletteEnergyStorage extends IndividuallyCappedSimplePigmentEnergyStorage {
	
	public PigmentPaletteEnergyStorage(long maxEnergyPerColor) {
		super(maxEnergyPerColor);
	}
	
	public PigmentPaletteEnergyStorage(long maxEnergyPerColor, Map<CMYKColor, Long> colors) {
		super(maxEnergyPerColor, colors);
	}
	
	public long addEnergy(CMYKColor color, long amount, ItemStack stack, ServerPlayerEntity serverPlayerEntity) {
		long leftoverEnergy = super.addEnergy(color, amount);
		if(leftoverEnergy != amount) {
			SpectrumAdvancementCriteria.PIGMENT_PALETTE_USE.trigger(serverPlayerEntity, stack, this, amount - leftoverEnergy);
		}
		return leftoverEnergy;
	}
	
	public boolean requestEnergy(CMYKColor color, long amount, ItemStack stack, ServerPlayerEntity serverPlayerEntity) {
		boolean success = super.requestEnergy(color, amount);
		if(success) {
			SpectrumAdvancementCriteria.PIGMENT_PALETTE_USE.trigger(serverPlayerEntity, stack, this, -amount);
		}
		return success;
	}
	
	public long drainEnergy(CMYKColor color, long amount, ItemStack stack, ServerPlayerEntity serverPlayerEntity) {
		long drainedAmount = super.drainEnergy(color, amount);
		if(drainedAmount != 0) {
			SpectrumAdvancementCriteria.PIGMENT_PALETTE_USE.trigger(serverPlayerEntity, stack, this, -drainedAmount);
		}
		return drainedAmount;
	}
	
	public static @Nullable PigmentPaletteEnergyStorage fromNbt(@NotNull NbtCompound compound) {
		if(compound.contains("MaxEnergyPerColor", NbtElement.LONG_TYPE)) {
			long maxEnergyPerColor = compound.getLong("MaxEnergyPerColor");
			
			Map<CMYKColor, Long> colors = new HashMap<>();
			for(CMYKColor color : CMYKColor.all()) {
				colors.put(color, compound.getLong(color.toString()));
			}
			return new PigmentPaletteEnergyStorage(maxEnergyPerColor, colors);
		}
		return null;
	}
	
	@Environment(EnvType.CLIENT)
	public void addTooltip(World world, List<Text> tooltip, TooltipContext context) {
		for(Map.Entry<CMYKColor, Long> color : this.storedEnergy.entrySet()) {
			if(color.getValue() > 0) {
				tooltip.add(new TranslatableText("item.spectrum.artists_palette.tooltip.stored_energy." + color.getKey().toString().toLowerCase(Locale.ROOT), color.getValue()));
			}
		}
	}
}