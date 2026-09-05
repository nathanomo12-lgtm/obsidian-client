package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

/** Shows the remaining durability percentage of each equipped armor piece. */
public class ArmorDurabilityModule extends Module {

	private static final EquipmentSlot[] ARMOR_SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};
	private static final String[] LABELS = {"Helmet", "Chestplate", "Leggings", "Boots"};

	public ArmorDurabilityModule() {
		super("Armor Status", "Shows equipped armor durability.", ModuleCategory.PLAYER, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(DrawContext context, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.currentScreen != null) {
			return;
		}

		int baseY = client.getWindow().getScaledHeight() - 90;
		int line = 0;

		for (int i = 0; i < ARMOR_SLOTS.length; i++) {
			ItemStack stack = client.player.getEquippedStack(ARMOR_SLOTS[i]);
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
				int remaining = maxDamage - stack.getDamage();
				int percent = Math.round((remaining / (float) maxDamage) * 100f);
				text = LABELS[i] + ": " + percent + "%";
				color = percent <= 15 ? 0xFFE74C3C : (percent <= 40 ? 0xFFF1C40F : Theme.moduleEnabled());
			}

			context.drawTextWithShadow(client.textRenderer, text, 4, baseY + line * 10, color);
			line++;
		}
	}
}
