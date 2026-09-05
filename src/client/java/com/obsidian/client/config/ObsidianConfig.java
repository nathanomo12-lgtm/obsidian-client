package com.obsidian.client.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Plain-data root object serialized to {@code .minecraft/config/obsidian/obsidian-client.json}.
 */
public class ObsidianConfig {

	/** Schema version, bumped whenever the on-disk format changes, to support migrations. */
	public int configVersion = 1;

	/** Per-module persisted state, keyed by {@link com.obsidian.client.module.Module#getName()}. */
	public Map<String, ModuleState> modules = new LinkedHashMap<>();

	public static class ModuleState {
		public boolean enabled;
		/** GLFW key code, or -1 (InputUtil.UNKNOWN_KEY) if unbound. */
		public int keyCode = -1;
	}
}
