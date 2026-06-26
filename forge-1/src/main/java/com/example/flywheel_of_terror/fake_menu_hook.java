package com.example.flywheel_of_terror;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.FileUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.locale.Language;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ChatTypeDecoration;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ChatType.Bound;
import net.minecraft.network.chat.ChatTypeDecoration.Parameter;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ColorResolverManager;
import net.minecraftforge.client.DimensionSpecialEffectsManager;
import net.minecraftforge.client.EntitySpectatorShaderManager;
import net.minecraftforge.client.ExtendedServerListData;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.ItemDecoratorHandler;
import net.minecraftforge.client.NamedRenderTypeManager;
import net.minecraftforge.client.RecipeBookManager;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerChangeGameTypeEvent;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderBlockScreenEffectEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.client.event.ToastAddEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent.System;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent.BossEventProgress;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.InputEvent.InteractionKeyMappingTriggered;
import net.minecraftforge.client.event.InputEvent.Key;
import net.minecraftforge.client.event.InputEvent.MouseScrollingEvent;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.client.event.ModelEvent.ModifyBakingResult;
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional;
import net.minecraftforge.client.event.RegisterColorHandlersEvent.Item;
import net.minecraftforge.client.event.RenderBlockScreenEffectEvent.OverlayType;
import net.minecraftforge.client.event.RenderHighlightEvent.Block;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.event.RenderTooltipEvent.Color;
import net.minecraftforge.client.event.RenderTooltipEvent.GatherComponents;
import net.minecraftforge.client.event.ScreenEvent.RenderInventoryMobEffects;
import net.minecraftforge.client.event.ScreenEvent.Render.Pre;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.minecraftforge.client.event.ViewportEvent.ComputeFogColor;
import net.minecraftforge.client.event.ViewportEvent.ComputeFov;
import net.minecraftforge.client.event.ViewportEvent.RenderFog;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.client.extensions.common.IClientItemExtensions.FontContext;
import net.minecraftforge.client.gui.ClientTooltipComponentManager;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import net.minecraftforge.client.textures.TextureAtlasSpriteLoaderManager;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.ServerStatusPing.ChannelData;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@EventBusSubscriber(value = {Dist.CLIENT})
public class fake_menu_hook {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker CLIENTHOOKS = MarkerManager.getMarker("CLIENTHOOKS");
   private static final Stack<Screen> guiLayers = new Stack<>();
   public static String forgeStatusLine;
   private static int slotMainHand = 0;
   private static final Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions = new HashMap<>();
   private static final ResourceLocation ICON_SHEET = new ResourceLocation("forge", "textures/gui/icons.png");
   private static final ChatTypeDecoration SYSTEM_CHAT_TYPE_DECORATION = new ChatTypeDecoration(
      "forge.chatType.system", List.of(Parameter.CONTENT), Style.EMPTY
   );
   private static final ChatType SYSTEM_CHAT_TYPE = new ChatType(SYSTEM_CHAT_TYPE_DECORATION, SYSTEM_CHAT_TYPE_DECORATION);
   private static final Bound SYSTEM_CHAT_TYPE_BOUND = SYSTEM_CHAT_TYPE.bind(Component.literal("System"));
   private static boolean initializedClientHooks = false;

   public static void resizeGuiLayers(Minecraft minecraft, int width, int height) {
      guiLayers.forEach(screen -> screen.resize(minecraft, width, height));
   }

   public static void clearGuiLayers(Minecraft minecraft) {
      while (guiLayers.size() > 0) {
         popGuiLayerInternal(minecraft);
      }
   }

   private static void popGuiLayerInternal(Minecraft minecraft) {
      if (minecraft.screen != null) {
         minecraft.screen.removed();
      }

      minecraft.screen = guiLayers.pop();
   }

   public static void pushGuiLayer(Minecraft minecraft, Screen screen) {
      if (minecraft.screen != null) {
         guiLayers.push(minecraft.screen);
      }

      minecraft.screen = Objects.requireNonNull(screen);
      screen.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
      minecraft.getNarrator().sayNow(screen.getNarrationMessage());
   }

   public static void popGuiLayer(Minecraft minecraft) {
      if (guiLayers.size() == 0) {
         minecraft.setScreen(null);
      } else {
         popGuiLayerInternal(minecraft);
         if (minecraft.screen != null) {
            minecraft.getNarrator().sayNow(minecraft.screen.getNarrationMessage());
         }
      }
   }

   public static float getGuiFarPlane() {
      return 1000.0F + 10000.0F * (float)(1 + guiLayers.size());
   }

   public static String getArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlot slot, String type) {
      String result = armor.getItem().getArmorTexture(armor, entity, slot, type);
      return result != null ? result : _default;
   }

   public static boolean onDrawHighlight(
      LevelRenderer context, Camera camera, HitResult target, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource
   ) {
      switch (target.getType()) {
         case BLOCK:
            if (target instanceof BlockHitResult blockTarget) {
               return MinecraftForge.EVENT_BUS.post(new Block(context, camera, blockTarget, partialTick, poseStack, bufferSource));
            }

            return false;
         case ENTITY:
            if (target instanceof EntityHitResult entityTarget) {
               return MinecraftForge.EVENT_BUS
                  .post(new net.minecraftforge.client.event.RenderHighlightEvent.Entity(context, camera, entityTarget, partialTick, poseStack, bufferSource));
            }

            return false;
         default:
            return false;
      }
   }

   public static void dispatchRenderStage(
      Stage stage, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, Camera camera, Frustum frustum
   ) {
      Minecraft mc = Minecraft.getInstance();
      ProfilerFiller profiler = mc.getProfiler();
      profiler.push(stage.toString());
      MinecraftForge.EVENT_BUS
         .post(new RenderLevelStageEvent(stage, levelRenderer, poseStack, projectionMatrix, renderTick, mc.getPartialTick(), camera, frustum));
      profiler.pop();
   }

   public static void dispatchRenderStage(
      RenderType renderType, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, Camera camera, Frustum frustum
   ) {
      Stage stage = Stage.fromRenderType(renderType);
      if (stage != null) {
         dispatchRenderStage(stage, levelRenderer, poseStack, projectionMatrix, renderTick, camera, frustum);
      }
   }

   public static boolean renderSpecificFirstPersonHand(
      InteractionHand hand,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      float partialTick,
      float interpPitch,
      float swingProgress,
      float equipProgress,
      ItemStack stack
   ) {
      return MinecraftForge.EVENT_BUS
         .post(new RenderHandEvent(hand, poseStack, bufferSource, packedLight, partialTick, interpPitch, swingProgress, equipProgress, stack));
   }

   public static boolean renderSpecificFirstPersonArm(
      PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, AbstractClientPlayer player, HumanoidArm arm
   ) {
      return MinecraftForge.EVENT_BUS.post(new RenderArmEvent(poseStack, multiBufferSource, packedLight, player, arm));
   }

   public static void onTextureStitchedPost(TextureAtlas map) {
      ModLoader.get().postEvent(new Post(map));
   }

   public static void onBlockColorsInit(BlockColors blockColors) {
      ModLoader.get().postEvent(new net.minecraftforge.client.event.RegisterColorHandlersEvent.Block(blockColors));
   }

   public static void onItemColorsInit(ItemColors itemColors, BlockColors blockColors) {
      ModLoader.get().postEvent(new Item(itemColors, blockColors));
   }

   public static Model getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> _default) {
      return IClientItemExtensions.of(itemStack).getGenericArmorModel(entityLiving, itemStack, slot, _default);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public static <T extends LivingEntity> void copyModelProperties(HumanoidModel<T> original, HumanoidModel<?> replacement) {
      original.copyPropertiesTo((HumanoidModel<T>)(HumanoidModel)replacement);
      replacement.head.visible = original.head.visible;
      replacement.hat.visible = original.hat.visible;
      replacement.body.visible = original.body.visible;
      replacement.rightArm.visible = original.rightArm.visible;
      replacement.leftArm.visible = original.leftArm.visible;
      replacement.rightLeg.visible = original.rightLeg.visible;
      replacement.leftLeg.visible = original.leftLeg.visible;
   }

   public static String fixDomain(String base, String complex) {
      int idx = complex.indexOf(58);
      if (idx == -1) {
         return base + complex;
      } else {
         String name = complex.substring(idx + 1, complex.length());
         if (idx > 1) {
            String domain = complex.substring(0, idx);
            return domain + ":" + base + name;
         } else {
            return base + name;
         }
      }
   }

   public static float getFieldOfViewModifier(Player entity, float fovModifier) {
      ComputeFovModifierEvent fovModifierEvent = new ComputeFovModifierEvent(entity, fovModifier);
      MinecraftForge.EVENT_BUS.post(fovModifierEvent);
      return fovModifierEvent.getNewFovModifier();
   }

   public static double getFieldOfView(GameRenderer renderer, Camera camera, double partialTick, double fov, boolean usedConfiguredFov) {
      ComputeFov event = new ComputeFov(renderer, camera, partialTick, fov, usedConfiguredFov);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getFOV();
   }

   public static void renderMainMenu(fake_main_menu gui, GuiGraphics guiGraphics, Font font, int width, int height, int alpha) {
      Status status = ForgeVersion.getStatus();
      fake_menu_renderer.renderMainMenuWarning(status, gui, guiGraphics, font, width, height, alpha);

      forgeStatusLine = switch (status) {
         case OUTDATED, BETA_OUTDATED -> I18n.get("forge.update.newversion", new Object[]{ForgeVersion.getTarget()});
         default -> null;
      };
   }

   @Nullable
   public static SoundInstance playSound(SoundEngine manager, SoundInstance sound) {
      PlaySoundEvent e = new PlaySoundEvent(manager, sound);
      MinecraftForge.EVENT_BUS.post(e);
      return e.getSound();
   }

   public static void drawScreen(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      guiGraphics.pose().pushPose();
      guiLayers.forEach(layer -> {
         drawScreenInternal(layer, guiGraphics, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTick);
         guiGraphics.pose().translate(0.0F, 0.0F, 2000.0F);
      });
      drawScreenInternal(screen, guiGraphics, mouseX, mouseY, partialTick);
      guiGraphics.pose().popPose();
   }

   private static void drawScreenInternal(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      if (!MinecraftForge.EVENT_BUS.post(new Pre(screen, guiGraphics, mouseX, mouseY, partialTick))) {
         screen.renderWithTooltip(guiGraphics, mouseX, mouseY, partialTick);
      }

      MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.Render.Post(screen, guiGraphics, mouseX, mouseY, partialTick));
   }

   public static Vector3f getFogColor(
      Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, float fogRed, float fogGreen, float fogBlue
   ) {
      FluidState state = level.getFluidState(camera.getBlockPosition());
      Vector3f fluidFogColor = new Vector3f(fogRed, fogGreen, fogBlue);
      if (camera.getPosition().y < (double)((float)camera.getBlockPosition().getY() + state.getHeight(level, camera.getBlockPosition()))) {
         fluidFogColor = IClientFluidTypeExtensions.of(state).modifyFogColor(camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor);
      }

      ComputeFogColor event = new ComputeFogColor(camera, partialTick, fluidFogColor.x(), fluidFogColor.y(), fluidFogColor.z());
      MinecraftForge.EVENT_BUS.post(event);
      fluidFogColor.set(event.getRed(), event.getGreen(), event.getBlue());
      return fluidFogColor;
   }

   public static void onFogRender(
      FogMode mode, FogType type, Camera camera, float partialTick, float renderDistance, float nearDistance, float farDistance, FogShape shape
   ) {
      FluidState state = camera.getEntity().level().getFluidState(camera.getBlockPosition());
      if (camera.getPosition().y < (double)((float)camera.getBlockPosition().getY() + state.getHeight(camera.getEntity().level(), camera.getBlockPosition()))) {
         IClientFluidTypeExtensions.of(state).modifyFogRender(camera, mode, renderDistance, partialTick, nearDistance, farDistance, shape);
      }

      RenderFog event = new RenderFog(mode, type, camera, partialTick, nearDistance, farDistance, shape);
      if (MinecraftForge.EVENT_BUS.post(event)) {
         RenderSystem.setShaderFogStart(event.getNearPlaneDistance());
         RenderSystem.setShaderFogEnd(event.getFarPlaneDistance());
         RenderSystem.setShaderFogShape(event.getFogShape());
      }
   }

   public static ComputeCameraAngles onCameraSetup(GameRenderer renderer, Camera camera, float partial) {
      ComputeCameraAngles event = new ComputeCameraAngles(renderer, camera, (double)partial, camera.getYRot(), camera.getXRot(), 0.0F);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
   }

   public static void onModifyBakingResult(Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) {
      ModLoader.get().postEvent(new ModifyBakingResult(models, modelBakery));
   }

   public static void onModelBake(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) {
      ModLoader.get().postEvent(new BakingCompleted(modelManager, Collections.unmodifiableMap(models), modelBakery));
   }

   public static BakedModel handleCameraTransforms(
      PoseStack poseStack, BakedModel model, ItemDisplayContext cameraTransformType, boolean applyLeftHandTransform
   ) {
      return model.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
   }

   public static TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter level, BlockPos pos, FluidState fluidStateIn) {
      IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStateIn);
      ResourceLocation overlayTexture = props.getOverlayTexture(fluidStateIn, level, pos);
      return new TextureAtlasSprite[]{
         (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(props.getStillTexture(fluidStateIn, level, pos)),
         (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(props.getFlowingTexture(fluidStateIn, level, pos)),
         overlayTexture == null ? null : (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(overlayTexture)
      };
   }

   public static Material getBlockMaterial(ResourceLocation loc) {
      return new Material(TextureAtlas.LOCATION_BLOCKS, loc);
   }

   public static void fillNormal(int[] faceData, Direction facing) {
      Vector3f v1 = getVertexPos(faceData, 3);
      Vector3f t1 = getVertexPos(faceData, 1);
      Vector3f v2 = getVertexPos(faceData, 2);
      Vector3f t2 = getVertexPos(faceData, 0);
      v1.sub(t1);
      v2.sub(t2);
      v2.cross(v1);
      v2.normalize();
      int x = (byte)Math.round(v2.x() * 127.0F) & 255;
      int y = (byte)Math.round(v2.y() * 127.0F) & 255;
      int z = (byte)Math.round(v2.z() * 127.0F) & 255;
      int normal = x | y << 8 | z << 16;

      for (int i = 0; i < 4; i++) {
         faceData[i * 8 + 7] = normal;
      }
   }

   private static Vector3f getVertexPos(int[] data, int vertex) {
      int idx = vertex * 8;
      float x = Float.intBitsToFloat(data[idx]);
      float y = Float.intBitsToFloat(data[idx + 1]);
      float z = Float.intBitsToFloat(data[idx + 2]);
      return new Vector3f(x, y, z);
   }

   public static boolean calculateFaceWithoutAO(
      BlockAndTintGetter getter, BlockState state, BlockPos pos, BakedQuad quad, boolean isFaceCubic, float[] brightness, int[] lightmap
   ) {
      if (quad.hasAmbientOcclusion()) {
         return false;
      } else {
         BlockPos lightmapPos = isFaceCubic ? pos.relative(quad.getDirection()) : pos;
         brightness[0] = brightness[1] = brightness[2] = brightness[3] = getter.getShade(quad.getDirection(), quad.isShade());
         lightmap[0] = lightmap[1] = lightmap[2] = lightmap[3] = LevelRenderer.getLightColor(getter, state, lightmapPos);
         return true;
      }
   }

   public static void loadEntityShader(Entity entity, GameRenderer entityRenderer) {
      if (entity != null) {
         ResourceLocation shader = EntitySpectatorShaderManager.get(entity.getType());
         if (shader != null) {
            entityRenderer.loadEffect(shader);
         }
      }
   }

   public static boolean shouldCauseReequipAnimation(@NotNull ItemStack from, @NotNull ItemStack to, int slot) {
      boolean fromInvalid = from.isEmpty();
      boolean toInvalid = to.isEmpty();
      if (fromInvalid && toInvalid) {
         return false;
      } else if (!fromInvalid && !toInvalid) {
         boolean changed = false;
         if (slot != -1) {
            changed = slot != slotMainHand;
            slotMainHand = slot;
         }

         return from.getItem().shouldCauseReequipAnimation(from, to, changed);
      } else {
         return true;
      }
   }

   public static BossEventProgress onCustomizeBossEventProgress(GuiGraphics guiGraphics, Window window, LerpingBossEvent bossInfo, int x, int y, int increment) {
      BossEventProgress evt = new BossEventProgress(window, guiGraphics, Minecraft.getInstance().getPartialTick(), bossInfo, x, y, increment);
      MinecraftForge.EVENT_BUS.post(evt);
      return evt;
   }

   public static ScreenshotEvent onScreenshot(NativeImage image, File screenshotFile) {
      ScreenshotEvent event = new ScreenshotEvent(image, screenshotFile);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
   }

   public static void onClientChangeGameType(PlayerInfo info, GameType currentGameMode, GameType newGameMode) {
      if (currentGameMode != newGameMode) {
         ClientPlayerChangeGameTypeEvent evt = new ClientPlayerChangeGameTypeEvent(info, currentGameMode, newGameMode);
         MinecraftForge.EVENT_BUS.post(evt);
      }
   }

   public static void onMovementInputUpdate(Player player, Input movementInput) {
      MinecraftForge.EVENT_BUS.post(new MovementInputUpdateEvent(player, movementInput));
   }

   public static boolean onScreenMouseClickedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.MouseButtonPressed.Pre(guiScreen, mouseX, mouseY, button);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onScreenMouseClickedPost(Screen guiScreen, double mouseX, double mouseY, int button, boolean handled) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.MouseButtonPressed.Post(guiScreen, mouseX, mouseY, button, handled);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getResult() == Result.DEFAULT ? handled : event.getResult() == Result.ALLOW;
   }

   public static boolean onScreenMouseReleasedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.MouseButtonReleased.Pre(guiScreen, mouseX, mouseY, button);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onScreenMouseReleasedPost(Screen guiScreen, double mouseX, double mouseY, int button, boolean handled) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.MouseButtonReleased.Post(guiScreen, mouseX, mouseY, button, handled);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getResult() == Result.DEFAULT ? handled : event.getResult() == Result.ALLOW;
   }

   public static boolean onScreenMouseDragPre(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.MouseDragged.Pre(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static void onScreenMouseDragPost(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.MouseDragged.Post(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
      MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onScreenMouseScrollPre(MouseHandler mouseHelper, Screen guiScreen, double scrollDelta) {
      Window mainWindow = guiScreen.getMinecraft().getWindow();
      double mouseX = mouseHelper.xpos() * (double)mainWindow.getGuiScaledWidth() / (double)mainWindow.getScreenWidth();
      double mouseY = mouseHelper.ypos() * (double)mainWindow.getGuiScaledHeight() / (double)mainWindow.getScreenHeight();
      Event event = new net.minecraftforge.client.event.ScreenEvent.MouseScrolled.Pre(guiScreen, mouseX, mouseY, scrollDelta);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static void onScreenMouseScrollPost(MouseHandler mouseHelper, Screen guiScreen, double scrollDelta) {
      Window mainWindow = guiScreen.getMinecraft().getWindow();
      double mouseX = mouseHelper.xpos() * (double)mainWindow.getGuiScaledWidth() / (double)mainWindow.getScreenWidth();
      double mouseY = mouseHelper.ypos() * (double)mainWindow.getGuiScaledHeight() / (double)mainWindow.getScreenHeight();
      Event event = new net.minecraftforge.client.event.ScreenEvent.MouseScrolled.Post(guiScreen, mouseX, mouseY, scrollDelta);
      MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onScreenKeyPressedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.KeyPressed.Pre(guiScreen, keyCode, scanCode, modifiers);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onScreenKeyPressedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.KeyPressed.Post(guiScreen, keyCode, scanCode, modifiers);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onScreenKeyReleasedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.KeyReleased.Pre(guiScreen, keyCode, scanCode, modifiers);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onScreenKeyReleasedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.KeyReleased.Post(guiScreen, keyCode, scanCode, modifiers);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onScreenCharTypedPre(Screen guiScreen, char codePoint, int modifiers) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.CharacterTyped.Pre(guiScreen, codePoint, modifiers);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static void onScreenCharTypedPost(Screen guiScreen, char codePoint, int modifiers) {
      Event event = new net.minecraftforge.client.event.ScreenEvent.CharacterTyped.Post(guiScreen, codePoint, modifiers);
      MinecraftForge.EVENT_BUS.post(event);
   }

   public static void onRecipesUpdated(RecipeManager mgr) {
      Event event = new RecipesUpdatedEvent(mgr);
      MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onMouseButtonPre(int button, int action, int mods) {
      return MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.InputEvent.MouseButton.Pre(button, action, mods));
   }

   public static void onMouseButtonPost(int button, int action, int mods) {
      MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.InputEvent.MouseButton.Post(button, action, mods));
   }

   public static boolean onMouseScroll(MouseHandler mouseHelper, double scrollDelta) {
      Event event = new MouseScrollingEvent(
         scrollDelta, mouseHelper.isLeftPressed(), mouseHelper.isMiddlePressed(), mouseHelper.isRightPressed(), mouseHelper.xpos(), mouseHelper.ypos()
      );
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static void onKeyInput(int key, int scanCode, int action, int modifiers) {
      MinecraftForge.EVENT_BUS.post(new Key(key, scanCode, action, modifiers));
   }

   public static InteractionKeyMappingTriggered onClickInput(int button, KeyMapping keyBinding, InteractionHand hand) {
      InteractionKeyMappingTriggered event = new InteractionKeyMappingTriggered(button, keyBinding, hand);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
   }

   public static boolean isNameplateInRenderDistance(Entity entity, double squareDistance) {
      if (entity instanceof LivingEntity) {
         AttributeInstance attribute = ((LivingEntity)entity).getAttribute((Attribute)ForgeMod.NAMETAG_DISTANCE.get());
         if (attribute != null) {
            return !(squareDistance > attribute.getValue() * attribute.getValue());
         }
      }

      return !(squareDistance > 4096.0);
   }

   public static void renderPistonMovedBlocks(
      BlockPos pos,
      BlockState state,
      PoseStack stack,
      MultiBufferSource bufferSource,
      Level level,
      boolean checkSides,
      int packedOverlay,
      BlockRenderDispatcher blockRenderer
   ) {
      BakedModel model = blockRenderer.getBlockModel(state);

      for (RenderType renderType : model.getRenderTypes(state, RandomSource.create(state.getSeed(pos)), ModelData.EMPTY)) {
         VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType));
         blockRenderer.getModelRenderer()
            .tesselateBlock(
               level,
               model,
               state,
               pos,
               stack,
               vertexConsumer,
               checkSides,
               RandomSource.create(),
               state.getSeed(pos),
               packedOverlay,
               ModelData.EMPTY,
               renderType
            );
      }
   }

   public static boolean shouldRenderEffect(MobEffectInstance effectInstance) {
      return IClientMobEffectExtensions.of(effectInstance).isVisibleInInventory(effectInstance);
   }

   @Nullable
   public static SpriteContents loadSpriteContents(
      ResourceLocation name, Resource resource, FrameSize frameSize, NativeImage image, AnimationMetadataSection animationMeta
   ) {
      try {
         ForgeTextureMetadata forgeMeta = ForgeTextureMetadata.forResource(resource);
         return forgeMeta.getLoader() == null ? null : forgeMeta.getLoader().loadContents(name, resource, frameSize, image, animationMeta, forgeMeta);
      } catch (IOException var6) {
         LOGGER.error("Unable to get Forge metadata for {}, falling back to vanilla loading", name);
         var6.printStackTrace();
         return null;
      }
   }

   @Nullable
   public static TextureAtlasSprite loadTextureAtlasSprite(
      ResourceLocation atlasName, SpriteContents contents, int atlasWidth, int atlasHeight, int spriteX, int spriteY, int mipmapLevel
   ) {
      return contents.forgeMeta != null && contents.forgeMeta.getLoader() != null
         ? contents.forgeMeta.getLoader().makeSprite(atlasName, contents, atlasWidth, atlasHeight, spriteX, spriteY, mipmapLevel)
         : null;
   }

   public static void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
      layerDefinitions.put(layerLocation, supplier);
   }

   public static void loadLayerDefinitions(Builder<ModelLayerLocation, LayerDefinition> builder) {
      layerDefinitions.forEach((k, v) -> builder.put(k, v.get()));
   }

   public static void processForgeListPingData(ServerStatus packet, ServerData target) {
      packet.forgeData()
         .ifPresentOrElse(
            forgeData -> {
               Map<String, String> mods = forgeData.getRemoteModData();
               Map<ResourceLocation, ChannelData> remoteChannels = forgeData.getRemoteChannels();
               int fmlver = forgeData.getFMLNetworkVersion();
               boolean fmlNetMatches = fmlver == 3;
               boolean channelsMatch = NetworkRegistry.checkListPingCompatibilityForClient(remoteChannels);
               AtomicBoolean result = new AtomicBoolean(true);
               List<String> extraClientMods = new ArrayList<>();
               ModList.get().forEachModContainer((modid, mc) -> mc.getCustomExtension(DisplayTest.class).ifPresent(ext -> {
                     boolean foundModOnServer = ext.remoteVersionTest().test(mods.get(modid), true);
                     result.compareAndSet(true, foundModOnServer);
                     if (!foundModOnServer) {
                        extraClientMods.add(modid);
                     }
                  }));
               boolean modsMatch = result.get();
               Map<String, String> extraServerMods = mods.entrySet()
                  .stream()
                  .filter(
                     e -> !Objects.equals(
                           "OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31",
                           e.getValue()
                        )
                  )
                  .filter(e -> !ModList.get().isLoaded(e.getKey()))
                  .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
               LOGGER.debug(
                  CLIENTHOOKS,
                  "Received FML ping data from server at {}: FMLNETVER={}, mod list is compatible : {}, channel list is compatible: {}, extra server mods: {}",
                  target.ip,
                  fmlver,
                  modsMatch,
                  channelsMatch,
                  extraServerMods
               );
               String extraReason = null;
               if (!extraServerMods.isEmpty()) {
                  extraReason = "fml.menu.multiplayer.extraservermods";
                  LOGGER.info(
                     CLIENTHOOKS,
                     ForgeI18n.parseMessage(extraReason, new Object[0]) + ": {}",
                     extraServerMods.entrySet().stream().map(e -> e.getKey() + "@" + e.getValue()).collect(Collectors.joining(", "))
                  );
               }

               if (!modsMatch) {
                  extraReason = "fml.menu.multiplayer.modsincompatible";
                  LOGGER.info(CLIENTHOOKS, "Client has mods that are missing on server: {}", extraClientMods);
               }

               if (!channelsMatch) {
                  extraReason = "fml.menu.multiplayer.networkincompatible";
               }

               if (fmlver < 3) {
                  extraReason = "fml.menu.multiplayer.serveroutdated";
               }

               if (fmlver > 3) {
                  extraReason = "fml.menu.multiplayer.clientoutdated";
               }

               target.forgeData = new ExtendedServerListData(
                  "FML", extraServerMods.isEmpty() && fmlNetMatches && channelsMatch && modsMatch, mods.size(), extraReason, forgeData.isTruncated()
               );
            },
            () -> target.forgeData = new ExtendedServerListData("VANILLA", NetworkRegistry.canConnectToVanillaServer(), 0, null)
         );
   }

   public static void drawForgePingInfo(
      JoinMultiplayerScreen gui, ServerData target, GuiGraphics guiGraphics, int x, int y, int width, int relativeMouseX, int relativeMouseY
   ) {
      if (target.forgeData != null) {
         String var10 = target.forgeData.type();
         byte var11 = -1;
         switch (var10.hashCode()) {
            case 69733:
               if (var10.equals("FML")) {
                  var11 = 0;
               }
               break;
            case 951084891:
               if (var10.equals("VANILLA")) {
                  var11 = 1;
               }
         }

         int idx;
         String tooltip;

         tooltip = switch (var11) {
            case 0 -> {
               String ping;
               if (target.forgeData.isCompatible()) {
                  idx = 0;
                  ping = ForgeI18n.parseMessage("fml.menu.multiplayer.compatible", new Object[]{target.forgeData.numberOfMods()});
               } else {
                  idx = 16;
                  if (target.forgeData.extraReason() != null) {
                     String extraReason = ForgeI18n.parseMessage(target.forgeData.extraReason(), new Object[0]);
                     ping = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible.extra", new Object[]{extraReason});
                  } else {
                     ping = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible", new Object[0]);
                  }
               }

               if (target.forgeData.truncated()) {
                  ping = ping + "\n" + ForgeI18n.parseMessage("fml.menu.multiplayer.truncated", new Object[0]);
               }

               yield ping;
            }
            case 1 -> {
               if (target.forgeData.isCompatible()) {
                  idx = 48;
                  yield ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla", new Object[0]);
               } else {
                  idx = 80;
                  yield ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla.incompatible", new Object[0]);
               }
            }
            default -> {
               idx = 64;
               yield ForgeI18n.parseMessage("fml.menu.multiplayer.unknown", new Object[]{target.forgeData.type()});
            }
         };

         guiGraphics.blit(ICON_SHEET, x + width - 18, y + 10, 16, 16, 0.0F, (float)idx, 16, 16, 256, 256);
         if (relativeMouseX > width - 15 && relativeMouseX < width && relativeMouseY > 10 && relativeMouseY < 26) {
            gui.setToolTip(Arrays.stream(tooltip.split("\n")).map(Component::literal).collect(Collectors.toList()));
         }
      }
   }

   private static Connection getClientConnection() {
      return Minecraft.getInstance().getConnection() != null ? Minecraft.getInstance().getConnection().getConnection() : null;
   }

   public static void handleClientLevelClosing(ClientLevel level) {
      Connection client = getClientConnection();
      if (client != null && !client.isMemoryConnection()) {
         GameData.revertToFrozen();
      }
   }

   public static void firePlayerLogin(MultiPlayerGameMode pc, LocalPlayer player, Connection networkManager) {
      MinecraftForge.EVENT_BUS.post(new LoggingIn(pc, player, networkManager));
   }

   public static void firePlayerLogout(@Nullable MultiPlayerGameMode pc, @Nullable LocalPlayer player) {
      MinecraftForge.EVENT_BUS.post(new LoggingOut(pc, player, player != null ? (player.connection != null ? player.connection.getConnection() : null) : null));
   }

   public static void firePlayerRespawn(MultiPlayerGameMode pc, LocalPlayer oldPlayer, LocalPlayer newPlayer, Connection networkManager) {
      MinecraftForge.EVENT_BUS.post(new Clone(pc, oldPlayer, newPlayer, networkManager));
   }

   public static void onRegisterParticleProviders(ParticleEngine particleEngine) {
      ModLoader.get().postEvent(new RegisterParticleProvidersEvent(particleEngine));
   }

   public static void onRegisterKeyMappings(Options options) {
      ModLoader.get().postEvent(new RegisterKeyMappingsEvent(options));
   }

   public static void onRegisterAdditionalModels(Set<ResourceLocation> additionalModels) {
      ModLoader.get().postEvent(new RegisterAdditional(additionalModels));
   }

   @Nullable
   public static Component onClientChat(Bound boundChatType, Component message, UUID sender) {
      ClientChatReceivedEvent event = new ClientChatReceivedEvent(boundChatType, message, sender);
      return MinecraftForge.EVENT_BUS.post(event) ? null : event.getMessage();
   }

   @Nullable
   public static Component onClientPlayerChat(Bound boundChatType, Component message, PlayerChatMessage playerChatMessage, UUID sender) {
      net.minecraftforge.client.event.ClientChatReceivedEvent.Player event = new net.minecraftforge.client.event.ClientChatReceivedEvent.Player(
         boundChatType, message, playerChatMessage, sender
      );
      return MinecraftForge.EVENT_BUS.post(event) ? null : event.getMessage();
   }

   @Nullable
   public static Component onClientSystemChat(Component message, boolean overlay) {
      System event = new System(SYSTEM_CHAT_TYPE_BOUND, message, overlay);
      return MinecraftForge.EVENT_BUS.post(event) ? null : event.getMessage();
   }

   @NotNull
   public static String onClientSendMessage(String message) {
      ClientChatEvent event = new ClientChatEvent(message);
      return MinecraftForge.EVENT_BUS.post(event) ? "" : event.getMessage();
   }

   @NotNull
   public static RenderType getEntityRenderType(RenderType chunkRenderType, boolean cull) {
      return RenderTypeHelper.getEntityRenderType(chunkRenderType, cull);
   }

   public static Font getTooltipFont(@NotNull ItemStack stack, Font fallbackFont) {
      Font stackFont = IClientItemExtensions.of(stack).getFont(stack, FontContext.TOOLTIP);
      return stackFont == null ? fallbackFont : stackFont;
   }

   public static net.minecraftforge.client.event.RenderTooltipEvent.Pre onRenderTooltipPre(
      @NotNull ItemStack stack,
      GuiGraphics graphics,
      int x,
      int y,
      int screenWidth,
      int screenHeight,
      @NotNull List<ClientTooltipComponent> components,
      @NotNull Font fallbackFont,
      @NotNull ClientTooltipPositioner positioner
   ) {
      net.minecraftforge.client.event.RenderTooltipEvent.Pre preEvent = new net.minecraftforge.client.event.RenderTooltipEvent.Pre(
         stack, graphics, x, y, screenWidth, screenHeight, getTooltipFont(stack, fallbackFont), components, positioner
      );
      MinecraftForge.EVENT_BUS.post(preEvent);
      return preEvent;
   }

   public static Color onRenderTooltipColor(
      @NotNull ItemStack stack, GuiGraphics graphics, int x, int y, @NotNull Font font, @NotNull List<ClientTooltipComponent> components
   ) {
      Color colorEvent = new Color(stack, graphics, x, y, font, -267386864, 1347420415, 1344798847, components);
      MinecraftForge.EVENT_BUS.post(colorEvent);
      return colorEvent;
   }

   public static List<ClientTooltipComponent> gatherTooltipComponents(
      ItemStack stack, List<? extends FormattedText> textElements, int mouseX, int screenWidth, int screenHeight, Font fallbackFont
   ) {
      return gatherTooltipComponents(stack, textElements, Optional.empty(), mouseX, screenWidth, screenHeight, fallbackFont);
   }

   public static List<ClientTooltipComponent> gatherTooltipComponents(
      ItemStack stack,
      List<? extends FormattedText> textElements,
      Optional<TooltipComponent> itemComponent,
      int mouseX,
      int screenWidth,
      int screenHeight,
      Font fallbackFont
   ) {
      Font font = getTooltipFont(stack, fallbackFont);
      List<Either<FormattedText, TooltipComponent>> elements = textElements.stream()
         .<Either<FormattedText, TooltipComponent>>map(Either::left)
         .collect(Collectors.toCollection(ArrayList::new));
      itemComponent.ifPresent(c -> elements.add(1, Either.right(c)));
      GatherComponents event = new GatherComponents(stack, screenWidth, screenHeight, elements, -1);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         return List.of();
      } else {
         int tooltipTextWidth = event.getTooltipElements().stream().mapToInt(either -> (Integer)either.map(font::width, component -> 0)).max().orElse(0);
         boolean needsWrap = false;
         int tooltipX = mouseX + 12;
         if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) {
               if (mouseX > screenWidth / 2) {
                  tooltipTextWidth = mouseX - 12 - 8;
               } else {
                  tooltipTextWidth = screenWidth - 16 - mouseX;
               }

               needsWrap = true;
            }
         }

         if (event.getMaxWidth() > 0 && tooltipTextWidth > event.getMaxWidth()) {
            tooltipTextWidth = event.getMaxWidth();
            needsWrap = true;
         }

         int tooltipTextWidthF = tooltipTextWidth;
         return needsWrap
            ? event.getTooltipElements()
               .stream()
               .flatMap(
                  either -> (Stream<ClientTooltipComponent>)either.map(
                        text -> splitLine(text, font, tooltipTextWidthF), component -> Stream.of(ClientTooltipComponent.create(component))
                     )
               )
               .toList()
            : event.getTooltipElements()
               .stream()
               .map(
                  either -> (ClientTooltipComponent)either.map(
                        text -> ClientTooltipComponent.create(text instanceof Component ? ((Component)text).getVisualOrderText() : Language.getInstance().getVisualOrder(text)),
                        ClientTooltipComponent::create
                     )
               )
               .toList();
      }
   }

   private static Stream<ClientTooltipComponent> splitLine(FormattedText text, Font font, int maxWidth) {
      if (text instanceof Component component && component.getString().isEmpty()) {
         return Stream.of(component.getVisualOrderText()).map(ClientTooltipComponent::create);
      }

      return font.split(text, maxWidth).stream().map(ClientTooltipComponent::create);
   }

   public static Comparator<ParticleRenderType> makeParticleRenderTypeComparator(List<ParticleRenderType> renderOrder) {
      Comparator<ParticleRenderType> vanillaComparator = Comparator.comparingInt(renderOrder::indexOf);
      return (typeOne, typeTwo) -> {
         boolean vanillaOne = renderOrder.contains(typeOne);
         boolean vanillaTwo = renderOrder.contains(typeTwo);
         if (vanillaOne && vanillaTwo) {
            return vanillaComparator.compare(typeOne, typeTwo);
         } else if (!vanillaOne && !vanillaTwo) {
            return Integer.compare(java.lang.System.identityHashCode(typeOne), java.lang.System.identityHashCode(typeTwo));
         } else {
            return vanillaOne ? -1 : 1;
         }
      };
   }

   public static RenderInventoryMobEffects onScreenPotionSize(Screen screen, int availableSpace, boolean compact, int horizontalOffset) {
      RenderInventoryMobEffects event = new RenderInventoryMobEffects(screen, availableSpace, compact, horizontalOffset);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
   }

   public static boolean onToastAdd(Toast toast) {
      return MinecraftForge.EVENT_BUS.post(new ToastAddEvent(toast));
   }

   public static boolean isBlockInSolidLayer(BlockState state) {
      BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
      return model.getRenderTypes(state, RandomSource.create(), ModelData.EMPTY).contains(RenderType.solid());
   }

   public static void createWorldConfirmationScreen(Runnable doConfirmedWorldLoad) {
      Component title = Component.translatable("selectWorld.backupQuestion.experimental");
      Component msg = Component.translatable("selectWorld.backupWarning.experimental")
         .append("\n\n")
         .append(Component.translatable("forge.selectWorld.backupWarning.experimental.additional"));
      Screen screen = new ConfirmScreen(confirmed -> {
         if (confirmed) {
            doConfirmedWorldLoad.run();
         } else {
            Minecraft.getInstance().setScreen(null);
         }
      }, title, msg, CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL);
      Minecraft.getInstance().setScreen(screen);
   }

   public static boolean renderFireOverlay(Player player, PoseStack mat) {
      return renderBlockOverlay(player, mat, OverlayType.FIRE, Blocks.FIRE.defaultBlockState(), player.blockPosition());
   }

   public static boolean renderWaterOverlay(Player player, PoseStack mat) {
      return renderBlockOverlay(player, mat, OverlayType.WATER, Blocks.WATER.defaultBlockState(), player.blockPosition());
   }

   public static boolean renderBlockOverlay(Player player, PoseStack mat, OverlayType type, BlockState block, BlockPos pos) {
      return MinecraftForge.EVENT_BUS.post(new RenderBlockScreenEffectEvent(player, mat, type, block, pos));
   }

   public static int getMaxMipmapLevel(int width, int height) {
      return Math.min(Mth.log2(Math.max(1, width)), Mth.log2(Math.max(1, height)));
   }

   public static ResourceLocation getShaderImportLocation(String basePath, boolean isRelative, String importPath) {
      ResourceLocation loc = new ResourceLocation(importPath);
      String normalised = FileUtil.normalizeResourcePath((isRelative ? basePath : "shaders/include/") + loc.getPath());
      return new ResourceLocation(loc.getNamespace(), normalised);
   }

   public static void onCreativeModeTabBuildContents(
      CreativeModeTab tab, ResourceKey<CreativeModeTab> tabKey, DisplayItemsGenerator originalGenerator, ItemDisplayParameters params, Output output
   ) {
      MutableHashedLinkedMap<ItemStack, TabVisibility> entries = new MutableHashedLinkedMap(
         ItemStackLinkedSet.TYPE_AND_TAG, (key, left, right) -> TabVisibility.PARENT_AND_SEARCH_TABS
      );
      originalGenerator.accept(params, (stack, vis) -> {
         if (stack.getCount() != 1) {
            throw new IllegalArgumentException("The stack count must be 1");
         } else {
            entries.put(stack, vis);
         }
      });
      ModLoader.get().postEvent(new BuildCreativeModeTabContentsEvent(tab, tabKey, params, entries));

      for (Entry<ItemStack, TabVisibility> entry : entries) {
         output.accept(entry.getKey(), entry.getValue());
      }
   }

   @Internal
   public static void initClientHooks(Minecraft mc, ReloadableResourceManager resourceManager) {
      if (initializedClientHooks) {
         throw new IllegalStateException("Client hooks initialized more than once");
      } else {
         initializedClientHooks = true;
         ForgeGameTestHooks.registerGametests();
         ModLoader.get().postEvent(new RegisterClientReloadListenersEvent(resourceManager));
         ModLoader.get().postEvent(new RegisterLayerDefinitions());
         ModLoader.get().postEvent(new RegisterRenderers());
         TextureAtlasSpriteLoaderManager.init();
         ClientTooltipComponentManager.init();
         EntitySpectatorShaderManager.init();
         ForgeHooksClient.onRegisterKeyMappings(mc.options);
         RecipeBookManager.init();
         GuiOverlayManager.init();
         DimensionSpecialEffectsManager.init();
         NamedRenderTypeManager.init();
         ColorResolverManager.init();
         ItemDecoratorHandler.init();
         fake_preset.init();
      }
   }

   @EventBusSubscriber(
      value = {Dist.CLIENT},
      modid = "forge",
      bus = Bus.MOD
   )
   public static class ClientEvents {
      @Nullable
      private static ShaderInstance rendertypeEntityTranslucentUnlitShader;

      public static ShaderInstance getEntityTranslucentUnlitShader() {
         return Objects.requireNonNull(
            rendertypeEntityTranslucentUnlitShader, "Attempted to call getEntityTranslucentUnlitShader before shaders have finished loading."
         );
      }

      @SubscribeEvent
      public static void registerShaders(RegisterShadersEvent event) throws IOException {
         event.registerShader(
            new ShaderInstance(event.getResourceProvider(), new ResourceLocation("forge", "rendertype_entity_unlit_translucent"), DefaultVertexFormat.NEW_ENTITY),
            p_172645_ -> rendertypeEntityTranslucentUnlitShader = p_172645_
         );
      }
   }
}
