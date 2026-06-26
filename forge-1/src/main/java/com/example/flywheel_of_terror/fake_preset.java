package com.example.flywheel_of_terror;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraftforge.client.event.RegisterPresetEditorsEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public final class fake_preset {
   private static Map<ResourceKey<WorldPreset>, PresetEditor> editors = Map.of();

   private fake_preset() {
   }

   public static void init() {
      Map<ResourceKey<WorldPreset>, PresetEditor> gatheredEditors = new HashMap<>();
      PresetEditor.EDITORS.forEach((k, v) -> k.ifPresent(key -> gatheredEditors.put((ResourceKey<WorldPreset>)key, v)));
      RegisterPresetEditorsEvent event = new RegisterPresetEditorsEvent(gatheredEditors);
      ModLoader.get().postEventWrapContainerInModOrder(event);
      editors = gatheredEditors;
   }

   @Nullable
   public static PresetEditor get(ResourceKey<WorldPreset> key) {
      return editors.get(key);
   }
}
