package com.solarrabbit.largeraids.config;

import org.bukkit.configuration.ConfigurationSection;

public class MiscConfig {
    private final double traderDropChance;
    private final int maxRaids;
    private final boolean shouldBellOutlineNormal;
    private final boolean shouldBellOutlineLarge;
    private final int bellOutlineDuration;

    public MiscConfig(ConfigurationSection config) {
        traderDropChance = config.getDouble("trader-drop-chance");
        maxRaids = config.getInt("max-raids");
        ConfigurationSection bellOutlineConfig = config.getConfigurationSection("bell-outline-raiders");
        shouldBellOutlineNormal = bellOutlineConfig.getBoolean("normal-raid");
        shouldBellOutlineLarge = bellOutlineConfig.getBoolean("large-raid");
        bellOutlineDuration = bellOutlineConfig.getInt("duration");
    }
    
    public double getTraderDropChance() {
    	return traderDropChance;
    }

    public int getMaxRaid() {
        return maxRaids;
    }

    public boolean shouldBellOutlineLarge() {
        return shouldBellOutlineLarge;
    }

    public boolean shouldBellOutlineNormal() {
        return shouldBellOutlineNormal;
    }

    public int getBellOutlineDuration() {
        return bellOutlineDuration;
    }
}
