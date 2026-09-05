package com.obsidian.client.module;

/**
 * Declares whether a module is safe to use on any server, or whether it may
 * be restricted by some servers' rules and should be reviewed by the user
 * before enabling. Every {@link Module} must declare one explicitly &mdash;
 * see {@link ModuleManager#register(Module)} for the fail-closed check.
 */
public enum ComplianceTier {

	/** Pure client-side display/automation of the player's own input; not disallowed by any known server ruleset. */
	SAFE("SAFE"),

	/**
	 * May be treated as a cheat or be explicitly disallowed on some servers
	 * (e.g. camera-affecting features like Freelook). Force-disabled under
	 * the "Competitive" profile regardless of the user's per-module toggle.
	 */
	SERVER_DEPENDENT("CHECK RULES");

	private final String badgeLabel;

	ComplianceTier(String badgeLabel) {
		this.badgeLabel = badgeLabel;
	}

	public String getBadgeLabel() {
		return badgeLabel;
	}
}
