package com.obsidian.client.module.impl;

import com.obsidian.client.ObsidianClientMod;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

/**
 * Minimal reference implementation showing how to wire a new module into the
 * lifecycle. Draws a small label in the top-left corner while enabled.
 *
 * <p>Replace/extend this with real modules (Keystrokes, CPS Counter, FPS/Ping,
 * etc.) using the same pattern.</p>
 */
public class ExampleModule extends Module {

	private int ticksEnabled;

	public ExampleModule() {
		super("Example", "Reference module demonstrating the module lifecycle.", ModuleCategory.UTILITY, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onEnable() {
		ticksEnabled = 0;
		ObsidianClientMod.LOGGER.info("[Example] enabled");
	}

	@Override
	public void onDisable() {
		ObsidianClientMod.LOGGER.info("[Example] disabled");
	}

	@Override
	public void onTick() {
		ticksEnabled++;
	}

	@Override
	public void onRender(DrawContext context, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null) {
			return;
		}
		context.drawText(client.textRenderer, "Obsidian Client (" + ticksEnabled + "t)", 4, 4, 0xFFFFFF, true);
	}
}
