package com.example.flywheel_of_terror;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class invalid extends Zombie {
   public invalid(EntityType<? extends Zombie> type, Level level) {
      super(type, level);
   }

   public static Builder createAttributes() {
      return Zombie.createAttributes()
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.FOLLOW_RANGE, 35.0)
         .add(Attributes.MOVEMENT_SPEED, 0.1)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.ARMOR, 2.0);
   }
}
