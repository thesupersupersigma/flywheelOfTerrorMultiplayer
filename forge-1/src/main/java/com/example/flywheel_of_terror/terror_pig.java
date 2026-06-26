package com.example.flywheel_of_terror;

import net.minecraft.world.entity.EntityType;
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
public class terror_pig extends Pig {
   public terror_pig(EntityType<? extends Pig> type, Level level) {
      super(type, level);
   }

   public static Builder createAttributes() {
      return Pig.createAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.FOLLOW_RANGE, 35.0).add(Attributes.MOVEMENT_SPEED, 2.0);
   }
}
