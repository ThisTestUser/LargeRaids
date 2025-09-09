package com.solarrabbit.largeraids.versioned.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftServerWrapper;

import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;

public class CraftServerWrapper extends AbstractCraftServerWrapper {

    public CraftServerWrapper(Server server) {
        super(server);
    }

    @Override
    public MinecraftServerWrapper getServer() {
        return new MinecraftServerWrapper(((CraftServer) this.server).getServer());
    }

}
