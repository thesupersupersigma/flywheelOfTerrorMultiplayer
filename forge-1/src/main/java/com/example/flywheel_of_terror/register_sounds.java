package com.example.flywheel_of_terror;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(
   modid = "flywheel_of_terror",
   bus = Bus.FORGE
)
public class register_sounds {
   public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "flywheel_of_terror");
   public static final RegistryObject<SoundEvent> angel_sound = SOUND_EVENTS.register(
      "angel_sound", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "angel_sound"))
   );
   public static final RegistryObject<SoundEvent> paralysis = SOUND_EVENTS.register(
      "paralysis", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "paralysis"))
   );
   public static final RegistryObject<SoundEvent> crowd_madness = SOUND_EVENTS.register(
      "crowd_madness", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "crowd_madness"))
   );
   public static final RegistryObject<SoundEvent> error = SOUND_EVENTS.register(
      "error", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "error"))
   );
   public static final RegistryObject<SoundEvent> heart_attack = SOUND_EVENTS.register(
      "heart_attack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "heart_attack"))
   );
   public static final RegistryObject<SoundEvent> knife_attack = SOUND_EVENTS.register(
      "knife_attack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "knife_attack"))
   );
   public static final RegistryObject<SoundEvent> christ_spawn = SOUND_EVENTS.register(
      "christ_spawn", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "christ_spawn"))
   );
   public static final RegistryObject<SoundEvent> break_christ = SOUND_EVENTS.register(
      "break_christ", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "break_christ"))
   );
   public static final RegistryObject<SoundEvent> below1 = SOUND_EVENTS.register(
      "below1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "below1"))
   );
   public static final RegistryObject<SoundEvent> below2 = SOUND_EVENTS.register(
      "below2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "below2"))
   );
   public static final RegistryObject<SoundEvent> disappear = SOUND_EVENTS.register(
      "disappear", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "disappear"))
   );
   public static final RegistryObject<SoundEvent> break_mob = SOUND_EVENTS.register(
      "break_mob", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "break_mob"))
   );
   public static final RegistryObject<SoundEvent> leave_the_house = SOUND_EVENTS.register(
      "leave_the_house", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "leave_the_house"))
   );
   public static final RegistryObject<SoundEvent> break_house = SOUND_EVENTS.register(
      "break_house", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "break_house"))
   );
   public static final RegistryObject<SoundEvent> spawn = SOUND_EVENTS.register(
      "spawn", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "spawn"))
   );
   public static final RegistryObject<SoundEvent> dead = SOUND_EVENTS.register(
      "dead", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "dead"))
   );
   public static final RegistryObject<SoundEvent> breath1 = SOUND_EVENTS.register(
      "breath1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "breath1"))
   );
   public static final RegistryObject<SoundEvent> steps1 = SOUND_EVENTS.register(
      "steps1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "steps1"))
   );
   public static final RegistryObject<SoundEvent> force_open = SOUND_EVENTS.register(
      "force_open", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "force_open"))
   );
   public static final RegistryObject<SoundEvent> atmosphere1 = SOUND_EVENTS.register(
      "atmosphere1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "atmosphere1"))
   );
   public static final RegistryObject<SoundEvent> headless_spawn = SOUND_EVENTS.register(
      "headless_spawn", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "headless_spawn"))
   );
   public static final RegistryObject<SoundEvent> atmosphere2 = SOUND_EVENTS.register(
      "atmosphere2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "atmosphere2"))
   );
   public static final RegistryObject<SoundEvent> atmosphere3 = SOUND_EVENTS.register(
      "atmosphere3", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "atmosphere3"))
   );
   public static final RegistryObject<SoundEvent> atmosphere4 = SOUND_EVENTS.register(
      "atmosphere4", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "atmosphere4"))
   );
   public static final RegistryObject<SoundEvent> atmosphere5 = SOUND_EVENTS.register(
      "atmosphere5", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "atmosphere5"))
   );
   public static final RegistryObject<SoundEvent> atmosphere6 = SOUND_EVENTS.register(
      "atmosphere6", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "atmosphere6"))
   );
   public static final RegistryObject<SoundEvent> atmosphere7 = SOUND_EVENTS.register(
      "atmosphere7", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "atmosphere7"))
   );
   public static final RegistryObject<SoundEvent> atmosphere8 = SOUND_EVENTS.register(
      "atmosphere8", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "atmosphere8"))
   );
   public static final RegistryObject<SoundEvent> labyr = SOUND_EVENTS.register(
      "labyr", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "labyr"))
   );
   public static final RegistryObject<SoundEvent> fake_house = SOUND_EVENTS.register(
      "fake_house", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "fake_house"))
   );
   public static final RegistryObject<SoundEvent> apocalypsis = SOUND_EVENTS.register(
      "apocalypsis", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "apocalypsis"))
   );
   public static final RegistryObject<SoundEvent> big_glitch = SOUND_EVENTS.register(
      "big_glitch", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "big_glitch"))
   );
   public static final RegistryObject<SoundEvent> nightmare = SOUND_EVENTS.register(
      "nightmare", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "nightmare"))
   );
   public static final RegistryObject<SoundEvent> teleport = SOUND_EVENTS.register(
      "teleport", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "teleport"))
   );
   public static final RegistryObject<SoundEvent> shock = SOUND_EVENTS.register(
      "shock", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "shock"))
   );
   public static final RegistryObject<SoundEvent> repair = SOUND_EVENTS.register(
      "repair", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "repair"))
   );
   public static final RegistryObject<SoundEvent> lost_call = SOUND_EVENTS.register(
      "lost_call", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "lost_call"))
   );
   public static final RegistryObject<SoundEvent> corrupted_kill = SOUND_EVENTS.register(
      "corrupted_kill", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "corrupted_kill"))
   );
   public static final RegistryObject<SoundEvent> corrupted_break = SOUND_EVENTS.register(
      "corrupted_break", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "corrupted_break"))
   );
   public static final RegistryObject<SoundEvent> his_hunt = SOUND_EVENTS.register(
      "his_hunt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "his_hunt"))
   );
   public static final RegistryObject<SoundEvent> shipwrecked = SOUND_EVENTS.register(
      "shipwrecked", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "shipwrecked"))
   );
   public static final RegistryObject<SoundEvent> oh_no_here = SOUND_EVENTS.register(
      "oh_no_here", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "oh_no_here"))
   );
   public static final RegistryObject<SoundEvent> some_eyes = SOUND_EVENTS.register(
      "some_eyes", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("flywheel_of_terror", "some_eyes"))
   );
}
