package io.github.redwallhp.villagerutils.commands.vtrade;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot edit villagers.", NamedTextColor.RED));
            return false;
        }

        if (!plugin.getWorkspaceManager().hasWorkspace(player)) {
            player.sendMessage(Component.text("You do not have a villager trade loaded. Use '/vtrade new' to start one.", NamedTextColor.RED));
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(Component.text("Invalid arguments. Usage: " + getUsage(), NamedTextColor.RED));
            return false;
        }

        TradeDraft draft = plugin.getWorkspaceManager().getWorkspace(player);
        if (draft == null) {
            sender.sendMessage(Component.text("Your workspace is empty.", NamedTextColor.RED));
            return false;
        }

        int value;
        try {
            if (args[0].equalsIgnoreCase("max")) {
                value = Integer.MAX_VALUE;
            } else {
                value = Integer.parseInt(args[0]);
                if (value <= 0) {
                    sender.sendMessage(Component.text("The maximum number of uses must be at least 1.", NamedTextColor.RED));
                    return false;
                }
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(Component.text("The number of uses must be an integer or the word 'max'.", NamedTextColor.RED));
            return false;
        }

        // Set the max uses in TradeDraft
        draft.setMaxUses(value);

        sender.sendMessage(Component.text("Max uses for trade set to " + value + ".", NamedTextColor.DARK_AQUA));
        return true;
    }
}

