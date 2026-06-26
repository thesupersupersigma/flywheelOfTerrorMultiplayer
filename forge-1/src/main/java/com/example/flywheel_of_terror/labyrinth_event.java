package com.example.flywheel_of_terror;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class labyrinth_event {
   public static boolean event_in_process = false;
   public static int tics_to_reload_music = 0;
   public static Random random = new Random();
   public static boolean in_lab = false;
   public static int state = 0;

   public static void build(ServerLevel serv, BlockPos pos, String name) {
      InputStream stream = labyrinth_event.class.getResourceAsStream("/data/flywheel_of_terror/structures/" + name + ".nbt");

      try {
         if (stream != null) {
            StructureTemplate template = new StructureTemplate();
            template.load(serv.registryAccess().lookupOrThrow(Registries.BLOCK), NbtIo.readCompressed(stream));
            template.placeInWorld(serv, pos, pos, new StructurePlaceSettings(), serv.random, 2);
            stream.close();
         }
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }
   }

   @SubscribeEvent
   public static void break_bedrock(BreakEvent event) {
      Player player = event.getPlayer();
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      BlockPos pos = new BlockPos(event.getPos());
      if (player.level().getBlockState(pos).getBlock() == Blocks.BEDROCK.defaultBlockState().getBlock()) {
         event.getPlayer().sendSystemMessage(Component.literal("§cYou're just a human being."));
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void build(PlayerTickEvent event) {
      Player player = event.player;
      if (!player.level().isClientSide()) {
         for (headless_villager vill : player.level().getEntitiesOfClass(headless_villager.class, player.getBoundingBox().inflate(5.0))) {
            CompoundTag global_tag2 = vill.getPersistentData();
            CompoundTag tag2 = global_tag2.getCompound("flywheel_of_terror");
            tag2.putInt("time_to_delete", tag2.getInt("time_to_delete") - 1);
            global_tag2.put("flywheel_of_terror", tag2);
            if (tag2.getInt("time_to_delete") <= 0) {
               vill.remove(RemovalReason.DISCARDED);
            }
         }
      }

      if (!player.level().isClientSide) {
         CompoundTag global_tag = player.getPersistentData();
         CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
         if (!tag.getBoolean("laby_builded") && event_in_process) {
            tag.putBoolean("laby_builded", true);

            for (double x = (double)(terror_continue.xxx - 20); x <= (double)(terror_continue.xxx + 20); x++) {
               for (double y = (double)(terror_continue.yyy - 1); y <= (double)(terror_continue.yyy + 2); y++) {
                  for (double z = (double)(terror_continue.zzz - 20); z <= (double)(terror_continue.zzz + 20); z++) {
                     String coordinates = (int)x + "," + (int)y + "," + (int)z;
                     BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                     if (!(player.level().getBlockState(pos).getBlock() instanceof ChestBlock)) {
                        tag.putBoolean(coordinates + "laby", true);
                     }
                  }
               }
            }
         }

         boolean labyblock = false;

         for (double x = player.getX() - 2.0; x <= player.getX() + 2.0; x++) {
            for (double y = player.getY() - 2.0; y <= player.getY() + 2.0; y++) {
               for (double zx = player.getZ() - 2.0; zx <= player.getZ() + 2.0; zx++) {
                  String coordinates = (int)x + "," + (int)y + "," + (int)zx;
                  if (tag.getBoolean(coordinates + "laby")) {
                     labyblock = true;
                  }
               }
            }
         }

         if (labyblock) {
            state = 1;
            in_lab = true;
            MobEffectInstance blind = new MobEffectInstance(MobEffects.BLINDNESS, 50, 10, false, true, false);
            player.addEffect(blind);
            if (tics_to_reload_music <= 0) {
               player.level()
                  .playSound(
                     null, player.getX(), player.getY(), player.getZ(), (SoundEvent)register_sounds.labyr.get(), SoundSource.PLAYERS, 1.0F, 1.0F
                  );
               tics_to_reload_music = 200;
            }

            tics_to_reload_music--;
            global_tag.put("flywheel_of_terror", tag);
            if (random.nextInt(1, 3000) == 41) {
               int i = random.nextInt(1, 6);
               switch (i) {
                  case 1:
                     player.level()
                        .playSound(
                           null,
                           player.getX(),
                           player.getY(),
                           player.getZ(),
                           (SoundEvent)register_sounds.atmosphere1.get(),
                           SoundSource.PLAYERS,
                           1.0F,
                           1.0F
                        );
                     break;
                  case 2:
                     player.level()
                        .playSound(
                           null,
                           player.getX(),
                           player.getY(),
                           player.getZ(),
                           (SoundEvent)register_sounds.atmosphere2.get(),
                           SoundSource.PLAYERS,
                           1.0F,
                           1.0F
                        );
                     break;
                  case 3:
                     player.level()
                        .playSound(
                           null,
                           player.getX(),
                           player.getY(),
                           player.getZ(),
                           (SoundEvent)register_sounds.atmosphere3.get(),
                           SoundSource.PLAYERS,
                           1.0F,
                           1.0F
                        );
                     break;
                  case 4:
                     player.level()
                        .playSound(
                           null,
                           player.getX(),
                           player.getY(),
                           player.getZ(),
                           (SoundEvent)register_sounds.atmosphere4.get(),
                           SoundSource.PLAYERS,
                           1.0F,
                           1.0F
                        );
                     break;
                  case 5:
                     player.level()
                        .playSound(
                           null,
                           player.getX(),
                           player.getY(),
                           player.getZ(),
                           (SoundEvent)register_sounds.atmosphere5.get(),
                           SoundSource.PLAYERS,
                           1.0F,
                           1.0F
                        );
               }
            }

            if (random.nextInt(1, 4800) == 4) {
               headless_villager villx = new headless_villager((EntityType<? extends Villager>)add_humans.headless_villager.get(), player.level());
               double xx = player.getLookAngle().x;
               double zz = player.getLookAngle().z;
               Vec3 pos = new Vec3(player.getX() + 2.0 * zz, player.getY(), player.getZ() + 2.0 * xx);
               villx.setPos(pos);
               CompoundTag global_tag2 = villx.getPersistentData();
               CompoundTag tag2 = global_tag2.getCompound("flywheel_of_terror");
               tag2.putInt("time_to_delete", 200);
               player.level().addFreshEntity(villx);
               global_tag2.put("flywheel_of_terror", tag2);
            }
         }

         if (state == 1 && !labyblock) {
            state = 0;
            in_lab = false;
            player.removeEffect(MobEffects.BLINDNESS);
            ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(null, null);
            if (player instanceof ServerPlayer serv) {
               serv.connection.send(packet);
            }

            tics_to_reload_music = 0;
         }

         for (double x = player.getX() - 3.0; x <= player.getX() + 3.0; x++) {
            for (double y = player.getY() - 1.0; y <= player.getY(); y++) {
               for (double zxx = player.getZ() - 3.0; zxx <= player.getZ() + 3.0; zxx++) {
                  String coordinates = (int)x + "," + (int)y + "," + (int)zxx;
                  if (tag.getBoolean(coordinates + "labyrchest")) {
                     tag.putBoolean(coordinates + "labyrchest", false);
                     BlockPos pos = new BlockPos((int)x, (int)y - 1, (int)zxx);
                     player.level().setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
                  }
               }
            }
         }
      }
   }
}
