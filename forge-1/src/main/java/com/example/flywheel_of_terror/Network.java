package com.example.flywheel_of_terror;

import java.util.Optional;
import java.util.function.Supplier;
import com.example.flywheel_of_terror.client.client_net;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Phase 3 networking layer. Replaces the original mod's "set a static flag on the server and read it
 * on the client in the same JVM" pattern — which only worked in single-player — with real S2C / C2S
 * packets so each connected client gets only its own horror effects on a dedicated server.
 *
 * <p>All client-touching work is funnelled through {@link client_net} via
 * {@link DistExecutor#unsafeRunWhenOn} so a dedicated server never loads {@code Minecraft}.
 */
public final class Network {
   private static final String PROTOCOL = "1";
   public static SimpleChannel CHANNEL;
   private static int packet_id = 0;

   private Network() {
   }

   // ---- FX action ids (S2C FxPacket.action) -------------------------------------------------
   public static final int RED_WATER_ON = 1;   // shipwrecked red-water shader on
   public static final int RED_WATER_OFF = 2;  // shipwrecked red-water shader off
   public static final int BLACKOUT = 3;       // panic blackout shader (i = tics)
   public static final int EYES = 4;           // eye_intervention overlay start
   public static final int APOC = 5;           // apocalypsis blood-sky shader (i = tics)
   public static final int THUNDER_PUNCH = 6;  // thunder_behind "turn around" madness (i = tics)
   public static final int OHNO_FRAMES = 7;    // oh_no_between_screens overlay (i = tics)
   public static final int CALL = 8;           // paranoia incoming-call image (i = current_call)
   public static final int EXIST_TYPE = 9;     // exist_terror chat typing (s = context so far)
   public static final int EXIST_CLOSE = 10;   // exist_terror finished: close container + restore
   public static final int GAMERULES_CAM = 11; // game_rules: force first-person + cap render dist
   public static final int ALL_LOOK = 12;      // all_look_at_you: third-person + mobs stare
   public static final int HE_IS_HERE_DIST = 13; // he_is_here: raise render distance
   public static final int CLEAR_CHAT = 14;    // paranoia: clear chat for the "call"
   public static final int SCREENSHOT = 15;    // fake_darknet_access: grab screenshot + message
   public static final int RESTORE_SENS = 16;  // information: restore saved mouse sensitivity (f)
   public static final int INDEP_ATTACK = 17;  // independence: client-side attack swing (i = entityId)
   public static final int SILENCE = 18;        // something_wrong: mute sounds for i client tics

   public static void register() {
      CHANNEL = NetworkRegistry.ChannelBuilder
         .named(new ResourceLocation("flywheel_of_terror", "main"))
         .networkProtocolVersion(() -> PROTOCOL)
         .clientAcceptedVersions(PROTOCOL::equals)
         .serverAcceptedVersions(PROTOCOL::equals)
         .simpleChannel();

      CHANNEL.registerMessage(
         packet_id++, PlaySoundPacket.class, PlaySoundPacket::encode, PlaySoundPacket::new, PlaySoundPacket::handle,
         Optional.of(NetworkDirection.PLAY_TO_CLIENT)
      );
      CHANNEL.registerMessage(
         packet_id++, FxPacket.class, FxPacket::encode, FxPacket::new, FxPacket::handle,
         Optional.of(NetworkDirection.PLAY_TO_CLIENT)
      );
      CHANNEL.registerMessage(
         packet_id++, ScreenStatePacket.class, ScreenStatePacket::encode, ScreenStatePacket::new, ScreenStatePacket::handle,
         Optional.of(NetworkDirection.PLAY_TO_SERVER)
      );
   }

   // ---- send helpers ------------------------------------------------------------------------

   private static void toClient(Player player, Object msg) {
      if (player instanceof ServerPlayer serv) {
         CHANNEL.send(PacketDistributor.PLAYER.with(() -> serv), msg);
      }
   }

   /** Play a registered sound on this player's client only (replaces the old sound_must_be flags). */
   public static void sound(Player player, SoundEvent sound, float volume, float pitch) {
      if (sound != null) {
         toClient(player, new PlaySoundPacket(ForgeRegistries.SOUND_EVENTS.getKey(sound), volume, pitch));
      }
   }

   public static void sound(Player player, SoundEvent sound) {
      sound(player, sound, 1.0F, 1.0F);
   }

   /** Fire a one-shot client effect (shader/overlay/camera/action) on this player's client only. */
   public static void fx(Player player, int action) {
      toClient(player, new FxPacket(action, 0, 0.0F, ""));
   }

   public static void fx(Player player, int action, int i) {
      toClient(player, new FxPacket(action, i, 0.0F, ""));
   }

   public static void fx(Player player, int action, float f) {
      toClient(player, new FxPacket(action, 0, f, ""));
   }

   public static void fx(Player player, int action, String s) {
      toClient(player, new FxPacket(action, 0, 0.0F, s));
   }

   /** C2S: client informs the server which GUI it currently has open. */
   public static void sendScreenState(boolean inventory_open, boolean crafting_open) {
      CHANNEL.sendToServer(new ScreenStatePacket(inventory_open, crafting_open));
   }

   // ---- packets -----------------------------------------------------------------------------

   public static class PlaySoundPacket {
      public final ResourceLocation sound;
      public final float volume;
      public final float pitch;

      public PlaySoundPacket(ResourceLocation sound, float volume, float pitch) {
         this.sound = sound;
         this.volume = volume;
         this.pitch = pitch;
      }

      public PlaySoundPacket(FriendlyByteBuf buf) {
         this.sound = buf.readResourceLocation();
         this.volume = buf.readFloat();
         this.pitch = buf.readFloat();
      }

      public void encode(FriendlyByteBuf buf) {
         buf.writeResourceLocation(this.sound == null ? new ResourceLocation("empty", "empty") : this.sound);
         buf.writeFloat(this.volume);
         buf.writeFloat(this.pitch);
      }

      public void handle(Supplier<NetworkEvent.Context> ctx) {
         ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client_net.playSound(this)));
         ctx.get().setPacketHandled(true);
      }
   }

   public static class FxPacket {
      public final int action;
      public final int i;
      public final float f;
      public final String s;

      public FxPacket(int action, int i, float f, String s) {
         this.action = action;
         this.i = i;
         this.f = f;
         this.s = s == null ? "" : s;
      }

      public FxPacket(FriendlyByteBuf buf) {
         this.action = buf.readVarInt();
         this.i = buf.readVarInt();
         this.f = buf.readFloat();
         this.s = buf.readUtf();
      }

      public void encode(FriendlyByteBuf buf) {
         buf.writeVarInt(this.action);
         buf.writeVarInt(this.i);
         buf.writeFloat(this.f);
         buf.writeUtf(this.s);
      }

      public void handle(Supplier<NetworkEvent.Context> ctx) {
         ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client_net.fx(this)));
         ctx.get().setPacketHandled(true);
      }
   }

   public static class ScreenStatePacket {
      public final boolean inventory_open;
      public final boolean crafting_open;

      public ScreenStatePacket(boolean inventory_open, boolean crafting_open) {
         this.inventory_open = inventory_open;
         this.crafting_open = crafting_open;
      }

      public ScreenStatePacket(FriendlyByteBuf buf) {
         this.inventory_open = buf.readBoolean();
         this.crafting_open = buf.readBoolean();
      }

      public void encode(FriendlyByteBuf buf) {
         buf.writeBoolean(this.inventory_open);
         buf.writeBoolean(this.crafting_open);
      }

      public void handle(Supplier<NetworkEvent.Context> ctx) {
         NetworkEvent.Context context = ctx.get();
         context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
               state.putBool(sender, "inv_open", this.inventory_open);
               state.putBool(sender, "crafting_open", this.crafting_open);
            }
         });
         context.setPacketHandled(true);
      }
   }
}
