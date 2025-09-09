package com.solarrabbit.largeraids.raid;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.event.LargeRaidExtendEvent;
import com.solarrabbit.largeraids.event.LargeRaidTriggerEvent;
import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftWorldWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;
import com.solarrabbit.largeraids.util.VersionUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class RaidManager implements Listener {
    public final Set<LargeRaid> currentRaids = new HashSet<>();
    public final Set<AbstractRaidWrapper> outlinedRaids = new HashSet<>();
    private final LargeRaids plugin;
    private boolean isIdle;

    public RaidManager(LargeRaids plugin) {
        this.plugin = plugin;
        isIdle = false;
    }

    public boolean isIdle() {
        return isIdle;
    }

    /**
     * Idles the listener, mainly used to signify that any {@link RaidTriggerEvent}
     * fired after part of a {@link LargeRaid}.
     */
    public void setIdle() {
        isIdle = true;
    }

    /**
     * Re-activates the listener, mainly used to signify that any
     * {@link RaidTriggerEvent} fired after are vanilla.
     */
    public void setActive() {
        isIdle = false;
    }

    public int getNumOfRegisteredRaids() {
        return currentRaids.size();
    }

    @EventHandler
    private void onSpawn(RaidSpawnWaveEvent evt) {
        // TODO Confirm to prevent ConcurrentModificationException
        Bukkit.getScheduler().runTask(plugin, () -> getLargeRaid(evt.getRaid()).ifPresent(largeRaid -> {
            setIdle();
            largeRaid.spawnWave();
            setActive();
        }));
    }

    /**
     * Disables normal raid if enabled in configurations.
     *
     * @param evt raid triggering event
     */
    @EventHandler
    private void onNormalRaidTrigger(RaidTriggerEvent evt) {
        if (evt.getRaid().getBadOmenLevel() != 0) // Raid is getting extended
            return;
        if (isIdle()) // LargeRaid triggering
            return;
        if (!plugin.getTriggerConfig().canNormalRaid())
            evt.setCancelled(true);
    }

    @EventHandler
    private void onFinish(RaidFinishEvent evt) {
        Raid raid = evt.getRaid();
        getLargeRaid(raid).ifPresent(largeRaid -> {
            RaidStatus status = raid.getStatus();
            if (status == RaidStatus.VICTORY)
                largeRaid.announceVictory();
            else if (status == RaidStatus.LOSS)
                largeRaid.announceDefeat();
        });
    }

    @EventHandler
    private void onRaidStop(RaidStopEvent evt) {
        getLargeRaid(evt.getRaid()).ifPresent(largeRaid -> currentRaids.remove(largeRaid));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDamage(EntityDamageByEntityEvent evt) {
        if (evt.isCancelled())
            return;
        Entity killed = evt.getEntity();
        if (!(killed instanceof Raider))
            return;
        Raider raider = (Raider) killed;
        Entity attacker = evt.getDamager();
        Player damager;
        switch (evt.getCause()) {
            case ENTITY_ATTACK:
            case ENTITY_SWEEP_ATTACK:
                if (!(attacker instanceof Player))
                    return;
                damager = (Player) attacker;
                break;
            case PROJECTILE:
                Projectile projectile = (Projectile) attacker;
                ProjectileSource source = projectile.getShooter();
                if (!(source instanceof Player))
                    return;
                damager = (Player) source;
                break;
            default:
                return;
        }

        AbstractRaiderWrapper wrapper = VersionUtil.getCraftRaiderWrapper(raider).getHandle();
        AbstractRaidWrapper nmsRaid = wrapper.getCurrentRaid();
        if (nmsRaid.isEmpty())
            return;
        AbstractCraftRaidWrapper craftRaid = VersionUtil.getCraftRaidWrapper(nmsRaid);
        Optional<LargeRaid> lr = getLargeRaid(craftRaid.getRaid());
        lr.ifPresent(r -> {
            r.incrementPlayerDamage(damager, Math.min(raider.getHealth(), evt.getFinalDamage()));
            if (raider.getHealth() - evt.getFinalDamage() <= 0)
                r.incrementPlayerKill(damager);
        });
    }

    public void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 0, 1);
    }

    private void tick() {
        PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, 5, 0);
        for (LargeRaid largeRaid : currentRaids)
            // Each vanilla raid is supposed to spawn one wave, before being replaced by another instance of raid.
            // If the first wave has spawned and all raiders are dead, this indicates that it is time to trigger the next wave.
            if (largeRaid.isActive() && largeRaid.getTotalRaidersAlive() == 0 && largeRaid.firstWaveSpawned()
                    && !largeRaid.isLastWave()) {
                setIdle();
                largeRaid.triggerNextWave();
                setActive();
            } else if (largeRaid.areRaidersOutlined())
                largeRaid.applyGlowing();

        Iterator<AbstractRaidWrapper> itr = outlinedRaids.iterator();
        while (itr.hasNext()) {
            AbstractRaidWrapper wrapper = itr.next();
            Raid raid = VersionUtil.getCraftRaidWrapper(wrapper).getRaid();

            if (raid.getStatus() != RaidStatus.ONGOING)
                itr.remove();
            else
                raid.getRaiders().forEach(raider -> raider.addPotionEffect(effect));
        }
    }

    public Optional<LargeRaid> getLargeRaid(Location location) {
        AbstractBlockPositionWrapper blockPos = VersionUtil.getBlockPositionWrapper(location);
        AbstractCraftWorldWrapper craftWorld = VersionUtil.getCraftWorldWrapper(location.getWorld());
        AbstractRaidWrapper nmsRaid = craftWorld.getHandle().getRaidAt(blockPos);
        if (nmsRaid.isEmpty())
            return Optional.empty();
        AbstractCraftRaidWrapper craftRaid = VersionUtil.getCraftRaidWrapper(nmsRaid);
        return getLargeRaid(craftRaid.getRaid());
    }

    public Optional<LargeRaid> getLargeRaid(Raid raid) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(raid)).findFirst();
    }

    public Optional<Raid> getRaid(Location location) {
        AbstractBlockPositionWrapper blockPos = VersionUtil.getBlockPositionWrapper(location);
        AbstractCraftWorldWrapper world = VersionUtil.getCraftWorldWrapper(location.getWorld());
        AbstractRaidWrapper raid = world.getHandle().getRaidAt(blockPos);
        return Optional.of(raid).filter(r -> !r.isEmpty()).map(VersionUtil::getCraftRaidWrapper)
                .map(AbstractCraftRaidWrapper::getRaid);
    }

    /**
     * Creates a raid at a given location with the given omen level. This method
     * will fail silently if there is already an ongoing raid in this vacinity.
     *
     * @param location  to trigger the raid
     * @param omenLevel for the raid start with
     */
    public void createRaid(Location location, int omenLevel) {
        Optional<LargeRaid> currentRaid = getLargeRaid(location);
        if (currentRaid.isPresent())
            return;
        LargeRaid largeRaid = new LargeRaid(plugin.getRaidConfig(), plugin.getRewardsConfig(), location, omenLevel);
        setIdle();
        if (largeRaid.startRaid()) {
            LargeRaidTriggerEvent evt = new LargeRaidTriggerEvent(largeRaid);
            Bukkit.getPluginManager().callEvent(evt);
            if (evt.isCancelled())
                largeRaid.stopRaid();
            else
                currentRaids.add(largeRaid);
        }
        setActive();
    }

    /**
     * Extends the given raid by absorbing the given omen level.
     *
     * @param raid      to absorb omen
     * @param omenLevel levels to absorb
     */
    public void extendRaid(LargeRaid raid, int omenLevel) {
        int oldLevel = raid.getBadOmenLevel();
        raid.absorbOmenLevel(omenLevel);
        int newLevel = raid.getBadOmenLevel();
        if (newLevel != oldLevel)
            Bukkit.getPluginManager().callEvent(new LargeRaidExtendEvent(raid, oldLevel, newLevel));
    }

}