package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class oh_no_stalker extends PathfinderMob {
   public BlockPos target_tree;
   public Vec3 required_pos;
   public static Random random = new Random();

   public oh_no_stalker(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   protected void registerGoals() {
   }

   public static Builder createAttributes() {
      return PathfinderMob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 470.0)
         .add(Attributes.MOVEMENT_SPEED, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 0.0)
         .add(Attributes.FOLLOW_RANGE, 1000.0)
         .add(Attributes.ATTACK_SPEED, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 66.0);
   }

   public void tick() {
      super.tick();
      boolean client = this.level().isClientSide();
      boolean server = !this.level().isClientSide();
      // Per-player: the tree to hide behind is computed from this stalker's nearest player, not a
      // shared global (was oh_no_stalker_event.nearest_tree).
      Player nearestPlayer = this.level().getNearestPlayer(this, 400.0);
      BlockPos nearest_tree = nearestPlayer != null ? oh_no_stalker_event.get_nearest_tree(nearestPlayer) : null;
      if (nearest_tree != null && nearest_tree.getY() == 0) {
         MobEffectInstance vanish = new MobEffectInstance(MobEffects.INVISIBILITY, 10, 10, false, true, false);
         this.addEffect(vanish);
      }

      if (this.tickCount % 200 == 0 && nearest_tree != null) {
         this.target_tree = nearest_tree;
         MobEffectInstance vanish = new MobEffectInstance(MobEffects.INVISIBILITY, 10, 10, false, true, false);
         this.addEffect(vanish);
      }

      if (server && this.target_tree != null) {
         this.heal(10.0F);

         for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(400.0))) {
            this.lookAt(Anchor.EYES, player.getEyePosition());
            double xangle = -this.getLookAngle().x * 2.0;
            double zangle = -this.getLookAngle().z * 2.0;
            double xpos = (double)this.target_tree.getX() + xangle;
            double zpos = (double)this.target_tree.getZ() + zangle;
            double ypos = (double)this.level().getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)xpos, (int)zpos);
            this.required_pos = new Vec3(xpos, ypos, zpos);
            this.teleportTo(xpos, ypos, zpos);
            if (this.distanceTo(player) < 4.0F) {
               CompoundTag global_tag = player.getPersistentData();
               CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
               this.remove(RemovalReason.DISCARDED);
               MobEffectInstance blind = new MobEffectInstance(MobEffects.BLINDNESS, 80, 10, false, true, false);
               player.addEffect(blind);
               tag.putInt("oh_no_stalker_event", 1);
            }
         }
      }
   }

   @SubscribeEvent
   public static void no_dead(LivingDeathEvent event) {
      if (event.getEntity() instanceof oh_no_stalker) {
         event.setCanceled(true);
      }
   }
}
