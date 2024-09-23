---
layout: page
title: Mechanics
---

## Triggering Mechanism

### Raid Omen

The main triggering mechanism of the plugin, where every raid in the server turns into a large raid. As such, it is inadvisable to enable this mechanism alongside others at the same time.

A large raid have at least 5 waves. With raid omen level of 6 or higher, the raid will have equal the number of waves as its raid omen level. _This differs from a normal raid, where raid omen level of 2 or higher entails a bonus round._

> ❓ **TRIVIA**: If a player with Raid Omen enters a large raid, the effect will be absorbed by the progressing raid, increasing its raid omen level.

The maximum number of waves is set by the `raid.waves` configuration option in `config.yml`, instead of depending on individual worlds' difficulties.

### Dropping Item into Lava

This triggering mechanism is inspired by Terraria, where players summon the Wall of Flesh by throwing a Voodoo Doll into lava. The summoning item can be configured in `config.yml` and set as a crate reward (command/item). Upon throwing the summoning item into lava within the vicinity of a village, a raid will be triggered. The raid will have the maximum raid omen level, set by the `raid.waves` configuration option in `config.yml`.

> 💡 **TIP**: You can create maps with a lava well as an artificial village center to make a cool raid arena.

### Time Bomb

This mechanism aims to surprise unprepared players loitering around villages during a certain tick in the Minecraft's [daylight cycle](https://minecraft.fandom.com/wiki/Daylight_cycle). The raid will have the maximum raid omen level, set by the `raid.waves` configuration option in `config.yml`.

## Behaviour

![Raid FlowChart](./assets/images/raid-flowchart.png){: .image-center }

Notable differences in mechanisms between large raids and normal raids include:

- If the spawn location of a wave is out of radius from the village center, the wave will be aborted (no raiders will spawn) and the next wave will be triggered immediately. This is to prevent the use of automatic raid farms.
- When a wave spawns, raiders are invulnerable for 1 second to avoid entity cramming.

Otherwise, large raids mimic the behaviour of normal raids, sharing mostly similarities. Hence, it can be easily defeated yet again given enough preparation, altering of landscape or taking advantage of it. Hence, an `artificial-only` configuration option is provided in the `config.yml`.

If `artificial-only` is enabled, then large raids can only be triggered around registered artificial village centers. As the game takes surrounding occupied village blocks/points of interest (POI) to determine a raid center, artificial village centers must be at least 128 blocks/8 chunks away from each other and other villager-claimed POIs (e.g. job block, bed, bell) to maximize the effective area for triggering large raids. As the raid omen triggering mechanism turns every raid into a large raid, no raids (normal/large) can be triggered in normal villages if `artificial-only` is enabled.

<style>
    .image-center {
        display: block;
        float: none;
        margin-left: auto;
        margin-right: auto;
    }
</style>

## Miscellaneous

Wandering traders have a _0.1%_ chance of dropping a written book upon death. It is a write-up on the trader's experience and witness on the tragedy of a raid.
