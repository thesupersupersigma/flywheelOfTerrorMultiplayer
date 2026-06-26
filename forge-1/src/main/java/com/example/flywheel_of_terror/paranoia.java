package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiEvent.Post;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import com.example.flywheel_of_terror.client.client_safe;

@EventBusSubscriber
public class paranoia {
   public static Random random = new Random();
   public static boolean steps_must_be = false;
   public static boolean force_open_door_must_be = false;
   public static int tics = 0;
   public static int min_time = 300;
   public static int max_time = 600;
   public static int time_to_event = random.nextInt(min_time, max_time);
   public static int ii = 6;
   public static boolean sleep = false;
   public static int tics_to_door = -80;
   public static int tics_of_call = -4;
   public static int current_call = 1;
   public static int tics_from_last_event = 0;
   public static int tics_without_events = -2;
   public static int seconds_to_undefined_noise = -random.nextInt(700, 1400);
   public static int tics_of_steps = -1;
   public static boolean call_sound_must_be = false;
   public static final String seconds_to_call_nbt = "second_to_call";

   @SubscribeEvent
   public static void silence(PlaySoundEvent event) {
      if (event.getSound() != null) {
         if (event.getSound().getLocation().getPath().contains("entity")
            && !event.getSound().getLocation().getPath().contains("player")
            && !event.getSound().getLocation().getPath().contains("bolt")) {
            event.setSound(null);
            return;
         }

         if (event.getSound().getLocation().getPath().contains("door")) {
            event.setSound(null);
            return;
         }

         if (event.getSound().getLocation().getPath().contains("lava")) {
            event.setSound(null);
            return;
         }
      }
   }

   @SubscribeEvent
   public static void undefined_noises(PlayerTickEvent event) {
      Player player = event.player;
      if (player.tickCount % 40 == 0) {
         seconds_to_undefined_noise--;
      }

      if (player.level().isClientSide()) {
         tics_of_steps--;
         if (tics_of_steps > 0 && tics_of_steps % 20 == 0) {
            player.playSound(SoundEvents.GRASS_STEP);
         }

         if (seconds_to_undefined_noise <= 0) {
            seconds_to_undefined_noise = random.nextInt(700, 1400);
            int i = random.nextInt(1, 9);
            switch (i) {
               case 1:
                  player.playSound(SoundEvents.GRASS_PLACE);
                  break;
               case 2:
                  player.playSound(SoundEvents.CHERRY_WOOD_PLACE);
                  break;
               case 3:
                  player.playSound(SoundEvents.WOOD_BREAK);
                  break;
               case 4:
                  player.playSound(SoundEvents.STONE_PLACE);
                  break;
               case 5:
                  player.playSound(SoundEvents.STONE_BREAK);
                  break;
               case 6:
                  player.playSound(SoundEvents.PLAYER_ATTACK_CRIT);
                  break;
               case 7:
                  tics_of_steps = random.nextInt(60, 120);
                  break;
               case 8:
                  player.playSound(SoundEvents.GRASS_BREAK);
            }
         }
      }
   }

   @SubscribeEvent
   public static void spawn_wrong(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof Animal shish && !(event.getEntity() instanceof wrong_sheep) && random.nextInt(1, 200) == 122) {
         int i = random.nextInt(1, 3);
         switch (i) {
            case 1:
               event.setCanceled(true);
               wrong_sheep bro = new wrong_sheep((EntityType<? extends Sheep>)add_humans.wrong_sheep.get(), event.getLevel());
               Vec3 pos = new Vec3(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ());
               bro.setPos(pos);
               bro.setSheared(false);
               event.getLevel().addFreshEntity(bro);
               break;
            case 2:
               event.setCanceled(true);
               wrong_cow bro2 = new wrong_cow((EntityType<? extends Cow>)add_humans.wrong_cow.get(), event.getLevel());
               Vec3 pos2 = new Vec3(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ());
               bro2.setPos(pos2);
               event.getLevel().addFreshEntity(bro2);
         }
      }
   }

   @SubscribeEvent
   public static void view(Post event) {
      if (tics_of_call > 0) {
         time_to_event++;
         int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
         int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
         event.getGuiGraphics()
            .blit(
               new ResourceLocation("flywheel_of_terror", "textures/calls/call" + current_call + ".png"), 0, 0, width, height, 0.0F, 0.0F, 1080, 608, 1080, 608
            );
         Minecraft.getInstance().setScreen(null);
      }
   }

   @SubscribeEvent
   public static void serv(ServerTickEvent event) {
      tics_of_call--;
   }

   public static void do_a_call(Player player) {
      tics_of_call = 60;
      double x = player.getX() + (double)random.nextFloat(-80.0F, 80.0F);
      double z = player.getZ() + (double)random.nextFloat(-80.0F, 80.0F);
      int y = player.level().getHeight(Types.WORLD_SURFACE, (int)x, (int)z) + 1;
      player.teleportTo(x, (double)y, z);
      call_sound_must_be = true;
      set_seconds_to_call(player, 300);
      current_call = random.nextInt(1, 4);
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client_safe.paranoiaClearChat());
   }

   @SubscribeEvent
   public static void sounds(PlayerTickEvent event) {
      Player player = event.player;
      boolean client = player.level().isClientSide();
      if (call_sound_must_be && client) {
         player.playSound((SoundEvent)register_sounds.lost_call.get(), 1.0F, 1.0F);
         call_sound_must_be = false;
      }
   }

   public static void set_seconds_to_call(Player player, int seconds) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putInt("second_to_call", seconds);
      global_tag.put("flywheel_of_terror", tag);
   }

   public static int get_seconds_to_call(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      return tag.getInt("second_to_call");
   }

   @SubscribeEvent
   public static void try_sleep(PlayerSleepInBedEvent event) {
      if (random.nextInt(1, 16) == 4) {
         sleep = true;
         tics_to_door = 80;
      }
   }

   @SubscribeEvent
   public static void wakeup(PlayerWakeUpEvent event) {
      sleep = false;
   }

   @SubscribeEvent
   public static void pkm(RightClickBlock event) {
      Player player = event.getEntity();
      BlockState block = player.level().getBlockState(event.getPos());
      if (block.getBlock() instanceof DoorBlock && random.nextInt(1, 100) == 22) {
         force_open_door_must_be = true;
      }
   }

   @SubscribeEvent
   public static void pkm2(RightClickBlock event) {
      Player player = event.getEntity();
      if (player.level().getBlockEntity(event.getPos()) instanceof ChestBlockEntity ches && random.nextInt(1, 15) == 5) {
         event.setCanceled(true);
         ches.startOpen(player);
      }
   }

   @SubscribeEvent
   public static void start_break(BreakSpeed event) {
      tics--;
      if (random.nextInt(1, 10000) == 1 && tics <= 0) {
         steps_must_be = true;
         tics = 10000;
      }
   }

   @SubscribeEvent
   public static void block_event(EntityPlaceEvent event) {
      tics_without_events = 200;
      if (random.nextInt(1, 120) == 5) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void corrupted_attack(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof Player player && random.nextInt(1, 200) == 1) {
         information.play_sound_at_server(player, (SoundEvent)register_sounds.corrupted_kill.get(), false);
      }

      if (event.getSource().getEntity() instanceof Player player && event.getEntity() instanceof wrong_sheep shish) {
         do_a_call(player);
         shish.remove(RemovalReason.DISCARDED);
      }

      if (event.getSource().getEntity() instanceof Player player && event.getEntity() instanceof oh_no_behind he) {
         he.remove(RemovalReason.DISCARDED);
      }
   }

   @SubscribeEvent
   public static void corrupted_break(BreakEvent event) {
      if (random.nextInt(1, 300) == 1) {
         information.play_sound_at_server(event.getPlayer(), (SoundEvent)register_sounds.corrupted_break.get(), false);
      }
   }

   @SubscribeEvent
   public static void check_weak(PlayerTickEvent event) {
      Player player = event.player;
      CompoundTag global_tag = event.player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      if (player.level().isClientSide()) {
         tics_from_last_event--;
         tics_without_events--;
         if (player.tickCount % 40 == 0 && terror_beginning.away_house && !labyrinth_event.in_lab && !terror_beginning.his_hunt) {
            time_to_event--;
         }

         if (force_open_door_must_be) {
            force_open_door_must_be = false;
            player.playSound((SoundEvent)register_sounds.force_open.get(), 1.0F, 1.0F);
         }

         if (steps_must_be) {
            player.playSound((SoundEvent)register_sounds.steps1.get(), 1.0F, 1.0F);
            steps_must_be = false;
         }
      } else {
         if (player.getY() < 55.0 && player.tickCount % 40 == 0) {
            set_seconds_to_call(player, get_seconds_to_call(player) - 1);
         }

         if (get_seconds_to_call(player) <= 0) {
            do_a_call(player);
         }
      }

      if (time_to_event <= 0 && terror_beginning.away_house && !labyrinth_event.in_lab && !player.level().isClientSide() && tics_without_events <= 0) {
         int i = random.nextInt(1, 27);
         switch (i) {
            case 1:
               if (tag.getInt("tool_break") < 1) {
                  tool_break.event_in_process = true;
                  System.out.println("must be event");
                  tag.putInt("tool_break", tag.getInt("tool_break") + 1);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 2:
               if (tag.getInt("sound_heart") < 1) {
                  sound_heart.event_in_process = true;
                  System.out.println("must be event");
                  tag.putInt("sound_heart", tag.getInt("sound_hearts") + 2);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 3:
               if (tag.getInt("sound_knife_attack") < 1) {
                  sound_knife_attack.event_in_process = true;
                  tag.putInt("sound_knife_attack", tag.getInt("sound_knife_attack") + 2);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 4:
               if (tag.getInt("circle_christ") < 1) {
                  circle_christ.event_in_process = true;
                  System.out.println("must be event");
                  tag.putInt("circle_christ", tag.getInt("circle_christ") + 1);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 5:
               if (tag.getInt("fire_steps") < 1) {
                  fire_steps.tics_of_event = random.nextInt(300, 500);
                  System.out.println("must be event");
                  tag.putInt("fire_steps", tag.getInt("fire_steps") + 1);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 6:
               if (tag.getInt("somewho") >= 1) {
                  break;
               }

               somewho copy = new somewho((EntityType<? extends PathfinderMob>)add_humans.somewho.get(), player.level());
               Vec3 pos = new Vec3(
                  player.getX() - player.getLookAngle().x * 10.0, player.getY(), player.getZ() - player.getLookAngle().z * 10.0
               );
               copy.setPos(pos);
               player.level().addFreshEntity(copy);

               for (double xxx = pos.x() - 1.0; xxx <= pos.x + 1.0; xxx++) {
                  for (double yyy = pos.y; yyy <= pos.y + 2.0; yyy++) {
                     for (double zzz = pos.z - 1.0; zzz <= pos.z + 1.0; zzz++) {
                        BlockPos area = new BlockPos((int)xxx, (int)yyy, (int)zzz);
                        player.level().destroyBlock(area, true, player);
                     }
                  }
               }

               time_to_event = random.nextInt(min_time, max_time);
               tics_from_last_event = 3000;
               break;
            case 7:
               if (tag.getInt("periferia") < 1) {
                  periferia.event_in_process = true;
                  tag.putInt("periferia", tag.getInt("periferia") + 1);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 8:
               if (tag.getInt("thunder_behind") < 2) {
                  thunder_behind.event_in_process = true;
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 9:
               if (tag.getInt("forge_revenge") < 1) {
                  forge_revenge.event_in_process = true;
                  tag.putInt("forge_revenge", tag.getInt("forge_revenge") + 1);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 10:
               if (tag.getInt("panic") < 1) {
                  panic.event_in_process = true;
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 11:
               if (tag.getInt("notice_in_inventory") < 1) {
                  notice_in_inventory.event_in_process = true;
                  time_to_event = random.nextInt(min_time, max_time);
                  tag.putInt("notice_in_inventory", tag.getInt("notice_in_inventory") + 1);
                  tics_from_last_event = 3000;
               }
               break;
            case 12:
               if (remove_entities.tics_without_life < 0 && tag.getInt("remove_entities") < 1) {
                  remove_entities.tics_without_life = 4800;
                  time_to_event = random.nextInt(min_time, max_time);
                  tag.putInt("remove_entities", tag.getInt("remove_entities") + 1);
                  break;
               }

               time_to_event = 1;
               break;
            case 13:
               if (tag.getInt("oh_no_behind") < 1) {
                  oh_no_behind oh = new oh_no_behind((EntityType<? extends PathfinderMob>)add_humans.oh_no_behind.get(), player.level());
                  double x = player.getX() - player.getLookAngle().x * 2.0;
                  double y = player.getY();
                  double z = player.getZ() - player.getLookAngle().z * 2.0;
                  oh.setPos(new Vec3(x, y, z));
                  player.level().addFreshEntity(oh);
                  tag.putInt("oh_no_behind", tag.getInt("oh_no_behind") + 1);
               }
               break;
            case 14:
               if (tag.getInt("all_look_at_you") < 1) {
                  all_look_at_you.do_event(player);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
                  tag.putInt("all_look_at_you", 1);
               }
               break;
            case 15:
               if (tag.getInt("oh_no_stalker_event") < 1) {
                  oh_no_stalker_event.do_event(player);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 16:
               if (tag.getInt("family") < 1) {
                  family.do_event(player);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 17:
               if (tag.getInt("invisible") < 1) {
                  invisible_path.do_event(player);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 18:
               if (tag.getInt("fake_darknet_access") < 1) {
                  fake_darknet_access.set_wait_situation(player, true);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 19:
               if (!tag.getBoolean("angel_sound")) {
                  System.out.println("angel");
                  angel_sound_event.event_in_process = true;
                  time_to_event = random.nextInt(min_time, max_time);
                  tag.putBoolean("angel_sound", true);
                  global_tag.put("flywheel_of_terror", tag);
                  tics_from_last_event = 3000;
               }
               break;
            case 20:
               if (!tag.getBoolean("paral")) {
                  tag.putBoolean("wait_paralysis", true);
                  time_to_event = random.nextInt(min_time, max_time);
                  global_tag.put("flywheel_of_terror", tag);
                  tics_from_last_event = 3000;
               }
               break;
            case 21:
               if (!tag.getBoolean("exist_terror")) {
                  System.out.println("exist");
                  exist_terror_event.event_in_process = true;
                  time_to_event = random.nextInt(min_time, max_time);
                  tag.putBoolean("exist_terror", true);
                  global_tag.put("flywheel_of_terror", tag);
                  tics_from_last_event = 3000;
               }
               break;
            case 22:
               if (!tag.getBoolean("below")) {
                  System.out.println("below");
                  below_event.event_in_process = true;
                  time_to_event = random.nextInt(min_time, max_time);
                  global_tag.put("flywheel_of_terror", tag);
                  tics_from_last_event = 3000;
               }
               break;
            case 23:
               if (!tag.getBoolean("apoc")) {
                  apocalypsis_event.event_in_process = true;
                  time_to_event = random.nextInt(min_time, max_time);
                  global_tag.put("flywheel_of_terror", tag);
                  tics_from_last_event = 3000;
               }
               break;
            case 24:
               if (!he_is_here_event.get_event_was(player)) {
                  he_is_here_event.do_event(player);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 25:
               if (!eye_intervention.get_event_was(player)) {
                  eye_intervention.do_event(player);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
               break;
            case 26:
               if (!oh_no_between_screens.get_event_was(player)) {
                  oh_no_between_screens.do_event(player);
                  time_to_event = random.nextInt(min_time, max_time);
                  tics_from_last_event = 3000;
               }
         }

         global_tag.put("flywheel_of_terror", tag);
         ii++;
      }

      if (random.nextInt(1, 400) == 54
         && player.tickCount % 40 == 0
         && (information.current_screen instanceof InventoryScreen || information.current_screen instanceof CraftingScreen)) {
         player.playSound((SoundEvent)register_sounds.breath1.get(), 1.0F, 1.0F);
      }

      if (!player.level().isClientSide() && sleep) {
         tics_to_door--;
         if (tics_to_door == 0) {
            for (double x = player.getX() - 10.0; x <= player.getX() + 10.0; x++) {
               for (double y = player.getY() - 10.0; y <= player.getY() + 10.0; y++) {
                  for (double z = player.getZ() - 10.0; z <= player.getZ() + 10.0; z++) {
                     BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
                     if (player.level().getBlockState(pos).getBlock() instanceof DoorBlock) {
                        player.level().destroyBlock(pos, true, player);
                        force_open_door_must_be = true;
                     }
                  }
               }
            }

            sleep = false;
            tics_to_door = -32;
         }
      }
   }
}
