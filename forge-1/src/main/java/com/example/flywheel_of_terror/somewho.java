package com.example.flywheel_of_terror;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
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
public class somewho extends PathfinderMob {
   public somewho(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(7, new FloatGoal(this));
      this.goalSelector.addGoal(6, new OpenDoorGoal(this, true));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(1, new MoveTowardsTargetGoal(this, 0.2, 500.0F));
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
      if (this.tickCount % 500 == 0 && information.livingingrok != null) {
         this.setTarget(information.livingingrok);
      }

      if (information.just_player != null) {
         ItemStack tool = information.just_player.getMainHandItem();
         this.setItemInHand(InteractionHand.MAIN_HAND, tool);
         ItemStack tool2 = information.just_player.getOffhandItem();
         this.setItemInHand(InteractionHand.OFF_HAND, tool2);
         if (!information.somewho_dropped && !this.level().isClientSide()) {
            information.somewho_dropped = true;
            this.dropItem(information.last_dropped);
         }

         if (information.just_player != null) {
            Player player = information.just_player;
            this.lookAt(Anchor.EYES, player.getEyePosition());
            this.getNavigation().moveTo(information.igrok, 0.2);
         }
      }
   }
}
