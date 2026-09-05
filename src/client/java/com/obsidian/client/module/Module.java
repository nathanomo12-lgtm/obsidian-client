package com.obsidian.client.module;

import com.mojang.blaze3d.platform.InputConstants;
import com.obsidian.client.module.setting.Setting;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base class for every Obsidian Client feature.
 *
 * <p>Modules are lazy: {@link #onTick()} and {@link #onRender(GuiGraphics, float)}
 * are only ever invoked by {@link ModuleManager} while the module is enabled,
 * so disabled modules consume zero tick/render time.</p>
 */
public abstract class Module {

	/** Shared keybind category shown as "Obsidian Client" in Options &gt; Controls. */
	public static final KeyMapping.Category KEY_CATEGORY =
			KeyMapping.Category.register(Identifier.fromNamespaceAndPath("obsidian-client", "modules"));

	private final String name;
	private final String description;
	private final ModuleCategory category;
	private final ComplianceTier tier;
	private final KeyMapping keyBinding;
	private final List<Setting<?>> settings = new ArrayList<>();
	private boolean enabled;

	/**
	 * @param name        display name shown in the ClickGUI
	 * @param description short description shown as a tooltip
	 * @param category    ClickGUI tab this module belongs to
	 * @param tier        compliance tier; required so no module can be registered without one
	 *                    declared (see {@link ModuleManager#register(Module)})
	 * @param defaultKey  default GLFW key code, or -1 (GLFW_KEY_UNKNOWN) for unbound
	 */
	protected Module(String name, String description, ModuleCategory category, ComplianceTier tier, int defaultKey) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.tier = Objects.requireNonNull(tier, () -> "Module '" + name + "' must declare an explicit ComplianceTier");
		this.keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.obsidian-client." + name.toLowerCase().replace(' ', '_'),
				InputConstants.Type.KEYSYM,
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
	public void onRender(GuiGraphics context, float tickDelta) {
	}

	/**
	 * Reserved for modules that need raw key events (e.g. an in-GUI keybind
	 * capture widget) rather than just their own toggle keybind. Not wired to
	 * a global input hook — doing so would require a mixin into keyboard
	 * handling, which the architecture avoids in hot paths. Call this
	 * directly from GUI code that owns input focus.
	 *
	 * @param keyCode  GLFW key code
	 * @param scanCode platform-specific scan code
	 * @param pressed  {@code true} on key-down, {@code false} on key-up
	 */
	public void onKeyInput(int keyCode, int scanCode, boolean pressed) {
	}

	/** Registers a configurable setting so it shows up in the ClickGUI's settings pane. */
	protected final <S extends Setting<?>> S addSetting(S setting) {
		settings.add(setting);
		return setting;
	}

	public List<Setting<?>> getSettings() {
		return Collections.unmodifiableList(settings);
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

	public ComplianceTier getTier() {
		return tier;
	}

	public KeyMapping getKeyBinding() {
		return keyBinding;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
