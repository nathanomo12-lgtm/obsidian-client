package com.obsidian.client.module;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Central registry and dispatcher for all modules.
 *
 * <p>Dispatch is lazy by construction: {@link #onClientTick()} and
 * {@link #onHudRender(DrawContext, float)} only iterate over enabled modules
 * (tracked in a separate list maintained alongside enable/disable), so
 * disabled modules never cost tick or render time.</p>
 */
public final class ModuleManager {

	private final Map<String, Module> modulesByName = new LinkedHashMap<>();
	private final List<Module> allModules = new ArrayList<>();
	private final List<Module> enabledModules = new ArrayList<>();

	public void register(Module module) {
		modulesByName.put(module.getName().toLowerCase(), module);
		allModules.add(module);
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
			while (module.getKeyBinding().wasPressed()) {
				toggle(module);
			}
		}
	}

	/** Toggles a module and keeps the fast-path enabled list in sync. */
	public void toggle(Module module) {
		module.toggle();
		syncEnabledState(module);
	}

	public void setEnabled(Module module, boolean enabled) {
		module.setEnabled(enabled);
		syncEnabledState(module);
	}

	public void onClientTick() {
		handleKeybinds();
		for (int i = 0; i < enabledModules.size(); i++) {
			enabledModules.get(i).onTick();
		}
	}

	public void onHudRender(DrawContext context, float tickDelta) {
		for (int i = 0; i < enabledModules.size(); i++) {
			enabledModules.get(i).onRender(context, tickDelta);
		}
	}
}
