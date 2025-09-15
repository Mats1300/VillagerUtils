package io.github.redwallhp.villagerutils.commands.vtrade;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import java.util.Random;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.redwallhp.villagerutils.TradeDraft;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;

/**
 * Command to create a new villager trade workspace for the player.
 * <p>
 * Usage: <code>/vtrade new [<maxuses>|max]</code>
 * </p>
 * <p>
 * This command initializes a new {@link TradeDraft} with a specified maximum
 * number of uses. If no value is provided, a random vanilla range (2–12) is used.
 * </p>
 * <p>
 * Permissions required: <code>villagerutils.editvillager</code>
 * </p>
 */
public class NewVtradeCommand extends AbstractCommand {

    /**
     * Constructs the command with the plugin instance and required permission.
     *
     * @param plugin the main {@link VillagerUtils} plugin instance
     */
    public NewVtradeCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    /**
     * Gets the name of this command.
     *
     * @return the name of the command ("new")
     */
    @Override
    public String getName() {
        return "new";
    }

    /**
     * Gets the usage message for this command.
     *
     * @return a string describing the command usage
     */
    @Override
    public String getUsage() {
        return "/vtrade new [<maxuses>|max]";
    }

    /**
     * Executes the command action when a player issues it.
     * <p>
     * If the sender is not a player, the command will fail. If no maximum uses
     * are provided, a random value between 2 and 12 is used. If the argument is
     * "max", the maximum integer value is used.
     * </p>
     * <p>
     * This method creates a new {@link TradeDraft} and stores it in the player's workspace.
     * </p>
     *
     * @param sender the command sender
     * @param args   optional arguments; first argument can be an integer or "max"
     * @return true if the command was successfully executed, false otherwise
     */
    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot edit villagers.", NamedTextColor.RED));
            return false;
        }
        int limit;
        if (args.length == 0) {
            limit = new Random().nextInt(11) + 2; // vanilla range
        } else {
            try {
                limit = args[0].equalsIgnoreCase("max") ? Integer.MAX_VALUE : Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                player.sendMessage(Component.text("Number of uses must be an integer or 'max'.", NamedTextColor.RED));
                return false;
            }
        }

        TradeDraft draft = new TradeDraft(limit);
        plugin.getWorkspaceManager().setWorkspace(player, draft);

        player.sendMessage(Component.text("New villager trade created with max " + limit + " uses.", NamedTextColor.DARK_AQUA));
        player.sendMessage(Component.text("Next: /vtrade items to edit the trade items.", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, true));
        return true;
    }
}

