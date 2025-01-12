package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.items.tools.GlassArrowItem;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class SpectrumRegistries {
	
	public static final RegistryKey<Registry<GlassArrowItem.Variant>> GLASS_ARROW_VARIANT_KEY = SpectrumRegistries.createRegistryKey("glass_arrow_variant");
	public static final Registry<GlassArrowItem.Variant> GLASS_ARROW_VARIANT = Registry.create(GLASS_ARROW_VARIANT_KEY, registry -> GlassArrowItem.Variant.MALACHITE);
	
	private static <T> RegistryKey<Registry<T>> createRegistryKey(String id) {
		return RegistryKey.ofRegistry(SpectrumCommon.locate(id));
	}
	
}
