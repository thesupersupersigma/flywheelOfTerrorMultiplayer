package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.SoundEngineLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;

@Mod("flywheel_of_terror")
public class flywheel_of_terror {
   public static final String MODID = "flywheel_of_terror";
   private static Random random = new Random();

   public flywheel_of_terror() {
      IEventBus ModEventBus = FMLJavaModLoadingContext.get().getModEventBus();
      add_items.do_item.register(ModEventBus);
      ModEventBus.addListener(this::commonSetup);
      MinecraftForge.EVENT_BUS.register(this);
      register_sounds.SOUND_EVENTS.register(ModEventBus);
      add_humans.add_human_zombie.register(ModEventBus);
      ModEventBus.addListener(this::registerAttributes);
      MixinEnvironment.getDefaultEnvironment().setSide(Side.CLIENT);
      MixinEnvironment.getDefaultEnvironment().setSide(Side.SERVER);
   }

   private void registerAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)add_humans.terror_pig.get(), terror_pig.createAttributes().build());
      event.put((EntityType)add_humans.terror_cow.get(), terror_cow.createAttributes().build());
      event.put((EntityType)add_humans.terror_sheep.get(), terror_cow.createAttributes().build());
      event.put((EntityType)add_humans.headless_villager.get(), headless_villager.createAttributes().build());
      event.put((EntityType)add_humans.true_me.get(), true_me.createAttributes().build());
      event.put((EntityType)add_humans.somewho.get(), somewho.createAttributes().build());
      event.put((EntityType)add_humans.fake_steve.get(), fake_steve.createAttributes().build());
      event.put((EntityType)add_humans.headless_steve.get(), headless_steve.createAttributes().build());
      event.put((EntityType)add_humans.faceless_villager.get(), faceless_villager.createAttributes().build());
      event.put((EntityType)add_humans.faceless_pig.get(), faceless_pig.createAttributes().build());
      event.put((EntityType)add_humans.faceless_cow.get(), faceless_cow.createAttributes().build());
      event.put((EntityType)add_humans.faceless_sheep.get(), faceless_sheep.createAttributes().build());
      event.put((EntityType)add_humans.invalid.get(), invalid.createAttributes().build());
      event.put((EntityType)add_humans.wrong_sheep.get(), wrong_sheep.createAttributes().build());
      event.put((EntityType)add_humans.wrong_cow.get(), wrong_cow.createAttributes().build());
      event.put((EntityType)add_humans.oh_no_behind.get(), oh_no_behind.createAttributes().build());
      event.put((EntityType)add_humans.oh_no.get(), oh_no.createAttributes().build());
      event.put((EntityType)add_humans.oh_no_here.get(), oh_no_here.createAttributes().build());
      event.put((EntityType)add_humans.oh_no_stalker.get(), oh_no_stalker.createAttributes().build());
      event.put((EntityType)add_humans.invisible.get(), invisible.createAttributes().build());
   }

   private void commonSetup(FMLCommonSetupEvent event) {
   }

   @SubscribeEvent
   public void hell(EntityTravelToDimensionEvent event) {
      if (event.getEntity() instanceof Player player) {
         event.setCanceled(true);
         CompoundTag global_tag = player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         player.level().playSound(null, player.blockPosition(), SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (!tag.getBoolean("builded")) {
            player.teleportTo(
               (double)player.level().getSharedSpawnPos().getX(),
               (double)player.level().getSharedSpawnPos().getY(),
               (double)player.level().getSharedSpawnPos().getZ()
            );
         } else if (house_defend.bed_here(player)) {
            BlockPos bed = house_defend.bed_pos(player);
            player.teleportTo((double)bed.getX(), (double)bed.getY(), (double)bed.getZ());
         }
      }
   }

   @SubscribeEvent
   public static void onSoundEngineLoad(SoundEngineLoadEvent event) {
      try {
         SoundEngine soundEngine = event.getEngine();
         System.out.println("[NoMusic] Звуковой движок загружен");
      } catch (Exception var2) {
         System.err.println("[NoMusic] Ошибка при загрузке звукового движка: " + var2.getMessage());
      }
   }

   @EventBusSubscriber(
      modid = "flywheel_of_terror",
      bus = Bus.MOD,
      value = {Dist.CLIENT}
   )
   public static class ClientModEvents {
      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
      }
   }
}
