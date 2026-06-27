package com.example.flywheel_of_terror;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class terror_continue {
   // Phase 2: per-player state ("need_try", "near_maze", "tics_cooldown") now lives in the player's
   // NBT compound. Phase 3: the chest-close sound is sent as an S2C packet to the affected player.
   public static Random random = new Random();

   public static boolean near_maze(Player player) {
      return state.getBool(player, "near_maze");
   }

   public static void spawn_your_legacy(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putBoolean("legacy_was", true);
      global_tag.put("flywheel_of_terror", tag);
      Vec3 pos = get_center_oh_house(player);
      ItemStack leg = new ItemStack((ItemLike)add_items.your_legacy.get());
      ItemEntity itemEntity = new ItemEntity(player.level(), pos.x, pos.y, pos.z, leg);
      player.level().addFreshEntity(itemEntity);
   }

   public static void create_labyrinth(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      boolean chest_finded = false;

      for (double x = player.getX() - 30.0; x <= player.getX() + 30.0; x++) {
         for (double y = player.getY() - 10.0; y <= player.getY() + 10.0; y++) {
            for (double z = player.getZ() - 30.0; z <= player.getZ() + 30.0; z++) {
               BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
               BlockState current_block = player.level().getBlockState(pos);
               if (current_block.getBlock() instanceof ChestBlock && !chest_finded) {
                  chest_finded = true;
                  List<ItemStack> items = new ArrayList<>();
                  BlockEntity ches = player.level().getBlockEntity(pos);
                  if (ches instanceof ChestBlockEntity) {
                     ChestBlockEntity ches2 = (ChestBlockEntity)ches;
                     double xx = player.getX() + (double)random.nextInt(100, 300);
                     double zz = player.getZ() + (double)random.nextInt(-100, 300);
                     int xxx = (int)xx;
                     int zzz = (int)zz;
                     int yyy = (int)(player.getY() + 40.0);
                     boolean chest_empty = true;
                     ItemStack zap = new ItemStack((ItemLike)add_items.notice.get());
                     zap.setHoverName(Component.literal("your items " + (int)xx + " " + (int)(player.getY() + 40.0) + " " + (int)zz));

                     for (int i = 0; i < ches2.getContainerSize(); i++) {
                        if (ches2.getItem(i) != ItemStack.EMPTY) {
                           items.add(ches2.getItem(i));
                           ches2.setItem(i, zap);
                           ches2.setChanged();
                           chest_empty = false;
                        }
                     }

                     if (chest_empty) {
                        return;
                     }

                     Network.sound(player, SoundEvents.CHEST_CLOSE);
                     if (player.level() instanceof ServerLevel serv) {
                        BlockPos pos2 = new BlockPos(xxx - 20, yyy - 1, zzz - 20);
                        labyrinth_event.build(serv, pos2, "labyrinth_of_terror");
                     }

                     BlockPos pos2 = new BlockPos((int)xx, (int)player.getY() + 40, (int)zz);
                     tag.putInt("lab_x", (int)xx);
                     tag.putInt("lab_y", (int)player.getY() + 40);
                     tag.putInt("lab_z", (int)zz);
                     player.level().setBlock(pos2, Blocks.CHEST.defaultBlockState(), 3);
                     String coordinates2 = pos2.getX() + "," + pos2.getY() + "," + pos2.getZ();
                     tag.putBoolean(coordinates2 + "labyrchest", true);
                     BlockEntity lastches = player.level().getBlockEntity(pos2);
                     if (lastches instanceof ChestBlockEntity) {
                        ChestBlockEntity lastches2 = (ChestBlockEntity)lastches;

                        for (int ix = 0; ix < items.size(); ix++) {
                           lastches2.setItem(ix, items.get(ix));
                           lastches2.setChanged();
                        }
                     }

                     labyrinth_event.set_active(player, true);
                  }
               }
            }
         }
      }
   }

   public static void summon_true_me(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      tag.putBoolean("intruder_was", true);
      true_me me = new true_me((EntityType<? extends PathfinderMob>)add_humans.true_me.get(), player.level());
      me.setPos(get_center_oh_house(player));
      information.setTarget(me, player);
      player.level().addFreshEntity(me);
   }

   public static void place_plita(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");

      for (double x = player.getX() - 30.0; x <= player.getX() + 30.0; x++) {
         for (double y = player.getY() - 30.0; y <= player.getY() + 30.0; y++) {
            for (double z = player.getZ() - 30.0; z <= player.getZ() + 30.0; z++) {
               String coordinates = (int)x + "," + (int)y + "," + (int)z;
               if (tag.getBoolean(coordinates + "house")) {
               }

               BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
               BlockState current_block = player.level().getBlockState(pos);
               if (current_block.getBlock() instanceof DoorBlock && current_block.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
                  String coordinates2 = (int)x + 2 + "," + ((int)y - 1) + "," + (int)z;
                  String coordinates3 = (int)x - 2 + "," + ((int)y - 1) + "," + (int)z;
                  String coordinates4 = (int)x + "," + ((int)y - 1) + "," + ((int)z + 2);
                  String coordinates5 = (int)x + "," + ((int)y - 1) + "," + ((int)z - 2);
                  BlockPos plita1 = new BlockPos((int)x - 1, (int)y, (int)z);
                  BlockPos plita2 = new BlockPos((int)x + 1, (int)y, (int)z);
                  BlockPos plita3 = new BlockPos((int)x, (int)y, (int)z - 1);
                  BlockPos plita4 = new BlockPos((int)x, (int)y, (int)z + 1);
                  if (!tag.getBoolean(coordinates2 + "house") && player.level().getBlockState(plita1).getBlock() == Blocks.AIR) {
                     player.level().setBlock(new BlockPos((int)x - 1, (int)y - 2, (int)z), Blocks.TNT.defaultBlockState(), 3);
                     player.level().setBlock(plita1, Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), 3);
                     tag.putBoolean("plita", true);
                     global_tag.put("flywheel_of_terror", tag);
                  } else if (!tag.getBoolean(coordinates3 + "house") && player.level().getBlockState(plita2).getBlock() == Blocks.AIR) {
                     player.level().setBlock(new BlockPos((int)x + 1, (int)y - 2, (int)z), Blocks.TNT.defaultBlockState(), 3);
                     player.level().setBlock(plita2, Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), 3);
                     tag.putBoolean("plita", true);
                     global_tag.put("flywheel_of_terror", tag);
                  } else if (!tag.getBoolean(coordinates4 + "house") && player.level().getBlockState(plita3).getBlock() == Blocks.AIR) {
                     player.level().setBlock(new BlockPos((int)x, (int)y - 2, (int)z - 1), Blocks.TNT.defaultBlockState(), 3);
                     player.level().setBlock(plita3, Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), 3);
                     tag.putBoolean("plita", true);
                     global_tag.put("flywheel_of_terror", tag);
                  } else if (!tag.getBoolean(coordinates5 + "house") && player.level().getBlockState(plita4).getBlock() == Blocks.AIR) {
                     player.level().setBlock(new BlockPos((int)x, (int)y - 2, (int)z + 1), Blocks.TNT.defaultBlockState(), 3);
                     player.level().setBlock(plita4, Blocks.OAK_PRESSURE_PLATE.defaultBlockState(), 3);
                     tag.putBoolean("plita", true);
                     global_tag.put("flywheel_of_terror", tag);
                  }
               }
            }
         }
      }
   }

   public static List<BlockPos> get_blocks_oh_house(Player player) {
      CompoundTag global_tag = player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      int house_x = tag.getInt("house_X");
      int house_y = tag.getInt("house_Y");
      int house_z = tag.getInt("house_Z");
      List<BlockPos> poses = new ArrayList<>();

      for (int x = house_x - 50; x <= house_x + 50; x++) {
         for (int y = house_y - 10; y <= house_y + 10; y++) {
            for (int z = house_z - 50; z <= house_z + 50; z++) {
               BlockPos pos = new BlockPos(x, y, z);
               String coordinates = information.getCoordinates((double)x, (double)y, (double)z);
               if (tag.getBoolean(coordinates + "house")) {
                  poses.add(pos);
               }
            }
         }
      }

      return poses;
   }

   public static Vec3 get_center_oh_house(Player player) {
      List<BlockPos> pos = get_blocks_oh_house(player);
      int final_x = 0;
      int final_y = 0;
      int final_z = 0;

      for (BlockPos i : pos) {
         final_x += i.getX();
         final_y += i.getY();
         final_z += i.getZ();
      }

      int o = pos.size();
      final_x /= o;
      final_y /= o;
      final_z /= o;
      return new Vec3((double)final_x, (double)final_y, (double)final_z);
   }

   @SubscribeEvent
   public static void return_home(PlayerTickEvent event) {
      CompoundTag global_tag = event.player.getPersistentData();
      CompoundTag tag = global_tag.getCompound("flywheel_of_terror");
      Player player = event.player;
      boolean client = player.level().isClientSide();
      // Cooldown is now server-authoritative (was decremented client-side via a shared static).
      if (!client && terror_beginning.away_house(player)) {
         tag.putInt("tics_cooldown", tag.getInt("tics_cooldown") - 1);
      }

      if (!terror_beginning.away_house(player)
         && tag.getBoolean("need_try")
         && tag.getBoolean("builded")
         && !player.level().isClientSide()
         && tag.getInt("tics_cooldown") <= 0) {
         tag.putInt("tics_cooldown", 8000);
         tag.putBoolean("need_try", false);
         int event_number = random.nextInt(1, 11);
         if (event_number == 1 && !tag.getBoolean("plita")) {
            place_plita(player);
         }

         if (event_number == 2 && !tag.getBoolean("intruder_was")) {
            summon_true_me(player);
         }

         if (event_number == 3 && !tag.getBoolean("laby_builded")) {
            create_labyrinth(player);
         }

         if (event_number == 4 && !tag.getBoolean("legacy_was")) {
            spawn_your_legacy(player);
         }
      }

      if (!client) {
         if (terror_beginning.away_house(player) && player.tickCount % 100 == 0 && tag.getInt("tics_cooldown") <= 0) {
            tag.putBoolean("need_try", true);
         }

         int x = tag.getInt("lab_x");
         int z = tag.getInt("lab_z");
         if ((double)(x - 40) < player.getX()
            && (double)(x + 40) > player.getX()
            && (double)(z - 40) < player.getX()
            && (double)(z + 40) > player.getX()) {
            tag.putBoolean("near_maze", true);
         } else {
            tag.putBoolean("near_maze", false);
         }

         global_tag.put("flywheel_of_terror", tag);
      }
   }
}
