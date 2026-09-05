package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

/**
 * Shows the remaining durability of the item in the player's main hand as a
 * warning note (not automation) so gear can be swapped before it breaks.
 */
public class HeldItemDurabilityModule extends Module {

	public HeldItemDurabilityModule() {
		super("Held Item Durability", "Shows durability of the item in your main hand.", ModuleCategory.PLAYER, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		ItemStack stack = client.player.getMainHandItem();
		if (stack.isEmpty() || stack.getMaxDamage() <= 0) {
			return;
		}

		int remaining = stack.getMaxDamage() - stack.getDamageValue();
		int percent = Math.round((remaining / (float) stack.getMaxDamage()) * 100f);
		String text = "Held: " + percent + "%";
		int color = percent <= 15 ? 0xFFE74C3C : (percent <= 40 ? 0xFFF1C40F : Theme.moduleEnabled());

		int y = client.getWindow().getGuiScaledHeight() - 50;
		context.drawString(client.font, text, 4, y, color);
	}
}
