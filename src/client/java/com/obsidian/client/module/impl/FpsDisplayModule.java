package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

/** Displays the client's current frames-per-second in the top-left corner. */
public class FpsDisplayModule extends Module {

	public FpsDisplayModule() {
		super("FPS Display", "Shows current frames per second.", ModuleCategory.UTILITY, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(DrawContext context, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.currentScreen != null) {
			return;
		}

		String text = client.getCurrentFps() + " fps";
		context.drawTextWithShadow(client.textRenderer, text, 4, 4, Theme.textPrimary());
	}
}
