package com.obsidian.client.module.impl;

import com.obsidian.client.module.ComplianceTier;
import com.obsidian.client.module.Module;
import com.obsidian.client.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.lwjgl.glfw.GLFW;

/**
 * Plays a short, subtle local-only sound cue whenever the player's own
 * attack lands, as an additional feedback channel alongside Hit Flash. Fed
 * by the same {@code AttackEntityCallback} hook wired in
 * {@code ObsidianClientModClient} (which only ever returns {@code PASS}) —
 * purely cosmetic audio, played client-side only via
 * {@code ClientLevel#playLocalSound}, never broadcast or gameplay-affecting.
 */
public class HitSoundModule extends Module {

	public HitSoundModule() {
		super("Hit Sound Cue", "Plays a subtle local sound when your attack lands.", ModuleCategory.COMBAT, ComplianceTier.SAFE, GLFW.GLFW_KEY_UNKNOWN);
	}

	/** Called from the global AttackEntityCallback hook when the player lands an attack. */
	public void onHitLanded() {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.level == null) {
			return;
		}
		client.level.playLocalSound(client.player, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.3f, 1.6f);
	}
}
