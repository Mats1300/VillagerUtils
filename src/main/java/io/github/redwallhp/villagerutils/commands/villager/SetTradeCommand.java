package io.github.redwallhp.villagerutils.commands.villager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MerchantRecipe;
import io.github.redwallhp.villagerutils.TradeDraft;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;
import io.github.redwallhp.villagerutils.helpers.VillagerHelper;
import org.jetbrains.annotations.NotNull;

public class SetTradeCommand extends AbstractCommand implements TabCompleter {

    public SetTradeCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "settrade";
    }

    @Override
    public String getUsage() {
        return "/villager settrade <position>";
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

        if (args.length != 1) {
            player.sendMessage(Component.text("Invalid arguments. Usage: " + getUsage(), NamedTextColor.RED));
            return false;
        }

        List<MerchantRecipe> recipes = new ArrayList<>(target.getRecipes());
        try {
            int position = Integer.parseInt(args[0]);
            if (position >= 1 && position <= recipes.size()) {
                TradeDraft draft = plugin.getWorkspaceManager().getWorkspace(player);
                if (draft == null || !draft.isComplete()) {
                    player.sendMessage(Component.text("You haven't finished setting up this trade!", NamedTextColor.RED));
                    return false;
                }
                MerchantRecipe recipe = draft.toRecipe();

                recipes.set(position - 1, recipe);
                target.setRecipes(recipes);
                player.sendMessage(Component.text("Villager trade " + position + " set from your workspace.", NamedTextColor.DARK_AQUA));
                return true;
            }
        } catch (NumberFormatException ex) {
            // fall through to error message below
        }

        player.sendMessage(Component.text("The position must be between 1 and the number of trades.", NamedTextColor.RED));
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            AbstractVillager target = VillagerHelper.getAbstractVillagerInLineOfSight(player);
            if (target != null) {
                return IntStream.rangeClosed(1, target.getRecipeCount())
                        .mapToObj(Integer::toString)
                        .filter(s -> s.startsWith(args[0]))
                        .toList();
            }
        }
        return Collections.emptyList();
    }

}
