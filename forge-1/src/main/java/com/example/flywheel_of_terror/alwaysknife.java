package com.example.flywheel_of_terror;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class alwaysknife {
   @SubscribeEvent
   public static void scan(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide()) {
         CompoundTag global_tag = player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         if (!terror_beginning.his_hunt && terror_end.phase == 0) {
            player.getInventory().setItem(0, new ItemStack((ItemLike)add_items.my_knife.get()));
         } else if (player.getMainHandItem().getItem() == add_items.my_knife.get()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
         } else if (player.getOffhandItem().getItem() == add_items.my_knife.get()) {
            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
         }
      }
   }

   @SubscribeEvent
   public static void declinedropknife(ItemTossEvent event) {
      if (event.getEntity().getItem().getItem() == add_items.my_knife.get()) {
         event.setCanceled(true);
      }
   }
}
