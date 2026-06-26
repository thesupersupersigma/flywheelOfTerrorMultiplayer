package com.example.flywheel_of_terror;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class oh_no_here extends PathfinderMob {
   public oh_no_here(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
      this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
   }

   public static Builder createAttributes() {
      return PathfinderMob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 470.0)
         .add(Attributes.MOVEMENT_SPEED, 1.0)
         .add(Attributes.ATTACK_DAMAGE, 200.0)
         .add(Attributes.FOLLOW_RANGE, 1000.0)
         .add(Attributes.ATTACK_SPEED, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 66.0);
   }

   public void spawn_bedrocks() {
      double xx = this.getX();
      double yy = this.getY();
      double zz = this.getZ();

      for (double x = xx - 2.0; x <= xx + 2.0; x++) {
         for (double y = yy - 1.0; y <= yy - 1.0; y++) {
            for (double z = zz - 2.0; z <= zz + 2.0; z++) {
               BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
               this.level().setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
            }
         }
      }
   }

   public void destroy() {
      double xx = this.getX();
      double yy = this.getY();
      double zz = this.getZ();

      for (double x = xx - 2.0; x <= xx + 2.0; x++) {
         for (double y = yy - 3.0; y <= yy + 3.0; y++) {
            for (double z = zz - 2.0; z <= zz + 2.0; z++) {
               BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
               if (this.level().getBlockState(pos).getBlock() != Blocks.BEDROCK) {
                  this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
               }
            }
         }
      }
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)add_items.punishment.get()));

         for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(400.0))) {
            this.lookAt(Anchor.EYES, player.getEyePosition());
            this.teleportTo(this.getX() + this.getLookAngle().x * 0.3F, player.getY(), this.getZ() + this.getLookAngle().z * 0.3F);
            this.spawn_bedrocks();
            this.destroy();
            if (he_is_here_event.get_tics_of_event(player) <= 0) {
               this.remove(RemovalReason.DISCARDED);
            }
         }

         for (Player playerx : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(4.0))) {
            this.remove(RemovalReason.DISCARDED);
            he_is_here_event.set_tics_of_event(playerx, -10);
            information.do_a_silence(playerx);
         }
      }
   }
}
