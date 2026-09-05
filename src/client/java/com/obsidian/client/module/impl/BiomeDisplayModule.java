package com.obsidian.client.module.impl;

import com.obsidian.client.gui.theme.Theme;
import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

/** Displays the name of the biome the player is currently standing in. */
public class BiomeDisplayModule extends Module {

	public BiomeDisplayModule() {
		super("Biome Display", "Shows the name of your current biome.", ModuleCategory.UTILITY, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	@Override
	public void onRender(GuiGraphics context, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null || client.level == null) {
			return;
		}

		BlockPos pos = client.player.blockPosition();
		Holder<Biome> biome = client.level.getBiome(pos);
		String name = formatBiomeName(biome);

		int x = client.getWindow().getGuiScaledWidth() - 4 - client.font.width(name);
		context.drawString(client.font, name, x, 28, Theme.textPrimary());
	}

	private static String formatBiomeName(Holder<Biome> biome) {
		Optional<ResourceKey<Biome>> key = biome.unwrapKey();
		if (key.isEmpty()) {
			return "Unknown";
		}

		String path = key.get().identifier().getPath();
		String[] words = path.split("_");
		StringBuilder builder = new StringBuilder();
		for (String word : words) {
			if (word.isEmpty()) {
				continue;
			}
			if (!builder.isEmpty()) {
				builder.append(' ');
			}
			builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
		}
		return builder.toString();
	}
}
