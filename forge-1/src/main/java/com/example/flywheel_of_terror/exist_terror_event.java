package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class exist_terror_event {
   // Phase 3: the "Let me out…" chat-typing is now server-authoritative and per-player. The typing
   // progress lives in NBT ("exist_tics" / "exist_next" / "exist_ctx"); the server sends each new
   // line to the affected player via the EXIST_TYPE FxPacket (and EXIST_CLOSE when it finishes).
   public static Random random = new Random();
   public static String mytext = "Let me out, someone, please ";

   public static void set_active(Player player, boolean value) {
      state.putBool(player, "exist_terror_active", value);
      if (value) {
         state.putInt(player, "exist_tics", 20);
         state.putInt(player, "exist_next", 0);
         state.putString(player, "exist_ctx", "");
      }
   }

   @SubscribeEvent
   public static void zerodamage(LivingHurtEvent event) {
      if (event.getEntity() instanceof Player player && state.getBool(player, "exist_terror_active")) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void writechar(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide() && state.getBool(player, "exist_terror_active")) {
         int tics = state.getInt(player, "exist_tics") - 1;
         int next = state.getInt(player, "exist_next");
         String context = state.getString(player, "exist_ctx");
         if (tics <= 0 && next < mytext.length()) {
            context = context + mytext.charAt(next);
            Network.fx(player, Network.EXIST_TYPE, context);
            tics = random.nextInt(3, 15);
            next++;
         }

         if (context.length() == mytext.length()) {
            state.putBool(player, "exist_terror_active", false);
            Network.fx(player, Network.EXIST_CLOSE);
            context = "";
            next = 0;
            tics = 10;
            player.sendSystemMessage(Component.literal("He has no permissions to anything."));
         }

         state.putInt(player, "exist_tics", tics);
         state.putInt(player, "exist_next", next);
         state.putString(player, "exist_ctx", context);
      }
   }

   @SubscribeEvent
   public static void declinebreak(ServerChatEvent event) {
      if (state.getBool(event.getPlayer(), "exist_terror_active")) {
         event.setCanceled(true);
      }
   }
}
