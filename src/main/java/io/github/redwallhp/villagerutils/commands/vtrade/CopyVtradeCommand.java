package io.github.redwallhp.villagerutils.commands.vtrade;

import java.util.List;

import io.github.redwallhp.villagerutils.helpers.VillagerHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

import io.github.redwallhp.villagerutils.TradeDraft;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;

/**
 * Handles the {@code /vtrade copy} command.
 * <p>
 * This command copies an existing trade from a villager into the
 * player's workspace so that it can be edited or saved later.
 * The player must be looking directly at a villager and specify
 * the position (1-based index) of the trade they want to copy.
 * </p>
 */
public class CopyVtradeCommand extends AbstractCommand {

    /**
     * Constructs a new {@link CopyVtradeCommand}.
     *
     * @param plugin the instance of the VillagerUtils plugin
     */
    public CopyVtradeCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    /**
     * {@inheritDoc}
     *
     * @return the command name, which is {@code "copy"}.
     */
    @Override
    public String getName() {
        return "copy";
    }

    /**
     * {@inheritDoc}
     *
     * @return the usage string, {@code "/vtrade copy <position>"}.
     */
    @Override
    public String getUsage() {
        return "/vtrade copy <position>";
    }

    /**
     * Executes the {@code /vtrade copy} command.
     * <ul>
     *   <li>Ensures the sender is a player (not console).</li>
     *   <li>Checks that exactly one argument (the trade position) was given.</li>
     *   <li>Finds the villager in the player's line of sight.</li>
     *   <li>Validates that the given position is within the range of the villager's trades.</li>
     *   <li>Copies the selected trade into a {@link TradeDraft}.</li>
     *   <li>Stores the draft in the {@link io.github.redwallhp.villagerutils.WorkspaceManager WorkspaceManager} for later editing.</li>
     * </ul>
     *
     * @param sender the command sender (must be a player)
     * @param args   the command arguments; expects one integer argument for the trade position
     * @return true if the command executed successfully, false otherwise
     */
    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot copy trades.");
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return false;
        }

        Villager target = VillagerHelper.getVillagerInLineOfSight(player);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "No villager in sight.");
            return false;
        }

        List<MerchantRecipe> recipes = target.getRecipes();
        try {
            int position = Integer.parseInt(args[0]);
            if (position < 1 || position > recipes.size()) {
                player.sendMessage(ChatColor.RED + "Position out of range.");
                return false;
            }

            MerchantRecipe selected = recipes.get(position - 1);
            TradeDraft draft = new TradeDraft(selected.getMaxUses());
            draft.setResult(selected.getResult());
            if (!selected.getIngredients().isEmpty()) {
                draft.setBuyItems(
                        selected.getIngredients().get(0),
                        selected.getIngredients().size() > 1 ? selected.getIngredients().get(1) : null
                );
            }

            plugin.getWorkspaceManager().setWorkspace(player, draft, target);
            player.sendMessage(ChatColor.GREEN + "Trade copied into your workspace!");
            return true;

        } catch (NumberFormatException ex) {
            player.sendMessage(ChatColor.RED + "Invalid number.");
            return false;
        }
    }
}