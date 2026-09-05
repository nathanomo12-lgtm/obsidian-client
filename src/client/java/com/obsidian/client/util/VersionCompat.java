package com.obsidian.client.util;

import com.obsidian.client.ObsidianClientMod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

import java.util.Optional;

/**
 * Verifies the running Minecraft version falls within the range Obsidian Client
 * has been tested against. Unsupported versions do not prevent the mod from
 * loading (graceful degradation) &mdash; they only log a warning, since most
 * modules are unaffected by minor Minecraft patch differences.
 */
public final class VersionCompat {

	public static final String MIN_SUPPORTED = "1.21.4";
	public static final String MAX_SUPPORTED = "1.21.11";

	private VersionCompat() {
	}

	public static void check() {
		Optional<ModContainer> minecraft = FabricLoader.getInstance().getModContainer("minecraft");
		if (minecraft.isEmpty()) {
			ObsidianClientMod.LOGGER.warn("Could not determine Minecraft version; skipping compatibility check.");
			return;
		}

		String current = minecraft.get().getMetadata().getVersion().getFriendlyString();

		try {
			// Typed as Version (not SemanticVersion) so compareTo resolves to the
			// non-deprecated Version#compareTo(Version) overload.
			Version currentVersion = SemanticVersion.parse(current);
			Version min = SemanticVersion.parse(MIN_SUPPORTED);
			Version max = SemanticVersion.parse(MAX_SUPPORTED);

			if (currentVersion.compareTo(min) < 0 || currentVersion.compareTo(max) > 0) {
				ObsidianClientMod.LOGGER.warn(
						"Obsidian Client is tested on Minecraft {}-{}, but detected {}. "
								+ "The mod will still load, but some modules may not behave correctly.",
						MIN_SUPPORTED, MAX_SUPPORTED, current);
			} else {
				ObsidianClientMod.LOGGER.info("Minecraft version {} is within the supported range.", current);
			}
		} catch (VersionParsingException e) {
			ObsidianClientMod.LOGGER.warn("Failed to parse Minecraft version '{}' for compatibility check: {}", current, e.getMessage());
		}
	}
}
