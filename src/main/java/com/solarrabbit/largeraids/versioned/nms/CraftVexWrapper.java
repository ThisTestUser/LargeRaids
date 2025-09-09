package com.solarrabbit.largeraids.versioned.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftVexWrapper;

import net.minecraft.world.entity.Mob;

import org.bukkit.craftbukkit.v1_20_R3.entity.CraftVex;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vex;

public class CraftVexWrapper implements AbstractCraftVexWrapper {
    private final Vex vex;

    public CraftVexWrapper(Vex vex) {
        this.vex = vex;
    }

    @Override
    public LivingEntity getOwner() {
        Mob owner = ((CraftVex) vex).getHandle().getOwner();
        return owner == null ? null : (LivingEntity) owner.getBukkitEntity();
    }
}
