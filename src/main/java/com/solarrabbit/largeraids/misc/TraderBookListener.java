package com.solarrabbit.largeraids.misc;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.solarrabbit.largeraids.LargeRaids;

public class TraderBookListener implements Listener {
    private final BookGenerator bookGen;
    private final LargeRaids plugin;

    public TraderBookListener(BookGenerator bookGen, LargeRaids plugin) {
        this.bookGen = bookGen;
        this.plugin = plugin;
    }

    @EventHandler
    public void onTraderDeath(EntityDeathEvent evt) {
        if (evt.getEntityType() != EntityType.WANDERING_TRADER)
            return;
        LivingEntity entity = evt.getEntity();
        if (hasDrop())
            entity.getWorld().dropItem(entity.getLocation(), bookGen.getBook());
    }

    private boolean hasDrop() {
        return Math.random() < plugin.getMiscConfig().getTraderDropChance();
    }
}
