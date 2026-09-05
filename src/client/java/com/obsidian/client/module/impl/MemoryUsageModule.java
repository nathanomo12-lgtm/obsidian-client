package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/** Displays current JVM heap usage in megabytes, similar to vanilla's F3 memory line. */
public class MemoryUsageModule extends Module {

	private static final long MEGABYTE = 1024L * 1024L;

	public MemoryUsageModule() {
		super("Memory Usage", "Shows current JVM heap usage.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		Runtime runtime = Runtime.getRuntime();
		long usedMb = (runtime.totalMemory() - runtime.freeMemory()) / MEGABYTE;
		long maxMb = runtime.maxMemory() / MEGABYTE;

		String text = usedMb + " / " + maxMb + " MB";
		int x = client.getWindow().getGuiScaledWidth() - 4 - client.font.width(text);
		context.drawString(client.font, text, x, 4, Theme.textPrimary());
	}
}
