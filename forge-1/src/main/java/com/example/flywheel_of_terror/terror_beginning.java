package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class terror_beginning {
   public static boolean house_builded_now = false;
   public static int count_of_player_block;
   public static boolean near_house = false;
   public static boolean first_message_was = false;
   public static boolean sound_terror = false;
   public static int tics_to_next_sound = 20;
   public static int count_of_victims = 11;
   public static int killed_victims = 0;
   public static Random random = new Random();
   public static int tics_to_next_house = 30;
   public static boolean away_house = true;
   public static boolean far_away_house = true;
   public static boolean sound_must_be = false;
   public static boolean his_hunt = false;
   public static boolean hunt_sound_must_be = false;
   public static boolean sound_house_destroyed = false;

   public static void destroy_house(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tics_to_next_house = 150;
      first_message_was = false;
      killed_victims = 0;
      System.out.println("HOUSE DESTROYED");
      double xx = (double)tag.getInt("house_X");
      double yy = (double)tag.getInt("house_Y");
      double zz = (double)tag.getInt("house_Z");

      for (double x = xx - 40.0; x <= xx + 40.0; x++) {
         for (double y = yy - 20.0; y <= yy + 20.0; y++) {
            for (double z = zz - 40.0; z <= zz + 40.0; z++) {
               String coordinates = (int)x + "," + (int)y + "," + (int)z;
               if (tag.getBoolean(coordinates + "house")) {
                  BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                  player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                  tag.putBoolean(coordinates, false);
                  tag.putBoolean(coordinates + "house", false);
                  global_tag.put("flywheel_of_terror", tag);
               }
            }
         }
      }

      tag.putBoolean("builded", false);
      global_tag.put("flywheel_of_terror", tag);
      System.out.println(tag.getBoolean("builded"));
      player.sendSystemMessage(Component.literal("house was slain by oh_no"));
   }

   @SubscribeEvent
   public static void nobreak(BreakEvent event) {
      if (his_hunt) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void noplace(EntityPlaceEvent event) {
      if (his_hunt || first_message_was) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void sleep(PlayerSleepInBedEvent event) {
      if (event.getEntity().level() instanceof ServerLevel serv && serv.getDayTime() > 12000L && serv.getDayTime() < 14000L) {
         event.getEntity().level().destroyBlock(event.getPos(), true);
      }

      if (first_message_was) {
         event.getEntity().level().destroyBlock(event.getPos(), true);
         event.getEntity().displayClientMessage(Component.literal("Hunting first"), true);
      }

      if (!near_house) {
         event.getEntity().level().destroyBlock(event.getPos(), true);
         event.getEntity().displayClientMessage(Component.literal("sleeping outside your home guarantees your capture"), true);
      }
   }

   @SubscribeEvent
   public static void save_info(Clone event) {
      if (event.isWasDeath()) {
         event.getEntity()
            .getPersistentData()
            .put("flywheel_of_terror", event.getOriginal().getPersistentData().getCompound("flywheel_of_terror").copy());
      }
   }

   @SubscribeEvent
   public static void kill_victim(LivingDeathEvent event) {
      if (event.getSource().getEntity() instanceof Player player
         && player.level() instanceof ServerLevel serv
         && serv.getDayTime() > 14000L
         && serv.getDayTime() < 17000L
         && first_message_was) {
         killed_victims++;
         player.displayClientMessage(Component.literal(killed_victims + "/" + count_of_victims), true);
         if (killed_victims >= count_of_victims) {
            killed_victims = 0;
            count_of_victims = 11;
            first_message_was = false;
         }
      }
   }

   @SubscribeEvent
   public static void hunting(PlayerTickEvent event) {
      if (terror_end.phase == 0) {
         CompoundTag global_tag = event.player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         Player player = event.player;
         boolean client = player.level().isClientSide();
         if (client && hunt_sound_must_be) {
            player.playSound((SoundEvent)register_sounds.his_hunt.get(), 1.0F, 1.0F);
            hunt_sound_must_be = false;
         }

         if (client && sound_house_destroyed) {
            player.playSound((SoundEvent)register_sounds.break_house.get(), 1.0F, 1.0F);
            sound_house_destroyed = false;
         }

         if (sound_must_be && event.player.level().isClientSide()) {
            event.player.playSound((SoundEvent)register_sounds.break_house.get(), 1.0F, 1.0F);
            sound_must_be = false;
         }

         if (near_house && !event.player.level().isClientSide() && event.player.level() instanceof ServerLevel serv) {
            if (serv.getDayTime() > 14000L && serv.getDayTime() < 14300L && !labyrinth_event.in_lab) {
               if (!first_message_was) {
                  count_of_victims = random.nextInt(5, 11);
               }

               event.player.displayClientMessage(Component.literal("It's time for you to hunt, get out of the house " + killed_victims + "/" + count_of_victims), true);
               first_message_was = true;
            }

            if (serv.getDayTime() > 14300L && serv.getDayTime() < 14600L && first_message_was) {
               event.player.displayClientMessage(Component.literal("§c leave the house, now."), true);
               sound_terror = true;
            }

            if (serv.getDayTime() == 14700L && first_message_was) {
               his_hunt = true;
               sound_house_destroyed = true;
               destroy_house(player);
               serv.setDayTime(17100L);
            }

            if (serv.getDayTime() > 14700L & serv.getDayTime() < 17000L && first_message_was) {
               event.player.displayClientMessage(Component.literal("You came back too soon."), true);
               first_message_was = false;
               sound_house_destroyed = true;
               destroy_house(player);
            }
         }

         if (event.player.level() instanceof ServerLevel serv) {
            if (serv.getDayTime() > 24000L) {
               serv.setDayTime(1L);
            }

            if (serv.getDayTime() > 14000L && serv.getDayTime() < 14300L && !near_house && !labyrinth_event.in_lab) {
               if (!first_message_was) {
                  count_of_victims = random.nextInt(5, 11);
               }

               if (tag.getBoolean("builded")) {
                  event.player.displayClientMessage(Component.literal("It's time for you to hunt " + killed_victims + "/" + count_of_victims + " Don't come home."), true);
               }

               if (!tag.getBoolean("builded")) {
                  event.player.displayClientMessage(Component.literal("It's time for you to hunt " + killed_victims + "/" + count_of_victims), true);
               }

               first_message_was = true;
            }
         }

         if (event.player.level() instanceof ServerLevel serv) {
            if (serv.getDayTime() == 17000L && first_message_was) {
               event.player.displayClientMessage(Component.literal("You failed"), true);
               first_message_was = false;
               killed_victims = 0;
               if (tag.getBoolean("builded")) {
                  destroy_house(player);
               } else {
                  his_hunt = true;
               }
            }

            if (serv.getDayTime() == 17100L && his_hunt && event.player.getY() < 55.0) {
               paranoia.do_a_call(event.player);
            }

            if (serv.getDayTime() == 17200L && his_hunt) {
               oh_no he = new oh_no((EntityType<? extends PathfinderMob>)add_humans.oh_no.get(), event.player.level());
               double x = event.player.getX() - player.getLookAngle().x * (double)random.nextFloat(10.0F, 20.0F);
               double z = event.player.getZ() - player.getLookAngle().z * (double)random.nextFloat(10.0F, 20.0F);
               int y = event.player.level().getHeight(Types.WORLD_SURFACE, (int)x, (int)z) + 1;
               he.setPos(x, (double)y, z);
               event.player.level().addFreshEntity(he);
               hunt_sound_must_be = true;
            }

            if (serv.getDayTime() > 17200L && serv.getDayTime() < 18900L && his_hunt) {
               event.player.displayClientMessage(Component.literal("It's time for him to hunt 0/1. Run"), true);
            }

            if (serv.getDayTime() > 18900L) {
               his_hunt = false;
            }
         }

         if (sound_terror && event.player.level().isClientSide()) {
            tics_to_next_sound--;
         }

         if (tics_to_next_sound <= 0 && sound_terror && event.player.level().isClientSide()) {
            if (near_house) {
               event.player.playSound((SoundEvent)register_sounds.leave_the_house.get(), 1.0F, 1.0F);
               tics_to_next_sound = 160;
            }

            sound_terror = false;
         }
      }
   }

   @SubscribeEvent
   public static void inhouse(PlayerTickEvent event) {
      tics_to_next_house--;
      Player player = event.player;
      if (!player.level().isClientSide()) {
         CompoundTag global_tag = event.player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         int count_of_blocks = 0;
         boolean context_near_house = false;
         boolean context_away_house = true;
         boolean dooris = false;
         boolean bedis = false;
         boolean furnaceis = false;
         boolean workis = false;

         for (double x = player.getX() - 10.0; x <= player.getX() + 10.0; x++) {
            for (double y = player.getY() - 10.0; y <= player.getY() + 10.0; y++) {
               for (double z = player.getZ() - 10.0; z <= player.getZ() + 10.0; z++) {
                  String coordinates = (int)x + "," + (int)y + "," + (int)z;
                  new BlockPos((int)x, (int)y, (int)z);
                  if (tag.getBoolean(coordinates + "house")) {
                     String coordinates1 = (int)x + "," + (int)(y + 1.0) + "," + (int)z;
                     String coordinates2 = (int)x + "," + (int)(y - 1.0) + "," + (int)z;
                     String coordinates3 = (int)(x + 1.0) + "," + (int)y + "," + (int)z;
                     String coordinates4 = (int)(x - 1.0) + "," + (int)y + "," + (int)z;
                     String coordinates5 = (int)x + "," + (int)y + "," + (int)(z + 1.0);
                     String coordinates6 = (int)x + "," + (int)y + "," + (int)(z - 1.0);
                     if (tag.getBoolean(coordinates1)) {
                        tag.putBoolean(coordinates1 + "house", true);
                     }

                     if (tag.getBoolean(coordinates2)) {
                        tag.putBoolean(coordinates2 + "house", true);
                     }

                     if (tag.getBoolean(coordinates3)) {
                        tag.putBoolean(coordinates3 + "house", true);
                     }

                     if (tag.getBoolean(coordinates4)) {
                        tag.putBoolean(coordinates4 + "house", true);
                     }

                     if (tag.getBoolean(coordinates5)) {
                        tag.putBoolean(coordinates5 + "house", true);
                     }

                     if (tag.getBoolean(coordinates6)) {
                        tag.putBoolean(coordinates6 + "house", true);
                     }
                  }

                  if (player.level().getBlockState(new BlockPos((int)x, (int)y, (int)z)).getBlock() instanceof DoorBlock) {
                     dooris = true;
                  }

                  if (player.level().getBlockState(new BlockPos((int)x, (int)y, (int)z)).getBlock() instanceof CraftingTableBlock) {
                     workis = true;
                  }

                  if (player.level().getBlockState(new BlockPos((int)x, (int)y, (int)z)).getBlock() instanceof FurnaceBlock) {
                     furnaceis = true;
                  }

                  if (player.level().getBlockState(new BlockPos((int)x, (int)y, (int)z)).getBlock() instanceof BedBlock) {
                     bedis = true;
                  }

                  if (tag.getBoolean(coordinates)) {
                     count_of_blocks++;
                  }
               }
            }
         }

         for (double x = player.getX() - 0.71F; x <= player.getX() + 0.71F; x++) {
            for (double y = player.getY() - 2.0; y <= player.getY() + 4.0; y++) {
               for (double z = player.getZ() - 0.71F; z <= player.getZ() + 0.71F; z++) {
                  String coordinatesx = (int)x + "," + (int)y + "," + (int)z;
                  if (tag.getBoolean(coordinatesx + "house") && tag.getBoolean("builded")) {
                     context_near_house = true;
                  }
               }
            }
         }

         for (double x = player.getX() - 10.0; x <= player.getX() + 10.0; x++) {
            for (double y = player.getY() - 10.0; y <= player.getY() + 10.0; y++) {
               for (double zx = player.getZ() - 10.0; zx <= player.getZ() + 10.0; zx++) {
                  String coordinatesx = (int)x + "," + (int)y + "," + (int)zx;
                  if (tag.getBoolean(coordinatesx + "house") && tag.getBoolean("builded")) {
                     context_away_house = false;
                  }
               }
            }
         }

         boolean context_far_away_house = true;

         for (double x = player.getX() - 20.0; x <= player.getX() + 20.0; x++) {
            for (double y = player.getY() - 20.0; y <= player.getY() + 20.0; y++) {
               for (double zxx = player.getZ() - 20.0; zxx <= player.getZ() + 20.0; zxx++) {
                  String coordinatesx = (int)x + "," + (int)y + "," + (int)zxx;
                  if (tag.getBoolean(coordinatesx + "house") && tag.getBoolean("builded")) {
                     context_far_away_house = false;
                  }
               }
            }
         }

         for (double x = player.getX() - 10.0; x <= player.getX() + 10.0; x++) {
            for (double y = player.getY() - 10.0; y <= player.getY() + 10.0; y++) {
               for (double zxxx = player.getZ() - 10.0; zxxx <= player.getZ() + 10.0; zxxx++) {
                  String coordinatesx = (int)x + "," + (int)y + "," + (int)zxxx;
                  BlockPos pos = new BlockPos((int)x, (int)y, (int)zxxx);
                  if (tag.getBoolean(coordinatesx) && player.level().getBlockState(pos).getBlock() instanceof AirBlock) {
                     tag.putBoolean(coordinatesx, false);
                  }
               }
            }
         }

         away_house = context_away_house;
         far_away_house = context_far_away_house;
         global_tag.put("flywheel_of_terror", tag);
         if (dooris) {
            count_of_blocks += 20;
         }

         if (workis) {
            count_of_blocks += 5;
         }

         if (bedis) {
            count_of_blocks += 30;
         }

         if (furnaceis) {
            count_of_blocks += 10;
         }

         count_of_player_block = count_of_blocks;
         near_house = context_near_house;
         if (count_of_player_block >= 90 && !tag.getBoolean("builded")) {
            house_builded_now = true;
         }

         if (count_of_player_block >= 90 && tag.getBoolean("builded") && away_house) {
            for (double x = player.getX() - 8.0; x <= player.getX() + 8.0; x++) {
               for (double y = player.getY() - 8.0; y <= player.getY() + 8.0; y++) {
                  for (double zxxxx = player.getZ() - 8.0; zxxxx <= player.getZ() + 8.0; zxxxx++) {
                     String coordinatesx = (int)x + "," + (int)y + "," + (int)zxxxx;
                     BlockPos pos = new BlockPos((int)x, (int)y, (int)zxxxx);
                     if (tag.getBoolean(coordinatesx)) {
                        tag.putBoolean(coordinatesx, false);
                        player.level().destroyBlock(pos, true, player);
                     }
                  }
               }
            }

            player.level()
               .playSound(
                  null,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  (SoundEvent)register_sounds.headless_spawn.get(),
                  SoundSource.RECORDS,
                  1.0F,
                  1.0F
               );
            event.player.displayClientMessage(Component.literal("§c you already have a house."), true);
         }
      }
   }

   @SubscribeEvent
   public static void build_house(PlayerTickEvent event) {
      Player player = event.player;
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      if (!player.level().isClientSide() && !tag.getBoolean("builded") && house_builded_now && tics_to_next_house <= 0) {
         tag.putBoolean("builded", true);
         boolean bedrock_placed = false;
         house_builded_now = false;
         System.out.println("HOUSE IS BUILDED");
         terror_continue.tics_cooldown = 400;
         away_house = false;
         terror_continue.need_try = false;

         for (double x = player.getX() - 10.0; x <= player.getX() + 10.0; x++) {
            for (double y = player.getY() - 10.0; y <= player.getY() + 10.0; y++) {
               for (double z = player.getZ() - 10.0; z <= player.getZ() + 10.0; z++) {
                  String coordinates2 = (int)x + "," + (int)y + "," + (int)z;
                  BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                  if (tag.getBoolean(coordinates2)) {
                     tag.putBoolean(coordinates2 + "house", true);
                     if (!bedrock_placed) {
                        player.level().setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
                        tag.putInt("house_X", pos.getX());
                        tag.putInt("house_Y", pos.getY());
                        tag.putInt("house_Z", pos.getZ());
                        bedrock_placed = true;
                     }
                  }
               }
            }
         }

         global_tag.put("flywheel_of_terror", tag);
      }
   }

   @SubscribeEvent
   public static void claim_blocks(EntityPlaceEvent event) {
      if (event.getEntity() instanceof Player player
         && !(event.getPlacedBlock().getBlock() instanceof FireBlock)
         && !(event.getPlacedBlock().getBlock() instanceof TntBlock)
         && !(event.getPlacedBlock().getBlock() instanceof DoorBlock)
         && !(event.getPlacedBlock().getBlock() instanceof FurnaceBlock)
         && !(event.getPlacedBlock().getBlock() instanceof CraftingTableBlock)
         && !(event.getPlacedBlock().getBlock() instanceof ChestBlock)
         && !(event.getPlacedBlock().getBlock() instanceof BedBlock)
         && !(event.getPlacedBlock().getBlock() instanceof TorchBlock)) {
         CompoundTag global_tag = player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         String coordinates = event.getPos().getX() + "," + event.getPos().getY() + "," + event.getPos().getZ();
         tag.putInt(coordinates + "time_to_house", 3);
         tag.putBoolean(coordinates, true);
         global_tag.put("flywheel_of_terror", tag);
      }
   }
}
