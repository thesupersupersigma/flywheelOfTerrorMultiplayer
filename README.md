# Flywheel of Terror — Multiplayer Conversion

> **All credit and original authorship belongs to the original developer of Flywheel of Terror.**
> This repository is an unofficial multiplayer conversion of their work. We make no claim of ownership
> over the original mod, its assets, sounds, or gameplay design. If the original author wishes this
> repository to be taken down, we will comply immediately.
> Licensed under Creative Commons 4.0 by korost.

---

## Why was this mod removed from CurseForge and Modrinth?

The short answer is: **a racial slur in the source code**, not the content of the mod itself.

Reddit user **u/TheRealOracleFox** did the digging and summarized it well: version 6.6.6 of the mod
contained a racial slur embedded in NBT keys and internal identifiers inside `world_change.class` and
`labyrinth_event.class`. This is the real reason CurseForge removed it. The scarier surface-level
content people assumed was the cause — faces of real people, disturbing imagery — had already been
toned down by version 0.7, the version this conversion is based on.

Reddit user **u/SebastianSekkusuu** independently decompiled the mod across three separate copies and
could not find any slurs in version 0.7, suggesting the identifiers were cleaned up between 6.6.6 and
0.7, or were subtle enough that they were missed by casual inspection. There is genuine disagreement
in the community about whether 0.7 still contained them.

**What we found:** When we decompiled version 1.0.1 for this multiplayer conversion, no racial slurs
were found — the author had already cleaned them up by this version.

---

## What is this repository?

This is a community multiplayer conversion of Flywheel of Terror v1.0.1 for Minecraft 1.20.1 (Forge).
The original mod was designed exclusively for single-player and was never intended to run on a
dedicated server. This project rewrites the necessary parts to make it work properly in multiplayer
while keeping the horror experience intact.

**Version:** 1.0.2
**Based on:** Flywheel of Terror v1.0.1 by korost
**Loader:** Forge 1.20.1 (47.4.20)
**Java:** 17

---

## What changed from the original

- Single JAR that works on both server and client
- Dedicated server no longer crashes on load
- Each player now has their own independent horror state — one player's events don't affect others
- All ~120 shared static fields moved to per-player NBT storage
- Full networking layer added (the original had zero networking) — sounds, shaders, and overlays
  are now sent as proper server→client packets to the correct player
- Custom mobs (`somewho`, `fake_steve`, `headless_steve`, faceless variants) now track their own
  target player individually via stored UUID
- Health and kill tracking is per-player
- All mixin `@Overwrite` targets fixed to use SRG names so they work at runtime
- Added a "hunt" multiplayer button to the custom main menu
- OP command system added (`/fot`) for server operators to control the horror experience
- Version bumped to 1.0.2

---

## Known multiplayer limitations

These are single-player features that could not be perfectly replicated in a true dedicated server
environment due to fundamental differences in how Minecraft handles certain systems:

**World time and weather during events** — In single-player, events like `apocalypsis_event` and the
nightly hunt in `terror_beginning` change the time of day and weather for the player experiencing
them. In multiplayer, Minecraft has one shared world clock and weather system for the entire server,
so these changes affect all players simultaneously rather than just the target. This is an inherent
Minecraft limitation — true per-player time and weather would require per-client packet overrides
on every tick, which was considered out of scope for this conversion.

**Inventory screen detection (`periferia` event)** — In single-player, the mod could directly check
whether your inventory was open client-side. On a dedicated server the server cannot see what screen
a client has open, so this is handled via a client→server sync packet. The `periferia` event, which
triggers when your inventory is open, may not fire as reliably on a dedicated server as it did in
single-player.

---
