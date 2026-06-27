package com.example.flywheel_of_terror;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class thunder_behind {
   // event_in_process → per-player NBT ("thunder_behind_active"). Phase 3: tics_to_punch /
   // tics_of_madness (the "turn around" madness shader + big_glitch sound) are set + ticked on the
   // client via the THUNDER_PUNCH FxPacket (see client_net.clientTick).
   public static Random random = new Random();
   public static int tics_to_punch = -10;
   public static int tics_of_madness = 0;

   public static void set_active(Player player, boolean value) {
      state.putBool(player, "thunder_behind_active", value);
   }

   @EventBusSubscriber(value = {Dist.CLIENT})
   public static class client_events {
      @SubscribeEvent
      public static void blood_rain(RenderLevelStageEvent event) {
         if (tics_of_madness >= 0) {
            RenderSystem.setShaderColor(20.0F, 10.0F, 40.0F, 1.0F);
         }
      }
   }

   @SubscribeEvent
   public static void open_chest(RightClickBlock event) {
      CompoundTag global_tag = event.getEntity().getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      String coordinates1 = event.getPos().getX() + "," + event.getPos().getY() + "," + event.getPos().getZ();
      if (!event.getLevel().isClientSide() && tag.getBoolean(coordinates1 + "around")) {
         Player player = event.getEntity();
         tag.putBoolean(coordinates1 + "around", false);
         global_tag.put("flywheel_of_terror", tag);
         Network.fx(player, Network.THUNDER_PUNCH, 220);
      }
   }

   @SubscribeEvent
   public static void every_time(PlayerTickEvent event) {
      Player player = event.player;
      CompoundTag global_tag = event.player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      // tics_to_punch / tics_of_madness + the big_glitch "turn around" sound now run client-side
      // (see client_net.clientTick), triggered by the THUNDER_PUNCH FxPacket.
      if (state.getBool(player, "thunder_behind_active") && !player.level().isClientSide()) {
         double xx = player.getLookAngle().x;
         double zz = player.getLookAngle().z;
         BlockPos pos = new BlockPos((int)(player.getX() - 5.0 * xx), (int)player.getY(), (int)(player.getZ() - zz * 5.0));
         if (player.level() instanceof ServerLevel serv) {
            LightningBolt thunder = (LightningBolt)EntityType.LIGHTNING_BOLT.create(serv);
            thunder.moveTo(pos, 0.0F, 0.0F);
            serv.addFreshEntity(thunder);
         }

         for (double xxx = (double)(pos.getX() - 1); xxx <= (double)(pos.getX() + 1); xxx++) {
            for (double yyy = (double)pos.getY(); yyy <= (double)(pos.getY() + 2); yyy++) {
               for (double zzz = (double)(pos.getZ() - 1); zzz <= (double)(pos.getZ() + 1); zzz++) {
                  BlockPos area = new BlockPos((int)xxx, (int)yyy, (int)zzz);
                  player.level().destroyBlock(area, true, player);
               }
            }
         }

         player.level().setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
         if (player.level().getBlockEntity(pos) instanceof ChestBlockEntity chest2) {
            int i = tag.getInt("thunder_behind");
            if (i == 0) {
               ItemStack item = new ItemStack(Items.DIAMOND);
               item.setCount(random.nextInt(1, 4));
               chest2.setItem(random.nextInt(1, 16), item);
            }

            if (i == 1) {
               String coordinates = pos.getX() + "," + pos.getY() + "," + pos.getZ();
               tag.putBoolean(coordinates + "around", true);
               global_tag.put("flywheel_of_terror", tag);
               ItemStack item = new ItemStack((ItemLike)add_items.notice.get());
               item.setHoverName(Component.literal("turn around"));
               chest2.setItem(random.nextInt(1, 16), item);
            }

            tag.putInt("thunder_behind", tag.getInt("thunder_behind") + 1);
         }

         state.putBool(player, "thunder_behind_active", false);
      }
   }
}
