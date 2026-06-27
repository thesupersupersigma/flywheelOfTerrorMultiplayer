package com.example.flywheel_of_terror;

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class game_rules {
   public static boolean kick_required = false;

   @SubscribeEvent
   public static void onMobDrops(LivingDropsEvent event) {
      if (event.getSource().getEntity() instanceof Player player) {
         if (terror_end.phase(player) == 0) {
            CompoundTag global_tag = player.getPersistentData();
            CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
            if (tag.getInt("state_of_lore") != 0) {
               return;
            }

            if (event.getEntity() instanceof Animal) {
               Collection<ItemEntity> drops = event.getDrops();
               Collection<ItemEntity> toRemove = new ArrayList<>();

               for (ItemEntity itemEntity : drops) {
                  ItemStack stack = itemEntity.getItem();
                  Item item = stack.getItem();
                  if (isMeatItem(item)) {
                     toRemove.add(itemEntity);
                  }
               }

               drops.removeAll(toRemove);
            }
         }
      }
   }

   private static boolean isMeatItem(Item item) {
      return item == Items.PORKCHOP
         || item == Items.COOKED_PORKCHOP
         || item == Items.BEEF
         || item == Items.COOKED_BEEF
         || item == Items.CHICKEN
         || item == Items.COOKED_CHICKEN
         || item == Items.MUTTON
         || item == Items.COOKED_MUTTON
         || item == Items.RABBIT
         || item == Items.COOKED_RABBIT
         || item == Items.LEATHER
         || item == Items.FEATHER
         || item == Items.EGG
         || item == Items.RABBIT_FOOT
         || item == Items.RABBIT_HIDE;
   }

   @SubscribeEvent
   public static void onFoodEaten(Finish event) {
      if (event.getEntity() instanceof Player player) {
         if (terror_end.phase(player) == 0) {
            ItemStack stack = event.getItem();
            Item item = stack.getItem();
            FoodProperties foodProps = item.getFoodProperties();
            if (foodProps != null && !player.level().isClientSide()) {
               player.getServer().execute(() -> applyCursedEffect(player, foodProps));
            }
         }
      }
   }

   private static void applyCursedEffect(Player player, FoodProperties foodProps) {
      FoodData foodData = player.getFoodData();
      int currentHunger = foodData.getFoodLevel();
      float currentSaturation = foodData.getSaturationLevel();
      int foodNutrition = foodProps.getNutrition();
      float foodSaturation = foodProps.getSaturationModifier();
      int hungerToRemove = foodNutrition + 2;
      int newHunger = Math.max(0, currentHunger - hungerToRemove);
      foodData.setFoodLevel(newHunger);
      float saturationToRemove = (float)foodNutrition * foodSaturation + 1.0F;
      float newSaturation = Math.max(0.0F, currentSaturation - saturationToRemove);
      foodData.setSaturation(newSaturation);
      player.setHealth(player.getHealth() - 1.0F);
   }

   @SubscribeEvent
   public static void every(PlayerTickEvent event) {
      Player player = event.player;
      if (terror_end.phase(player) == 0) {
         if (!player.level().isClientSide()) {
            // Force first-person + cap render distance on this player's client (Phase 3 S2C packet).
            Network.fx(player, Network.GAMERULES_CAM);

            if (player.level().getDifficulty() == Difficulty.PEACEFUL && player instanceof ServerPlayer serv) {
               serv.getServer().setDifficulty(Difficulty.NORMAL, true);
               serv.connection.disconnect(Component.literal("Entity.RemovalReason.MONSTER"));
            }

            if (player.tickCount % 2 == 0) {
               for (Villager villager : player.level().getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(8.0))) {
                  if (villager instanceof faceless_villager) {
                     return;
                  }

                  villager.goalSelector.addGoal(1, new AvoidEntityGoal(villager, Player.class, 8.0F, 1.0, 1.0));
               }

               for (IronGolem holem : player.level().getEntitiesOfClass(IronGolem.class, player.getBoundingBox().inflate(200.0))) {
                  holem.setLastHurtByMob(player);
                  holem.setAggressive(true);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onKillTest(LivingDeathEvent event) {
      if (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof Player player) {
         if (terror_end.phase(player) == 0) {
            CompoundTag global_tag = player.getPersistentData();
            CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
            LivingEntity entity = event.getEntity();
            FoodData foodData = player.getFoodData();
            foodData.setFoodLevel(Math.min(foodData.getFoodLevel() + 2, 20));
            foodData.setSaturation(Math.min(foodData.getSaturationLevel() + 1.0F, 20.0F));
            player.heal(2.0F);
            if (entity.getType() == EntityType.VILLAGER) {
               FoodData foodData2 = player.getFoodData();
               foodData2.setFoodLevel(Math.min(foodData2.getFoodLevel() + 3, 20));
               foodData2.setSaturation(Math.min(foodData2.getSaturationLevel() + 1.5F, 20.0F));
            }

            if (entity.getType() == EntityType.IRON_GOLEM) {
               FoodData foodData3 = player.getFoodData();
               foodData3.setFoodLevel(Math.min(foodData3.getFoodLevel() + 11, 20));
               foodData3.setSaturation(Math.min(foodData3.getSaturationLevel() + 5.5F, 20.0F));
               player.heal(12.0F);
            }

            if (entity.getType() == EntityType.ZOMBIE) {
               FoodData foodData4 = player.getFoodData();
               foodData4.setFoodLevel(Math.min(foodData4.getFoodLevel() + 1, 20));
               foodData4.setSaturation(Math.min(foodData4.getSaturationLevel() + 0.5F, 20.0F));
               player.heal(2.0F);
            }
         }
      }
   }
}
