package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.lwjgl.glfw.GLFW;

/** Displays the client player's current latency to the server. */
public class PingDisplayModule extends Module {

	public PingDisplayModule() {
		super("Ping Display", "Shows current server latency.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}

		ClientPacketListener networkHandler = client.getConnection();
		if (networkHandler == null) {
			return;
		}

		PlayerInfo entry = networkHandler.getPlayerInfo(client.player.getUUID());
		int ping = entry != null ? entry.getLatency() : -1;

		String text = ping >= 0 ? ping + " ms" : "-- ms";
		context.drawString(client.font, text, 4, 16, Theme.textPrimary());
	}
}
