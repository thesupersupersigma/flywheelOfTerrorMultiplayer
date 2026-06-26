package com.example.flywheel_of_terror;

import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class oh_no extends PathfinderMob {
   private double speed = 2.0;

   public oh_no(EntityType<? extends PathfinderMob> type, Level level) {
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
         .add(Attributes.MOVEMENT_SPEED, 0.1)
         .add(Attributes.ATTACK_DAMAGE, 200.0)
         .add(Attributes.FOLLOW_RANGE, 1000.0)
         .add(Attributes.ATTACK_SPEED, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 66.0);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)add_items.punishment.get()));
         if (this.tickCount % 40 == 0) {
            this.speed += 0.2;
         }

         if (!terror_beginning.his_hunt) {
            this.remove(RemovalReason.DISCARDED);
         }

         for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(400.0))) {
            this.getNavigation().moveTo(player, this.speed);
            this.setAggressive(true);
            this.setTarget(player);
         }

         for (oh_no copy : this.level().getEntitiesOfClass(oh_no.class, this.getBoundingBox().inflate(100.0))) {
            if (copy.getUUID() != this.getUUID()) {
               copy.remove(RemovalReason.DISCARDED);
            }
         }
      }
   }

   protected float getJumpPower() {
      return 0.7F * this.getBlockJumpFactor() + this.getJumpBoostPower();
   }

   @SubscribeEvent
   public static void kill_player(LivingDeathEvent event) {
      if (event.getSource().getEntity() instanceof oh_no he && event.getEntity() instanceof Player player) {
         he.remove(RemovalReason.DISCARDED);
         terror_beginning.his_hunt = false;
         ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(null, null);
         if (player instanceof ServerPlayer serv) {
            serv.connection.send(packet);
         }
      }
   }
}
