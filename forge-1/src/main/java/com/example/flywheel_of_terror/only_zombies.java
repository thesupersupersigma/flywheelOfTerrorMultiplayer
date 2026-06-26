package com.example.flywheel_of_terror;

import java.util.Random;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class only_zombies {
   public static Random random = new Random();

   @SubscribeEvent
   public static void delete_else(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof Monster monster && !(event.getEntity() instanceof Zombie) && !(event.getEntity() instanceof invalid)) {
         int i = random.nextInt(1, 51);
         if (i == 5) {
            event.setCanceled(true);
            double myX = event.getEntity().getX();
            double myY = event.getEntity().getY();
            double myZ = event.getEntity().getZ();
            Level level = event.getLevel();
            invalid zombie = new invalid((EntityType<? extends Zombie>)add_humans.invalid.get(), event.getEntity().level());
            zombie.setPos(myX, myY, myZ);
            level.addFreshEntity(zombie);
         } else {
            event.setCanceled(true);
            double myX = event.getEntity().getX();
            double myY = event.getEntity().getY();
            double myZ = event.getEntity().getZ();
            Level level = event.getLevel();
            Zombie zombie = new Zombie(level);
            zombie.setPos(myX, myY, myZ);
            level.addFreshEntity(zombie);
         }
      }
   }
}
