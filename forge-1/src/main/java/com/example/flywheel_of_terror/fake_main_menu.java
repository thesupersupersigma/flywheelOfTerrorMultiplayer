package com.example.flywheel_of_terror;

import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.internal.BrandingControl;
import org.slf4j.Logger;

@EventBusSubscriber
public class fake_main_menu extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String DEMO_LEVEL_ID = "Demo_World";
   public static final Component COPYRIGHT_TEXT = Component.literal("Copyright Mojang AB. Do not distribute!");
   public static final CubeMap CUBE_MAP = new CubeMap(new ResourceLocation("textures/gui/title/background/panorama"));
   private static final ResourceLocation PANORAMA_OVERLAY = new ResourceLocation("flywheel_of_terror", "textures/main_screen.png");
   @Nullable
   private SplashRenderer splash;
   private Button resetDemoButton;
   @Nullable
   private RealmsNotificationsScreen realmsNotificationsScreen;
   private final PanoramaRenderer panorama = new PanoramaRenderer(CUBE_MAP);
   private final boolean fading;
   private long fadeInStart;
   @Nullable
   private fake_main_menu.WarningLabel warningLabel;
   private final LogoRenderer logoRenderer;
   private fake_menu_update modUpdateNotification;

   public fake_main_menu() {
      this(false);
   }

   public fake_main_menu(boolean p_96733_) {
      this(p_96733_, (LogoRenderer)null);
   }

   public fake_main_menu(boolean p_265779_, @Nullable LogoRenderer p_265067_) {
      super(Component.translatable("narrator.screen.title"));
      this.fading = p_265779_;
      this.logoRenderer = Objects.requireNonNullElseGet(p_265067_, () -> new LogoRenderer(false));
   }

   private boolean realmsNotificationsEnabled() {
      return this.realmsNotificationsScreen != null;
   }

   public void tick() {
      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.tick();
      }

      this.minecraft.getRealms32BitWarningStatus().showRealms32BitWarningIfNeeded(this);
   }

   public static CompletableFuture<Void> preloadResources(TextureManager p_96755_, Executor p_96756_) {
      return CompletableFuture.allOf(
         p_96755_.preload(LogoRenderer.MINECRAFT_LOGO, p_96756_),
         p_96755_.preload(LogoRenderer.MINECRAFT_EDITION, p_96756_),
         p_96755_.preload(PANORAMA_OVERLAY, p_96756_),
         CUBE_MAP.preload(p_96755_, p_96756_)
      );
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      if (this.splash == null) {
         this.splash = this.minecraft.getSplashManager().getSplash();
      }

      int i = this.font.width(COPYRIGHT_TEXT);
      int j = this.width - i - 2;
      int k = 24;
      int l = this.height / 4 + 48;
      Button modButton = null;
      if (this.minecraft.isDemo()) {
         this.createDemoMenuOptions(l, 24);
      } else {
         this.createNormalMenuOptions(l, 24);
         modButton = (Button)this.addRenderableWidget(
            Button.builder(Component.literal("no matter"), button -> this.minecraft.setScreen(new ModListScreen(this)))
               .pos(this.width / 2 - 100, l + 48)
               .size(98, 20)
               .build()
         );
      }

      this.modUpdateNotification = fake_menu_update.init(this, modButton);
      this.addRenderableWidget(
         new PlainTextButton(
            j, this.height - 10, i, 10, COPYRIGHT_TEXT, p_280834_ -> this.minecraft.setScreen(new CreditsAndAttributionScreen(this)), this.font
         )
      );
      this.minecraft.setConnectedToRealms(false);
      if (this.realmsNotificationsScreen == null) {
         this.realmsNotificationsScreen = new RealmsNotificationsScreen();
      }

      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
      }

      if (!this.minecraft.is64Bit()) {
         this.warningLabel = new fake_main_menu.WarningLabel(
            this.font, MultiLineLabel.create(this.font, Component.translatable("title.32bit.deprecation"), 350, 2), this.width / 2, l - 24
         );
      }
   }

   private void createNormalMenuOptions(int p_96764_, int p_96765_) {
      this.addRenderableWidget(
         Button.builder(Component.literal("think twice"), p_280832_ -> this.minecraft.setScreen(new SelectWorldScreen(this)))
            .bounds(this.width / 2 - 100, p_96764_, 200, 20)
            .build()
      );
      Component component = this.getMultiplayerDisabledReason();
      boolean flag = component == null;
      if (component != null) {
         Tooltip.create(component);
      } else {
         Object var10000 = null;
      }
   }

   @Nullable
   private Component getMultiplayerDisabledReason() {
      if (this.minecraft.allowsMultiplayer()) {
         return null;
      } else {
         BanDetails bandetails = this.minecraft.multiplayerBan();
         if (bandetails != null) {
            return bandetails.expires() != null
               ? Component.translatable("title.multiplayer.disabled.banned.temporary")
               : Component.translatable("title.multiplayer.disabled.banned.permanent");
         } else {
            return Component.translatable("title.multiplayer.disabled");
         }
      }
   }

   private void createDemoMenuOptions(int p_96773_, int p_96774_) {
      boolean flag = this.checkDemoWorldPresence();
      this.addRenderableWidget(Button.builder(Component.translatable("menu.playdemo"), p_280837_ -> {
         if (flag) {
            this.minecraft.createWorldOpenFlows().loadLevel(this, "Demo_World");
         } else {
            this.minecraft.createWorldOpenFlows().createFreshLevel("Demo_World", MinecraftServer.DEMO_SETTINGS, WorldOptions.DEMO_OPTIONS, WorldPresets::createNormalWorldDimensions);
         }
      }).bounds(this.width / 2 - 100, p_96773_, 200, 20).build());
      this.resetDemoButton = (Button)this.addRenderableWidget(
         Button.builder(
               Component.translatable("menu.resetdemo"),
               p_232770_ -> {
                  LevelStorageSource levelstoragesource = this.minecraft.getLevelSource();

                  try {
                     LevelStorageAccess levelstoragesource$levelstorageaccess = levelstoragesource.createAccess("Demo_World");

                     try {
                        LevelSummary levelsummary = levelstoragesource$levelstorageaccess.getSummary();
                        if (levelsummary != null) {
                           this.minecraft
                              .setScreen(
                                 new ConfirmScreen(
                                    this::confirmDemo,
                                    Component.translatable("selectWorld.deleteQuestion"),
                                    Component.translatable("selectWorld.deleteWarning", new Object[]{levelsummary.getLevelName()}),
                                    Component.translatable("selectWorld.deleteButton"),
                                    CommonComponents.GUI_CANCEL
                                 )
                              );
                        }
                     } catch (Throwable var7) {
                        if (levelstoragesource$levelstorageaccess != null) {
                           try {
                              levelstoragesource$levelstorageaccess.close();
                           } catch (Throwable var6) {
                              var7.addSuppressed(var6);
                           }
                        }

                        throw var7;
                     }

                     if (levelstoragesource$levelstorageaccess != null) {
                        levelstoragesource$levelstorageaccess.close();
                     }
                  } catch (IOException var8) {
                     SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
                     LOGGER.warn("Failed to access demo world", var8);
                  }
               }
            )
            .bounds(this.width / 2 - 100, p_96773_ + p_96774_ * 1, 200, 20)
            .build()
      );
      this.resetDemoButton.active = flag;
   }

   private boolean checkDemoWorldPresence() {
      try {
         LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft.getLevelSource().createAccess("Demo_World");

         boolean flag;
         try {
            flag = levelstoragesource$levelstorageaccess.getSummary() != null;
         } catch (Throwable var6) {
            if (levelstoragesource$levelstorageaccess != null) {
               try {
                  levelstoragesource$levelstorageaccess.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (levelstoragesource$levelstorageaccess != null) {
            levelstoragesource$levelstorageaccess.close();
         }

         return flag;
      } catch (IOException var7) {
         SystemToast.onWorldAccessFailure(this.minecraft, "Demo_World");
         LOGGER.warn("Failed to read demo world data", var7);
         return false;
      }
   }

   private void realmsButtonClicked() {
      this.minecraft.setScreen(new RealmsMainScreen(this));
   }

   public void render(GuiGraphics p_282860_, int p_281753_, int p_283539_, float p_282628_) {
      if (this.fadeInStart == 0L && this.fading) {
         this.fadeInStart = Util.getMillis();
      }

      float f = this.fading ? (float)(Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
      this.panorama.render(p_282628_, Mth.clamp(f, 0.0F, 1.0F));
      RenderSystem.enableBlend();
      p_282860_.setColor(1.0F, 1.0F, 1.0F, this.fading ? (float)Mth.ceil(Mth.clamp(f, 0.0F, 1.0F)) : 1.0F);
      p_282860_.blit(PANORAMA_OVERLAY, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
      p_282860_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      float f1 = this.fading ? Mth.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
      this.logoRenderer.renderLogo(p_282860_, this.width, f1);
      int i = Mth.ceil(f1 * 255.0F) << 24;
      if ((i & -67108864) != 0) {
         if (this.warningLabel != null) {
            this.warningLabel.render(p_282860_, i);
         }

         fake_menu_hook.renderMainMenu(this, p_282860_, this.font, this.width, this.height, i);
         if (this.splash != null) {
            this.splash.render(p_282860_, this.width, this.font, i);
         }

         String s = "Minecraft " + SharedConstants.getCurrentVersion().getName();
         if (this.minecraft.isDemo()) {
            s = s + " Demo";
         } else {
            s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
         }

         if (Minecraft.checkModStatus().shouldReportAsModified()) {
            s = s + I18n.get("menu.modded", new Object[0]);
         }

         BrandingControl.forEachLine(
            true, true, (brdline, brd) -> p_282860_.drawString(this.font, brd, 2, this.height - (10 + brdline * (9 + 1)), 16777215 | i)
         );
         BrandingControl.forEachAboveCopyrightLine(
            (brdline, brd) -> p_282860_.drawString(
                  this.font, brd, this.width - this.font.width(brd), this.height - (10 + (brdline + 1) * (9 + 1)), 16777215 | i
               )
         );

         for (GuiEventListener guieventlistener : this.children()) {
            if (guieventlistener instanceof AbstractWidget) {
               ((AbstractWidget)guieventlistener).setAlpha(f1);
            }
         }

         super.render(p_282860_, p_281753_, p_283539_, p_282628_);
         if (this.realmsNotificationsEnabled() && f1 >= 1.0F) {
            RenderSystem.enableDepthTest();
            this.realmsNotificationsScreen.render(p_282860_, p_281753_, p_283539_, p_282628_);
         }

         if (f1 >= 1.0F) {
            this.modUpdateNotification.render(p_282860_, p_281753_, p_283539_, p_282628_);
         }
      }
   }

   public boolean mouseClicked(double p_96735_, double p_96736_, int p_96737_) {
      return super.mouseClicked(p_96735_, p_96736_, p_96737_)
         ? true
         : this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(p_96735_, p_96736_, p_96737_);
   }

   public void removed() {
      if (this.realmsNotificationsScreen != null) {
         this.realmsNotificationsScreen.removed();
      }
   }

   public void added() {
      super.added();
      if (this.realmsNotificationsScreen != null) {
         this.realmsNotificationsScreen.added();
      }
   }

   private void confirmDemo(boolean p_96778_) {
      if (p_96778_) {
         try {
            LevelStorageAccess levelstoragesource$levelstorageaccess = this.minecraft.getLevelSource().createAccess("Demo_World");

            try {
               levelstoragesource$levelstorageaccess.deleteLevel();
            } catch (Throwable var6) {
               if (levelstoragesource$levelstorageaccess != null) {
                  try {
                     levelstoragesource$levelstorageaccess.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (levelstoragesource$levelstorageaccess != null) {
               levelstoragesource$levelstorageaccess.close();
            }
         } catch (IOException var7) {
            SystemToast.onWorldDeleteFailure(this.minecraft, "Demo_World");
            LOGGER.warn("Failed to delete demo world", var7);
         }
      }

      this.minecraft.setScreen(this);
   }

   @OnlyIn(Dist.CLIENT)
   static record WarningLabel(Font font, MultiLineLabel label, int x, int y) {
      public void render(GuiGraphics p_281783_, int p_281383_) {
         this.label.renderBackgroundCentered(p_281783_, this.x, this.y, 9, 2, 2097152 | Math.min(p_281383_, 1426063360));
         this.label.renderCentered(p_281783_, this.x, this.y, 9, 16777215 | p_281383_);
      }
   }
}
