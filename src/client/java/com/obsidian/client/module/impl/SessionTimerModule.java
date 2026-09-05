package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Tracks elapsed playtime since this module was enabled, displayed as
 * {@code h:mm:ss}. Resets on re-enable, matching the "session" counters used
 * by CPS/Combo Counter elsewhere in this mod.
 */
public class SessionTimerModule extends Module {

	private long ticks;

	public SessionTimerModule() {
		super("Session Timer", "Tracks elapsed playtime since enabled.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onEnable() {
		ticks = 0;
	}

	@Override
	public void onTick() {
		Minecraft client = Minecraft.getInstance();
		if (client.player != null) {
			ticks++;
		}
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		long totalSeconds = ticks / 20;
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		String text = String.format("Session: %d:%02d:%02d", hours, minutes, seconds);

		int x = client.getWindow().getGuiScaledWidth() - 4 - client.font.width(text);
		context.drawString(client.font, text, x, 64, Theme.textPrimary());
	}
}
