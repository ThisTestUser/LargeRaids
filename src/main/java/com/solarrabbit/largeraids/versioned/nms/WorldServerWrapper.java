package com.solarrabbit.largeraids.versioned.nms;

import java.util.List;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.phys.AABB;

public class WorldServerWrapper implements AbstractWorldServerWrapper {
    final ServerLevel server;

    WorldServerWrapper(ServerLevel server) {
        this.server = server;
    }

    @Override
    public int clearMobTargets(Class<?> entityClass, AbstractBlockPositionWrapper search, double range) {
        if (!(Mob.class.isAssignableFrom(entityClass)))
            return -1;

        BlockPos searchPos = ((BlockPositionWrapper) search).blockPos;
        AABB aabb = new AABB(searchPos.getX() - range, searchPos.getY() - range, searchPos.getZ() - range,
                searchPos.getX() + range, searchPos.getY() + range, searchPos.getZ() + range);
        List<? extends Mob> mobs = server.getEntitiesOfClass((Class<? extends Mob>) entityClass, aabb);
        int count = 0;
        for (Mob mob : mobs)
            for (WrappedGoal goal : mob.goalSelector.getAvailableGoals()) {
                if (goal.getGoal() instanceof PathfindToTargetGoal) {
                    mob.goalSelector.removeGoal(goal.getGoal());
                    count++;
                    break;
                }
            }

        return count;
    }

    @Override
    public int setMobTargets(Class<?> entityClass, AbstractBlockPositionWrapper search, double range,
            AbstractBlockPositionWrapper target, double radius, double navSpeed, int prio, boolean pathfindOnce) {
        if (!(Mob.class.isAssignableFrom(entityClass)))
            return -1;

        BlockPos searchPos = ((BlockPositionWrapper) search).blockPos;
        AABB aabb = new AABB(searchPos.getX() - range, searchPos.getY() - range, searchPos.getZ() - range,
                searchPos.getX() + range, searchPos.getY() + range, searchPos.getZ() + range);
        List<? extends Mob> mobs = server.getEntitiesOfClass((Class<? extends Mob>) entityClass, aabb);
        for (Mob mob : mobs) {
            for (WrappedGoal goal : mob.goalSelector.getAvailableGoals()) {
                if (goal.getGoal() instanceof PathfindToTargetGoal) {
                    mob.goalSelector.removeGoal(goal.getGoal());
                    break;
                }
            }

            PathfindToTargetGoal goal = new PathfindToTargetGoal(mob);
            goal.setTargetPos(((BlockPositionWrapper) target).blockPos, radius, navSpeed, pathfindOnce);
            mob.goalSelector.addGoal(prio, goal);
        }

        return mobs.size();
    }

    @Override
    public RaidWrapper getRaidAt(AbstractBlockPositionWrapper blockPos) {
        return new RaidWrapper(this.server.getRaidAt(((BlockPositionWrapper) blockPos).blockPos));
    }

    @Override
    public RaidsWrapper getRaids() {
        return new RaidsWrapper(this.server.getRaids());
    }

    @Override
    public VillageManagerWrapper getVillageRecordManager() {
        return new VillageManagerWrapper(this.server.getPoiManager());
    }

}
