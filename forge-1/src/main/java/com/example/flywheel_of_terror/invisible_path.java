package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class invisible_path {
   public static Random random = new Random();

   public static void do_event(Player player) {
      double x = player.getX() + (double)random.nextInt(-50, 50);
      double z = player.getZ() + (double)random.nextInt(-50, 50);
      double y = (double)player.level().getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z);
      Vec3 pos = new Vec3(x, y, z);
      invisible vanish = new invisible((EntityType<? extends PathfinderMob>)add_humans.invisible.get(), player.level());
      vanish.setPos(pos);
      player.level().addFreshEntity(vanish);
   }
}
