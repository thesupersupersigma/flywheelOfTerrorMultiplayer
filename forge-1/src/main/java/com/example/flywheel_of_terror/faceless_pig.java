package com.example.flywheel_of_terror;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class faceless_pig extends Pig {
   public int tics_to_delete = this.random.nextInt(200, 300);

   public faceless_pig(EntityType<? extends Pig> type, Level level) {
      super(type, level);
      this.tics_to_delete = this.random.nextInt(200, 300);
   }

   public static Builder createAttributes() {
      return Pig.createAttributes().add(Attributes.MAX_HEALTH, 100.0).add(Attributes.FOLLOW_RANGE, 35.0).add(Attributes.MOVEMENT_SPEED, 0.01);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide() && information.just_player != null) {
         this.lookAt(Anchor.EYES, information.just_player.getEyePosition());
         this.tics_to_delete--;
         if (this.tics_to_delete < 0) {
            this.remove(RemovalReason.DISCARDED);
         }
      }
   }
}
