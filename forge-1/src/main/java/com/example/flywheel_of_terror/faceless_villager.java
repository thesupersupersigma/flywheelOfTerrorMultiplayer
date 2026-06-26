package com.example.flywheel_of_terror;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class faceless_villager extends Villager {
   public int hits_to_remove = 4;

   public faceless_villager(EntityType<? extends Villager> type, Level level) {
      super(type, level);
   }

   public static Builder createAttributes() {
      return Villager.createAttributes().add(Attributes.MAX_HEALTH, 100.0).add(Attributes.FOLLOW_RANGE, 100.0).add(Attributes.MOVEMENT_SPEED, 0.0);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         boolean player_near = false;

         for (Player igrok : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(50.0))) {
            this.lookAt(Anchor.EYES, igrok.getEyePosition());
            player_near = true;
         }

         for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(100.0))) {
            if (!player_near) {
               double x = player.getX() - player.getLookAngle().x * 2.0;
               double z = player.getZ() - player.getLookAngle().z * 2.0;
               this.teleportTo(x, player.getY(), z);
            }
         }

         if (this.hits_to_remove <= 0) {
            this.remove(RemovalReason.DISCARDED);
         }
      }
   }
}
