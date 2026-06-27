package com.example.flywheel_of_terror;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class periferia {
   public static boolean event_in_process = false;
   public static int tics_to_spawn = 40;

   @SubscribeEvent
   public static void every_time(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide()) {
         if (event_in_process && information.inventory_open) {
            tics_to_spawn--;
         }

         if (tics_to_spawn <= 0) {
            System.out.println("spawn must be");
            double x = player.getLookAngle().x;
            double z = player.getLookAngle().z;
            fake_steve me = new fake_steve((EntityType<? extends PathfinderMob>)add_humans.fake_steve.get(), player.level());
            Vec3 pos = new Vec3(player.getX() + x * 0.3, player.getY(), player.getZ() + z * 0.3);
            me.setPos(pos);
            player.level().addFreshEntity(me);
            tics_to_spawn = 40;
            event_in_process = false;
         }
      }
   }
}
