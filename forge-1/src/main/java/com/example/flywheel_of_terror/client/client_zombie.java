package com.example.flywheel_of_terror.client;

import com.example.flywheel_of_terror.add_humans;
import com.example.flywheel_of_terror.client.renderer.faceless_cow_renderer;
import com.example.flywheel_of_terror.client.renderer.faceless_pig_renderer;
import com.example.flywheel_of_terror.client.renderer.faceless_sheep_renderer;
import com.example.flywheel_of_terror.client.renderer.faceless_villager_renderer;
import com.example.flywheel_of_terror.client.renderer.fake_steve_renderer;
import com.example.flywheel_of_terror.client.renderer.headless_steve_renderer;
import com.example.flywheel_of_terror.client.renderer.headless_villager_renderer;
import com.example.flywheel_of_terror.client.renderer.invalid_renderer;
import com.example.flywheel_of_terror.client.renderer.invisible_renderer;
import com.example.flywheel_of_terror.client.renderer.oh_no_behind_renderer;
import com.example.flywheel_of_terror.client.renderer.oh_no_here_renderer;
import com.example.flywheel_of_terror.client.renderer.oh_no_renderer;
import com.example.flywheel_of_terror.client.renderer.oh_no_stalker_renderer;
import com.example.flywheel_of_terror.client.renderer.somewho_renderer;
import com.example.flywheel_of_terror.client.renderer.terror_cow_renderer;
import com.example.flywheel_of_terror.client.renderer.terror_pig_renderer;
import com.example.flywheel_of_terror.client.renderer.terror_sheep_renderer;
import com.example.flywheel_of_terror.client.renderer.true_me_renderer;
import com.example.flywheel_of_terror.client.renderer.wrong_cow_renderer;
import com.example.flywheel_of_terror.client.renderer.wrong_sheep_renderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class client_zombie {
   @SubscribeEvent
   public static void registerRenderers(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)add_humans.terror_pig.get(), terror_pig_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.terror_cow.get(), terror_cow_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.terror_sheep.get(), terror_sheep_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.headless_villager.get(), headless_villager_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.true_me.get(), true_me_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.somewho.get(), somewho_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.fake_steve.get(), fake_steve_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.headless_steve.get(), headless_steve_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.faceless_pig.get(), faceless_pig_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.faceless_cow.get(), faceless_cow_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.faceless_sheep.get(), faceless_sheep_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.faceless_villager.get(), faceless_villager_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.invalid.get(), invalid_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.wrong_sheep.get(), wrong_sheep_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.wrong_cow.get(), wrong_cow_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.oh_no_behind.get(), oh_no_behind_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.oh_no.get(), oh_no_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.oh_no_here.get(), oh_no_here_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.oh_no_stalker.get(), oh_no_stalker_renderer::new);
      event.registerEntityRenderer((EntityType)add_humans.invisible.get(), invisible_renderer::new);
   }
}
