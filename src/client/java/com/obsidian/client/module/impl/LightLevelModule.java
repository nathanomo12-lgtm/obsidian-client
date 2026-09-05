package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import org.lwjgl.glfw.GLFW;

/**
 * Shows the block light level at the player's feet, color-coded as a rough
 * mob-spawn safety indicator (block light 8+ prevents most hostile mob
 * spawns on a solid surface; this is informational only, like vanilla's F3
 * debug screen light values).
 */
public class LightLevelModule extends Module {

	public LightLevelModule() {
		super("Light Level", "Shows block light level at your feet as a spawn-safety hint.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null || client.level == null) {
			return;
		}

		BlockPos pos = client.player.blockPosition();
		int light = client.level.getBrightness(LightLayer.BLOCK, pos);
		String text = "Light: " + light;
		int color = light <= 7 ? 0xFFE74C3C : Theme.moduleEnabled();

		context.drawString(client.font, text, 4, 88, color);
	}
}
