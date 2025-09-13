package io.github.redwallhp.villagerutils.commands.vtrade;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.redwallhp.villagerutils.TradeDraft;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;

public class MaxUsesVtradeCommand extends AbstractCommand {

    public MaxUsesVtradeCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "maxuses";
    }

    @Override
    public String getUsage() {
        return "/vtrade maxuses <int>|max";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot edit villagers.");
            return false;
        }

        Player player = (Player) sender;

        if (!plugin.getWorkspaceManager().hasWorkspace(player)) {
            player.sendMessage(ChatColor.RED + "You do not have a villager trade loaded. Use '/vtrade new' to start one.");
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Invalid arguments. Usage: " + getUsage());
            return false;
        }

        TradeDraft draft = plugin.getWorkspaceManager().getWorkspace(player);
        if (draft == null) {
            sender.sendMessage(ChatColor.RED + "Your workspace is empty.");
            return false;
        }

        int value;
        try {
            if (args[0].equalsIgnoreCase("max")) {
                value = Integer.MAX_VALUE;
            } else {
                value = Integer.parseInt(args[0]);
                if (value <= 0) {
                    sender.sendMessage(ChatColor.RED + "The maximum number of uses must be at least 1.");
                    return false;
                }
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "The number of uses must be an integer or the word 'max'.");
            return false;
        }

        // Set the max uses in TradeDraft
        draft.setMaxUses(value);

        sender.sendMessage(ChatColor.DARK_AQUA + "Max uses for trade set to " + value + ".");
        return true;
    }
}

