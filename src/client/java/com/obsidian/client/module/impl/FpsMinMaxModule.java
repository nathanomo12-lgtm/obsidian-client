package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Tracks the minimum and maximum FPS observed over a rolling one-second
 * window, as a cheap "1% low"-style companion to FPS Display for spotting
 * stutters that an averaged number would hide.
 */
public class FpsMinMaxModule extends Module {

	private int windowMin = Integer.MAX_VALUE;
	private int windowMax = Integer.MIN_VALUE;
	private int displayMin;
	private int displayMax;
	private int ticksInWindow;

	public FpsMinMaxModule() {
		super("FPS Min Max", "Shows the min/max FPS over the last second.", ModuleCategory.PERFORMANCE, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onEnable() {
		windowMin = Integer.MAX_VALUE;
		windowMax = Integer.MIN_VALUE;
		displayMin = 0;
		displayMax = 0;
		ticksInWindow = 0;
	}

	@Override
	public void onTick() {
		int fps = Minecraft.getInstance().getFps();
		windowMin = Math.min(windowMin, fps);
		windowMax = Math.max(windowMax, fps);

		if (++ticksInWindow >= 20) {
			displayMin = windowMin;
			displayMax = windowMax;
			windowMin = Integer.MAX_VALUE;
			windowMax = Integer.MIN_VALUE;
			ticksInWindow = 0;
		}
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		String text = "FPS Min Max: " + displayMin + " / " + displayMax;
		int x = client.getWindow().getGuiScaledWidth() - 4 - client.font.width(text);
		context.drawString(client.font, text, x, 100, Theme.textPrimary());
	}
}
