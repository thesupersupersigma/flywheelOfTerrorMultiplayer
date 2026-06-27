package com.example.flywheel_of_terror.client;

import com.example.flywheel_of_terror.Network;
import com.example.flywheel_of_terror.apocalypsis_event;
import com.example.flywheel_of_terror.eye_intervention;
import com.example.flywheel_of_terror.oh_no_between_screens;
import com.example.flywheel_of_terror.panic;
import com.example.flywheel_of_terror.paranoia;
import com.example.flywheel_of_terror.register_sounds;
import com.example.flywheel_of_terror.shipwrecked;
import com.example.flywheel_of_terror.something_wrong;
import com.example.flywheel_of_terror.thunder_behind;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Client-side terminus of the {@link Network} packets. The server decides <em>that</em> an effect
 * should happen for a particular player and sends a packet; this class makes it happen on that one
 * player's client. The per-effect render handlers (in {@code shipwrecked}, {@code panic}, …) read
 * the same {@code static} fields they always did — but on a dedicated server those fields are now
 * written here, by a packet, instead of being shared in-JVM with the server thread.
 *
 * <p>{@code @OnlyIn(Dist.CLIENT)} plus packet-only invocation means a dedicated server never loads
 * this class or {@code Minecraft}.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = "flywheel_of_terror", value = {Dist.CLIENT})
public final class client_net {
   private client_net() {
   }

   public static void playSound(Network.PlaySoundPacket msg) {
      Player local = Minecraft.getInstance().player;
      if (local == null || msg.sound == null) {
         return;
      }

      SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(msg.sound);
      if (sound != null) {
         local.playSound(sound, msg.volume, msg.pitch);
      }
   }

   public static void fx(Network.FxPacket msg) {
      Player local = Minecraft.getInstance().player;

      switch (msg.action) {
         case Network.RED_WATER_ON:
            shipwrecked.red_water = true;
            break;
         case Network.RED_WATER_OFF:
            shipwrecked.red_water = false;
            break;
         case Network.BLACKOUT:
            panic.tics_of_black = msg.i;
            break;
         case Network.EYES:
            eye_intervention.event_in_process = true;
            eye_intervention.current_count_of_eyes = 0;
            eye_intervention.tics_to_next_eye = 1;
            eye_intervention.red_intense = 1.0F;
            break;
         case Network.APOC:
            apocalypsis_event.tics_of_event = msg.i;
            apocalypsis_event.red_intense = 0.5F;
            break;
         case Network.THUNDER_PUNCH:
            thunder_behind.tics_to_punch = msg.i;
            break;
         case Network.OHNO_FRAMES:
            oh_no_between_screens.tics_of_event = msg.i;
            break;
         case Network.CALL:
            paranoia.tics_of_call = 60;
            paranoia.current_call = msg.i;
            break;
         case Network.EXIST_TYPE:
            client_safe.existTerrorTypeChat(msg.s);
            break;
         case Network.EXIST_CLOSE:
            client_safe.existTerrorClose();
            break;
         case Network.GAMERULES_CAM:
            client_safe.gameRulesCameraTick();
            break;
         case Network.ALL_LOOK:
            if (local != null) {
               client_safe.allLookAtYouTick(local);
            }
            break;
         case Network.HE_IS_HERE_DIST:
            client_safe.heIsHereRenderDistance();
            break;
         case Network.CLEAR_CHAT:
            client_safe.paranoiaClearChat();
            break;
         case Network.SCREENSHOT:
            client_safe.darknetScreenshot();
            break;
         case Network.RESTORE_SENS:
            client_safe.restoreSensitivity(msg.f);
            break;
         case Network.SILENCE:
            something_wrong.tics_of_silence = msg.i;
            break;
         case Network.INDEP_ATTACK:
            if (local != null && Minecraft.getInstance().level != null) {
               Entity target = Minecraft.getInstance().level.getEntity(msg.i);
               if (target instanceof LivingEntity living) {
                  client_safe.independenceAttack(local, living);
               }
            }
            break;
         default:
      }
   }

   /**
    * Client-authoritative ticking of the one-client visual effects. The server only sends the
    * trigger ("blackout for 200 tics"); the countdown and frame animation run here so they stay in
    * step with the local render thread instead of a remote server tick.
    */
   @SubscribeEvent
   public static void clientTick(ClientTickEvent event) {
      if (event.phase != Phase.END) {
         return;
      }

      if (panic.tics_of_black > -2) {
         panic.tics_of_black--;
      }

      if (apocalypsis_event.tics_of_event > -2) {
         apocalypsis_event.tics_of_event--;
      }

      if (paranoia.tics_of_call > -4) {
         paranoia.tics_of_call--;
      }

      if (something_wrong.tics_of_silence > 0) {
         something_wrong.tics_of_silence--;
      }

      thunder_behind.tics_to_punch--;
      thunder_behind.tics_of_madness--;
      if (thunder_behind.tics_to_punch == 0) {
         thunder_behind.tics_of_madness = 40;
         Player local = Minecraft.getInstance().player;
         if (local != null) {
            local.playSound((SoundEvent)register_sounds.big_glitch.get(), 1.0F, 1.0F);
         }
      }

      if (eye_intervention.event_in_process) {
         eye_intervention.tics_to_next_eye--;
         if (eye_intervention.tics_to_next_eye <= 0) {
            eye_intervention.tics_to_next_eye = eye_intervention.constant_tics_to_next_eye;
            eye_intervention.current_count_of_eyes++;
         }
      }

      if (eye_intervention.end_music) {
         eye_intervention.end_music = false;
         Minecraft.getInstance().getSoundManager().stop();
      }

      if (oh_no_between_screens.tics_of_event >= 1) {
         oh_no_between_screens.tics_of_event--;
         if (oh_no_between_screens.cycle == 1) {
            oh_no_between_screens.current_frame++;
         } else {
            oh_no_between_screens.current_frame--;
         }

         if (oh_no_between_screens.current_frame == oh_no_between_screens.count_of_frames && oh_no_between_screens.cycle == 1) {
            oh_no_between_screens.cycle = 2;
         }

         if (oh_no_between_screens.current_frame == 1 && oh_no_between_screens.cycle == 2) {
            oh_no_between_screens.cycle = 1;
         }
      }
   }
}
