package com.example.flywheel_of_terror;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class fake_steve extends PathfinderMob {
   public int tics_of_live = 20;

   public fake_steve(EntityType<? extends PathfinderMob> type, Level level) {
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

   public void dropItem(ItemStack itemStack) {
      if (!itemStack.isEmpty() && !this.level().isClientSide()) {
         this.spawnAtLocation(itemStack, 1.0F);
      }
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide() && information.just_player != null) {
         this.lookAt(Anchor.EYES, information.just_player.getEyePosition());
         this.tics_of_live--;
         if (this.tics_of_live <= 0) {
            this.remove(RemovalReason.DISCARDED);
         }
      }
   }
}
