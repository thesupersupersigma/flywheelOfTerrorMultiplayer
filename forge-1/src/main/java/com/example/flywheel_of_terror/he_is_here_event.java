package com.example.flywheel_of_terror;

import com.example.flywheel_of_terror.client.client_safe;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class he_is_here_event {
   public static final String tics_of_event_he_is_here_nbt = "tics_of_event_he_is_here";
   public static final String tics_to_event_he_is_here_nbt = "tics_to_event_he_is_here";
   public static final String event_was_he_is_here_nbt = "event_was_he_is_here";

   public static void set_tics_of_event(Player player, int tics) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putInt("tics_of_event_he_is_here", tics);
      global_tag.put("flywheel_of_terror", tag);
   }

   public static void set_tics_to_event(Player player, int tics) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putInt("tics_to_event_he_is_here", tics);
      global_tag.put("flywheel_of_terror", tag);
   }

   public static void set_event_was(Player player, boolean state) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putBoolean("event_was_he_is_here", state);
      global_tag.put("flywheel_of_terror", tag);
   }

   public static int get_tics_of_event(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getInt("tics_of_event_he_is_here");
   }

   public static int get_tics_to_event(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getInt("tics_to_event_he_is_here");
   }

   public static boolean get_event_was(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getBoolean("event_was_he_is_here");
   }

   public static void do_event(Player player) {
      player.sendSystemMessage(Component.literal("hello"));
      set_tics_to_event(player, 200);
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client_safe.heIsHereRenderDistance());
   }

   public static void do_event2(Player player) {
      set_tics_of_event(player, 600);
      information.play_sound_at_server(player, (SoundEvent)register_sounds.oh_no_here.get(), true);
      spawn_oh_no(player);
      set_event_was(player, true);
   }

   public static boolean may_do_event2(Player player) {
      return get_tics_to_event(player) == 1 && !get_event_was(player);
   }

   public static void spawn_oh_no(Player player) {
      double x = player.getX() - player.getLookAngle().x * 60.0;
      double z = player.getZ() - player.getLookAngle().z * 60.0;
      oh_no_here he = new oh_no_here((EntityType<? extends PathfinderMob>)add_humans.oh_no_here.get(), player.level());
      he.setPos(new Vec3(x, player.getY(), z));
      player.level().addFreshEntity(he);
   }

   public static int get_distance_to_oh_no(Player player) {
      List<oh_no_here> bebra = player.level().getEntitiesOfClass(oh_no_here.class, player.getBoundingBox().inflate(400.0));
      Iterator var2 = bebra.iterator();
      if (var2.hasNext()) {
         oh_no_here he = (oh_no_here)var2.next();
         return (int)player.distanceTo(he);
      } else {
         return 666;
      }
   }

   @SubscribeEvent
   public static void ticc(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide()) {
         set_tics_of_event(player, get_tics_of_event(player) - 1);
         set_tics_to_event(player, get_tics_to_event(player) - 1);
         if (may_do_event2(player)) {
            do_event2(player);
         }

         if (get_tics_of_event(player) > 0) {
            player.displayClientMessage(Component.literal(get_distance_to_oh_no(player) + " blocks to you"), true);
            MobEffectInstance blind = new MobEffectInstance(MobEffects.BLINDNESS, 40, 40, false, true, false);
            player.addEffect(blind);
         }
      }
   }
}
