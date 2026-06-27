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
   // event_in_process is per-player gameplay (NBT "tool_break_active"); the break sound is now an
   // S2C packet to the affected player (Phase 3).
   public static void set_active(Player player, boolean value) {
      state.putBool(player, "tool_break_active", value);
   }

   @SubscribeEvent
   public static void checkarm(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide()) {
         int to_reload_event = state.getInt(player, "tool_break_reload");
         if (player.tickCount % 80 == 0) {
            to_reload_event--;
         }

         if (state.getBool(player, "tool_break_active") && to_reload_event <= 0) {
            to_reload_event = 20;
            if (player.getMainHandItem().getItem() instanceof PickaxeItem
               || player.getMainHandItem().getItem() instanceof AxeItem
               || player.getMainHandItem().getItem() instanceof ShovelItem
               || player.getMainHandItem().getItem() instanceof HoeItem) {
               player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
               state.putBool(player, "tool_break_active", false);
               Network.sound(player, SoundEvents.ITEM_BREAK, 10.0F, 1.0F);
            }
         }

         state.putInt(player, "tool_break_reload", to_reload_event);
      }
   }
}
