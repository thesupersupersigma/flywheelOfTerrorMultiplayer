package com.example.flywheel_of_terror;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class tool_break {
   public static boolean event_in_process = false;
   public static boolean sound_must_be = false;
   public static int to_reload_event = 0;

   @SubscribeEvent
   public static void checkarm(PlayerTickEvent event) {
      Player player = event.player;
      if (player.tickCount % 80 == 0) {
         to_reload_event--;
      }

      if (player.level().isClientSide() && sound_must_be) {
         player.playSound(SoundEvents.ITEM_BREAK, 10.0F, 1.0F);
         sound_must_be = false;
      }

      if (!player.level().isClientSide()) {
         if (event_in_process && to_reload_event <= 0) {
            to_reload_event = 20;
            if (player.getMainHandItem().getItem() instanceof PickaxeItem
               || player.getMainHandItem().getItem() instanceof AxeItem
               || player.getMainHandItem().getItem() instanceof ShovelItem
               || player.getMainHandItem().getItem() instanceof HoeItem) {
               System.out.println("must be break");
               player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
               event_in_process = false;
               sound_must_be = true;
            }
         }
      }
   }
}
