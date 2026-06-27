package com.example.flywheel_of_terror;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class true_me extends PathfinderMob {
   public true_me(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(2, new OpenDoorGoal(this, true));
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
      this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
   }

   public static Builder createAttributes() {
      return PathfinderMob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 80.0)
         .add(Attributes.MOVEMENT_SPEED, 1.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.FOLLOW_RANGE, 50.0)
         .add(Attributes.ATTACK_SPEED, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
   }

   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public void tick() {
      super.tick();
      boolean client = this.level().isClientSide();
      if (!client) {
         this.heal(5.0F);
         int x = (int)this.getX();
         int y = (int)this.getY();
         int z = (int)this.getZ();
         BlockPos pos = new BlockPos(x, y, z);
         BlockPos pos2 = new BlockPos(x, y + 1, z);
         this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
         this.level().setBlock(pos2, Blocks.AIR.defaultBlockState(), 3);

         Player player = information.getTarget(this);
         if (player != null && this.distanceTo(player) <= 100.0) {
            this.lookAt(Anchor.EYES, player.getEyePosition());
            this.getNavigation().moveTo(player, 0.7F);
            int xx = (int)this.getX();
            int yy = (int)this.getZ();
            int zz = (int)this.getZ();

            for (int xxx = xx - 10; xxx <= xx + 10; xxx++) {
               for (int yyy = yy - 5; yyy <= yy + 5; yyy++) {
                  for (int zzz = zz - 10; zzz <= zz + 10; zzz++) {
                     BlockPos pos3 = new BlockPos(x, y, z);
                     if (this.level().getBlockState(pos).getBlock() instanceof DoorBlock) {
                        this.level().destroyBlock(pos3, true);
                     }
                  }
               }
            }
         }
      }
   }
}
