package com.example.flywheel_of_terror;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AirBlock;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class decline_run {
   // tick_to_knock → per-player NBT ("decline_knock"); the punishment sound is now an S2C packet.
   @SubscribeEvent
   public static void check_build_up(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide()) {
         int tick_to_knock = state.getInt(player, "decline_knock") - 1;
         state.putInt(player, "decline_knock", tick_to_knock);
         boolean no_any_blocks = true;
         CompoundTag global_tag = player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         int count_of_blocks = 0;

         for (double x = player.getX() - 3.0; x <= player.getX() + 3.0; x++) {
            for (double y = player.getY() - 5.0; y <= player.getY() + 3.0; y++) {
               for (double z = player.getZ() - 3.0; z <= player.getZ() + 3.0; z++) {
                  BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                  String coordinates = (int)x + "," + (int)y + "," + (int)z;
                  if (tag.getBoolean(coordinates)) {
                     count_of_blocks++;
                  } else if (!(player.level().getBlockState(pos).getBlock() instanceof AirBlock)) {
                     no_any_blocks = false;
                  }
               }
            }
         }

         if (count_of_blocks == 5
            && tick_to_knock <= 0
            && player.onGround()
            && no_any_blocks
            && !terror_continue.near_maze(player)
            && player.getY() > 50.0
            && !advanced_baron_detector.player_is_baron(player)) {
            player.teleportTo(player.getX() + 2.0, player.getY(), player.getZ());
            state.putInt(player, "decline_knock", 200);
            Network.sound(player, (SoundEvent)register_sounds.break_christ.get());
            player.sendSystemMessage(Component.literal("Are you going somewhere?"));
            player.hurt(player.damageSources().generic(), 1.0F);
         }
      }
   }
}
