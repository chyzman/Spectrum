package de.dafuqs.spectrum.inventories;

import de.dafuqs.spectrum.energy.color.InkColors;
import de.dafuqs.spectrum.items.tools.RangedWorkstaffItem;
import de.dafuqs.spectrum.items.tools.WorkstaffItem;
import de.dafuqs.spectrum.networking.SpectrumC2SPacketSender;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class WorkstaffScreen extends QuickNavigationGridScreen<WorkstaffScreenHandler> {

	private static final Grid RANGE_GRID = new Grid(
			GridEntry.EMPTY,
			GridEntry.of(InkColors.GRAY.getColor(), new Point(0, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.SELECT_1x1)),
			GridEntry.of(InkColors.GRAY.getColor(), new Point(32, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.SELECT_5x5)),
			GridEntry.BACK,
			GridEntry.of(InkColors.GRAY.getColor(), new Point(16, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.SELECT_3x3))
	);

	private static final Grid ENCHANTMENT_GRID = new Grid(
			GridEntry.EMPTY,
			GridEntry.of(InkColors.GRAY.getColor(), new Point(48, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.SELECT_SILK_TOUCH)),
			GridEntry.BACK,
			GridEntry.of(InkColors.GRAY.getColor(), new Point(64, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.SELECT_RESONANCE)),
			GridEntry.of(InkColors.GRAY.getColor(), new Point(80, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.SELECT_FORTUNE))
	);

	public WorkstaffScreen(WorkstaffScreenHandler handler, PlayerInventory playerInventory, Text title) {
		super(handler, playerInventory, title);

		GridEntry rightClickGridEntry;
		ItemStack mainHandStack = playerInventory.player.getMainHandStack();
		if (WorkstaffItem.canTill(mainHandStack.getNbt())) {
			rightClickGridEntry = GridEntry.of(InkColors.GRAY.getColor(), new Point(144, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.DISABLE_RIGHT_CLICK_ACTIONS));
		} else {
			rightClickGridEntry = GridEntry.of(InkColors.GRAY.getColor(), new Point(128, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.ENABLE_RIGHT_CLICK_ACTIONS));
		}

		if (mainHandStack.getItem() instanceof RangedWorkstaffItem) {

			GridEntry projectileEntry = RangedWorkstaffItem.canShoot(mainHandStack.getNbt())
					? GridEntry.of(InkColors.GRAY.getColor(), new Point(176, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.DISABLE_PROJECTILES))
					: GridEntry.of(InkColors.GRAY.getColor(), new Point(160, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.ENABLE_PROJECTILES));

			gridStack.push(new Grid(
					GridEntry.CLOSE,
					GridEntry.of(InkColors.GRAY.getColor(), new Point(112, 38), (screen) -> screen.selectGrid(RANGE_GRID)),
					rightClickGridEntry,
					projectileEntry,
					GridEntry.of(InkColors.GRAY.getColor(), new Point(48, 38), (screen) -> screen.selectGrid(ENCHANTMENT_GRID))
			));

		} else {

			GridEntry enchantmentEntry = EnchantmentHelper.getLevel(Enchantments.FORTUNE, mainHandStack) > 0
					? GridEntry.of(InkColors.GRAY.getColor(), new Point(48, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.SELECT_SILK_TOUCH))
					: GridEntry.of(InkColors.GRAY.getColor(), new Point(160, 38), (screen) -> WorkstaffScreen.select(WorkstaffItem.GUIToggle.SELECT_FORTUNE));

			gridStack.push(new Grid(
					GridEntry.CLOSE,
					GridEntry.of(InkColors.GRAY.getColor(), new Point(112, 38), (screen) -> screen.selectGrid(RANGE_GRID)),
					rightClickGridEntry,
					GridEntry.EMPTY,
					enchantmentEntry
			));

		}

	}

	protected static void select(WorkstaffItem.GUIToggle toggle) {
		SpectrumC2SPacketSender.sendWorkstaffToggle(toggle);
		MinecraftClient client = MinecraftClient.getInstance();
		client.world.playSound(client.player.getBlockPos(), SpectrumSoundEvents.PAINTBRUSH_SELECT, SoundCategory.NEUTRAL, 0.6F, 1.0F, false);
		client.player.closeHandledScreen();
	}
	
}