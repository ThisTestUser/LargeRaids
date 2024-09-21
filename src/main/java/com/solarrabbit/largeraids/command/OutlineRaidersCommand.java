package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.raid.LargeRaid;
import com.solarrabbit.largeraids.raid.RaidManager;
import com.solarrabbit.largeraids.raid.RaidersOutliner;
import com.solarrabbit.largeraids.util.VersionUtil;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Outlines all raiders in a raid, regardless whether it is a large raid or not.
 */
public class OutlineRaidersCommand extends RaidersOutliner implements CommandExecutor {
    private final LargeRaids plugin;
    private final RaidManager manager;
    
    public OutlineRaidersCommand(LargeRaids plugin) {
        this.plugin = plugin;
        manager = plugin.getRaidManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + this.plugin.getMessage("sender-player"));
            return false;
        }
        if (args.length < 1)
            return false;
        Location location = ((Player) sender).getLocation();
        
        try {
            int seconds = Integer.parseInt(args[0]);
            if (seconds <= 0) {
                sender.sendMessage(ChatColor.RED + this.plugin.getMessage("outline-raiders.need-positive"));
                return false;
            } else {
                Optional<Raid> optional = manager.getRaid(location);
                if (optional.isEmpty())
                    sender.sendMessage(ChatColor.RED + this.plugin.getMessage("outline-raiders.no-raid-exists"));
                else if (optional.get().getRaiders().isEmpty())
                    sender.sendMessage(ChatColor.RED + this.plugin.getMessage("outline-raiders.no-raiders"));
                else {
                    resonateBell(location);
                    outlineAllRaidersImmediately(optional.get(), seconds * 20);
                    sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("outline-raiders.raiders-outlined"),
                            seconds, seconds == 1 ? "" : "s"));
                }
                return true;
            }
        } catch (NumberFormatException e) {
            if (args[0].equals("toggle")) {
                Optional<LargeRaid> largeRaid = manager.getLargeRaid(location);
                if (largeRaid.isPresent()) {
                    if (largeRaid.get().areRaidersOutlined()) {
                        largeRaid.get().setRaidersOutlined(false);
                        sender.sendMessage(ChatColor.GREEN + this.plugin.getMessage("outline-raiders.toggle-outlines-large-false"));
                    } else {
                        largeRaid.get().setRaidersOutlined(true);
                        sender.sendMessage(ChatColor.GREEN + this.plugin.getMessage("outline-raiders.toggle-outlines-large-true"));
                    }
                    return true;
                }
                Optional<Raid> raid = manager.getRaid(location);
                if (raid.isPresent()) {
                    AbstractRaidWrapper wrapper = VersionUtil.getCraftRaidWrapper(raid.get()).getHandle();
                    if (manager.outlinedRaids.contains(wrapper)) {
                        manager.outlinedRaids.remove(wrapper);
                        sender.sendMessage(ChatColor.GREEN + this.plugin.getMessage("outline-raiders.toggle-outlines-false"));
                    } else {
                        manager.outlinedRaids.add(wrapper);
                        sender.sendMessage(ChatColor.GREEN + this.plugin.getMessage("outline-raiders.toggle-outlines-true"));
                    }
                    return true;
                }
                sender.sendMessage(ChatColor.RED + this.plugin.getMessage("outline-raiders.no-raid-exists"));
                return true;
            }
        }
        return false;
    }

}
