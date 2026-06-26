package com.example.flywheel_of_terror;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class oh_no_behind extends PathfinderMob {
   public int tics = 4800;

   public oh_no_behind(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   public static Builder createAttributes() {
      return PathfinderMob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.MOVEMENT_SPEED, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.FOLLOW_RANGE, 50.0)
         .add(Attributes.ATTACK_SPEED, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 66.0);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         this.tics--;
         if (this.tics <= 0) {
            this.remove(RemovalReason.DISCARDED);
         }

         for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(20.0))) {
            this.lookAt(Anchor.EYES, player.getEyePosition());
            if (this.tickCount % 40 == 0) {
               double x = player.getX() - player.getLookAngle().x * 2.0;
               double y = player.getY();
               double z = player.getZ() - player.getLookAngle().z * 2.0;
               oh_no_behind teleported = new oh_no_behind((EntityType<? extends PathfinderMob>)add_humans.oh_no_behind.get(), player.level());
               teleported.tics = this.tics;
               this.remove(RemovalReason.DISCARDED);
               teleported.setPos(new Vec3(x, y, z));
               player.level().addFreshEntity(teleported);
            }
         }
      }
   }
}
