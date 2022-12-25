package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.trigger.Trigger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartRaidCommand extends Trigger implements CommandExecutor {

    public StartRaidCommand(LargeRaids plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args.length < 2)
                return false;
            Location location;
            switch (args[0]) {
                case "player":
                    Player player = Bukkit.getPlayer(args[1]);
                    location = player == null ? null : player.getLocation();
                    if (location == null) {
                        sender.sendMessage(ChatColor.RED + this.plugin.getMessage("start-raid.player-not-found"));
                        return false;
                    }
                    break;
                case "center":
                    location = plugin.getDatabaseAdapter().getCentre(args[1]);
                    if (location == null) {
                        sender.sendMessage(ChatColor.RED + this.plugin.getMessage("start-raid.no-village-centers"));
                        return false;
                    } else if (location.getWorld() == null) {
                        sender.sendMessage(ChatColor.RED + this.plugin.getMessage("start-raid.village-center-world-missing"));
                        return false;
                    }
                    break;
                default:
                    return false;
            }
            sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("start-raid.started-at"),
        			location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            triggerRaid(sender, location);
            return true;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            sender.sendMessage(ChatColor.GREEN + String.format(this.plugin.getMessage("start-raid.started-at"),
            		location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            triggerRaid(sender, location);
            return true;
        }
        if (!(sender instanceof Player))
        	sender.sendMessage(ChatColor.RED + this.plugin.getMessage("start-raid.specify-player"));
        return false;
    }

    @Override
    public void unregisterListener() {
        // Nothing to do here since this trigger isn't a configurable option.
    }

}
