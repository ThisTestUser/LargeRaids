package com.solarrabbit.largeraids.versioned.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftRaiderWrapper;

import org.bukkit.craftbukkit.entity.CraftRaider;
import org.bukkit.entity.Raider;

public class CraftRaiderWrapper extends AbstractCraftRaiderWrapper {

    public CraftRaiderWrapper(Raider raider) {
        super(raider);
    }

    public RaiderWrapper getHandle() {
        return new RaiderWrapper(((CraftRaider) this.raider).getHandle());
    }

}
