package com.solarrabbit.largeraids.versioned.nms;

import java.util.function.Predicate;

import com.solarrabbit.largeraids.nms.AbstractPoiTypeWrapper;
import com.solarrabbit.largeraids.nms.AbstractProfessionWrapper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerProfession;

public class ProfessionWrapper implements AbstractProfessionWrapper {
    public static final ProfessionWrapper MASON = new ProfessionWrapper(VillagerProfession.MASON);
    final VillagerProfession profession;

    ProfessionWrapper(VillagerProfession profession) {
        this.profession = profession;
    }

    @Override
    public Predicate<? super AbstractPoiTypeWrapper> getPredicate() {
        return (poiTypeWrapper) -> profession.acquirableJobSite().test(((PoiTypeWrapper) poiTypeWrapper).poiTypeHolder);
    }

}
