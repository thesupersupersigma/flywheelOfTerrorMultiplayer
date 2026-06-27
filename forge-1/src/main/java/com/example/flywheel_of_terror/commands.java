package com.example.flywheel_of_terror;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * Phase 5 — operator command system. All commands live under {@code /fot} and require OP level 2.
 *
 * <p>Every sub-command takes an {@link EntityArgument#players() player selector}, so the standard
 * selectors ({@code @a}, {@code @p}, {@code @s}, {@code @r}) and raw player names all work and the
 * action is applied to each matched player. Because {@code EntityArgument} only ever resolves
 * <em>online</em> players, an offline / unknown target naturally produces an error instead of a
 * silent no-op.
 */
@EventBusSubscriber(modid = flywheel_of_terror.MODID)
public class commands {
   /** Event names accepted by {@code /fot event}, in trigger order. */
   private static final String[] EVENTS = {
      "paranoia", "labyrinth", "apocalypsis", "below", "panic", "thunder", "paralysis", "circle",
      "tool_break", "fire", "exist_terror", "scarecrow", "darknet", "he_is_here", "oh_no",
      "independence", "all_look", "shipwrecked", "something_wrong", "baron", "remove_entities"
   };

   /** Item names accepted by {@code /fot give}. */
   private static final String[] ITEMS = {"knife", "notice", "punishment", "your_legacy", "truth"};

   private static final SuggestionProvider<CommandSourceStack> EVENT_SUGGEST =
      (ctx, builder) -> SharedSuggestionProvider.suggest(EVENTS, builder);
   private static final SuggestionProvider<CommandSourceStack> ITEM_SUGGEST =
      (ctx, builder) -> SharedSuggestionProvider.suggest(ITEMS, builder);

   @SubscribeEvent
   public static void register(RegisterCommandsEvent event) {
      register(event.getDispatcher());
   }

   private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("fot")
         .requires(source -> source.hasPermission(2));

      root.then(Commands.literal("reset")
         .then(playerArg().executes(ctx -> forEach(ctx, commands::reset))));

      root.then(Commands.literal("status")
         .then(playerArg().executes(ctx -> forEach(ctx, commands::status))));

      root.then(Commands.literal("start")
         .then(playerArg().executes(ctx -> forEach(ctx, commands::start))));

      root.then(Commands.literal("stop")
         .then(playerArg().executes(ctx -> forEach(ctx, commands::stop))));

      root.then(Commands.literal("hunt")
         .then(playerArg().executes(ctx -> forEach(ctx, commands::hunt))));

      root.then(Commands.literal("house")
         .then(playerArg().executes(ctx -> forEach(ctx, commands::house))));

      root.then(Commands.literal("event")
         .then(playerArg()
            .then(Commands.argument("event", StringArgumentType.word()).suggests(EVENT_SUGGEST)
               .executes(ctx -> {
                  String name = StringArgumentType.getString(ctx, "event");
                  return forEach(ctx, (src, player) -> event(src, player, name));
               }))));

      root.then(Commands.literal("phase")
         .then(playerArg()
            .then(Commands.argument("phase", IntegerArgumentType.integer(0, 3))
               .executes(ctx -> {
                  int phase = IntegerArgumentType.getInteger(ctx, "phase");
                  return forEach(ctx, (src, player) -> phase(src, player, phase));
               }))));

      root.then(Commands.literal("give")
         .then(playerArg()
            .then(Commands.argument("item", StringArgumentType.word()).suggests(ITEM_SUGGEST)
               .executes(ctx -> {
                  String item = StringArgumentType.getString(ctx, "item");
                  return forEach(ctx, (src, player) -> give(src, player, item));
               }))));

      dispatcher.register(root);
   }

   private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, ?> playerArg() {
      return Commands.argument("player", EntityArgument.players());
   }

   /** A per-player action; returns true on success so the dispatcher can report a count. */
   @FunctionalInterface
   private interface Action {
      boolean apply(CommandSourceStack source, ServerPlayer player);
   }

   private static int forEach(CommandContext<CommandSourceStack> ctx, Action action) throws CommandSyntaxException {
      Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "player");
      int count = 0;
      for (ServerPlayer player : players) {
         if (action.apply(ctx.getSource(), player)) {
            count++;
         }
      }

      return count;
   }

   private static void ok(CommandSourceStack source, String message) {
      source.sendSuccess(() -> Component.literal("[FoT] " + message), true);
   }

   private static void fail(CommandSourceStack source, String message) {
      source.sendFailure(Component.literal("[FoT] " + message));
   }

   // ---- sub-command implementations -------------------------------------------------------------

   private static boolean reset(CommandSourceStack source, ServerPlayer player) {
      player.getPersistentData().remove("flywheel_of_terror");
      ok(source, "Wiped all terror state for " + player.getName().getString() + ".");
      return true;
   }

   private static boolean start(CommandSourceStack source, ServerPlayer player) {
      // Mirror first_entry's fresh-run seeding and force the run back to phase 0.
      state.putInt(player, nbt_adresses.phase_nbt, 0);
      state.putBool(player, "his_hunt", false);
      state.putBool(player, "first_message_was", false);
      paranoia.set_seconds_to_call(player, 299);
      state.putInt(player, "tics_cooldown", 4000);
      paranoia.seed_time_to_event(player);
      state.putInt(player, "seconds_to_change", something_wrong.random.nextInt(180, 360));
      state.putBool(player, "first_entry", true);
      Network.sound(player, (SoundEvent)register_sounds.his_hunt.get());
      ok(source, "Started a fresh horror run for " + player.getName().getString() + ".");
      return true;
   }

   private static boolean stop(CommandSourceStack source, ServerPlayer player) {
      labyrinth_event.set_active(player, false);
      apocalypsis_event.set_active(player, false);
      below_event.set_active(player, false);
      panic.set_active(player, false);
      thunder_behind.set_active(player, false);
      circle_christ.set_active(player, false);
      tool_break.set_active(player, false);
      exist_terror_event.set_active(player, false);
      fire_steps.set_tics(player, 0);
      remove_entities.set_tics_without_life(player, -1);
      independence.start(player, 0);
      shipwrecked.set_may_drown_player(player, false);
      shipwrecked.set_shipwrecked(player, false);
      // Paralysis / paranoia-call flags live directly on the compound.
      CompoundTag tag = state.tag(player);
      tag.putBoolean("paral", false);
      tag.putBoolean("wait_paralysis", false);
      tag.putInt("indep_tics", 0);
      state.save(player, tag);
      he_is_here_event.set_tics_of_event(player, 0);
      ok(source, "Cancelled all active events for " + player.getName().getString() + ".");
      return true;
   }

   private static boolean hunt(CommandSourceStack source, ServerPlayer player) {
      terror_beginning.set_his_hunt(player, true);
      // Spawn the chaser behind the player and announce, mirroring the nightly hunt trigger.
      oh_no he = new oh_no((EntityType<? extends PathfinderMob>)add_humans.oh_no.get(), player.level());
      double x = player.getX() - player.getLookAngle().x * 15.0;
      double z = player.getZ() - player.getLookAngle().z * 15.0;
      int y = player.level().getHeight(Types.WORLD_SURFACE, (int)x, (int)z) + 1;
      he.setPos(x, (double)y, z);
      player.level().addFreshEntity(he);
      Network.sound(player, (SoundEvent)register_sounds.his_hunt.get());
      player.displayClientMessage(Component.literal("It's time for him to hunt 0/1. Run"), true);
      ok(source, "Forced the hunt on " + player.getName().getString() + ".");
      return true;
   }

   private static boolean phase(CommandSourceStack source, ServerPlayer player, int phase) {
      state.putInt(player, nbt_adresses.phase_nbt, phase);
      ok(source, "Set phase of " + player.getName().getString() + " to " + phase + ".");
      return true;
   }

   private static boolean house(CommandSourceStack source, ServerPlayer player) {
      if (!house_defend.bed_here(player)) {
         fail(source, player.getName().getString() + " has no registered house yet.");
         return false;
      }

      BlockPos bed = house_defend.bed_pos(player);
      ServerPlayer op = source.getPlayer();
      if (op != null) {
         op.teleportTo((double)bed.getX() + 0.5, (double)bed.getY(), (double)bed.getZ() + 0.5);
         ok(source, "Teleported to " + player.getName().getString() + "'s house at "
            + bed.getX() + ", " + bed.getY() + ", " + bed.getZ() + ".");
      } else {
         ok(source, player.getName().getString() + "'s house is at "
            + bed.getX() + ", " + bed.getY() + ", " + bed.getZ() + ".");
      }

      return true;
   }

   private static boolean give(CommandSourceStack source, ServerPlayer player, String name) {
      Item item;
      switch (name) {
         case "knife":
         case "truth":
            // "truth" is the thematic alias for the mod's signature knife (my_knife).
            item = (Item)add_items.my_knife.get();
            break;
         case "notice":
            item = (Item)add_items.notice.get();
            break;
         case "punishment":
            item = (Item)add_items.punishment.get();
            break;
         case "your_legacy":
            item = (Item)add_items.your_legacy.get();
            break;
         default:
            fail(source, "Unknown item '" + name + "'. Valid: knife, notice, punishment, your_legacy, truth.");
            return false;
      }

      ItemStack stack = new ItemStack(item);
      if (!player.getInventory().add(stack)) {
         player.drop(stack, false);
      }

      ok(source, "Gave " + name + " to " + player.getName().getString() + ".");
      return true;
   }

   private static boolean status(CommandSourceStack source, ServerPlayer player) {
      CompoundTag tag = state.tag(player);
      String name = player.getName().getString();
      ok(source, "--- Terror status: " + name + " ---");
      ok(source, "phase=" + tag.getInt(nbt_adresses.phase_nbt)
         + "  his_hunt=" + tag.getBoolean("his_hunt")
         + "  builded=" + tag.getBoolean("builded")
         + "  near_house=" + tag.getBoolean("near_house"));
      ok(source, "victims=" + tag.getInt("killed_victims") + "/" + tag.getInt("count_of_victims")
         + "  maxhp=" + tag.getFloat("maxhp")
         + "  kills=" + tag.getInt("kills"));
      if (house_defend.bed_here(player)) {
         BlockPos bed = house_defend.bed_pos(player);
         ok(source, "house=" + bed.getX() + ", " + bed.getY() + ", " + bed.getZ());
      } else {
         ok(source, "house=none");
      }

      ok(source, "time_to_event=" + tag.getInt("time_to_event")
         + "  seconds_to_call=" + tag.getInt("second_to_call"));
      StringBuilder active = new StringBuilder();
      appendIf(active, tag.getBoolean("labyrinth_active"), "labyrinth");
      appendIf(active, tag.getBoolean("apoc_active"), "apocalypsis");
      appendIf(active, tag.getBoolean("below_active"), "below");
      appendIf(active, tag.getBoolean("panic_active"), "panic");
      appendIf(active, tag.getBoolean("thunder_behind_active"), "thunder");
      appendIf(active, tag.getBoolean("circle_christ_active"), "circle");
      appendIf(active, tag.getBoolean("tool_break_active"), "tool_break");
      appendIf(active, tag.getBoolean("exist_terror_active"), "exist_terror");
      appendIf(active, tag.getBoolean("paral"), "paralysis");
      ok(source, "active events: " + (active.length() == 0 ? "none" : active.toString()));
      return true;
   }

   private static void appendIf(StringBuilder builder, boolean condition, String name) {
      if (condition) {
         if (builder.length() > 0) {
            builder.append(", ");
         }

         builder.append(name);
      }
   }

   private static boolean event(CommandSourceStack source, ServerPlayer player, String name) {
      switch (name) {
         case "paranoia":
            paranoia.do_a_call(player);
            break;
         case "labyrinth":
            labyrinth_event.set_active(player, true);
            break;
         case "apocalypsis":
            apocalypsis_event.set_active(player, true);
            break;
         case "below":
            below_event.set_active(player, true);
            break;
         case "panic":
            panic.set_active(player, true);
            break;
         case "thunder":
            thunder_behind.set_active(player, true);
            break;
         case "paralysis":
            paralysis_event.do_event(player, 100);
            break;
         case "circle":
            circle_christ.set_active(player, true);
            break;
         case "tool_break":
            tool_break.set_active(player, true);
            break;
         case "fire":
            fire_steps.set_tics(player, 400);
            break;
         case "exist_terror":
            exist_terror_event.set_active(player, true);
            break;
         case "scarecrow":
            if (!tag_nearest_mob(player, "scarecrow", 10)) {
               fail(source, "No nearby mob to turn into a scarecrow for " + player.getName().getString() + ".");
               return false;
            }
            break;
         case "darknet":
            fake_darknet_access.set_wait_situation(player, true);
            break;
         case "he_is_here":
            he_is_here_event.do_event(player);
            break;
         case "oh_no":
            spawn_oh_no(player);
            break;
         case "independence":
            independence.start(player, 200);
            break;
         case "all_look":
            all_look_at_you.do_event(player);
            break;
         case "shipwrecked":
            shipwrecked.do_ship_wrecked(player);
            break;
         case "something_wrong":
            something_wrong.create_pit_behind(player, 5, 2, 2);
            break;
         case "baron":
            advanced_baron_detector.destroy_baron_tower(player, 3);
            break;
         case "remove_entities":
            remove_entities.set_tics_without_life(player, 4800);
            break;
         default:
            fail(source, "Unknown event '" + name + "'.");
            return false;
      }

      ok(source, "Triggered event '" + name + "' on " + player.getName().getString() + ".");
      return true;
   }

   private static void spawn_oh_no(ServerPlayer player) {
      oh_no he = new oh_no((EntityType<? extends PathfinderMob>)add_humans.oh_no.get(), player.level());
      double x = player.getX() - player.getLookAngle().x * 2.0;
      double y = player.getY();
      double z = player.getZ() - player.getLookAngle().z * 2.0;
      he.setPos(new Vec3(x, y, z));
      player.level().addFreshEntity(he);
   }

   private static boolean tag_nearest_mob(ServerPlayer player, String key, int value) {
      LivingEntity nearest = null;
      double best = Double.MAX_VALUE;
      for (LivingEntity mob : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(24.0))) {
         if (mob == player || mob instanceof net.minecraft.world.entity.player.Player) {
            continue;
         }

         double dist = mob.distanceToSqr(player);
         if (dist < best) {
            best = dist;
            nearest = mob;
         }
      }

      if (nearest == null) {
         return false;
      }

      nearest.getPersistentData().putInt(key, value);
      return true;
   }
}
