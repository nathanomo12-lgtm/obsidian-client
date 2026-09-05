package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import org.lwjgl.glfw.GLFW;

/**
 * Counts how many times the death screen has appeared since this module was
 * enabled. Purely observational: it only reads {@code Minecraft#screen}, it
 * never affects respawn, health, or damage.
 */
public class DeathCounterModule extends Module {

	private int deaths;
	private boolean wasShowingDeathScreen;

	public DeathCounterModule() {
		super("Death Counter", "Counts deaths since this module was enabled.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onEnable() {
		deaths = 0;
		wasShowingDeathScreen = false;
	}

	@Override
	public void onTick() {
		Minecraft client = Minecraft.getInstance();
		boolean showingDeathScreen = client.screen instanceof DeathScreen;
		if (showingDeathScreen && !wasShowingDeathScreen) {
			deaths++;
		}
		wasShowingDeathScreen = showingDeathScreen;
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		String text = "Deaths: " + deaths;
		int x = client.getWindow().getGuiScaledWidth() - 4 - client.font.width(text);
		context.drawString(client.font, text, x, 76, Theme.textPrimary());
	}
}
