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
   // Per-player "no life" window → NBT ("tics_without_life"). The spawn-cancel event has no player,
   // so it cancels when any nearby player's window is active.
   public static void set_tics_without_life(Player player, int value) {
      state.putInt(player, "tics_without_life", value);
   }

   @SubscribeEvent
   public static void removing(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof LivingEntity
         && !(event.getEntity() instanceof Player)
         && !(event.getEntity() instanceof oh_no)
         && !(event.getEntity() instanceof Villager)) {
         for (Player player : event.getLevel().getEntitiesOfClass(Player.class, event.getEntity().getBoundingBox().inflate(100.0))) {
            if (state.getInt(player, "tics_without_life") > 0) {
               event.setCanceled(true);
               return;
            }
         }
      }
   }

   @SubscribeEvent
   public static void remove_food(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide()) {
         int tics_without_life = state.getInt(player, "tics_without_life") - 1;
         state.putInt(player, "tics_without_life", tics_without_life);
         if (tics_without_life > 0 && !terror_beginning.first_message_was(player) || terror_beginning.his_hunt(player)) {
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
