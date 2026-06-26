package com.example.flywheel_of_terror;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class add_humans {
   public static final DeferredRegister<EntityType<?>> add_human_zombie = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "flywheel_of_terror");
   public static final RegistryObject<EntityType<terror_pig>> terror_pig = add_human_zombie.register(
      "terror_pig", () -> Builder.of(terror_pig::new, MobCategory.CREATURE).sized(0.9F, 0.9F).build("terror_pig")
   );
   public static final RegistryObject<EntityType<terror_cow>> terror_cow = add_human_zombie.register(
      "terror_cow", () -> Builder.of(terror_cow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).build("terror_cow")
   );
   public static final RegistryObject<EntityType<terror_sheep>> terror_sheep = add_human_zombie.register(
      "terror_sheep", () -> Builder.of(terror_sheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F).build("terror_sheep")
   );
   public static final RegistryObject<EntityType<headless_villager>> headless_villager = add_human_zombie.register(
      "headless_villager", () -> Builder.of(headless_villager::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("headless_villager")
   );
   public static final RegistryObject<EntityType<true_me>> true_me = add_human_zombie.register(
      "true_me", () -> Builder.of(true_me::new, MobCategory.MONSTER).sized(0.6F, 1.8F).build("true_me")
   );
   public static final RegistryObject<EntityType<somewho>> somewho = add_human_zombie.register(
      "somewho", () -> Builder.of(somewho::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("somewho")
   );
   public static final RegistryObject<EntityType<fake_steve>> fake_steve = add_human_zombie.register(
      "fake_steve", () -> Builder.of(fake_steve::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("fake_steve")
   );
   public static final RegistryObject<EntityType<headless_steve>> headless_steve = add_human_zombie.register(
      "headless_steve", () -> Builder.of(headless_steve::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("headless_steve")
   );
   public static final RegistryObject<EntityType<faceless_villager>> faceless_villager = add_human_zombie.register(
      "faceless_villager", () -> Builder.of(faceless_villager::new, MobCategory.CREATURE).sized(0.6F, 1.8F).build("faceless_villager")
   );
   public static final RegistryObject<EntityType<faceless_cow>> faceless_cow = add_human_zombie.register(
      "faceless_cow", () -> Builder.of(faceless_cow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).build("faceless_cow")
   );
   public static final RegistryObject<EntityType<faceless_pig>> faceless_pig = add_human_zombie.register(
      "faceless_pig", () -> Builder.of(faceless_pig::new, MobCategory.CREATURE).sized(0.9F, 0.9F).build("faceless_pig")
   );
   public static final RegistryObject<EntityType<faceless_sheep>> faceless_sheep = add_human_zombie.register(
      "faceless_sheep", () -> Builder.of(faceless_sheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F).build("faceless_sheep")
   );
   public static final RegistryObject<EntityType<invalid>> invalid = add_human_zombie.register(
      "invalid", () -> Builder.of(invalid::new, MobCategory.MONSTER).sized(0.9F, 1.3F).build("invalid")
   );
   public static final RegistryObject<EntityType<wrong_sheep>> wrong_sheep = add_human_zombie.register(
      "wrong_sheep", () -> Builder.of(wrong_sheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F).build("wrong_sheep")
   );
   public static final RegistryObject<EntityType<wrong_cow>> wrong_cow = add_human_zombie.register(
      "wrong_cow", () -> Builder.of(wrong_cow::new, MobCategory.CREATURE).sized(0.9F, 1.3F).build("wrong_sheep")
   );
   public static final RegistryObject<EntityType<oh_no_behind>> oh_no_behind = add_human_zombie.register(
      "oh_no_behind", () -> Builder.of(oh_no_behind::new, MobCategory.CREATURE).sized(0.9F, 1.8F).build("oh_no_behind")
   );
   public static final RegistryObject<EntityType<oh_no>> oh_no = add_human_zombie.register(
      "oh_no", () -> Builder.of(oh_no::new, MobCategory.CREATURE).sized(0.9F, 1.8F).build("oh_no")
   );
   public static final RegistryObject<EntityType<oh_no_here>> oh_no_here = add_human_zombie.register(
      "oh_no_here", () -> Builder.of(oh_no_here::new, MobCategory.CREATURE).sized(0.9F, 1.8F).build("oh_no_here")
   );
   public static final RegistryObject<EntityType<oh_no_stalker>> oh_no_stalker = add_human_zombie.register(
      "oh_no_stalker", () -> Builder.of(oh_no_stalker::new, MobCategory.CREATURE).sized(0.9F, 1.8F).build("oh_no_stalker")
   );
   public static final RegistryObject<EntityType<invisible>> invisible = add_human_zombie.register(
      "invisible", () -> Builder.of(invisible::new, MobCategory.CREATURE).sized(0.9F, 1.8F).build("invisible")
   );
}
