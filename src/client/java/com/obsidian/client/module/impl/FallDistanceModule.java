package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

/**
 * Shows the player's current fall distance while airborne, purely as
 * informational feedback (helps judge whether a jump is safe) — it does not
 * suppress or reduce fall damage in any way, unlike a "NoFall" cheat module.
 */
public class FallDistanceModule extends Module {

	public FallDistanceModule() {
		super("Fall Distance", "Shows current fall distance while falling.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		double fallDistance = client.player.fallDistance;
		if (fallDistance <= 0) {
			return;
		}

		String text = String.format("Falling: %.1f blocks", fallDistance);
		int color = fallDistance >= 4 ? 0xFFE74C3C : Theme.textPrimary();
		context.drawString(client.font, text, 4, 76, color);
	}
}
