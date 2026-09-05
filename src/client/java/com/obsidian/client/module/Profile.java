package com.obsidian.client.module;

/**
 * A named set of persisted module toggles. Each profile keeps its own saved
 * state in config, so switching profiles switches your whole module loadout.
 *
 * <p>{@link #COMPETITIVE} is special: regardless of what's saved for it,
 * {@link ModuleManager} refuses to enable any {@link ComplianceTier#SERVER_DEPENDENT}
 * module while it is active.</p>
 */
public enum Profile {
	PVP,
	SURVIVAL,
	CUSTOM,
	COMPETITIVE;

	public static Profile fromNameOrDefault(String name, Profile fallback) {
		if (name == null) {
			return fallback;
		}
		for (Profile profile : values()) {
			if (profile.name().equalsIgnoreCase(name)) {
				return profile;
			}
		}
		return fallback;
	}
}
