package com.example.flywheel_of_terror.client;

import com.example.flywheel_of_terror.state;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Holds every {@code net.minecraft.client.*} call that the original mod made from the common
 * (FORGE) event bus. These methods are only ever reached through
 * {@link net.minecraftforge.fml.DistExecutor#unsafeRunWhenOn(Dist, java.util.function.Supplier)}
 * with {@link Dist#CLIENT}, so a dedicated server never loads this class (or {@code Minecraft}).
 *
 * <p>Behaviour on a physical client (including the integrated single-player server, which runs on
 * the CLIENT dist) is identical to the original; on a dedicated server the client half is simply
 * skipped. Phase 3 will replace this with real S2C packets.
 */
@OnlyIn(Dist.CLIENT)
public final class client_safe {
   private client_safe() {
   }

   /** game_rules.every: force first person + cap render distance. */
   public static void gameRulesCameraTick() {
      Player local = Minecraft.getInstance().player;
      if (local == null || state.getInt(local, "tics_of_looking") <= 0) {
         Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
      }

      if ((Integer)Minecraft.getInstance().options.renderDistance().get() > 6) {
         Minecraft.getInstance().options.renderDistance().set(6);
         Minecraft.getInstance().options.save();
      }
   }

   /** all_look_at_you.every: third-person front + make nearby mobs stare at the camera. */
   public static void allLookAtYouTick(Player player) {
      Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_FRONT);
      if (!player.level().isClientSide()) {
         for (Entity mob : player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(300.0))) {
            Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 pos = cam.getPosition();
            mob.lookAt(Anchor.EYES, pos);
         }
      }
   }

   /** information.return_to_normal: restore saved mouse sensitivity. */
   public static void restoreSensitivity(double sens) {
      Minecraft.getInstance().options.sensitivity().set(sens);
      Minecraft.getInstance().options.save();
   }

   /** independence.tick_move_player_to_living_entity: client-side attack swing. */
   public static void independenceAttack(Player player, LivingEntity target) {
      Minecraft.getInstance().gameMode.attack(player, target);
   }

   /** paranoia.do_a_call: clear the chat for the incoming "call". */
   public static void paranoiaClearChat() {
      Minecraft.getInstance().gui.getChat().clearMessages(false);
   }

   /** he_is_here_event.do_event: raise render distance to at least 5. */
   public static void heIsHereRenderDistance() {
      if ((Integer)Minecraft.getInstance().options.renderDistance().get() < 5) {
         Minecraft.getInstance().options.renderDistance().set(5);
         Minecraft.getInstance().options.save();
      }
   }

   /** exist_terror_event.writechar: type the fake "Let me out" text into a chat screen. */
   public static void existTerrorTypeChat(String context) {
      Minecraft.getInstance().setScreen(new ChatScreen(context));
   }

   /** exist_terror_event.writechar: close the player's container once the message finishes. */
   public static void existTerrorClose() {
      if (Minecraft.getInstance().player != null) {
         Minecraft.getInstance().player.closeContainer();
      }
   }

   /** fake_darknet_access.do_event: grab a screenshot + push the message into chat. */
   public static void darknetScreenshot() {
      Screenshot.grab(Minecraft.getInstance().gameDirectory, Minecraft.getInstance().getMainRenderTarget(), message -> {
         if (message != null) {
            Minecraft.getInstance().gui.getChat().addMessage(message);
         }
      });
   }
}
