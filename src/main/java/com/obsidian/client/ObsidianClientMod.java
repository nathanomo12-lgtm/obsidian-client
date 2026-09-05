package com.obsidian.client;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common (client + server safe) entrypoint. Kept intentionally minimal since
 * Obsidian Client is a client-side-only mod; all module/GUI/render logic
 * lives in the "client" source set (see {@link com.obsidian.client.client.ObsidianClientModClient}).
 */
public class ObsidianClientMod implements ModInitializer {

	public static final String MOD_ID = "obsidian-client";
	public static final Logger LOGGER = LoggerFactory.getLogger("Obsidian Client");

	@Override
	public void onInitialize() {
		LOGGER.info("Obsidian Client common initializer loaded.");
	}
}
