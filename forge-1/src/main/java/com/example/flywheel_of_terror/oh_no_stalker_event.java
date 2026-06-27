package com.example.flywheel_of_terror;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class oh_no_stalker_event {
   public static void do_event(Player player) {
      oh_no_stalker oh_no = new oh_no_stalker((EntityType<? extends PathfinderMob>)add_humans.oh_no_stalker.get(), player.level());
      double x = (double)get_nearest_tree(player).getX();
      double y = (double)get_nearest_tree(player).getY();
      double z = (double)get_nearest_tree(player).getZ();
      oh_no.setPos(x, y, z);
      player.level().addFreshEntity(oh_no);
   }

   public static BlockPos get_nearest_tree(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");

      for (double z = player.getZ() - 15.0; z <= player.getZ() + 15.0; z++) {
         for (double x = player.getX() - 15.0; x <= player.getX() + 15.0; x++) {
            for (double y = player.getY() - 5.0; y <= player.getY() + 5.0; y++) {
               BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
               BlockPos pos2 = new BlockPos((int)x, (int)y - 1, (int)z);
               Block current_block = player.level().getBlockState(pos).getBlock();
               String coordinates = (int)x + "," + (int)y + "," + (int)z;
               if (!tag.getBoolean(coordinates + "house") && is_tree(current_block) && player.level().getBlockState(pos2).getBlock() != Blocks.AIR) {
                  return pos;
               }
            }
         }
      }

      return new BlockPos(0, 0, 0);
   }

   public static boolean is_tree(Block block) {
      return block == Blocks.OAK_LOG
         || block == Blocks.DARK_OAK_LOG
         || block == Blocks.SPRUCE_LOG
         || block == Blocks.BIRCH_LOG
         || block == Blocks.MANGROVE_LOG
         || block == Blocks.JUNGLE_LOG
         || block == Blocks.CHERRY_LOG
         || block == Blocks.ACACIA_LOG;
   }

}
