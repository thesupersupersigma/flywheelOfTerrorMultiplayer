package com.example.flywheel_of_terror;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class invisible extends PathfinderMob {
   public float speed = 0.1F;
   public float phase = 1.0F;

   public invisible(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   public static Builder createAttributes() {
      return PathfinderMob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 1000.0)
         .add(Attributes.MOVEMENT_SPEED, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.FOLLOW_RANGE, 50.0)
         .add(Attributes.ATTACK_SPEED, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 66.0);
   }

   public void tick() {
      super.tick();
      boolean client = this.level().isClientSide();
      boolean server = !this.level().isClientSide();
      if (server) {
         for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(400.0))) {
            this.lookAt(Anchor.EYES, player.getEyePosition());
            double x = this.getX() + this.getLookAngle().x * (double)this.speed;
            double z = this.getZ() + this.getLookAngle().z * (double)this.speed;
            int y = this.level().getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z);
            int yy = this.level()
               .getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x + this.getLookAngle().x * 2.0), (int)(z + this.getLookAngle().z * 2.0));
            if (this.phase == 2.0F) {
               x = this.getX() - this.getLookAngle().x * (double)this.speed;
               z = this.getZ() - this.getLookAngle().z * (double)this.speed;
               y = this.level().getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z);
               yy = this.level()
                  .getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)(x - this.getLookAngle().x * 2.0), (int)(z - this.getLookAngle().z * 2.0));
            }

            this.teleportTo(x, (double)y, z);
            BlockPos pos = new BlockPos((int)x, yy - 1, (int)z);
            this.level().destroyBlock(pos, true);
            if (this.distanceTo(player) < 2.0F) {
               CompoundTag global_tag = player.getPersistentData();
               CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
               tag.putInt("invisible", 1);
               this.phase = 2.0F;
               this.speed = 0.5F;
            }
         }
      }
   }
}
