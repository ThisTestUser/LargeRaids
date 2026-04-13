# LargeRaids

[![](https://jitpack.io/v/zhenghanlee/LargeRaids-API.svg)](https://github.com/zhenghanlee/LargeRaids-API)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/e2b8ef0d41e3404b91a62a35196c7e9e)](https://www.codacy.com/gh/zhenghanlee/LargeRaids/dashboard?utm_source=github.com&utm_medium=referral&utm_content=zhenghanlee/LargeRaids&utm_campaign=Badge_Grade)
[![License](https://img.shields.io/github/license/zhenghanlee/LargeRaids)](https://img.shields.io/github/license/zhenghanlee/LargeRaids)
[![Spigot Downloads](http://badge.henrya.org/spigotbukkit/downloads?spigot=95422&name=spigot_downloads)](https://www.spigotmc.org/resources/largeraids-1-14-x-1-17-x.95422/)
[![Commit Activity](https://img.shields.io/github/commit-activity/m/zhenghanlee/LargeRaids)](https://img.shields.io/github/commit-activity/m/zhenghanlee/LargeRaids)
[![Discord](https://img.shields.io/discord/846941711741222922.svg?logo=discord)](https://discord.gg/YSv7pptDjE)

**LargeRaids** is a vanilla game experience enhancement plugin for [raids](https://minecraft.fandom.com/wiki/Raid), which were added to the game in the _Village & Pillage Update_. It expands the raid's mechanism to accommodate for the multiplayer environment with higher difficulties, higher raid omen levels, more raiders, more waves and better rewards.

This is a fork of the original LargeRaids plugin, updated to the latest Minecraft versions. All the latest builds for each version can be found in the releases tab. Older versions are no longer supported.

This plugin will work for both Spigot and Paper, but the latter is recommended because one of the optional extensions (MythicMobs) relies on it.

## Updating to Later Versions

In build.gradle.kts, change the variable mcVersion to the new version. Go to LargeRaids.java and change the version also. Then run ```gradlew build```. If there are still errors, the source code will need to be manually updated to be compatible with the new Minecraft version.

In addition to fixing errors, you should look at the obfuscated mapping for the ```groupsSpawned``` variable over at [mappings.dev](https://mappings.dev/) in class ```net.minecraft.world.entity.raid.Raid```. If it is not ```H```, then you should change it at ```RaidWrapper.setGroupsSpawned()```.

For 1.21.11, the mapping is here: https://mappings.dev/1.21.11/net/minecraft/world/entity/raid/Raid.html

## Known Bugs
For any large raid, the minimum Hero of the Village effect given is level 2. This affects all versions of LargeRaids (including versions from the original mod author), and is caused by vanilla giving out the effect before ```RaidFinishEvent``` is fired.

## Statistics

[![](https://bstats.org/signatures/bukkit/LargeRaids.svg)](https://bstats.org/plugin/bukkit/LargeRaids/13910)

## Installation

This fork now uses Paper's gradle build system. Run 'gradlew build' to generate the output JARs. If you wish to import this project into your IDE, import it as a gradle project after you've run the command.

## Using the API

The API for the plugin has a separate [repository](https://github.com/zhenghanlee/LargeRaids-API). The instructions are duplicated here for your convenience.

### Maven Repository

You can add the project as your dependency by including the JitPack repository in your `pom.xml`:

```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

Then after add the dependency like so (replace `VERSION` with the version provided by the jitpack badge located at the start of this document):

```xml
<dependency>
	<groupId>com.github.zhenghanlee</groupId>
	<artifactId>LargeRaids-API</artifactId>
	<version>VERSION</version>
</dependency>
```

### Gradle Repository

You can add the project as your dependency by including the JitPack repository:

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Then after add the dependency like so (replace `VERSION` with the version provided by the jitpack badge located at the start of this document):

```gradle
dependencies {
	    implementation 'com.github.zhenghanlee:LargeRaids-API:VERSION'
}
```

### Example Usage

#### Getting the Plugin

```java
Plugin plugin = Bukkit.getPluginManager().getPlugin("LargeRaids");
if (plugin != null) {
    LargeRaids lr = (LargeRaids) plugin;
    // Rest of the operations here
}
```

#### Getting Corresponding LargeRaid

A LargeRaid object can be obtained from either a Bukkit's `Location` or Bukkit's `Raid` instance.

```java
RaidManager raidManager = lr.getRaidManager(); // where lr is a LargeRaids instance
Optional<LargeRaid> raid = raidManager.getLargeRaid(location);
```

#### Getting Player Kills

We can get the number of kills a player have in a large raid when it finishes (or any time of the raid) as follows:

```java
@EventHandler
public void onRaidFinish(RaidFinishEvent evt) {
    Raid raid = evt.getRaid(); // Vanilla raid
    if (raid.getStatus() != RaidStatus.VICTORY)
        return;
    Optional<LargeRaid> largeRaid = raidManager.getLargeRaid(raid);
    if (!largeRaid.isPresent()) // No corresponding LargeRaid instance
        return;
    Optional<Integer> killCount = largeRaid.map(LargeRaid::getPlayerKills)
            .map(map -> map.get(player.getUniqueId()));
    if (!killCount.isPresent()) // Player is not a hero of this raid
        return;
    // Perform operations with the kill count (e.g. rewarding players based on kill count)
}
```
