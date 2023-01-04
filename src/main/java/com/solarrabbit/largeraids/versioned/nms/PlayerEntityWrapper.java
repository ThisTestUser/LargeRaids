package com.solarrabbit.largeraids.versioned.nms;

import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;
import com.solarrabbit.largeraids.nms.AbstractPlayerEntityWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;

public class PlayerEntityWrapper implements AbstractPlayerEntityWrapper {
    final ServerPlayer player;

    public PlayerEntityWrapper(AbstractMinecraftServerWrapper server, AbstractWorldServerWrapper world,
            GameProfile profile, ProfilePublicKey publicKey) {
        this.player = new ServerPlayer(((MinecraftServerWrapper) server).server, ((WorldServerWrapper) world).server,
                profile);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        this.player.setPos(x, y, z);
    }
}
