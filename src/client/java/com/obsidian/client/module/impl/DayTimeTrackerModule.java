package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Shows the in-game day count and whether it's currently day or night, plus
 * ticks remaining until the next transition — a companion to Light Level for
 * anticipating hostile mob spawn windows.
 */
public class DayTimeTrackerModule extends Module {

	private static final long NIGHT_START = 13000;
	private static final long NIGHT_END = 23000;
	private static final long DAY_LENGTH = 24000;

	public DayTimeTrackerModule() {
		super("Day Time Tracker", "Shows in-game day/night status and time until it changes.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null || client.level == null) {
			return;
		}

		long dayTime = client.level.getDayTime();
		long timeOfDay = dayTime % DAY_LENGTH;
		long dayCount = dayTime / DAY_LENGTH;

		boolean isNight = timeOfDay >= NIGHT_START && timeOfDay < NIGHT_END;
		long ticksUntilChange = isNight ? (NIGHT_END - timeOfDay) : (timeOfDay < NIGHT_START ? NIGHT_START - timeOfDay : DAY_LENGTH - timeOfDay + NIGHT_START);
		long secondsUntilChange = ticksUntilChange / 20;

		String label = isNight ? "Night" : "Day";
		String text = "Day " + dayCount + " - " + label + " (" + secondsUntilChange + "s)";
		int color = isNight ? 0xFF7C5CFF : Theme.textPrimary();

		int x = client.getWindow().getGuiScaledWidth() - 4 - client.font.width(text);
		context.drawString(client.font, text, x, 40, color);
	}
}
