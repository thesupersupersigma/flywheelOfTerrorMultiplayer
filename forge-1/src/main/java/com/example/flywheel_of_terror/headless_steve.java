package com.example.flywheel_of_terror;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class headless_steve extends PathfinderMob {
   public headless_steve(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(7, new FloatGoal(this));
      this.goalSelector.addGoal(6, new OpenDoorGoal(this, true));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
   }

   public static Builder createAttributes() {
      return PathfinderMob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.MOVEMENT_SPEED, 1.0)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.FOLLOW_RANGE, 500.0)
         .add(Attributes.ATTACK_SPEED, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
   }

   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public void tick() {
      super.tick();
      this.setItemInHand(InteractionHand.MAIN_HAND, information.head);
      this.setItemInHand(InteractionHand.OFF_HAND, information.head);
      if (information.just_player != null) {
         this.lookAt(Anchor.EYES, information.just_player.getEyePosition());
         this.getNavigation().moveTo(information.igrok, 0.8);
      }

      if (apocalypsis_event.tics_of_event < 0 && !this.level().isClientSide()) {
         this.remove(RemovalReason.DISCARDED);
      }
   }
}
