package com.example.flywheel_of_terror;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class apocalypsis_event {
   // event_in_process and the server-side gameplay timer move to per-player NBT ("apoc_active" /
   // "apoc_tics"). tics_of_event + red_intense + sound_must_be stay static for the client shader/
   // sound (Phase 3).
   public static int tics_of_event = -2;
   public static boolean sound_must_be = false;
   public static Random random = new Random();
   public static float red_intense = 0.0F;

   public static void set_active(Player player, boolean value) {
      state.putBool(player, "apoc_active", value);
   }

   @EventBusSubscriber(value = {Dist.CLIENT})
   public static class client_events {
      @SubscribeEvent
      public static void blood_rain(RenderLevelStageEvent event) {
         if (tics_of_event >= 0) {
            RenderSystem.setShaderColor(red_intense, 0.0F, 0.0F, 1.0F);
            if (red_intense < 3.0F) {
               red_intense += 3.5E-4F;
            }
         }
      }
   }

   @SubscribeEvent
   public static void every_time(PlayerTickEvent event) {
      Player player = event.player;
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      if (player.level().isClientSide() && sound_must_be) {
         player.playNotifySound((SoundEvent)register_sounds.apocalypsis.get(), SoundSource.RECORDS, 1.0F, 1.0F);
         sound_must_be = false;
      }

      if (state.getBool(player, "apoc_active") && !player.level().isClientSide() && player.getY() > 60.0) {
         tag.putBoolean("apoc", true);
         global_tag.put("flywheel_of_terror", tag);
         red_intense = 0.5F;
         sound_must_be = true;
         if (player.level() instanceof ServerLevel serv) {
            serv.setDayTime(14500L);
            serv.setWeatherParameters(0, 360, true, true);
         }

         state.putBool(player, "apoc_active", false);
         tics_of_event = 860;
         state.putInt(player, "apoc_tics", 860);
      }

      if (player.level().isClientSide()) {
         tics_of_event--;
      }

      int apoc_tics = state.getInt(player, "apoc_tics");
      if (apoc_tics > 0 && !player.level().isClientSide()) {
         boolean bottom_of_tree_finded = false;
         BlockPos bottom_pos = new BlockPos(1, 1, 1);

         for (double x = player.getX() - 15.0; x <= player.getX() + 15.0; x++) {
            for (double y = player.getY() - 3.0; y <= player.getY() + 3.0; y++) {
               for (double z = player.getZ() - 15.0; z <= player.getZ() + 15.0; z++) {
                  BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                  if ((
                        player.level().getBlockState(pos).getBlock() == Blocks.OAK_LOG
                           || player.level().getBlockState(pos).getBlock() == Blocks.DARK_OAK_LOG
                           || player.level().getBlockState(pos).getBlock() == Blocks.SPRUCE_LOG
                           || player.level().getBlockState(pos).getBlock() == Blocks.BIRCH_LOG
                           || player.level().getBlockState(pos).getBlock() == Blocks.MANGROVE_LOG
                           || player.level().getBlockState(pos).getBlock() == Blocks.JUNGLE_LOG
                           || player.level().getBlockState(pos).getBlock() == Blocks.CHERRY_LOG
                           || player.level().getBlockState(pos).getBlock() == Blocks.ACACIA_LOG
                     )
                     && !bottom_of_tree_finded) {
                     bottom_pos = pos;
                     bottom_of_tree_finded = true;
                  }
               }
            }
         }

         int height = random.nextInt(10, 30);

         for (double x = (double)(bottom_pos.getX() - 3); x <= (double)(bottom_pos.getX() + 3); x++) {
            for (double y = (double)bottom_pos.getY(); y <= player.getY() + 10.0; y++) {
               for (double zx = (double)(bottom_pos.getZ() - 3); zx <= (double)(bottom_pos.getZ() + 3); zx++) {
                  BlockPos pos = new BlockPos((int)x, (int)y, (int)zx);
                  if (bottom_of_tree_finded) {
                     BlockPos pos2 = new BlockPos((int)x, (int)y + height, (int)zx);
                     BlockState current_blocks = player.level().getBlockState(pos);
                     player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                     player.level().setBlock(pos2, current_blocks, 3);
                  }
               }
            }
         }

         for (LivingEntity loh : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(30.0))) {
            if (!(loh instanceof Player) && !(loh instanceof headless_steve)) {
               loh.setSecondsOnFire(5);
            }
         }

         if (apoc_tics % 20 == 0) {
            double x = player.getLookAngle().x;
            double zxx = player.getLookAngle().z;
            double xx = x * (double)random.nextInt(-12, -5);
            double zz = zxx * (double)random.nextInt(-12, -5);
            headless_steve copy = new headless_steve((EntityType<? extends PathfinderMob>)add_humans.headless_steve.get(), player.level());
            Vec3 pos = new Vec3(player.getX() + xx, player.getY(), player.getZ() + zz);
            copy.setPos(pos);
            int y = player.level().getHeight(Types.WORLD_SURFACE, (int)copy.getX(), (int)copy.getZ());
            pos = new Vec3(player.getX() + xx, (double)(y + 1), player.getZ() + zz);
            copy.setPos(pos);
            player.level().addFreshEntity(copy);
         }

         state.putInt(player, "apoc_tics", apoc_tics - 1);
      }
   }
}
