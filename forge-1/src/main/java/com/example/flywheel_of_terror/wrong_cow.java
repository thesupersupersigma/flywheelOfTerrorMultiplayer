package com.example.flywheel_of_terror;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class wrong_cow extends Cow {
   public wrong_cow(EntityType<? extends Cow> type, Level level) {
      super(type, level);
   }

   public static Builder createAttributes() {
      return Cow.createAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.FOLLOW_RANGE, 35.0).add(Attributes.MOVEMENT_SPEED, 0.0);
   }

   public void tick() {
      super.tick();
      if (information.just_player != null) {
         this.lookAt(Anchor.EYES, information.just_player.getEyePosition());

         for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(9.0))) {
            double x = this.getX() + this.getLookAngle().x * 0.5;
            double y = this.getY();
            double z = this.getZ() + this.getLookAngle().z * 0.5;
            this.teleportTo(x, y, z);
         }

         for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(0.2F))) {
            paranoia.do_a_call(player);
            this.remove(RemovalReason.DISCARDED);
         }
      }
   }
}
