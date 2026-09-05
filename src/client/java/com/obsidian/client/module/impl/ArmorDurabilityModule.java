package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

/** Shows the remaining durability percentage of each equipped armor piece. */
public class ArmorDurabilityModule extends Module {

	private static final EquipmentSlot[] ARMOR_SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};
	private static final String[] LABELS = {"Helmet", "Chestplate", "Leggings", "Boots"};

	public ArmorDurabilityModule() {
		super("Armor Status", "Shows equipped armor durability.", ModuleCategory.PLAYER, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		int baseY = client.getWindow().getGuiScaledHeight() - 90;
		int line = 0;

		for (int i = 0; i < ARMOR_SLOTS.length; i++) {
			ItemStack stack = client.player.getItemBySlot(ARMOR_SLOTS[i]);
			if (stack.isEmpty()) {
				continue;
			}

			int maxDamage = stack.getMaxDamage();
			String text;
			int color;
			if (maxDamage <= 0) {
				text = LABELS[i] + ": --";
				color = Theme.textSecondary();
			} else {
				int remaining = maxDamage - stack.getDamageValue();
				int percent = Math.round((remaining / (float) maxDamage) * 100f);
				text = LABELS[i] + ": " + percent + "%";
				color = percent <= 15 ? 0xFFE74C3C : (percent <= 40 ? 0xFFF1C40F : Theme.moduleEnabled());
			}

			context.drawString(client.font, text, 4, baseY + line * 10, color);
			line++;
		}
	}
}
