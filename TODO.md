# TODO — Flywheel of Terror Multiplayer Conversion

Phase structure mirrors the previous version's plan. See `CLAUDE.md` for the full audit and the
rationale behind each item.

Legend: `[ ]` not started · `[~]` in progress · `[x]` done

---

## Phase 0 — Offensive-identifier cleanup (verification only)
- [x] Scan all `.java` for racial slurs / offensive identifiers — **none found** in v1.0.1.
- [x] Confirm `world_change` (the historic offender) is absent from this tree.
- [x] Tidy non-slur cruft: `bebra` variable in `horror_environment`, Cyrillic `System.out.println`
      debug strings (`flywheel_of_terror`, `game_rules`), stray `System.out.println` event logs.

## Phase 1 — Foundation: remap, build & dedicated-server safety
- [x] Remap SRG decompile → official Mojang mappings; create a buildable Forge 1.20.1 MDK workspace.
- [x] Confirm `gradlew build` produces a JAR.
- [x] Move all common-bus `net.minecraft.client.*` access off the server path.
- [x] Fix all mixin `@Overwrite`/`@Shadow` to use SRG names + `remap=false`.
- [x] Gate all client-only `@EventBusSubscriber` classes with `Dist.CLIENT`.
- [x] Dedicated server boots clean (verified).

## Phase 2 — De-globalize per-player state — DONE (build green)
- [x] Migrate all per-player static fields to per-player NBT via `state` helper.
- [x] Core controllers: `health_decrease`, `terror_beginning`, `terror_continue`, `terror_end`, `house_defend`, `paranoia`.
- [x] All ~22 event class `event_in_process`/timer statics migrated.
- [x] `fake_main_menu`: added "hunt" multiplayer button → `JoinMultiplayerScreen`.

## Phase 3 — Client/server networking layer — DONE (build green)
- [x] Added `Network` (Forge `SimpleChannel`) with `PlaySoundPacket` (S2C), `FxPacket` (S2C), `ScreenStatePacket` (C2S).
- [x] All cross-side sound flags replaced with `Network.sound(player, ...)`.
- [x] All shader/overlay statics replaced with `FxPacket`.
- [x] GUI state sent C2S via `ScreenStatePacket`.
- [x] Visual timers moved to `client_net.clientTick`.

## Phase 4 — Per-mob target tracking, polish & QA
- [x] Give each custom mob its own target instead of `information.just_player`/`igrok`/`livingingrok`:
  - [x] `somewho`, `fake_steve`, `headless_steve`, `true_me`, `wrong_sheep` (also `wrong_cow` +
        the three `faceless_*` animals). Target UUID stored in mob persistent NBT
        (`information.setTarget`/`getTarget`), set at spawn from the triggering player.
  - [x] `horror_environment` aggro (uses the ticking player / per-mob target).
  - [ ] `oh_no` lifecycle (already multi-player: targets all players in range, gated on per-player
        `terror_beginning.his_hunt` NBT — left as-is).
- [x] Reduce `information` to stateless helper methods (all mutable static fields removed; client-only
      GUI state moved into the `Dist.CLIENT` `client_events` nested class).
- [ ] Multiplayer QA on a dedicated server (2+ players, independent events).
- [ ] Version bump + changelog + README update.
- [x] Tidy debug cruft: `bebra`, Cyrillic prints, stray `System.out.println`.

## Phase 5 — OP Commands — DONE (build green)
Command system in `commands.java` (`@EventBusSubscriber` FORGE bus). All commands under `/fot`,
gated on `source.hasPermission(2)`. Every sub-command uses `EntityArgument.players()` so `@a`/`@p`/
`@s`/`@r`/names all work and the action applies to each matched player; offline/unknown targets
error out naturally (EntityArgument only resolves online players).

- [x] `RegisterCommandsEvent` handler registers `/fot` as the root literal.
- [x] `/fot start <player>` — mirrors `first_entry` fresh-run seeding (phase 0, reseed call/cooldown/
      scheduler/seconds_to_change, clear his_hunt/first_message_was).
- [x] `/fot reset <player>` — removes the whole `flywheel_of_terror` NBT compound.
- [x] `/fot status <player>` — prints phase, his_hunt, builded, victims, maxhp, kills, house coords,
      scheduler timers, and active-event flags.
- [x] `/fot event <player> <event>` — triggers any of the 21 named events via their existing
      `set_active`/`do_event`/`set_tics` triggers (scarecrow tags the nearest mob; oh_no spawns the
      chaser; something_wrong carves a pit behind).
- [x] `/fot stop <player>` — deactivates all `*_active` flags + paralysis/independence/shipwrecked/
      fire/remove_entities/he_is_here state.
- [x] `/fot phase <player> <0-3>` — sets the `terror_end` phase NBT.
- [x] `/fot hunt <player>` — forces `his_hunt`, spawns `oh_no`, plays the hunt cue immediately.
- [x] `/fot house <player>` — teleports the OP to the player's bed/house, or errors if none set.
- [x] `/fot give <player> <item>` — gives `knife`/`notice`/`punishment`/`your_legacy`/`truth`
      (`truth` is an alias for the signature knife `my_knife`).
- [x] Tab-completion for player, event, and item arguments.
- [x] Offline/unknown target → error (inherent to `EntityArgument.players()`).
