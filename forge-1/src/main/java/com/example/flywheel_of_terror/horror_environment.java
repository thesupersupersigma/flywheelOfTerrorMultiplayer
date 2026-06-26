package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class horror_environment {
   public static Random random = new Random();
   public static boolean sound_must_be = false;
   public static boolean sound_must_be2 = false;

   @SubscribeEvent
   public static void true_me_spawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof true_me monster) {
         ItemStack knife = new ItemStack((ItemLike)add_items.my_knife.get());
         monster.setItemInHand(InteractionHand.MAIN_HAND, knife);
      }

      if (event.getEntity() instanceof somewho monster) {
         monster.setTarget(information.just_player);
      }
   }

   @SubscribeEvent
   public static void true_me_disappear(PlayerTickEvent event) {
      Player player = event.player;
      if (player.level().isClientSide() && sound_must_be2) {
         player.playSound((SoundEvent)register_sounds.dead.get(), 1.0F, 1.0F);
         sound_must_be2 = false;
      }

      if (!player.level().isClientSide()) {
         for (true_me me : player.level().getEntitiesOfClass(true_me.class, player.getBoundingBox().inflate(2.0))) {
            me.remove(RemovalReason.DISCARDED);
            sound_must_be2 = true;
            MobEffectInstance blind = new MobEffectInstance(MobEffects.BLINDNESS, 80, 14, false, true, false);
            player.addEffect(blind);
         }

         for (headless_steve me : player.level().getEntitiesOfClass(headless_steve.class, player.getBoundingBox().inflate(2.0))) {
            me.remove(RemovalReason.DISCARDED);
            if (player.getHealth() > 2.0F) {
               player.hurt(player.damageSources().generic(), 1.0F);
            }
         }

         for (somewho mex : player.level().getEntitiesOfClass(somewho.class, player.getBoundingBox().inflate(3.0))) {
            float newxrot = mex.getXRot();
            float newyrot = mex.getYRot();
            double newx = mex.getX();
            double newy = mex.getY();
            double newz = mex.getZ();
            if (player instanceof ServerPlayer serv) {
               serv.connection.teleport(newx, newy, newz, newyrot, newxrot);
            }

            mex.remove(RemovalReason.DISCARDED);
            CompoundTag global_tag = player.getPersistentData();
            CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
            tag.putInt("somewho", tag.getInt("somewho") + 1);
            global_tag.put("flywheel_of_terror", tag);
         }
      }
   }

   @SubscribeEvent
   public static void no_red(LivingDamageEvent event) {
      event.getEntity().hurtTime = 0;
   }

   @SubscribeEvent
   public static void corrupted_mobs(EntityJoinLevelEvent event) {
      CompoundTag tag = event.getEntity().getPersistentData();
      if (!(event.getEntity() instanceof Player)
         && !(event.getEntity() instanceof somewho)
         && !(event.getEntity() instanceof faceless_cow)
         && !(event.getEntity() instanceof faceless_pig)
         && !(event.getEntity() instanceof faceless_sheep)
         && !(event.getEntity() instanceof faceless_villager)
         && !(event.getEntity() instanceof headless_steve)
         && !(event.getEntity() instanceof fake_steve)
         && !(event.getEntity() instanceof Zombie)
         && !(event.getEntity() instanceof invisible)
         && !(event.getEntity() instanceof oh_no)
         && !(event.getEntity() instanceof oh_no_stalker)
         && !(event.getEntity() instanceof oh_no_here)
         && !(event.getEntity() instanceof oh_no_behind)
         && !(event.getEntity() instanceof true_me)) {
         int bebra = random.nextInt(1, 200);
         if (bebra >= 1 && bebra < 6) {
            tag.putInt("lookatplayer", 5);
         }

         if (bebra > 6
            && bebra < 11
            && !(event.getEntity() instanceof terror_pig)
            && !(event.getEntity() instanceof terror_cow)
            && !(event.getEntity() instanceof terror_sheep)
            && !(event.getEntity() instanceof Zombie)) {
            tag.putInt("god", 5);
         }

         if (bebra == 11 && (event.getEntity() instanceof Pig || event.getEntity() instanceof Cow || event.getEntity() instanceof Sheep)) {
            tag.putInt("terror", 5);
            tag.putInt("lookatplayer", 0);
         }

         if (bebra > 11 && bebra < 17) {
            tag.putInt("coward", 5);
         }

         if (bebra > 17 && bebra < 23) {
            tag.putInt("fake", 5);
            tag.putInt("terror", 0);
         }

         if (bebra > 23 && bebra < 30) {
            tag.putInt("smart", 5);
            tag.putInt("lookatplayer", 0);
         }

         if (bebra > 30 && bebra < 37) {
            tag.putInt("wrong_reaction", 5);
         }

         if (bebra > 37 && bebra < 43) {
            tag.putInt("uroboros", 5);
         }

         if (bebra > 43 && bebra < 49) {
            tag.putInt("scarecrow", 10);
         }
      }
   }

   @SubscribeEvent
   public static void before_hurt(LivingHurtEvent event) {
      CompoundTag tag = event.getEntity().getPersistentData();
      if (tag.getInt("fake") == 5) {
         event.getEntity().remove(RemovalReason.DISCARDED);
      }

      if (tag.getInt("wrong_reaction") == 5 && event.getSource().getEntity() instanceof Player player) {
         event.setCanceled(true);
         tag.putInt("lookatplayer", 5);
         tag.putInt("tics_to_die", 80);
      }

      if (tag.getInt("uroboros") == 5 && event.getSource().getEntity() instanceof Player player) {
         event.setCanceled(true);
         double x = player.getX() - player.getLookAngle().x * 2.0;
         double z = player.getZ() - player.getLookAngle().z * 2.0;
         event.getEntity().teleportTo(x, player.getY(), z);
      }
   }

   @SubscribeEvent
   public static void mortal_mob(LivingHurtEvent event) {
      if (!(event.getEntity() instanceof Player)) {
         CompoundTag tag = event.getEntity().getPersistentData();
         if (tag.getInt("god") == 5) {
            event.setCanceled(true);
            event.getEntity().heal(40.0F);
         }

         if (tag.getInt("terror") == 5 && event.getSource().getEntity() instanceof Player player) {
            MobEffectInstance blind = new MobEffectInstance(MobEffects.BLINDNESS, 140, 10, false, true, false);
            player.addEffect(blind);
            if (tag.getInt("lookatplayer") != 5) {
               sound_must_be = true;
               player.hurt(player.damageSources().generic(), random.nextFloat(2.0F, 8.0F));
               double mobx = event.getEntity().getX();
               double moby = event.getEntity().getY();
               double mobz = event.getEntity().getZ();
               float mobxrotate = event.getEntity().getXRot();
               float mobyrotate = event.getEntity().getYRot();
               event.getEntity().remove(RemovalReason.DISCARDED);
               if (event.getEntity() instanceof Pig) {
                  terror_pig newpig = new terror_pig((EntityType<? extends Pig>)add_humans.terror_pig.get(), player.level());
                  CompoundTag tag2 = newpig.getPersistentData();
                  tag2.putInt("lookatplayer", 5);
                  tag2.putInt("terror", 5);
                  tag2.putInt("timetodelete", 7);
                  Vec3 pos = new Vec3(mobx, moby, mobz);
                  newpig.setPos(pos);
                  newpig.setXRot(mobxrotate);
                  newpig.setYRot(mobyrotate);
                  player.level().addFreshEntity(newpig);
                  newpig.hurt(event.getSource(), 1.0F);
               }

               if (event.getEntity() instanceof Cow && !(event.getEntity() instanceof wrong_cow)) {
                  terror_cow newcow = new terror_cow((EntityType<? extends Cow>)add_humans.terror_cow.get(), player.level());
                  CompoundTag tag2 = newcow.getPersistentData();
                  tag2.putInt("lookatplayer", 5);
                  tag2.putInt("terror", 5);
                  tag2.putInt("timetodelete", 7);
                  Vec3 pos = new Vec3(mobx, moby, mobz);
                  newcow.setPos(pos);
                  newcow.setXRot(mobxrotate);
                  newcow.setYRot(mobyrotate);
                  player.level().addFreshEntity(newcow);
                  newcow.hurt(event.getSource(), 1.0F);
               }

               if (event.getEntity() instanceof Sheep && !(event.getEntity() instanceof wrong_sheep)) {
                  terror_sheep newsheep = new terror_sheep((EntityType<? extends Sheep>)add_humans.terror_sheep.get(), player.level());
                  CompoundTag tag2 = newsheep.getPersistentData();
                  tag2.putInt("lookatplayer", 5);
                  tag2.putInt("terror", 5);
                  tag2.putInt("timetodelete", 7);
                  Vec3 pos = new Vec3(mobx, moby, mobz);
                  newsheep.setPos(pos);
                  newsheep.setXRot(mobxrotate);
                  newsheep.setYRot(mobyrotate);
                  player.level().addFreshEntity(newsheep);
                  newsheep.hurt(event.getSource(), 1.0F);
                  newsheep.shear(SoundSource.PLAYERS);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void do_terror(PlayerTickEvent event) {
      Player player = event.player;
      CompoundTag global_tag2 = player.getPersistentData();
      CompoundTag tag2 = global_tag2.getCompound("flywheel_of_terror");
      if (sound_must_be && player.level().isClientSide) {
         player.playSound((SoundEvent)register_sounds.break_mob.get(), 1.0F, 1.0F);
         sound_must_be = false;
      }

      if (!player.level().isClientSide()) {
         for (Entity mob : player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(300.0))) {
            CompoundTag tag = mob.getPersistentData();
            if ((tag.getInt("lookatplayer") == 5 || terror_beginning.first_message_was || tag.getBoolean("family"))
               && !(mob instanceof Player)
               && all_look_at_you.tics_of_looking <= 0) {
               mob.lookAt(Anchor.EYES, player.getEyePosition());
            }

            if (random.nextInt(1, 1000) == 85 && player.tickCount % 40 == 0 && !(mob instanceof Player)) {
               mob.kill();
            }

            tag.putInt("tics_to_die", tag.getInt("tics_to_die") - 1);
            if (tag.getInt("tics_to_die") == 1) {
               mob.kill();
            }
         }

         if (player.tickCount % 40 == 0) {
            for (Animal mob : player.level().getEntitiesOfClass(Animal.class, player.getBoundingBox().inflate(30.0))) {
               CompoundTag tagx = mob.getPersistentData();
               if (mob instanceof terror_cow || mob instanceof terror_pig || mob instanceof terror_sheep) {
                  tagx.putInt("timetodelete", tagx.getInt("timetodelete") - 1);
                  if (tagx.getInt("timetodelete") <= 0) {
                     mob.remove(RemovalReason.DISCARDED);
                  }
               }

               if (tagx.getInt("smart") == 5 && information.just_player != null) {
                  mob.getNavigation().moveTo(information.just_player, 1.0);
               }
            }

            for (Animal mob : player.level().getEntitiesOfClass(Animal.class, player.getBoundingBox().inflate(8.0))) {
               CompoundTag tagxx = mob.getPersistentData();
               if (tagxx.getInt("coward") == 5) {
                  mob.setLastHurtByMob(information.livingingrok);
               }
            }

            for (Animal mobx : player.level().getEntitiesOfClass(Animal.class, player.getBoundingBox().inflate(1.0))) {
               CompoundTag tagxx = mobx.getPersistentData();
               if (tagxx.getInt("smart") == 5) {
                  tagxx.putInt("smart", 0);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void wrong_die(LivingDeathEvent event) {
      if (!(event.getEntity() instanceof Player) && random.nextInt(1, 50) == 20) {
         CompoundTag tag = event.getEntity().getPersistentData();
         tag.putInt("scarecrow", 5);
      }
   }
}
