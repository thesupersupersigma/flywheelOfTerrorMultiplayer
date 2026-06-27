package com.example.flywheel_of_terror;

import com.example.flywheel_of_terror.client.client_safe;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class independence {
   // Per-player "possession hunt": timer + target UUID → NBT ("indep_tics" / "indep_target").
   public static Random random = new Random();

   public static void start(Player player, int tics) {
      LivingEntity mob = get_nearest_living_entity(player, 20.0F);
      if (mob != null) {
         state.putInt(player, "indep_tics", tics);
         state.putString(player, "indep_target", mob.getUUID().toString());
      }
   }

   public static boolean player_on_surface(Player player) {
      int yy = player.level().getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)player.getX(), (int)player.getZ());
      return (double)yy == player.getY() && player.onGround();
   }

   @Nullable
   public static LivingEntity get_nearest_living_entity(Player player, float radius) {
      for (LivingEntity mob : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate((double)radius))) {
         if (!(mob instanceof Player) && mob.isAlive() && !mob.isInWater()) {
            return mob;
         }
      }

      return null;
   }

   public static void tick_move_player_to_living_entity(Player player, LivingEntity target, float speed_per_second) {
      player.getInventory().selected = 0;
      player.lookAt(Anchor.EYES, target.position());
      double move_speed = (double)(speed_per_second / 20.0F);
      double x = player.getX() + player.getLookAngle().x * move_speed;
      double z = player.getZ() + player.getLookAngle().z * move_speed;
      double y = (double)player.level()
         .getHeight(
            Types.MOTION_BLOCKING_NO_LEAVES,
            (int)(player.getX() + player.getLookAngle().x * 1.0),
            (int)(player.getZ() + player.getLookAngle().z * 1.0)
         );
      player.teleportTo(x, y, z);
      if (player.distanceTo(target) < 1.5F) {
         LivingEntity attackTarget = target;
         DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client_safe.independenceAttack(player, attackTarget));
         target.hurt(player.damageSources().playerAttack(player), 2000.0F);
         state.putInt(player, "indep_tics", 0);
         player.swing(InteractionHand.MAIN_HAND);
         player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.0F);
      }
   }

   public static void tick_move_player_to_pos(Player player, Vec3 target, float speed_per_second) {
      player.getInventory().selected = 0;
      player.lookAt(Anchor.EYES, target);
      double move_speed = (double)(speed_per_second / 20.0F);
      double x = player.getX() + player.getLookAngle().x * move_speed;
      double z = player.getZ() + player.getLookAngle().z * move_speed;
      double y = (double)player.level()
         .getHeight(
            Types.MOTION_BLOCKING_NO_LEAVES,
            (int)(player.getX() + player.getLookAngle().x * 1.0),
            (int)(player.getZ() + player.getLookAngle().z * 1.0)
         );
      player.teleportTo(x, y, z);
   }

   @SubscribeEvent
   public static void tickevente(PlayerTickEvent event) {
      Player player = event.player;
      boolean client = player.level().isClientSide();
      if (!client && event.phase != Phase.END) {
         int tics_of_hunt = state.getInt(player, "indep_tics");
         if (tics_of_hunt > 0 && player.level() instanceof ServerLevel serv) {
            Entity target = serv.getEntity(UUID.fromString(state.getString(player, "indep_target")));
            if (target instanceof LivingEntity living && living.isAlive()) {
               tick_move_player_to_living_entity(player, living, 8.0F);
            }
         }

         state.putInt(player, "indep_tics", tics_of_hunt - 1);
      }

      if (!client && player.tickCount % 40 == 0 && random.nextInt(1, 1001) == 200 && player_on_surface(player) && !terror_beginning.near_house(player)) {
         start(player, 300);
      }
   }
}
