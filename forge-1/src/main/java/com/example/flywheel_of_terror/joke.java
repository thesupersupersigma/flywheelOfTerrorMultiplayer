package com.example.flywheel_of_terror;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class joke {
   @SubscribeEvent
   public static void ticker(PlayerTickEvent event) {
      Player player = event.player;
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      if (!player.level().isClientSide()) {
         for (IronGolem holem : player.level().getEntitiesOfClass(IronGolem.class, player.getBoundingBox().inflate(3.0))) {
            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();
            String coordinates1 = (int)x + "," + (int)(y - 1.0) + "," + (int)z;
            String coordinates2 = (int)x + "," + (int)(y - 2.0) + "," + (int)z;
            String coordinates3 = (int)x + "," + (int)(y - 3.0) + "," + (int)z;
            if (tag.getBoolean(coordinates1) && tag.getBoolean(coordinates2) && tag.getBoolean(coordinates3)) {
               BlockPos pos1 = new BlockPos((int)x, (int)(y - 1.0), (int)z);
               BlockPos pos2 = new BlockPos((int)x, (int)(y - 2.0), (int)z);
               BlockPos pos3 = new BlockPos((int)x, (int)(y - 3.0), (int)z);
               player.level().setBlock(pos1, Blocks.AIR.defaultBlockState(), 3);
               player.level().setBlock(pos2, Blocks.AIR.defaultBlockState(), 3);
               player.level().setBlock(pos3, Blocks.AIR.defaultBlockState(), 3);
            }
         }
      }
   }
}
