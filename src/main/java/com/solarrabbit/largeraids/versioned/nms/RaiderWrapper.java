package com.solarrabbit.largeraids.versioned.nms;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.PathfindToRaidGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.raid.Raider;

public class RaiderWrapper implements AbstractRaiderWrapper {
    final Raider raider;

    RaiderWrapper(Raider raider) {
        this.raider = raider;
    }

    @Override
    public RaidWrapper getCurrentRaid() {
        return new RaidWrapper(raider.getCurrentRaid());
    }

    @Override
    public void setRaiderTarget(AbstractBlockPositionWrapper pos, double radius, double navSpeed) {
        BlockPos blockPos = pos == null ? null : ((BlockPositionWrapper) pos).blockPos;

        for (WrappedGoal goal : raider.goalSelector.getAvailableGoals()) {
            if (goal.getGoal().getClass() == PathfindToRaidGoal.class) {
                int prio = goal.getPriority();
                raider.goalSelector.removeGoal(goal.getGoal());
                RaiderPathfindToTargetGoal<Raider> newGoal = new RaiderPathfindToTargetGoal<>(raider);
                newGoal.setTargetPos(blockPos, radius, navSpeed);
                raider.goalSelector.addGoal(prio, newGoal);
                break;
            } else if (goal.getGoal() instanceof RaiderPathfindToTargetGoal raiderGoal) {
                raiderGoal.setTargetPos(blockPos, radius, navSpeed);
                break;
            }
        }
    }

    @Override
    public boolean addAttackGoal(int prio, boolean mustSee, Class<?> entityClass) {
        if (!(LivingEntity.class.isAssignableFrom(entityClass)))
            return false;
        raider.targetSelector.addGoal(prio, new NearestAttackableTargetGoal<>(raider, (Class<? extends LivingEntity>)entityClass, mustSee));
        return true;
    }
}
