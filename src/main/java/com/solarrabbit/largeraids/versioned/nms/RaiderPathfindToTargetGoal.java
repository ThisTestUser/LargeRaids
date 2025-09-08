package com.solarrabbit.largeraids.versioned.nms;

import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PathfindToRaidGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.phys.Vec3;

public class RaiderPathfindToTargetGoal<T extends Raider> extends PathfindToRaidGoal<T> {
    private static final int RECRUITMENT_SEARCH_TICK_DELAY = 20;
    private final T mob;
    private int recruitmentTick;
    private BlockPos targetPos;
    private double targetRadius;
    private double navSpeed;
    private boolean changed;

    public RaiderPathfindToTargetGoal(T mob) {
        super(mob);
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public void setTargetPos(BlockPos pos, double radius, double navSpeed) {
        this.targetPos = pos;
        this.targetRadius = radius;
        this.navSpeed = pos == null ? 1 : navSpeed;
        this.changed = true;
    }

    private boolean isCloseToGoal() {
        if (targetPos == null)
            return getServerLevel(this.mob.level()).isVillage(this.mob.blockPosition());

        return targetPos.closerToCenterThan(this.mob.position(), targetRadius);
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() == null
            && !this.mob.hasControllingPassenger()
            && this.mob.hasActiveRaid()
            && !this.mob.getCurrentRaid().isOver()
            && !isCloseToGoal();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && !isCloseToGoal();
    }

    @Override
    public void tick() {
        if (this.mob.hasActiveRaid()) {
            Raid currentRaid = this.mob.getCurrentRaid();
            if (this.mob.tickCount > this.recruitmentTick) {
                this.recruitmentTick = this.mob.tickCount + RECRUITMENT_SEARCH_TICK_DELAY;
                this.recruitNearby(currentRaid);
            }

            if (this.changed) { 
                this.mob.getNavigation().stop();
                this.changed = false;
            }
            if (!this.mob.isPathFinding()) {
                Vec3 posTowards = targetPos != null ? targetPos.getBottomCenter()
                        : DefaultRandomPos.getPosTowards(this.mob, 15, 4, Vec3.atBottomCenterOf(
                        currentRaid.getCenter()), (float) (Math.PI / 2));
                if (posTowards != null) {
                    this.mob.getNavigation().moveTo(posTowards.x, posTowards.y, posTowards.z, navSpeed);
                }
            }
        }
    }

    private void recruitNearby(Raid raid) {
        if (raid.isActive()) {
            ServerLevel serverLevel = getServerLevel(this.mob.level());
            Set<Raider> set = Sets.newHashSet();
            List<Raider> entitiesOfClass = serverLevel.getEntitiesOfClass(
                Raider.class, this.mob.getBoundingBox().inflate(16.0), raider1 -> !raider1.hasActiveRaid() && Raids.canJoinRaid(raider1, raid)
            );
            set.addAll(entitiesOfClass);

            for (Raider raider : set) {
                raid.joinRaid(raid.getGroupsSpawned(), raider, null, true);
            }
        }
    }
}
