package com.example.flywheel_of_terror;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class forge_revenge {
   public static void set_active(Player player, boolean value) {
      state.putBool(player, "forge_revenge_active", value);
   }

   @SubscribeEvent
   public static void time(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide() && state.getBool(player, "forge_revenge_active")) {
         double myX = player.getX();
         double myY = player.getY();
         double myZ = player.getZ();
         BlockPos forgepos = new BlockPos((int)myX, (int)myY + 4, (int)myZ);
         BlockPos forgepos2 = new BlockPos((int)myX - 1, (int)myY + 4, (int)myZ);
         BlockPos forgepos3 = new BlockPos((int)myX, (int)myY + 4, (int)myZ - 1);
         BlockPos forgepos4 = new BlockPos((int)myX - 1, (int)myY + 4, (int)myZ - 1);
         player.level().setBlock(forgepos, Blocks.ANVIL.defaultBlockState(), 3);
         player.level().setBlock(forgepos2, Blocks.ANVIL.defaultBlockState(), 3);
         player.level().setBlock(forgepos3, Blocks.ANVIL.defaultBlockState(), 3);
         player.level().setBlock(forgepos4, Blocks.ANVIL.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX, (int)myY + 3, (int)myZ), Blocks.AIR.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX, (int)myY + 2, (int)myZ), Blocks.AIR.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX, (int)myY + 1, (int)myZ), Blocks.AIR.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX - 1, (int)myY + 3, (int)myZ), Blocks.AIR.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX - 1, (int)myY + 2, (int)myZ), Blocks.AIR.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX - 1, (int)myY + 1, (int)myZ), Blocks.AIR.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX, (int)myY + 3, (int)myZ - 1), Blocks.AIR.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX, (int)myY + 2, (int)myZ - 1), Blocks.AIR.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX, (int)myY + 1, (int)myZ - 1), Blocks.AIR.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX - 1, (int)myY + 3, (int)myZ - 1), Blocks.AIR.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX - 1, (int)myY + 2, (int)myZ - 1), Blocks.AIR.defaultBlockState(), 3);
         player.level().setBlock(new BlockPos((int)myX - 1, (int)myY + 1, (int)myZ - 1), Blocks.AIR.defaultBlockState(), 3);
         state.putBool(player, "forge_revenge_active", false);
      }
   }
}
