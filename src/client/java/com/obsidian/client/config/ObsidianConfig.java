package com.obsidian.client.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Plain-data root object serialized to {@code .minecraft/config/obsidian/obsidian-client.json}.
 *
 * <p>Module state is stored per {@link com.obsidian.client.module.Profile} so switching
 * profiles switches your whole module loadout.</p>
 */
public class ObsidianConfig {

	/** Schema version, bumped whenever the on-disk format changes, to support migrations. */
	public int configVersion = 2;

	/** Name of the {@link com.obsidian.client.module.Profile} active when this was last saved. */
	public String activeProfile = "CUSTOM";

	/** Per-profile, per-module persisted state: profile name -&gt; module name -&gt; state. */
	public Map<String, Map<String, ModuleState>> profiles = new LinkedHashMap<>();

	public static class ModuleState {
		public boolean enabled;
		/** GLFW key code, or -1 (InputUtil.UNKNOWN_KEY) if unbound. */
		public int keyCode = -1;
	}
}
