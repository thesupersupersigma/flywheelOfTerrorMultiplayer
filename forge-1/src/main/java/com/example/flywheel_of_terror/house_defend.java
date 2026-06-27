package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class house_defend {
   public static Random random = new Random();
   // Tuning constants (never mutated) — fine to remain static.
   public static int blocks_repair_per_time = 1;
   public static int required_to_thunder = 8;

   // Per-player bed claim, read by flywheel_of_terror.hell().
   public static boolean bed_here(Player player) {
      return state.getBool(player, "bed_here");
   }

   public static BlockPos bed_pos(Player player) {
      CompoundTag tag = state.tag(player);
      return new BlockPos(tag.getInt("bed_x"), tag.getInt("bed_y"), tag.getInt("bed_z"));
   }

   @SubscribeEvent
   public static void breakb(BreakEvent event) {
      Player player = event.getPlayer();
      if (!player.level().isClientSide()) {
         CompoundTag global_tag = player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         String coordinates = event.getPos().getX() + "," + event.getPos().getY() + "," + event.getPos().getZ();
         if (tag.getBoolean(coordinates + "house") || tag.getBoolean(coordinates + "destroyed_house")) {
            tag.putFloat("breaked_for_last_seconds", tag.getFloat("breaked_for_last_seconds") + 1.0F);
            global_tag.put("flywheel_of_terror", tag);
         }
      }
   }

   @SubscribeEvent
   public static void defend(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide()) {
         CompoundTag global_tag = event.player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         float breaked_for_last_seconds = tag.getFloat("breaked_for_last_seconds");
         if (player.tickCount % 200 == 0 & breaked_for_last_seconds > 0.0F) {
            breaked_for_last_seconds--;
         }

         if (breaked_for_last_seconds >= (float)required_to_thunder) {
            breaked_for_last_seconds -= 3.0F;
            information.thunder_player(player);
         }

         tag.putFloat("breaked_for_last_seconds", breaked_for_last_seconds);
         global_tag.put("flywheel_of_terror", tag);

         if (player.tickCount % 200 == 0 && tag.getBoolean("builded")) {
            boolean fire_finded = false;
            int house_x = tag.getInt("house_X");
            int house_y = tag.getInt("house_Y");
            int house_z = tag.getInt("house_Z");
            int count_of_destroyed_house_blocks = 0;
            boolean house_is_gone = true;
            boolean bed_finded = false;

            for (double x = (double)(house_x - 20); x <= (double)(house_x + 20); x++) {
               for (double y = (double)(house_y - 10); y <= (double)(house_y + 10); y++) {
                  for (double z = (double)(house_z - 20); z <= (double)(house_z + 20); z++) {
                     String coordinates = (int)x + "," + (int)y + "," + (int)z;
                     BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                     if (tag.getBoolean(coordinates)) {
                        tag.putInt(coordinates + "time_to_house", tag.getInt(coordinates + "time_to_house") - 1);
                        global_tag.put("flywheel_of_terror", tag);
                     }

                     if (tag.getBoolean(coordinates + "house")
                        && player.level().getBlockState(pos).getBlock() instanceof AirBlock
                        && tag.getInt(coordinates + "time_to_house") <= 0) {
                        tag.putBoolean(coordinates + "house", false);
                        tag.putBoolean(coordinates + "destroyed_house", true);
                        global_tag.put("flywheel_of_terror", tag);
                     }

                     if (tag.getBoolean(coordinates + "destroyed_house")) {
                        count_of_destroyed_house_blocks++;
                     }

                     if (tag.getBoolean(coordinates + "destroyed_house")
                        && !(player.level().getBlockState(pos).getBlock() instanceof AirBlock)
                        && !(player.level().getBlockState(pos).getBlock() instanceof FireBlock)) {
                        tag.putBoolean(coordinates + "destroyed_house", false);
                        global_tag.put("flywheel_of_terror", tag);
                     }

                     if (player.level() instanceof ServerLevel serv && player.level().getBlockState(pos).getBlock() instanceof FireBlock && !fire_finded) {
                        serv.setWeatherParameters(0, 200, true, false);
                        fire_finded = true;
                     }

                     if (tag.getBoolean(coordinates + "house")) {
                        house_is_gone = false;
                     }

                     if (player.level().getBlockState(pos).getBlock() instanceof BedBlock) {
                        bed_finded = true;
                        tag.putInt("bed_x", (int)x);
                        tag.putInt("bed_y", (int)y);
                        tag.putInt("bed_z", (int)z);
                     }
                  }
               }
            }

            tag.putBoolean("bed_here", bed_finded);
            if (house_is_gone) {
               tag.putBoolean("builded", false);
            }

            global_tag.put("flywheel_of_terror", tag);

            if (count_of_destroyed_house_blocks == 0) {
               return;
            }

            int block_repaired = 0;

            for (double x = (double)(house_x - 20); x <= (double)(house_x + 20); x++) {
               for (double y = (double)(house_y - 10); y <= (double)(house_y + 10); y++) {
                  for (double z = (double)(house_z - 20); z <= (double)(house_z + 20); z++) {
                     String coordinatesx = (int)x + "," + (int)y + "," + (int)z;
                     BlockPos posx = new BlockPos((int)x, (int)y, (int)z);
                     if (tag.getBoolean(coordinatesx + "house")) {
                        BlockState current_block = player.level().getBlockState(posx);
                        if (current_block.getBlock() != Blocks.OAK_PLANKS
                           && current_block.getBlock() != Blocks.DARK_OAK_PLANKS
                           && current_block.getBlock() != Blocks.BIRCH_PLANKS
                           && current_block.getBlock() != Blocks.SPRUCE_PLANKS
                           && current_block.getBlock() != Blocks.MANGROVE_PLANKS
                           && current_block.getBlock() != Blocks.CHERRY_PLANKS
                           && current_block.getBlock() != Blocks.BAMBOO_PLANKS
                           && current_block.getBlock() != Blocks.ACACIA_PLANKS
                           && current_block.getBlock() != Blocks.JUNGLE_PLANKS
                           && current_block.getBlock() != Blocks.CRIMSON_PLANKS
                           && current_block.getBlock() != Blocks.WARPED_PLANKS) {
                           current_block = Blocks.COBBLESTONE.defaultBlockState();
                        }

                        String coordinates1 = (int)x + "," + (int)(y + 1.0) + "," + (int)z;
                        String coordinates2 = (int)x + "," + (int)(y - 1.0) + "," + (int)z;
                        String coordinates3 = (int)(x + 1.0) + "," + (int)y + "," + (int)z;
                        String coordinates4 = (int)(x - 1.0) + "," + (int)y + "," + (int)z;
                        String coordinates5 = (int)x + "," + (int)y + "," + (int)(z + 1.0);
                        String coordinates6 = (int)x + "," + (int)y + "," + (int)(z - 1.0);
                        BlockPos pos1 = new BlockPos((int)x, (int)y + 1, (int)z);
                        BlockPos pos2 = new BlockPos((int)x, (int)y - 1, (int)z);
                        BlockPos pos3 = new BlockPos((int)x + 1, (int)y, (int)z);
                        BlockPos pos4 = new BlockPos((int)x - 1, (int)y, (int)z);
                        BlockPos pos5 = new BlockPos((int)x, (int)y, (int)z + 1);
                        BlockPos pos6 = new BlockPos((int)x, (int)y, (int)z - 1);
                        if (tag.getBoolean(coordinates1 + "destroyed_house") && block_repaired < blocks_repair_per_time) {
                           tag.putBoolean(coordinates1 + "house", true);
                           player.level().setBlock(pos1, current_block, 3);
                           tag.putBoolean(coordinates1 + "destroyed_house", false);
                           block_repaired++;
                        }

                        if (tag.getBoolean(coordinates2 + "destroyed_house") && block_repaired < blocks_repair_per_time) {
                           tag.putBoolean(coordinates2 + "house", true);
                           player.level().setBlock(pos2, current_block, 3);
                           tag.putBoolean(coordinates2 + "destroyed_house", false);
                           block_repaired++;
                        }

                        if (tag.getBoolean(coordinates3 + "destroyed_house") && block_repaired < blocks_repair_per_time) {
                           tag.putBoolean(coordinates3 + "house", true);
                           player.level().setBlock(pos3, current_block, 3);
                           tag.putBoolean(coordinates3 + "destroyed_house", false);
                           block_repaired++;
                        }

                        if (tag.getBoolean(coordinates4 + "destroyed_house") && block_repaired < blocks_repair_per_time) {
                           tag.putBoolean(coordinates4 + "house", true);
                           player.level().setBlock(pos4, current_block, 3);
                           tag.putBoolean(coordinates4 + "destroyed_house", false);
                           block_repaired++;
                        }

                        if (tag.getBoolean(coordinates5 + "destroyed_house") && block_repaired < blocks_repair_per_time) {
                           tag.putBoolean(coordinates5 + "house", true);
                           player.level().setBlock(pos5, current_block, 3);
                           tag.putBoolean(coordinates5 + "destroyed_house", false);
                           block_repaired++;
                        }

                        if (tag.getBoolean(coordinates6 + "destroyed_house") && block_repaired < blocks_repair_per_time) {
                           tag.putBoolean(coordinates6 + "house", true);
                           player.level().setBlock(pos6, current_block, 3);
                           tag.putBoolean(coordinates6 + "destroyed_house", false);
                           block_repaired++;
                        }

                        global_tag.put("flywheel_of_terror", tag);
                     }
                  }
               }
            }

            if (block_repaired != 0) {
               player.level()
                  .playSound(
                     null, player.getX(), player.getY(), player.getZ(), (SoundEvent)register_sounds.repair.get(), SoundSource.BLOCKS, 1.0F, 1.0F
                  );
            }
         }
      }
   }
}
