package com.example.flywheel_of_terror;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * Per-player horror state. Phase 2 replaced the old shared {@code public static} fields with
 * per-player storage inside each player's {@code "flywheel_of_terror"} NBT compound, so two
 * players on a dedicated server keep independent timers, flags and counters.
 *
 * <p>Every accessor reads/writes that one player's compound and persists it immediately, which
 * keeps the same "set a field, read it next tick" semantics the original statics had — only now
 * scoped to the entity instead of the JVM.
 */
public final class state {
   private state() {
   }

   public static CompoundTag tag(Player player) {
      return player.getPersistentData().getCompound("flywheel_of_terror");
   }

   public static void save(Player player, CompoundTag tag) {
      player.getPersistentData().put("flywheel_of_terror", tag);
   }

   public static boolean getBool(Player player, String key) {
      return tag(player).getBoolean(key);
   }

   public static void putBool(Player player, String key, boolean value) {
      CompoundTag tag = tag(player);
      tag.putBoolean(key, value);
      save(player, tag);
   }

   public static int getInt(Player player, String key) {
      return tag(player).getInt(key);
   }

   public static void putInt(Player player, String key, int value) {
      CompoundTag tag = tag(player);
      tag.putInt(key, value);
      save(player, tag);
   }

   public static long getLong(Player player, String key) {
      return tag(player).getLong(key);
   }

   public static void putLong(Player player, String key, long value) {
      CompoundTag tag = tag(player);
      tag.putLong(key, value);
      save(player, tag);
   }

   public static float getFloat(Player player, String key) {
      return tag(player).getFloat(key);
   }

   public static void putFloat(Player player, String key, float value) {
      CompoundTag tag = tag(player);
      tag.putFloat(key, value);
      save(player, tag);
   }

   public static double getDouble(Player player, String key) {
      return tag(player).getDouble(key);
   }

   public static void putDouble(Player player, String key, double value) {
      CompoundTag tag = tag(player);
      tag.putDouble(key, value);
      save(player, tag);
   }

   public static String getString(Player player, String key) {
      return tag(player).getString(key);
   }

   public static void putString(Player player, String key, String value) {
      CompoundTag tag = tag(player);
      tag.putString(key, value);
      save(player, tag);
   }
}
