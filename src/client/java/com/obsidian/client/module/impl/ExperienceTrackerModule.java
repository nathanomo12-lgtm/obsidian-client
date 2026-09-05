package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/** Displays the player's experience level and progress toward the next level. */
public class ExperienceTrackerModule extends Module {

	public ExperienceTrackerModule() {
		super("XP Tracker", "Shows experience level and progress to the next level.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		int level = client.player.experienceLevel;
		int percent = Math.round(client.player.experienceProgress * 100f);
		String text = "Level " + level + " (" + percent + "%)";

		context.drawString(client.font, text, 4, 100, Theme.textPrimary());
	}
}
