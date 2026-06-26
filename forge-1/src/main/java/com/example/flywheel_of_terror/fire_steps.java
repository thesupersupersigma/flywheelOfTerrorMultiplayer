package com.example.flywheel_of_terror;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class fire_steps {
   public static int tics_of_event = 0;

   @SubscribeEvent
   public static void spawn_fire(PlayerTickEvent event) {
      Player player = event.player;
      if (player.level().isClientSide()) {
         tics_of_event--;
      }

      if (tics_of_event > 0 && player.onGround()) {
         double x = player.getLookAngle().x;
         double z = player.getLookAngle().z;
         BlockPos pos = new BlockPos((int)(player.getX() - 2.0 * x), (int)player.getY(), (int)(player.getZ() - 2.0 * z));
         player.level().setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
         MobEffectInstance resist = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 3, false, true, false);
         player.addEffect(resist);
      }
   }
}
