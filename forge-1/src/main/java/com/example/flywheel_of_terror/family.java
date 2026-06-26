package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class family {
   public static int tics_of_house = 0;
   public static Random random = new Random();

   public static void do_event(Player player) {
      Villager father = new Villager(EntityType.VILLAGER, player.level());
      Villager mother = new Villager(EntityType.VILLAGER, player.level());
      Villager son = new Villager(EntityType.VILLAGER, player.level());
      Villager daughter = new Villager(EntityType.VILLAGER, player.level());
      son.setBaby(true);
      daughter.setBaby(true);
      double x = player.getX() + (double)random.nextInt(-50, 50);
      double z = player.getZ() + (double)random.nextInt(-50, 50);
      double y = (double)player.level().getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z);
      Vec3 pos = new Vec3(x, y, z);
      Vec3 pos2 = new Vec3(x + 1.0, y, z);
      Vec3 pos3 = new Vec3(x, y, z + 1.0);
      Vec3 pos4 = new Vec3(x - 1.0, y, z + 1.0);
      father.setPos(pos);
      mother.setPos(pos2);
      son.setPos(pos3);
      daughter.setPos(pos4);
      father.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
      mother.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
      son.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
      daughter.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
      father.getPersistentData().putBoolean("family", true);
      mother.getPersistentData().putBoolean("family", true);
      son.getPersistentData().putBoolean("family", true);
      daughter.getPersistentData().putBoolean("family", true);
      player.level().addFreshEntity(father);
      player.level().addFreshEntity(mother);
      player.level().addFreshEntity(son);
      player.level().addFreshEntity(daughter);
   }

   @EventBusSubscriber(value = {Dist.CLIENT})
   public static class client_events {
      @SubscribeEvent
      public static void render_house(RenderGuiEvent event) {
         int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
         int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
         if (tics_of_house > 0) {
            event.getGuiGraphics()
               .blit(new ResourceLocation("flywheel_of_terror", "textures/lore/house.png"), 0, 0, width, height, 0.0F, 0.0F, 1080, 1080, 1080, 1080);
            Minecraft.getInstance().setScreen(null);
         }
      }
   }

   @SubscribeEvent
   public static void find_family(PlayerTickEvent event) {
      Player player = event.player;
      boolean client = player.level().isClientSide();
      boolean server = !player.level().isClientSide();
      if (server) {
         tics_of_house--;

         for (Villager vill : player.level().getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(5.0))) {
            if (vill.getPersistentData().getBoolean("family")) {
               tics_of_house = 120;
               remove_entities.tics_without_life = 600;

               for (Villager vill2 : player.level().getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(50.0))) {
                  if (vill2.getPersistentData().getBoolean("family")) {
                     vill2.remove(RemovalReason.DISCARDED);
                     CompoundTag global_tag = event.player.getPersistentData();
                     CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
                     tag.putInt("family", 1);
                     global_tag.put("flywheel_of_terror", tag);
                  }
               }
            }
         }
      }
   }
}
