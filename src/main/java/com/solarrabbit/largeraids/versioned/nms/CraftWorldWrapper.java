package com.solarrabbit.largeraids.versioned.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftWorldWrapper;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

public class CraftWorldWrapper extends AbstractCraftWorldWrapper {

    public CraftWorldWrapper(World world) {
        super(world);
    }

    @Override
    public WorldServerWrapper getHandle() {
        return new WorldServerWrapper(((CraftWorld) this.world).getHandle());
    }

}
