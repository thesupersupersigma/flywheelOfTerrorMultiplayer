package com.example.flywheel_of_terror;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class remove_entities {
   public static int tics_without_life = -3;

   @SubscribeEvent
   public static void removing(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof LivingEntity
         && !(event.getEntity() instanceof Player)
         && !(event.getEntity() instanceof oh_no)
         && tics_without_life > 0
         && !(event.getEntity() instanceof Villager)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void remove_food(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide()) {
         tics_without_life--;
         if (tics_without_life > 0 && !terror_beginning.first_message_was || terror_beginning.his_hunt) {
            for (LivingEntity bebra : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(100.0))) {
               if (!(bebra instanceof Player) && !(bebra instanceof oh_no) && !(bebra instanceof Villager)) {
                  bebra.remove(RemovalReason.DISCARDED);
               }
            }

            if (player.getFoodData().getFoodLevel() < 8) {
               player.getFoodData().setFoodLevel(8);
            }
         }
      }
   }
}
