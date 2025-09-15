package io.github.redwallhp.villagerutils.commands.vtrade;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;
import io.github.redwallhp.villagerutils.helpers.TradeHelper;
import io.github.redwallhp.villagerutils.TradeDraft;

public class InfoVtradeCommand extends AbstractCommand {

    public InfoVtradeCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getUsage() {
        return "/vtrade info";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot edit villagers.", NamedTextColor.RED));
            return false;
        }

        if (args.length != 0) {
            player.sendMessage(Component.text("Unexpected arguments. Usage: " + getUsage(), NamedTextColor.RED));
            return false;
        }

        TradeDraft draft = plugin.getWorkspaceManager().getWorkspace(player);
        if (draft == null) {
            player.sendMessage(Component.text("Your workspace is empty.", NamedTextColor.RED));
            return false;
        } else {
            player.sendMessage(Component.text("---------- Workspace ----------", NamedTextColor.DARK_AQUA));
            TradeHelper.describeTrade(sender, draft);
            return true;
        }
    }

}
