# CLAUDE.md — Flywheel of Terror (Multiplayer Conversion)

This file documents the decompiled source of **Flywheel of Terror v1.0.1** (Minecraft 1.20.1,
Forge 47.x, Java 17) and the plan to convert it from a single-player-only horror mod into a mod
that runs correctly on a dedicated server with multiple independent players.

> Status: **audit only — no code has been changed yet.** The `decompiled/` tree is the raw
> Vineflower output (SRG-named obfuscated method calls like `m_5776_()`, `m_128469_()`). The
> source must be remapped to official Mojang mappings before it can be built in a Forge MDK
> workspace (see Phase 1).

---

## 1. How the mod works (architecture overview)

The mod has **no central manager class**. Instead it is a large collection of `@Mod.EventBusSubscriber`
classes, each subscribing to Forge events (`PlayerTickEvent`, `ServerTickEvent`, `LivingDeathEvent`,
render/screen events, etc.) and each implementing one "horror event." All of these classes were
written against the assumption of **exactly one player in an integrated (single-player) server**,
where the client thread and the integrated-server thread share the same JVM and therefore the same
`static` fields.

Player progression is stored in the player's **persistent NBT** under the compound key
`"flywheel_of_terror"` (accessed via `player.getPersistentData().m_128469_("flywheel_of_terror")`).
This part is already per-player and is the one thing the original did right. The problem is
**everything that is not in that NBT compound** — transient event state, timers, sound/render
flags, and the "current player" — which lives in `public static` fields.

### The single-player assumptions that break in multiplayer

1. **`public static` mutable fields used as per-player state.** Because there is only one player in
   SP, a static like `terror_beginning.his_hunt` or `health_decrease.max_hp` effectively *is*
   per-player. On a server with N players these statics are shared, so the last player to tick wins
   and every player's experience collapses into one shared global state.

2. **Cross-side signalling through statics.** The dominant pattern is: the server-side branch of a
   `PlayerTickEvent` sets a static flag (`sound_must_be = true`), and the client-side branch of the
   *same* handler reads it and plays a sound / draws an overlay. This works **only** in SP because
   client and server are the same JVM. On a dedicated server the client is a different process, the
   flag is never seen, and the audio/visual half of every horror event silently fails.

3. **`net.minecraft.client.Minecraft` called from common (server-side) code.** Many FORGE-bus
   handlers (`PlayerTickEvent`, `PlayerLoggedInEvent`, `LivingDeathEvent`) call
   `Minecraft.m_91087_()` directly with no side guard. On a dedicated server the client classes do
   not exist on the classpath → `NoClassDefFoundError` / crash on load or first tick.

4. **A global "current player" singleton (`information`).** `information.just_player`,
   `information.igrok`, `information.livingingrok`, `information.playeruuid`,
   `information.block_under_player`, `information.time`, and `information.current_screen` are static
   and are overwritten every tick by whichever player ticked last. Every custom mob reads these to
   pick its target, so in MP **all custom mobs target one player** and react to one player's screen.

5. **Client-authoritative NBT writes.** Several events write to `getPersistentData()` on the
   **client** side of a tick (`m_5776_()`/`f_46443_` true). In SP the client and server entity are
   the same object so the write sticks; on a dedicated server the client player is a throwaway copy
   and the write is lost / not authoritative.

These five categories map directly onto the refactor phases in §4.

---

## 2. Class-by-class reference

### 2.1 Bootstrap & registration

| Class | What it does | Multiplayer concern |
|---|---|---|
| `flywheel_of_terror` | `@Mod` entry point. Registers DeferredRegisters (items, sounds, entities), entity attributes, the dimension-travel `hell` handler. Calls `MixinEnvironment...setSide(CLIENT/SERVER)`. | `hell()` reads `house_defend.bed_here` / `pos_of_bed` (statics, see below). The dual `setSide` call is suspect. Otherwise OK. |
| `add_humans` | DeferredRegister of all 20 custom `EntityType`s. | Fine. (Note: `wrong_cow` is registered with the name string `"wrong_sheep"` — pre-existing bug, harmless.) |
| `add_items` | DeferredRegister of items: `my_knife`, `notice`, `punishment`, `your_legacy`. | Fine. |
| `register_sounds` | DeferredRegister of all `SoundEvent`s. | Fine. |
| `Config` | Boilerplate Forge `ForgeConfigSpec` (the MDK example config; `logDirtBlock`/`magicNumber`). Unused by gameplay. | Harmless. Candidate for deletion. |
| `nbt_adresses` | String constants for a few NBT keys (`phase`, `between`, `seconds_to_die_oh_no`). | Fine. |

### 2.2 Core game-state controllers (heaviest refactor targets)

| Class | What it does | Multiplayer concern |
|---|---|---|
| `terror_beginning` | The "build a house / get hunted / oh_no chase" core loop, day-cycle driven. | **16 static per-player fields**: `his_hunt`, `near_house`, `away_house`, `far_away_house`, `first_message_was`, `killed_victims`, `count_of_victims`, `house_builded_now`, `sound_terror`, `hunt_sound_must_be`, `sound_house_destroyed`, `sound_must_be`, `tics_to_next_sound`, `tics_to_next_house`, … All shared across players. Manipulates server time `serv.m_8615_()` globally (affects all players). Cross-side sound flags. |
| `terror_continue` | Random "intruder" events once a house exists: labyrinth, true_me, your_legacy, plita trap. | **9 statics**: `need_try`, `near_maze`, `tics_cooldown`, `xxx/yyy/zzz` (last labyrinth coords — shared!), `sound_must_be`, `sound_chest_must_be`. `tics_cooldown` is decremented on the **client** side. |
| `terror_end` | Tracks the `phase` NBT and the "all events done → oh_no dies → ending" countdown. | `public static int phase` is global; read by many classes (`game_rules`, `terror_beginning`, etc.) as a gameplay gate. Must become per-player. The per-player half is already in NBT. |
| `house_defend` | Auto-repairs the player's claimed house, lightning-punishes attackers, tracks bed position. | **6 statics**: `breaked_for_last_seconds`, `bed_here`, `pos_of_bed` (used by `flywheel_of_terror.hell`), `blocks_repair_per_time`, `required_to_thunder`. Shared. |
| `paranoia` | The biggest "ambient dread" controller: undefined noises, the master event scheduler (`time_to_event` switch over 26 cases), phone "calls", forced doors, sleep scares. | **18 statics**, almost all per-player timers/flags. `do_a_call()` and `view()` call `Minecraft.m_91087_()` but `do_a_call` is invoked from **server-side** `terror_beginning` → **crash on dedicated server**. `time_to_event` decremented client-side. Central scheduler must become per-player + server-authoritative. |
| `information` | Utility + the **global player singleton**. Holds `just_player`, `igrok`, `livingingrok`, `playeruuid`, `block_under_player`, `time`, `current_screen`, `show_model`, `last_dropped`. Also helpers (`thunder_player`, `play_sound_at_server`, `do_a_silence`, coordinate strings) and the debug chat commands. | **The keystone problem.** Every custom mob targets `just_player`/`igrok`/`livingingrok`. `return_to_normal(PlayerLoggedInEvent)` calls `Minecraft` on the common bus → crash. `current_screen`/`show_model` are client-only but read by server-side schedulers (`paranoia`, `periferia`). |
| `health_decrease` | Per-player max-HP-decreases-on-death / increases-on-kills RPG mechanic. | **`static float max_hp` and `static int count_of_kills`** — loaded from one player's NBT on login, written back into *every* ticking player's NBT. In MP all players converge to a single shared HP/kill total. Textbook static-per-player bug. |

### 2.3 Horror "event" classes (FORGE-bus subscribers)

All follow the same shape: a `static boolean event_in_process` / timer, a `do_event(player)` trigger
called by `paranoia`'s scheduler, and a `PlayerTickEvent` that splits on `m_5776_()`. Unless noted,
the concerns are: **(a)** `event_in_process` / timers are global statics, **(b)** client A/V is
gated by a cross-side static flag, and many **(c)** also touch client classes.

| Class | What it does | Notable extra concern |
|---|---|---|
| `horror_environment` | Tags nearby vanilla mobs with random "corruption" behaviours (god/terror/coward/smart/fake/uroboros/scarecrow…), spawns `terror_*` variants, deletes `true_me`/`headless_steve`/`somewho` on contact. | Reads `information.just_player`/`livingingrok` for mob aggro. Uses per-entity NBT for tags (good) but driver flags `sound_must_be(2)` are static cross-side. |
| `labyrinth_event` | Builds the structure-NBT labyrinth, blindness + ambience while inside, spawns headless villagers, breaks bedrock guard. | `in_lab`, `state`, `event_in_process`, `tics_to_reload_music` static & shared. `terror_continue.xxx/yyy/zzz` consumed here. |
| `game_rules` | Forces 1st-person, render distance cap, peaceful→normal, villagers flee, iron golems aggro, "cursed" food/kill hunger rules. | `every(PlayerTickEvent)` calls `Minecraft.m_91087_()` **unconditionally** → dedicated-server crash. Gated on global `terror_end.phase`. |
| `all_look_at_you` | Forces 3rd-person, makes all nearby entities stare at the camera, god-mode during. | `every(PlayerTickEvent)` calls `Minecraft`/`Camera` unconditionally → crash. `tics_of_looking` global. |
| `eye_intervention` | Draws eyes on screen + reddening shader; per-login eye layout. | `static List` eye state shared by all players; `render_eyes` on `RenderGuiEvent` (client-only bus, OK) but the lists are global. `do_event` only sets statics (safe to call server-side). |
| `oh_no_between_screens` | Plays the 37-frame "oh_no" overlay animation between/over GUI screens. | Frame counters static & global; rendered for all clients at once. |
| `he_is_here_event` | "hello" chat → spawns `oh_no_here` stalker, distance HUD. | `do_event()` calls `Minecraft.m_91087_()` and is invoked from **server-side** `paranoia` → crash. Per-player timers already in NBT (good model to copy). |
| `fake_darknet_access` | On kill, takes a screenshot + fake "darknet access failed" message. | `do_event()` uses `Minecraft`/`Screenshot`, invoked from server-side `kill(LivingDeathEvent)` → crash. `tics_to_error` global. |
| `family` | Spawns a villager "family"; despawns them when approached; shows a house image overlay. | `render_house` calls `Minecraft` (client bus OK); `tics_of_house` static. Spawns fine server-side. |
| `panic` | `faceless_villager` multiplies when hit, then nightmare blackout + mob swarm. | `tics_of_black`, `event_in_process(2)`, sounds — static & global. Shader/sound cross-side. |
| `apocalypsis_event` | Blood-red sky, sets world time, burning trees lift off, spawns `headless_steve`. | Calls `serv.m_8615_()` / `m_8606_()` — **global** world time & weather, affects all players. Static timers. |
| `below_event` | Sinks the player through a collapsing void shaft, traps them. | All state static (`exclude_escape`, `XC/ZC`, `height`, `time_to_end`). Logic split awkwardly across client/server branches. |
| `exist_terror_event` | Types a "Let me out" message char-by-char into a fake chat screen, blocks damage/chat. | **Writes NBT on the client side** (`f_46443_`) and drives a `ChatScreen` from a common `PlayerTickEvent`. Won't persist on dedicated server. |
| `shipwrecked` | If the player is far out at sea, turns water red and drowns them. | `red_water`/`sound_must_be` static; render shader cross-side. Per-player bits already in NBT. |
| `something_wrong` | Silently reshapes terrain behind the player (pits, hills, removes foliage) + silences sounds. | `seconds_to_change` decremented client-side; `tics_of_silence` global. |
| `advanced_baron_detector` | If player name contains "baron" and they tower up, punishes the tower. | `tics_to_destroy_tower` static & decremented client-side. Name-gated. |
| `paralysis_event` | Freezes the player's position/rotation for N seconds; kills nearby mobs. | `time`, `yrot/xrot`, `xpos/ypos/zpos` static — one player's freeze pose shared by all. |
| `thunder_behind` | Lightning + chest-with-bait behind the player; "turn around" madness shader. | `tics_to_punch`/`tics_of_madness` decremented client-side; `event_in_process` global. |
| `circle_christ` | (event ~130 lines) ritual-circle event with cross-side sound flags. | `event_in_process`, `sound_must_be(2)` static cross-side. |
| `forge_revenge` | Anvil/forge-themed punishment event. | `event_in_process` global. |
| `angel_sound_event` | Plays the "angel" sound event server→client. | `event_in_process` global; server-only branch sets NBT (ok-ish). |
| `sound_heart` / `sound_knife_attack` | Play a heartbeat / knife sound; mark event done. | `event_in_process` + reload timer static; client-gated sound. |
| `tool_break` | Fakes the player's tool breaking. | `event_in_process`, `sound_must_be`, timer static cross-side. |
| `fire_steps` | Footstep-fire trail for a duration. | `tics_of_event` static, client-driven. |
| `notice_in_inventory` | Drops a creepy named "notice" into the player's inventory. | `event_in_process` global (server branch, ok-ish). |
| `periferia` | While in the inventory screen, spawns `fake_steve` in the periphery. | Reads `information.current_screen` (client-only) from a **server** `PlayerTickEvent` — never true on dedicated server. `event_in_process`/`tics_to_spawn` global. |
| `invisible_path` | Spawns an `invisible` mob at a random nearby location. | Stateless trigger; fine. |
| `oh_no_stalker_event` | Spawns `oh_no_stalker` at the nearest tree. | `static BlockPos nearest_tree` shared; recomputed each tick for last player. |
| `remove_entities` | "No life" window: cancels mob spawns / deletes nearby mobs, floors hunger. | `tics_without_life` static & global; cross-references `terror_beginning.first_message_was`/`his_hunt`. |
| `decline_run` | Knock-back / movement-decline punishment. | `tick_to_knock`, `sound_must_be` static, client sound. |
| `joke` | Minor server-side gag event. | `event`-style, low risk. |
| `hunger` | Hunger-related rule tweaks. | Server-gated (`f_46443_`); low risk. |
| `only_zombies` | Forces/biases mob spawns toward zombies. | `Random` static only; low risk. |
| `first_entry` | On first login sets walk speed + initial call timer; marks `first_entry` NBT. | Per-player NBT; fine. Note it sets `first_entry` every tick. |
| `independence` | Periodically possesses the player to sprint at + kill the nearest mob ("hunt"). | `current_target` (`static LivingEntity`) + `tics_of_hunt` shared. `tick_move_player_to_living_entity` calls `Minecraft.m_91087_()` from server-side tick → crash. |
| `deep_terror` | Replaces the pause & title screens with the fake menus. | Subscribes `ScreenEvent.Opening` (client-only bus) → safe, but constructs client `Screen`s. |
| `declinemusic` | `Dist.CLIENT` — suppresses background music. | Properly client-guarded. Fine. |

### 2.4 Custom entities

All extend a vanilla mob and are server-authoritative for movement, **except** that several read the
global `information.*` singleton to choose their target. Renderers are separate (client) classes.

| Class | Base | Multiplayer concern |
|---|---|---|
| `somewho` | `PathfinderMob` | Targets `information.livingingrok`/`just_player`/`igrok`; mirrors that player's held items. |
| `fake_steve` | `PathfinderMob` | Faces/targets `information.just_player`. |
| `headless_steve` | `PathfinderMob` | Faces `information.just_player`, paths to `information.igrok`. |
| `true_me` | `PathfinderMob` | Knife attacker; uses side checks; spawned per-player but globally targeted. |
| `oh_no` | `PathfinderMob` | The main chaser. Targets *all* players in 400 blocks (already MP-ish) but lifecycle gated on global `terror_beginning.his_hunt`. |
| `oh_no_here` / `oh_no_stalker` / `oh_no_behind` | `PathfinderMob` | Stalkers; server-side logic, teleport-behind behaviour; target nearest player. |
| `invisible` | `PathfinderMob` | Invisible stalker; client/server split. |
| `headless_villager` | `Villager` | Labyrinth mob; despawn timer in entity NBT (good). |
| `faceless_villager` | `Villager` | `hits_to_remove` field; used by `panic`. |
| `invalid` | `Zombie` | Corrupted zombie variant. |
| `terror_pig` / `terror_cow` / `terror_sheep` | `Pig`/`Cow`/`Sheep` | Aggressive farm-animal variants spawned by `horror_environment`. |
| `wrong_sheep` / `wrong_cow` | `Sheep`/`Cow` | Uncanny variants; `wrong_sheep` faces `information.just_player`. |
| `faceless_pig` / `faceless_cow` / `faceless_sheep` | `Pig`/`Cow`/`Sheep` | Faceless variants. |

### 2.5 Client-only rendering & UI (already correctly isolated)

- `client/client_zombie` — `Dist.CLIENT` client setup (renderer registration). OK.
- `client/renderer/*_renderer` (20 files) — one `EntityRenderer` per custom entity. Pure client. OK.
- `fake_main_menu`, `fake_exit`, `fake_menu_update` — fake `Screen`s (`fake_main_menu` partially
  `@OnlyIn(Dist.CLIENT)`; `fake_menu_update` fully). Client only. OK.
- `fake_menu_hook` (1222 lines) — a huge client-side reimplementation of menu/render internals
  (imports hundreds of `net.minecraft.client.*`). Client only. OK structurally, but verify it does
  not break a real `JoinMultiplayerScreen` flow (it imports it).
- `fake_menu_renderer`, `fake_preset` — client helpers for the fake menu. OK.
- `position_and_size_at_screen` — plain data holder for on-screen rectangles (used by overlay
  events). Side-agnostic; fine, though it lives in the common package.

### 2.6 Mixins (`mixin/`)

| Mixin | Target | Notes |
|---|---|---|
| `scarecrow` | `Entity#tick` (`@Overwrite`) | Freezes ticking for mobs tagged `scarecrow`. **`@Overwrite` of `Entity.tick` is dangerous** (mod-incompat, runs both sides). Review. |
| `scarecrow2` | `LivingEntity` | Death-animation freeze for scarecrow-tagged mobs; server-guarded. |
| `wrong_die` | `LivingEntity` | `@Shadow`-only accessor mixin (exposes `f_20916_`/`f_20917_` + death methods). Low risk. |
| `less_trees` | `TreeFeature` (`@Overwrite`) | Makes ~29/30 trees fail to generate (worldgen). `@Overwrite` — review for incompatibility. |
| `fake_invent` | `InventoryScreen` (`@Overwrite`) | Client-only mixin that conditionally hides the inventory player model based on **`information.show_model`** (a global static). In the previous version this mixin was deleted as runtime-fragile (see memory `[[fake-invent-mixin-followsangle]]`); evaluate removing it here too. |

### 2.7 Items

`myknife`, `alwaysknife`, `punishment` (+ the `my_knife`/`notice`/`your_legacy`/`punishment`
registrations in `add_items`). Standard `Item` subclasses; no obvious MP issue.

---

## 3. Offensive-identifier audit

The README states v6.6.6 contained a racial slur in NBT keys/identifiers inside `world_change` and
`labyrinth_event`, cleaned up by v0.7. A scan of this v1.0.1 decompile:

- **No common English slurs found** in any identifier (`grep -niE` over all `.java`). 
- `world_change` **does not exist** in this tree (the class is gone / renamed).
- The README's stated replacements (`corrupted_trees`, `mammoth`, `corruptedimmune`) are **not
  present either**, suggesting this decompile is already past that cleanup.
- Some non-slur but unprofessional identifiers remain in `horror_environment` (`bebra`, a Russian
  slang word) and Cyrillic `System.out.println` debug strings in `flywheel_of_terror` and
  `game_rules`. These are harmless but worth tidying.

**Conclusion:** Phase 0 (offensive-identifier removal) appears already complete for this version;
the refactor should *verify* this early and otherwise focus on the multiplayer work.

---

## 4. Prioritized 4-phase refactor plan

The phase structure mirrors the previous version's plan (build/cleanup → de-globalize → networking
→ targeting/QA). See `TODO.md` for the checklist form.

### Phase 1 — Foundation: remap, build, and dedicated-server safety
Goal: a single JAR that **loads on a dedicated server without crashing.**
1. Verify the offensive-identifier audit (§3) — confirm clean, tidy stray debug strings/`bebra`.
2. Remap the SRG decompile to official Mojang mappings and stand up a Forge 1.20.1 MDK workspace so
   it compiles (see memory `[[srg-source-needs-remap]]`).
3. Isolate **all** `net.minecraft.client.Minecraft`/`Camera`/`Screenshot`/`ChatScreen` access that
   currently runs on the common/server bus. Move it to client-only event subscribers
   (`Dist.CLIENT` `@EventBusSubscriber`), `DistExecutor.unsafeRunWhenOn`, or behind packet handlers.
   Known crashers: `game_rules.every`, `all_look_at_you.every`, `information.return_to_normal`,
   `independence.tick_move_player_to_living_entity`, `paranoia.do_a_call`, `he_is_here_event.do_event`,
   `fake_darknet_access.do_event`.
4. Audit the two `@Overwrite` mixins (`scarecrow`→`Entity.tick`, `less_trees`→`TreeFeature`) and the
   `fake_invent` mixin for server-side safety / removal.

### Phase 2 — De-globalize per-player state
Goal: each player has independent horror state.
1. Inventory every `public static` mutable field that represents per-player state (see §2.2/§2.3 —
   ~120+ fields across the listed classes).
2. Move them into per-player storage: extend the existing `"flywheel_of_terror"` NBT compound, or
   introduce a capability/attachment + a small per-player state object keyed by `UUID`.
3. Make `terror_end.phase` and `health_decrease.max_hp`/`count_of_kills` per-player.
4. Make world-affecting calls player-scoped where possible (`serv.m_8615_()` time / weather in
   `terror_beginning`, `apocalypsis_event` currently mutate global world state for everyone).
5. Defer the audio/visual halves (still static-flag driven) to Phase 3.

### Phase 3 — Client/server networking layer
Goal: replace static cross-side signalling with real packets.
1. Add a `SimpleChannel` (S2C + C2S) (see memory `[[phase3-networking-layer]]`).
2. Replace every `sound_must_be` / shader / overlay static flag with an **S2C** packet so each
   client only gets its own events (sounds, `RenderSystem.setShaderColor`, GUI overlays, eyes,
   oh_no frames, forced camera/render distance, fake chat typing).
3. Send client-only state the server scheduler needs (e.g. `current_screen`/inventory-open,
   `show_model`) to the server via **C2S** packets instead of reading client statics from
   `paranoia`/`periferia`.
4. Make all cross-side timers (`tics_cooldown`, `time_to_event`, `tics_to_*`) server-authoritative.

### Phase 4 — Per-mob target tracking, polish & QA
Goal: correct multi-player behaviour and verification.
1. Give each custom mob its own target (saved field / `m_6710_` target) instead of reading
   `information.just_player`/`igrok`/`livingingrok`. Set the target at spawn from the triggering
   player. Affected: `somewho`, `fake_steve`, `headless_steve`, `true_me`, `wrong_sheep`,
   `horror_environment` aggro logic, `oh_no*` lifecycle.
2. Retire/trim the `information` singleton to stateless helpers only.
3. Multiplayer testing: 2+ players on a dedicated server, concurrent independent events, login/
   logout, death/respawn NBT cloning (`terror_beginning.save_info`), and the full event roster.
4. Version bump + changelog.

---

## 5. Working notes / conventions

- The decompiled tree uses **SRG names** (`m_xxxxx_`, `f_xxxxx_`). Do not hand-edit these for
  gameplay until remapped (Phase 1). When reading, common ones: `m_5776_()` = `Level.isClientSide`,
  `f_46443_` = `Level.isClientSide` field, `m_9236_()` = `Entity.level()`, `getPersistentData()` /
  `m_128469_` = NBT compound get, `m_128365_` = NBT put.
- Per-player progress is the NBT compound `"flywheel_of_terror"`; many `get_*`/`set_*` helpers
  already wrap it — copy that pattern rather than adding new statics.
- `paranoia.check_weak`'s 26-case `switch` is the master event dispatcher; understand it before
  touching any individual event.
- Do not change code until asked — this document and `TODO.md` are the deliverable for now.
