package io.github.redwallhp.villagerutils.commands.vtrade;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import io.github.redwallhp.villagerutils.TradeDraft;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;
import org.jetbrains.annotations.NotNull;

public class GivesXPVtradeCommand extends AbstractCommand implements TabCompleter {

    public GivesXPVtradeCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "givesxp";
    }

    @Override
    public String getUsage() {
        return "/vtrade givesxp <given>";
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

        boolean value;
        switch (args[0].toLowerCase()) {
        case "false":
            value = false;
            break;
        case "true":
            value = true;
            break;
        default:
            sender.sendMessage(Component.text("The <given> argument must be true or false.", NamedTextColor.RED));
            return false;
        }

        TradeDraft draft = plugin.getWorkspaceManager().getWorkspace(player);
        draft.setGivesXp(value);
        player.sendMessage(Component.text("This trade will" + (value ? "" : " not") + " give experience.", NamedTextColor.DARK_AQUA));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return Stream.of("false", "true")
                    .filter(option -> option.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return Collections.emptyList();
    }
}
