package com.example.flywheel_of_terror;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class health_decrease {
   public static float max_hp = 20.0F;
   public static int count_of_kills = 0;

   @SubscribeEvent
   public static void whenentry(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      if (tag.getFloat("maxhp") == 0.0F) {
         tag.putFloat("maxhp", 20.0F);
         tag.putInt("kills", 0);
      }

      max_hp = tag.getFloat("maxhp");
      count_of_kills = tag.getInt("kills");
      event.getEntity().getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)max_hp);
   }

   @SubscribeEvent
   public static void saveall(PlayerTickEvent event) {
      Player player = event.player;
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putInt("kills", count_of_kills);
      tag.putFloat("maxhp", max_hp);
      global_tag.put("flywheel_of_terror", tag);
      MobEffectInstance strenght1 = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1, 0, false, true, false);
      MobEffectInstance strenght2 = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1, 1, false, true, false);
      MobEffectInstance strenght3 = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1, 2, false, true, false);
      MobEffectInstance regen2 = new MobEffectInstance(MobEffects.REGENERATION, 1, 1, false, true, false);
      MobEffectInstance regen3 = new MobEffectInstance(MobEffects.REGENERATION, 1, 2, false, true, false);
      MobEffectInstance speed1 = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1, 0, false, true, false);
      MobEffectInstance speed2 = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1, 1, false, true, false);
      MobEffectInstance speed3 = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1, 2, false, true, false);
      MobEffectInstance firedecline = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1, 3, false, true, false);
      MobEffectInstance falldecline = new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 1, false, true, false);
      if (max_hp <= 18.0F && max_hp != 12.0F && max_hp != 4.0F) {
         player.addEffect(strenght1);
      }

      if (max_hp <= 16.0F && max_hp != 10.0F && max_hp != 6.0F) {
         player.addEffect(speed1);
      }

      if (max_hp <= 14.0F && max_hp != 8.0F) {
         player.addEffect(regen2);
      }

      if (max_hp <= 12.0F && max_hp != 4.0F) {
         player.addEffect(strenght2);
      }

      if (max_hp <= 10.0F && max_hp != 6.0F) {
         player.addEffect(speed2);
      }

      if (max_hp <= 8.0F && max_hp != 2.0F) {
         player.addEffect(regen3);
      }

      if (max_hp <= 6.0F) {
         player.addEffect(speed3);
      }

      if (max_hp <= 4.0F) {
         player.addEffect(strenght3);
      }

      if (max_hp <= 2.0F) {
         player.addEffect(firedecline);
         player.addEffect(falldecline);
      }
   }

   @SubscribeEvent
   public static void setnewhp(PlayerRespawnEvent event) {
      event.getEntity().getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)max_hp);
   }

   @SubscribeEvent
   public static void whendie(LivingDeathEvent event) {
      CompoundTag global_tag = event.getEntity().getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      if (event.getEntity() instanceof Player player && max_hp >= 4.0F && tag.getInt("state_of_lore") == 0) {
         max_hp -= 2.0F;
      }
   }

   @SubscribeEvent
   public static void health_increase(LivingDeathEvent event) {
      if (event.getSource().getEntity() instanceof Player player) {
         CompoundTag global_tag = player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         if (player.getMaxHealth() < 20.0F) {
            count_of_kills++;
         }

         if ((float)count_of_kills >= 42.0F - (20.0F - max_hp) * 2.0F && player.getMaxHealth() <= 18.0F) {
            count_of_kills = 0;
            max_hp += 2.0F;
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)max_hp);
         }
      }
   }
}
