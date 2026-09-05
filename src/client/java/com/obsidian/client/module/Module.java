package com.obsidian.client.module;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

/**
 * Base class for every Obsidian Client feature.
 *
 * <p>Modules are lazy: {@link #onTick()} and {@link #onRender(DrawContext, float)}
 * are only ever invoked by {@link ModuleManager} while the module is enabled,
 * so disabled modules consume zero tick/render time.</p>
 */
public abstract class Module {

	/** Shared keybind category shown as "Obsidian Client" in Options &gt; Controls. */
	public static final KeyBinding.Category KEY_CATEGORY =
			KeyBinding.Category.create(Identifier.of("obsidian-client", "modules"));

	private final String name;
	private final String description;
	private final ModuleCategory category;
	private final KeyBinding keyBinding;
	private boolean enabled;

	/**
	 * @param name        display name shown in the ClickGUI
	 * @param description short description shown as a tooltip
	 * @param category    ClickGUI tab this module belongs to
	 * @param defaultKey  default GLFW key code, or -1 (GLFW_KEY_UNKNOWN) for unbound
	 */
	protected Module(String name, String description, ModuleCategory category, int defaultKey) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.obsidian-client." + name.toLowerCase().replace(' ', '_'),
				InputUtil.Type.KEYSYM,
				defaultKey,
				KEY_CATEGORY
		));
	}

	/**
	 * Toggles the module on/off. This is the only place {@link #enabled} is mutated
	 * so {@link #onEnable()}/{@link #onDisable()} are guaranteed to run exactly once
	 * per state transition.
	 */
	public final void toggle() {
		setEnabled(!enabled);
	}

	public final void setEnabled(boolean value) {
		if (this.enabled == value) {
			return;
		}
		this.enabled = value;
		if (value) {
			onEnable();
		} else {
			onDisable();
		}
	}

	/** Called once when the module transitions from disabled to enabled. */
	public void onEnable() {
	}

	/** Called once when the module transitions from enabled to disabled. */
	public void onDisable() {
	}

	/** Called on every client tick while the module is enabled. */
	public void onTick() {
	}

	/**
	 * Called on every HUD render pass while the module is enabled.
	 *
	 * @param context   the draw context to batch draw calls into
	 * @param tickDelta partial tick time, for smooth interpolation
	 */
	public void onRender(DrawContext context, float tickDelta) {
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ModuleCategory getCategory() {
		return category;
	}

	public KeyBinding getKeyBinding() {
		return keyBinding;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
