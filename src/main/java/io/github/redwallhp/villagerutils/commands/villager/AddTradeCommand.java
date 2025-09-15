package io.github.redwallhp.villagerutils.commands.villager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MerchantRecipe;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;
import io.github.redwallhp.villagerutils.helpers.VillagerHelper;
import io.github.redwallhp.villagerutils.TradeDraft;
import org.jetbrains.annotations.NotNull;

public class AddTradeCommand extends AbstractCommand implements TabCompleter {

    public AddTradeCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "addtrade";
    }

    @Override
    public String getUsage() {
        return "/villager addtrade [<position>]";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot edit villagers.", NamedTextColor.RED));
            return false;
        }
        AbstractVillager target = VillagerHelper.getAbstractVillagerInLineOfSight(player);
        if (target == null) {
            player.sendMessage(Component.text("You're not looking at a villager.", NamedTextColor.RED));
            return false;
        }
        if (!plugin.getWorkspaceManager().hasWorkspace(player)) {
            player.sendMessage(Component.text("You do not have a trade loaded. Use '/vtrade'", NamedTextColor.RED));
            return false;
        }
        TradeDraft draft = plugin.getWorkspaceManager().getWorkspace(player);
        if (draft == null || !draft.isComplete()) {
            player.sendMessage(Component.text("You haven't finished setting up this trade!", NamedTextColor.RED));
            return false;
        }

        // Finalize the draft
        MerchantRecipe recipe = draft.toRecipe();

        // Clear the workspace now that it's finalized
        plugin.getWorkspaceManager().clearWorkspace(player);

        if (args.length > 1) {
            player.sendMessage(Component.text("Invalid arguments. Usage: " + getUsage(), NamedTextColor.RED));
            return false;
        }

        List<MerchantRecipe> recipes = new ArrayList<>(target.getRecipes());

        int position = Integer.MAX_VALUE;
        if (args.length == 1) {
            try {
                position = Integer.parseInt(args[0]);
            } catch (IllegalArgumentException ex) {
                player.sendMessage(Component.text("The position must be an integer.", NamedTextColor.RED));
                return false;
            }
        }

        if (position <= 0) {
            player.sendMessage(Component.text("The position must be at least 1.", NamedTextColor.RED));
            return false;
        } else if (position > recipes.size()) {
            // Allow any large position value when appending.
            recipes.add(recipe);
        } else {
            recipes.add(position - 1, recipe);
        }

        target.setRecipes(recipes);
        player.sendMessage(Component.text("Villager trade added.", NamedTextColor.DARK_AQUA));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 2 && sender instanceof Player player) {
            AbstractVillager target = VillagerHelper.getAbstractVillagerInLineOfSight(player);
            if (target != null) {
                return IntStream.rangeClosed(1, target.getRecipeCount() + 1)
                .mapToObj(Integer::toString)
                .filter(completion -> completion.startsWith(args[1]))
                .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

}
