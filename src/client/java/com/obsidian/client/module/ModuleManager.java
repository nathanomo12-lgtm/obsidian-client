package com.obsidian.client.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Central registry and dispatcher for all modules.
 *
 * <p>Dispatch is lazy by construction: {@link #onClientTick()} and
 * {@link #onHudRender(GuiGraphics, float)} only iterate over enabled modules
 * (tracked in a separate list maintained alongside enable/disable), so
 * disabled modules never cost tick or render time.</p>
 */
public final class ModuleManager {

	private final Map<String, Module> modulesByName = new LinkedHashMap<>();
	private final List<Module> allModules = new ArrayList<>();
	private final List<Module> enabledModules = new ArrayList<>();
	private Profile activeProfile = Profile.CUSTOM;

	/**
	 * Registers a module. Fail-closed: a module without an explicit
	 * {@link ComplianceTier} is refused rather than silently defaulted, since
	 * {@link Module}'s constructor already requires one, this is a defense-in-depth
	 * check against future refactors that might bypass it.
	 */
	public void register(Module module) {
		if (module.getTier() == null) {
			throw new IllegalStateException(
					"Refusing to register module '" + module.getName() + "': no ComplianceTier declared.");
		}
		modulesByName.put(module.getName().toLowerCase(), module);
		allModules.add(module);
	}

	public Profile getActiveProfile() {
		return activeProfile;
	}

	/**
	 * Switches the active profile. Switching into {@link Profile#COMPETITIVE}
	 * immediately force-disables any currently-enabled {@link ComplianceTier#SERVER_DEPENDENT}
	 * module, regardless of its saved/toggled state.
	 */
	public void setActiveProfile(Profile profile) {
		this.activeProfile = profile;
		if (profile == Profile.COMPETITIVE) {
			for (Module module : allModules) {
				if (module.getTier() == ComplianceTier.SERVER_DEPENDENT && module.isEnabled()) {
					module.setEnabled(false);
					syncEnabledState(module);
				}
			}
		}
	}

	/** Whether the given module is currently allowed to be enabled under the active profile. */
	public boolean isAllowedToEnable(Module module) {
		return !(activeProfile == Profile.COMPETITIVE && module.getTier() == ComplianceTier.SERVER_DEPENDENT);
	}

	public List<Module> getModules() {
		return Collections.unmodifiableList(allModules);
	}

	public List<Module> getModulesByCategory(ModuleCategory category) {
		List<Module> result = new ArrayList<>();
		for (Module module : allModules) {
			if (module.getCategory() == category) {
				result.add(module);
			}
		}
		return result;
	}

	public Module getModule(String name) {
		return modulesByName.get(name.toLowerCase());
	}

	@SuppressWarnings("unchecked")
	public <T extends Module> T getModule(Class<T> type) {
		for (Module module : allModules) {
			if (type.isInstance(module)) {
				return (T) module;
			}
		}
		return null;
	}

	/** Called by {@code Module#setEnabled} indirectly via {@link #syncEnabledState(Module)}. */
	void syncEnabledState(Module module) {
		if (module.isEnabled()) {
			if (!enabledModules.contains(module)) {
				enabledModules.add(module);
			}
		} else {
			enabledModules.remove(module);
		}
	}

	/** Polls keybinds and toggles the corresponding module; call once per client tick. */
	public void handleKeybinds() {
		for (Module module : allModules) {
			while (module.getKeyBinding().consumeClick()) {
				toggle(module);
			}
		}
	}

	/**
	 * Toggles a module and keeps the fast-path enabled list in sync. A no-op
	 * if this would enable a {@link ComplianceTier#SERVER_DEPENDENT} module
	 * while the {@link Profile#COMPETITIVE} profile is active.
	 */
	public void toggle(Module module) {
		if (!module.isEnabled() && !isAllowedToEnable(module)) {
			return;
		}
		module.toggle();
		syncEnabledState(module);
	}

	/** See {@link #toggle(Module)} for the Competitive-profile restriction. */
	public void setEnabled(Module module, boolean enabled) {
		if (enabled && !isAllowedToEnable(module)) {
			return;
		}
		module.setEnabled(enabled);
		syncEnabledState(module);
	}

	public void onClientTick() {
		handleKeybinds();
		for (int i = 0; i < enabledModules.size(); i++) {
			enabledModules.get(i).onTick();
		}
	}

	public void onHudRender(GuiGraphics context, float tickDelta) {
		for (int i = 0; i < enabledModules.size(); i++) {
			enabledModules.get(i).onRender(context, tickDelta);
		}
	}
}
