package com.example.flywheel_of_terror;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

/**
 * Phase 3: the knife-attack cue is no longer a cross-side static flag read on the client tick. The
 * server scheduler calls {@link #play(Player)} for the affected player, which sends a single S2C
 * sound packet to that one player's client.
 */
public final class sound_knife_attack {
   private sound_knife_attack() {
   }

   public static void play(Player player) {
      // Restore the original ~20-tick de-dup cooldown so a re-trigger inside that window does not
      // stack a second cue. Tracked per-player via game time instead of the old shared static.
      long now = player.level().getGameTime();
      if (now < state.getLong(player, "sound_knife_attack_cd")) {
         return;
      }

      state.putLong(player, "sound_knife_attack_cd", now + 20L);
      Network.sound(player, (SoundEvent)register_sounds.knife_attack.get(), 1.0F, 1.0F);
   }
}
