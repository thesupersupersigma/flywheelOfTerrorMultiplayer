package com.example.flywheel_of_terror;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class circle_christ {
   // event_in_process → per-player NBT ("circle_christ_active"); sound flags stay static (Phase 3).
   public static boolean sound_must_be = false;
   public static boolean sound_must_be2 = false;

   public static void set_active(Player player, boolean value) {
      state.putBool(player, "circle_christ_active", value);
   }

   @SubscribeEvent
   public static void nakaz(BreakEvent event) {
      Player player = event.getPlayer();
      int fixedX = (int)event.getPlayer().getX();
      int fixedY = (int)event.getPlayer().getY();
      int fixedZ = (int)event.getPlayer().getZ();
      if (event.getState().getBlock().defaultBlockState() == Blocks.NETHERITE_BLOCK.defaultBlockState()) {
         event.setCanceled(true);
         MobEffectInstance blind = new MobEffectInstance(MobEffects.BLINDNESS, 100, 10, false, true, false);
         player.addEffect(blind);
         sound_must_be2 = true;

         for (double x = (double)(fixedX - 15); x <= (double)(fixedX + 15); x++) {
            for (double y = (double)(fixedY - 10); y <= (double)(fixedY + 10); y++) {
               for (double z = (double)(fixedZ - 15); z <= (double)(fixedZ + 15); z++) {
                  BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                  if (player.level().getBlockState(pos).getBlock() == Blocks.NETHERITE_BLOCK.defaultBlockState().getBlock()) {
                     player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void everytime(PlayerTickEvent event) {
      Player player = event.player;
      if (sound_must_be2 && player.level().isClientSide()) {
         player.playSound((SoundEvent)register_sounds.break_christ.get(), 1.0F, 1.0F);
         sound_must_be2 = false;
      }

      if (state.getBool(player, "circle_christ_active") && !player.level().isClientSide() && !terror_continue.near_maze(player)) {
         int fixedX = (int)player.getX();
         int fixedY = (int)player.getY();
         int fixedZ = (int)player.getZ();

         for (double x = (double)(fixedX - 5); x <= (double)(fixedX + 5); x++) {
            for (double y = (double)fixedY; y <= (double)(fixedY + 6); y++) {
               for (double z = (double)(fixedZ - 5); z <= (double)(fixedZ + 5); z++) {
                  Level level = player.level();
                  BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                  level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
               }
            }
         }

         BlockPos pos1 = new BlockPos(fixedX + 5, fixedY, fixedZ);
         BlockPos pos2 = new BlockPos(fixedX + 5, fixedY + 1, fixedZ);
         BlockPos pos3 = new BlockPos(fixedX + 5, fixedY + 2, fixedZ);
         BlockPos pos4 = new BlockPos(fixedX + 5, fixedY + 3, fixedZ);
         BlockPos pos5 = new BlockPos(fixedX + 5, fixedY + 2, fixedZ + 1);
         BlockPos pos6 = new BlockPos(fixedX + 5, fixedY + 2, fixedZ - 1);
         BlockPos pos7 = new BlockPos(fixedX - 5, fixedY, fixedZ);
         BlockPos pos8 = new BlockPos(fixedX - 5, fixedY + 1, fixedZ);
         BlockPos pos9 = new BlockPos(fixedX - 5, fixedY + 2, fixedZ);
         BlockPos pos10 = new BlockPos(fixedX - 5, fixedY + 3, fixedZ);
         BlockPos pos11 = new BlockPos(fixedX - 5, fixedY + 2, fixedZ + 1);
         BlockPos pos12 = new BlockPos(fixedX - 5, fixedY + 2, fixedZ - 1);
         BlockPos pos13 = new BlockPos(fixedX, fixedY, fixedZ + 5);
         BlockPos pos14 = new BlockPos(fixedX, fixedY + 1, fixedZ + 5);
         BlockPos pos15 = new BlockPos(fixedX, fixedY + 2, fixedZ + 5);
         BlockPos pos16 = new BlockPos(fixedX, fixedY + 3, fixedZ + 5);
         BlockPos pos17 = new BlockPos(fixedX + 1, fixedY + 2, fixedZ + 5);
         BlockPos pos18 = new BlockPos(fixedX - 1, fixedY + 2, fixedZ + 5);
         BlockPos pos19 = new BlockPos(fixedX, fixedY, fixedZ - 5);
         BlockPos pos20 = new BlockPos(fixedX, fixedY + 1, fixedZ - 5);
         BlockPos pos21 = new BlockPos(fixedX, fixedY + 2, fixedZ - 5);
         BlockPos pos22 = new BlockPos(fixedX, fixedY + 3, fixedZ - 5);
         BlockPos pos23 = new BlockPos(fixedX + 1, fixedY + 2, fixedZ - 5);
         BlockPos pos24 = new BlockPos(fixedX - 1, fixedY + 2, fixedZ - 5);
         player.level().setBlock(pos1, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos2, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos3, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos4, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos5, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos6, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos7, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos8, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos9, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos10, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos11, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos12, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos13, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos14, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos15, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos16, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos17, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos18, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos19, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos20, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos21, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos22, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos23, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         player.level().setBlock(pos24, Blocks.NETHERITE_BLOCK.defaultBlockState(), 3);
         state.putBool(player, "circle_christ_active", false);
         sound_must_be = true;
      }

      if (sound_must_be && player.level().isClientSide()) {
         player.playSound((SoundEvent)register_sounds.christ_spawn.get(), 1.0F, 1.0F);
         sound_must_be = false;
      }
   }
}
