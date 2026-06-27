package com.example.flywheel_of_terror;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class notice_in_inventory {
   public static Random random = new Random();

   public static void set_active(Player player, boolean value) {
      state.putBool(player, "notice_in_inventory_active", value);
   }

   @SubscribeEvent
   public static void every(PlayerTickEvent event) {
      Player player = event.player;
      if (state.getBool(player, "notice_in_inventory_active") && !player.level().isClientSide()) {
         CompoundTag global_tag = player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         ItemStack notice_stack = new ItemStack((ItemLike)add_items.notice.get());
         List<String> info = new ArrayList<>();
         info.add("Help yourself");
         info.add("You can't hide");
         info.add("You don't know anything.");
         info.add("why?");
         info.add("you're mine!");
         if (tag.getBoolean("builded")) {
            int x = tag.getInt("house_X");
            int y = tag.getInt("house_Y");
            int z = tag.getInt("house_Z");
            info.add("Do you know this place " + x + " " + y + " " + z + "?");
         }

         notice_stack.setHoverName(Component.literal(info.get(random.nextInt(0, info.size()))));
         player.getInventory().setItem(15, notice_stack);
         state.putBool(player, "notice_in_inventory_active", false);
      }
   }
}
