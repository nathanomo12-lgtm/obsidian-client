package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.food.FoodData;
import org.lwjgl.glfw.GLFW;

/** Displays the player's current hunger level and saturation as numbers. */
public class HungerDisplayModule extends Module {

	public HungerDisplayModule() {
		super("Hunger Display", "Shows numeric food level and saturation.", ModuleCategory.PLAYER, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		FoodData food = client.player.getFoodData();
		String text = String.format("Hunger: %d/20 (Sat: %.1f)", food.getFoodLevel(), food.getSaturationLevel());
		int color = food.getFoodLevel() <= 6 ? 0xFFE74C3C : Theme.textPrimary();

		context.drawString(client.font, text, 4, 64, color);
	}
}
