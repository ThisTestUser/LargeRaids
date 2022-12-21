# LargeRaids

[![](https://jitpack.io/v/zhenghanlee/LargeRaids-API.svg)](https://github.com/zhenghanlee/LargeRaids-API)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/e2b8ef0d41e3404b91a62a35196c7e9e)](https://www.codacy.com/gh/zhenghanlee/LargeRaids/dashboard?utm_source=github.com&utm_medium=referral&utm_content=zhenghanlee/LargeRaids&utm_campaign=Badge_Grade)
[![License](https://img.shields.io/github/license/zhenghanlee/LargeRaids)](https://img.shields.io/github/license/zhenghanlee/LargeRaids)
[![Spigot Downloads](http://badge.henrya.org/spigotbukkit/downloads?spigot=95422&name=spigot_downloads)](https://www.spigotmc.org/resources/largeraids-1-14-x-1-17-x.95422/)
[![Commit Activity](https://img.shields.io/github/commit-activity/m/zhenghanlee/LargeRaids)](https://img.shields.io/github/commit-activity/m/zhenghanlee/LargeRaids)
[![Discord](https://img.shields.io/discord/846941711741222922.svg?logo=discord)](https://discord.gg/YSv7pptDjE)

**LargeRaids** is a vanilla Spigot game experience enhancement plugin for [raids](https://minecraft.fandom.com/wiki/Raid), which are added to the game in the _Village & Pillage Update_. It expands the raid's mechanism to accommodate for the multiplayer environment with higher difficulty, higher bad omen levels, more raiders, more waves and higher rewards.

This is a fork of the original LargeRaids plugin, updated to 1.19.2. This version will ONLY support 1.19.2.

## Server Requirements

- Version: 1.19.2

## Statistics

[![](https://bstats.org/signatures/bukkit/LargeRaids.svg)](https://bstats.org/plugin/bukkit/LargeRaids/13910)

## Installation

This fork now uses Paper's gradle build system. Run 'gradlew build' to generate the output JARs.

Even though the build now uses Paper, Spigot will still be supported.

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
