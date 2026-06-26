# Flywheel of Terror — Multiplayer Conversion

> **All credit and original authorship belongs to the original developer of Flywheel of Terror.**
> This repository is an unofficial multiplayer conversion of their work. We make no claim of ownership
> over the original mod, its assets, sounds, or gameplay design. If the original author wishes this
> repository to be taken down, we will comply immediately.

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

**What we found:** When we decompiled version 0.7 for this multiplayer conversion, we did find
offensive variable names — specifically a racial slur used as NBT key names in `world_change` and
as an internal identifier in `labyrinth_event`. These were not visible to players in any way, but
they were present in the code.

**What we did about it:** As part of the Phase 0 cleanup during the multiplayer conversion, we renamed
every offensive identifier to neutral alternatives (`corrupted_trees`, `mammoth`, `corruptedimmune`)
before any other work began. The gameplay and horror experience is completely unchanged — only the
internal variable names were touched.

The mod itself is genuinely not racist in its content or intent. The Russian developer appears to have
used the word without understanding its weight in English — a language barrier issue rather than
deliberate malice, though that doesn't make it acceptable to leave in. It's been fixed.

---

## What is this repository?

This is a community multiplayer conversion of Flywheel of Terror v0.7 for Minecraft 1.20.1 (Forge).
The original mod was designed exclusively for single-player and was never intended to run on a
dedicated server. This project rewrites the necessary parts to make it work properly in multiplayer
while keeping the horror experience intact.

**Version:** 1.0.0
**Based on:** Flywheel of Terror v0.7
**Loader:** Forge 1.20.1 (47.4.20)
**Java:** 17

---

## What changed from the original

- Single JAR that works on both server and client (original required separate patched versions)
- Dedicated server no longer crashes on load (client-only graphics code was running on the server)
- Each player now has their own independent horror state — one player's events don't affect others
- Custom mobs (somewho, fake_steve, headless_steve, faceless variants) now track their own target player individually
- Health and kill tracking is per-player, not shared across everyone on the server
- All offensive internal identifiers removed and replaced with neutral names
- Version bumped to 1.0.0

---
