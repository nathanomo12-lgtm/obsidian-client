package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/** Displays the player's current/max health as a number, alongside vanilla's heart icons. */
public class HealthDisplayModule extends Module {

	public HealthDisplayModule() {
		super("Health Display", "Shows numeric health as an alternative to counting hearts.", ModuleCategory.PLAYER, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		float health = client.player.getHealth();
		float maxHealth = client.player.getMaxHealth();
		String text = String.format("Health: %.1f / %.1f", health, maxHealth);

		int color;
		float ratio = maxHealth > 0 ? health / maxHealth : 0;
		if (ratio <= 0.25f) {
			color = 0xFFE74C3C;
		} else if (ratio <= 0.5f) {
			color = 0xFFF1C40F;
		} else {
			color = Theme.moduleEnabled();
		}

		context.drawString(client.font, text, 4, 52, color);
	}
}
