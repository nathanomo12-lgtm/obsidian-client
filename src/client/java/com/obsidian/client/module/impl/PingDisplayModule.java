package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.lwjgl.glfw.GLFW;

/** Displays the client player's current latency to the server. */
public class PingDisplayModule extends Module {

	public PingDisplayModule() {
		super("Ping Display", "Shows current server latency.", ModuleCategory.UTILITY, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(DrawContext context, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.currentScreen != null) {
			return;
		}

		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
		if (networkHandler == null) {
			return;
		}

		PlayerListEntry entry = networkHandler.getPlayerListEntry(client.player.getUuid());
		int ping = entry != null ? entry.getLatency() : -1;

		String text = ping >= 0 ? ping + " ms" : "-- ms";
		context.drawTextWithShadow(client.textRenderer, text, 4, 16, Theme.textPrimary());
	}
}
