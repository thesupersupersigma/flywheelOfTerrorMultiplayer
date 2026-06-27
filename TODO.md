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

## Phase 5 — OP Commands
Add a proper command system so server operators can manually control the horror experience.
All commands under `/fot` (short for Flywheel of Terror). Requires OP level 2.

- [ ] Set up a `RegisterCommandsEvent` handler and register `/fot` as the root literal.
- [ ] `/fot start <player>` — begin the full horror sequence on a specific player (sets their NBT
      state as if they had just started a fresh run).
- [ ] `/fot reset <player>` — wipe all `flywheel_of_terror` NBT for that player, completely
      resetting their horror state to zero.
- [ ] `/fot status <player>` — print the player's current terror state to the OP in chat
      (phase, active events, house location, health modifier, kill count, etc.).
- [ ] `/fot event <player> <event>` — manually trigger a specific named event on a player.
      Supported events: `paranoia`, `labyrinth`, `apocalypsis`, `below`, `panic`, `thunder`,
      `paralysis`, `circle`, `tool_break`, `fire`, `exist_terror`, `scarecrow`, `darknet`,
      `he_is_here`, `oh_no`, `independence`, `all_look`, `shipwrecked`, `something_wrong`,
      `baron`, `remove_entities`.
- [ ] `/fot stop <player>` — cancel all currently active events for a player (sets all
      `event_in_process` NBT flags to false/0).
- [ ] `/fot phase <player> <0-3>` — manually set the `terror_end` phase for a player.
- [ ] `/fot hunt <player>` — force-start the nightly hunt on a specific player immediately
      regardless of time of day.
- [ ] `/fot house <player>` — teleport the OP to that player's registered house coordinates,
      or print them if no house is set yet.
- [ ] `/fot give <player> <item>` — give a player a mod-specific item (`knife`, `notice`,
      `punishment`, `your_legacy`, `truth`).
- [ ] Tab-completion for all player and event arguments.
- [ ] Commands should be no-ops (with an error message) if the target player is not online.
