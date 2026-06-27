package com.example.flywheel_of_terror;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class panic {
   // event_in_process / event_in_process2 → per-player NBT; tics_of_black + sound flags stay static
   // (client blackout shader / sounds, Phase 3).
   public static int tics_of_black = -2;
   public static boolean sound_must_be = false;
   public static boolean sound_must_be2 = false;
   public static Random random = new Random();

   public static void set_active(Player player, boolean value) {
      state.putBool(player, "panic_active", value);
   }

   @SubscribeEvent
   public static void moveside(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof Player player) {
         CompoundTag global_tag = player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         if (event.getEntity() instanceof faceless_villager vill) {
            double xx = player.getLookAngle().x;
            double zz = player.getLookAngle().z;
            faceless_villager vill2 = new faceless_villager((EntityType<? extends Villager>)add_humans.faceless_villager.get(), player.level());
            vill2.setPos(new Vec3(player.getX() - 1.5 * xx, player.getY(), player.getZ() - 1.5 * zz));
            vill2.hits_to_remove = vill.hits_to_remove - 1;
            player.level().addFreshEntity(vill2);
            vill.remove(RemovalReason.DISCARDED);
            if (vill2.hits_to_remove > 0) {
               sound_must_be = true;
            } else {
               tag.putInt("panic", tag.getInt("panic") + 1);
               global_tag.put("flywheel_of_terror", tag);
               sound_must_be2 = true;
               tics_of_black = 200;
               state.putBool(player, "panic_active2", true);
            }
         }
      }
   }

   @EventBusSubscriber(value = {Dist.CLIENT})
   public static class client_events {
      @SubscribeEvent
      public static void night(RenderLevelStageEvent event) {
         if (tics_of_black > 0) {
            RenderSystem.setShaderColor(0.01F, 0.01F, 0.0F, 1.0F);
         }
      }
   }

   @SubscribeEvent
   public static void every_time(PlayerTickEvent event) {
      Player player = event.player;
      if (player.level().isClientSide()) {
         if (sound_must_be) {
            player.playSound((SoundEvent)register_sounds.teleport.get(), 1.0F, 1.0F);
            sound_must_be = false;
         }

         if (sound_must_be2) {
            player.playSound((SoundEvent)register_sounds.nightmare.get(), 1.0F, 1.0F);
            sound_must_be2 = false;
         }
      }

      if (!player.level().isClientSide()) {
         tics_of_black--;
         if (state.getBool(player, "panic_active")) {
            double xx = player.getLookAngle().x;
            double zz = player.getLookAngle().z;
            double yy = (double)player.level().getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)xx, (int)zz);
            faceless_villager vill2 = new faceless_villager((EntityType<? extends Villager>)add_humans.faceless_villager.get(), player.level());
            Vec3 pos = new Vec3(player.getX() + 15.0 * xx, yy, player.getZ() + 15.0 * zz);
            vill2.setPos(pos);

            for (double xxx = pos.x() - 1.0; xxx <= pos.x + 1.0; xxx++) {
               for (double yyy = pos.y; yyy <= pos.y + 2.0; yyy++) {
                  for (double zzz = pos.z - 1.0; zzz <= pos.z + 1.0; zzz++) {
                     BlockPos area = new BlockPos((int)xxx, (int)yyy, (int)zzz);
                     player.level().destroyBlock(area, true, player);
                  }
               }
            }

            player.level().addFreshEntity(vill2);
            state.putBool(player, "panic_active", false);
         }

         if (state.getBool(player, "panic_active2")) {
            state.putBool(player, "panic_active2", false);
            MobEffectInstance blind = new MobEffectInstance(MobEffects.BLINDNESS, 100, 2, false, true, false);
            MobEffectInstance slow = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 22, false, true, false);
            player.addEffect(blind);
            player.addEffect(slow);

            for (int i = 0; i < 30; i++) {
               int ii = random.nextInt(1, 4);
               Animal current_mob = new faceless_cow((EntityType<? extends Cow>)add_humans.faceless_cow.get(), player.level());
               switch (ii) {
                  case 1:
                     current_mob = new faceless_pig((EntityType<? extends Pig>)add_humans.faceless_pig.get(), player.level());
                     break;
                  case 2:
                     current_mob = new faceless_cow((EntityType<? extends Cow>)add_humans.faceless_cow.get(), player.level());
                     break;
                  case 3:
                     current_mob = new faceless_sheep((EntityType<? extends Sheep>)add_humans.faceless_sheep.get(), player.level());
               }

               int iii = random.nextInt(-10, 10);
               int iii2 = random.nextInt(-10, 10);
               current_mob.setPos(player.getX() + (double)iii, player.getY(), player.getZ() + (double)iii2);
               int y = player.level().getHeight(Types.MOTION_BLOCKING, (int)current_mob.getX(), (int)current_mob.getZ());
               current_mob.setPos(player.getX() + (double)iii, (double)(y + 1), player.getZ() + (double)iii2);
               player.level().addFreshEntity(current_mob);
            }
         }
      }
   }
}
