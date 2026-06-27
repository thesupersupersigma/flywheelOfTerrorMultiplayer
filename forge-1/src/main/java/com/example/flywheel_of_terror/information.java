package com.example.flywheel_of_terror;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ScreenEvent.Closing;
import net.minecraftforge.client.event.ScreenEvent.Opening;
import net.minecraftforge.client.event.ScreenEvent.Render.Post;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * Stateless helper class. Phase 4 removed every {@code public static} mutable field that used to
 * live here — the old global "current player" singleton ({@code just_player}/{@code igrok}/
 * {@code livingingrok}/{@code playeruuid}), the block-under-player / day-time caches, the somewho
 * drop relay, and the shared head ItemStack. Each custom mob now stores its own target player as a
 * UUID in its persistent NBT (see {@link #setTarget}/{@link #getTarget}); per-player gameplay state
 * lives in each player's {@code "flywheel_of_terror"} NBT compound (see {@link state}). What remains
 * here is purely stateless utility methods plus client-only GUI bookkeeping in {@link client_events}.
 */
@EventBusSubscriber
public class information {
   public static final String play_time_nbt = "play_time";
   /** NBT key under which a custom mob records the UUID of the player it is haunting. */
   public static final String target_nbt = "fot_target";

   /** Record {@code player} as this mob's target by storing the player's UUID in the mob's NBT. */
   public static void setTarget(Entity mob, Player player) {
      if (player != null) {
         mob.getPersistentData().putUUID(target_nbt, player.getUUID());
      }
   }

   /**
    * Resolve this mob's target player. Returns the player whose UUID was stored at spawn if they
    * are present and alive; otherwise falls back to the nearest player so a mob spawned without an
    * explicit target (e.g. a randomly-replaced animal) still behaves.
    */
   public static Player getTarget(Entity mob) {
      CompoundTag tag = mob.getPersistentData();
      if (tag.hasUUID(target_nbt)) {
         Player player = mob.level().getPlayerByUUID(tag.getUUID(target_nbt));
         if (player != null && player.isAlive()) {
            return player;
         }
      }

      return mob.level().getNearestPlayer(mob, 500.0);
   }

   /** Block directly beneath the player (replaces the old cached {@code block_under_player} static). */
   public static Block block_under(Player player) {
      BlockPos pos = new BlockPos((int)player.getX(), (int)player.getY() - 1, (int)player.getZ());
      return player.level().getBlockState(pos).getBlock();
   }

   /** A player-head ItemStack skinned to {@code player} (replaces the old shared {@code head} static). */
   public static ItemStack headFor(Player player) {
      ItemStack head = new ItemStack(Items.PLAYER_HEAD);
      CompoundTag tag = head.getOrCreateTag();
      CompoundTag owner = new CompoundTag();
      owner.putString("Name", "killer");
      owner.putUUID("Id", player.getUUID());
      tag.put("SkullOwner", owner);
      return head;
   }

   @SubscribeEvent
   public static void return_to_normal(PlayerLoggedInEvent event) {
      CompoundTag global_tag = event.getEntity().getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      if (tag.getBoolean("first_was") && tag.getDouble("sens") != 0.0) {
         double sens = tag.getDouble("sens");
         Network.fx(event.getEntity(), Network.RESTORE_SENS, (float)sens);
         tag.putBoolean("first_was", true);
         global_tag.put("flywheel_of_terror", tag);
      }

      event.getEntity().getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
   }

   @EventBusSubscriber(value = {Dist.CLIENT})
   public static class client_events {
      /** Client-local: whether the inventory player model should render (set randomly on open). */
      public static boolean show_model = true;
      private static final Random random = new Random();
      // Last GUI state reported to the server, so we only send a C2S ScreenStatePacket when it
      // actually changes instead of every render frame.
      private static boolean last_inv = false;
      private static boolean last_craft = false;

      private static void report(boolean inv, boolean craft) {
         if (inv != last_inv || craft != last_craft) {
            last_inv = inv;
            last_craft = craft;
            Network.sendScreenState(inv, craft);
         }
      }

      @SubscribeEvent(
         priority = EventPriority.LOWEST
      )
      public static void onRenderHud(Post event) {
         boolean inventory_open = event.getScreen() instanceof InventoryScreen;
         boolean crafting_open = event.getScreen() instanceof CraftingScreen;
         report(inventory_open, crafting_open);
      }

      @SubscribeEvent(
         priority = EventPriority.LOWEST
      )
      public static void close(Closing event) {
         show_model = true;
         report(false, false);
      }

      @SubscribeEvent(
         priority = EventPriority.LOWEST
      )
      public static void open(Opening event) {
         if (event.getScreen() instanceof InventoryScreen && random.nextInt(1, 30) == 4) {
            show_model = false;
         }
      }
   }

   @SubscribeEvent
   public static void debug(ServerChatEvent event) {
      Player player = event.getPlayer();
      String text = event.getMessage().toString().toLowerCase();
      if (text.contains("hunt")) {
         independence.start(player, 200);
      }

      if (text.contains("next1")) {
         state.putInt(player, "time_to_event", 1);
      }

      if (text.contains("between")) {
         oh_no_between_screens.do_event(player);
      }
   }

   @SubscribeEvent
   public static void last_drop(ItemTossEvent event) {
      Player player = event.getPlayer();
      CompoundTag tag = state.tag(player);
      CompoundTag item = new CompoundTag();
      event.getEntity().getItem().save(item);
      tag.put("somewho_drop", item);
      tag.putBoolean("somewho_dropped", false);
      state.save(player, tag);
   }

   public static String getCoordinates(double x, double y, double z) {
      return (int)x + "," + (int)y + "," + (int)z;
   }

   public static String getHouse(double x, double y, double z) {
      return (int)x + "," + (int)y + "," + (int)z + "house";
   }

   public static String get_name_of_the_player(Player player) {
      return player.getName().toString().toLowerCase();
   }

   public static void thunder_player(Player player) {
      if (player.level() instanceof ServerLevel serv) {
         BlockPos pos = new BlockPos((int)player.getX(), (int)player.getY(), (int)player.getZ());
         LightningBolt thunder = (LightningBolt)EntityType.LIGHTNING_BOLT.create(serv);
         thunder.moveTo(pos, 0.0F, 0.0F);
         serv.addFreshEntity(thunder);
      }
   }

   public static List<BlockPos> get_poses_of_blocks_placed_by_player(Player player, int x, int y, int z, boolean include_house_blocks) {
      List<BlockPos> poses = new ArrayList<>();
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");

      for (double xx = player.getX() - (double)x; xx <= player.getX() + (double)x; xx++) {
         for (double yy = player.getY() - (double)y; yy <= player.getY() + (double)y; yy++) {
            for (double zz = player.getZ() - (double)z; zz <= player.getZ() + (double)z; zz++) {
               BlockPos pos = new BlockPos((int)xx, (int)yy, (int)zz);
               String coordinates = getCoordinates(xx, yy, zz);
               if (tag.getBoolean(coordinates) && !tag.getBoolean(getHouse(xx, yy, zz))) {
                  poses.add(pos);
               }

               if (tag.getBoolean(getHouse(xx, yy, zz)) && include_house_blocks) {
                  poses.add(pos);
               }
            }
         }
      }

      return poses;
   }

   public static void do_a_silence(Player player) {
      ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(null, null);
      if (player instanceof ServerPlayer serv) {
         serv.connection.send(packet);
      }
   }

   public static List<Block> get_blocks_around_player(Player player, int x, int y, int z) {
      List<Block> current_blocks = new ArrayList<>();

      for (double xx = player.getX() - (double)x; xx <= player.getX() + (double)x; xx++) {
         for (double yy = player.getY() - (double)y - 1.0; yy <= player.getY() + (double)y - 1.0; yy++) {
            for (double zz = player.getZ() - (double)z; zz <= player.getZ() + (double)z; zz++) {
               BlockPos pos = new BlockPos((int)xx, (int)yy, (int)zz);
               current_blocks.add(player.level().getBlockState(pos).getBlock());
            }
         }
      }

      return current_blocks;
   }

   public static void play_sound_at_server(Player player, SoundEvent sound, Boolean everywhere) {
      if (everywhere) {
         player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.MUSIC, 1.0F, 1.0F);
      } else {
         player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);
      }
   }

   public static void set_play_time(Player player, int seconds) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putInt("play_time", seconds);
      global_tag.put("flywheel_of_terror", tag);
   }

   public static int get_play_time(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getInt("play_time");
   }

   @SubscribeEvent
   public static void check_state(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide() && player.tickCount % 40 == 0) {
         set_play_time(player, get_play_time(player) + 1);
      }
   }
}
