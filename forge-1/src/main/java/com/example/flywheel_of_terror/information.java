package com.example.flywheel_of_terror;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import com.example.flywheel_of_terror.client.client_safe;
import net.minecraft.client.gui.screens.Screen;
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
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class information {
   public static Block block_under_player;
   public static long time;
   public static Player just_player;
   public static Entity igrok;
   public static LivingEntity livingingrok;
   public static ItemStack last_dropped;
   public static boolean somewho_dropped = true;
   public static Screen current_screen;
   public static ItemStack head = new ItemStack(Items.PLAYER_HEAD);
   public static UUID playeruuid;
   public static boolean show_model = true;
   public static Random random = new Random();
   public static final String play_time_nbt = "play_time";

   @SubscribeEvent
   public static void return_to_normal(PlayerLoggedInEvent event) {
      CompoundTag global_tag = event.getEntity().getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      if (tag.getBoolean("first_was") && tag.getDouble("sens") != 0.0) {
         double sens = tag.getDouble("sens");
         DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client_safe.restoreSensitivity(sens));
         tag.putBoolean("first_was", true);
         global_tag.put("flywheel_of_terror", tag);
      }

      event.getEntity().getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onRenderHud(Post event) {
      current_screen = event.getScreen();
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void close(Closing event) {
      current_screen = null;
      show_model = true;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void open(Opening event) {
      if (event.getScreen() instanceof InventoryScreen && random.nextInt(1, 30) == 4) {
         show_model = false;
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
         paranoia.time_to_event = 1;
      }

      if (text.contains("between")) {
         oh_no_between_screens.do_event(player);
      }
   }

   @SubscribeEvent
   public static void last_drop(ItemTossEvent event) {
      Player player = event.getPlayer();
      last_dropped = event.getEntity().getItem();
      somewho_dropped = false;
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
      if (!player.level().isClientSide()) {
         BlockPos pos = new BlockPos((int)player.getX(), (int)player.getY() - 1, (int)player.getZ());
         block_under_player = player.level().getBlockState(pos).getBlock();
         igrok = player;
         livingingrok = player;
         just_player = player;
         playeruuid = igrok.getUUID();
         CompoundTag tag = head.getOrCreateTag();
         CompoundTag owner = new CompoundTag();
         owner.putString("Name", "killer");
         owner.putUUID("Id", playeruuid);
         tag.put("SkullOwner", owner);
         if (player.level() instanceof ServerLevel serv) {
            time = serv.getDayTime();
         }

         if (player.tickCount % 40 == 0) {
            set_play_time(player, get_play_time(player) + 1);
         }
      }
   }
}
