# TODO — Flywheel of Terror Multiplayer Conversion

Phase structure mirrors the previous version's plan. See `CLAUDE.md` for the full audit and the
rationale behind each item. **No code changed yet — this is the plan.**

Legend: `[ ]` not started · `[~]` in progress · `[x]` done

---

## Phase 0 — Offensive-identifier cleanup (verification only)
- [x] Scan all `.java` for racial slurs / offensive identifiers — **none found** in v1.0.1.
- [x] Confirm `world_change` (the historic offender) is absent from this tree.
- [ ] Tidy non-slur cruft: `bebra` variable in `horror_environment`, Cyrillic `System.out.println`
      debug strings (`flywheel_of_terror`, `game_rules`), stray `System.out.println` event logs.

## Phase 1 — Foundation: remap, build & dedicated-server safety
- [ ] Remap SRG decompile → official Mojang mappings; create a buildable Forge 1.20.1 MDK workspace.
- [ ] Confirm `gradlew build` produces a JAR and `runServer` starts.
- [ ] Move all common-bus `net.minecraft.client.*` access off the server path:
  - [ ] `game_rules.every` (calls `Minecraft` unconditionally in `PlayerTickEvent`).
  - [ ] `all_look_at_you.every` (calls `Minecraft`/`Camera` unconditionally).
  - [ ] `information.return_to_normal` (`PlayerLoggedInEvent` calls `Minecraft`).
  - [ ] `independence.tick_move_player_to_living_entity` (called from server tick).
  - [ ] `paranoia.do_a_call` / `paranoia.view` (called from server-side `terror_beginning`).
  - [ ] `he_is_here_event.do_event` (called from server-side `paranoia`).
  - [ ] `fake_darknet_access.do_event` (called from server-side `LivingDeathEvent`).
- [ ] Audit mixins for server safety: `scarecrow` (`@Overwrite Entity#tick`),
      `less_trees` (`@Overwrite TreeFeature`), `fake_invent` (`InventoryScreen`, global `show_model`).
- [ ] Dedicated server boots and a client can connect (no `NoClassDefFoundError`).

## Phase 2 — De-globalize per-player state
- [ ] Build the full inventory of `public static` per-player fields (~120+; see CLAUDE.md §2.2/§2.3).
- [ ] Choose storage: extend `"flywheel_of_terror"` NBT vs. capability/attachment keyed by UUID.
- [ ] Migrate core controllers:
  - [ ] `terror_beginning` (16 statics: `his_hunt`, `near_house`, `away_house`, `first_message_was`,
        `killed_victims`, `count_of_victims`, …).
  - [ ] `terror_continue` (`need_try`, `near_maze`, `tics_cooldown`, `xxx/yyy/zzz`, sound flags).
  - [ ] `terror_end.phase` → per-player.
  - [ ] `house_defend` (`breaked_for_last_seconds`, `bed_here`, `pos_of_bed`).
  - [ ] `paranoia` (18 statics incl. the master `time_to_event` scheduler).
  - [ ] `health_decrease.max_hp` + `count_of_kills` → per-player.
- [ ] Migrate every event class's `event_in_process` / timer statics to per-player.
- [ ] Scope world mutations per-player where feasible (`terror_beginning` & `apocalypsis_event`
      global time/weather via `serv.m_8615_()` / `m_8606_()`).

## Phase 3 — Client/server networking layer
- [ ] Add a `SimpleChannel` with versioned S2C and C2S packets.
- [ ] Replace static cross-side **sound** flags with S2C packets (`*.sound_must_be` across
      `terror_beginning`, `terror_continue`, `paranoia`, `horror_environment`, `panic`, `shipwrecked`,
      `circle_christ`, `tool_break`, `sound_heart`, `sound_knife_attack`, `decline_run`, `below_event`,
      `angel_sound_event`, `apocalypsis_event`, `eye_intervention`, …).
- [ ] Replace shader/overlay statics with S2C (eyes, oh_no frames, blood/red shaders, blackout,
      forced camera/render-distance, fake chat typing in `exist_terror_event`).
- [ ] Send client-only state to the server via C2S (`information.current_screen` / inventory-open,
      `show_model`) so `paranoia`/`periferia` stop reading client statics on the server.
- [ ] Make all cross-side timers server-authoritative.

## Phase 4 — Per-mob target tracking, polish & QA
- [ ] Give each custom mob its own target instead of `information.just_player`/`igrok`/`livingingrok`:
  - [ ] `somewho`, `fake_steve`, `headless_steve`, `true_me`, `wrong_sheep`.
  - [ ] `horror_environment` aggro (`smart`/`coward` use `just_player`/`livingingrok`).
  - [ ] `oh_no` lifecycle gated on global `terror_beginning.his_hunt`.
- [ ] Reduce `information` to stateless helper methods.
- [ ] Multiplayer QA on a dedicated server:
  - [ ] 2+ players run independent, concurrent events.
  - [ ] Login/logout, death/respawn NBT cloning (`terror_beginning.save_info`).
  - [ ] Full event roster verified per-player.
- [ ] Version bump + changelog + README update.
