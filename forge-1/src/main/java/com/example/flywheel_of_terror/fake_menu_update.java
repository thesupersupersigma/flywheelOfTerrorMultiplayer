package com.example.flywheel_of_terror;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.loading.ClientModLoader;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLConfig.ConfigValue;

@OnlyIn(Dist.CLIENT)
public class fake_menu_update extends Screen {
   private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation("forge", "textures/gui/version_check_icons.png");
   private final Button modButton;
   private Status showNotification = null;
   private boolean hasCheckedForUpdates = false;

   public fake_menu_update(Button modButton) {
      super(Component.translatable("forge.menu.updatescreen.title"));
      this.modButton = modButton;
   }

   public void init() {
      if (!this.hasCheckedForUpdates) {
         if (this.modButton != null) {
            this.showNotification = ClientModLoader.checkForUpdates();
         }

         this.hasCheckedForUpdates = true;
      }
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      if (this.showNotification != null && this.showNotification.shouldDraw() && FMLConfig.getBoolConfigValue(ConfigValue.VERSION_CHECK)) {
         int x = this.modButton.getX();
         int y = this.modButton.getY();
         int w = this.modButton.getWidth();
         int h = this.modButton.getHeight();
         guiGraphics.blit(
            VERSION_CHECK_ICONS,
            x + w - (h / 2 + 4),
            y + (h / 2 - 4),
            (float)(this.showNotification.getSheetOffset() * 8),
            this.showNotification.isAnimated() && (System.currentTimeMillis() / 800L & 1L) == 1L ? 8.0F : 0.0F,
            8,
            8,
            64,
            16
         );
      }
   }

   public static fake_menu_update init(fake_main_menu guiMainMenu, Button modButton) {
      fake_menu_update fake_menu_update = new fake_menu_update(modButton);
      fake_menu_update.resize(guiMainMenu.getMinecraft(), guiMainMenu.width, guiMainMenu.height);
      fake_menu_update.init();
      return fake_menu_update;
   }
}
