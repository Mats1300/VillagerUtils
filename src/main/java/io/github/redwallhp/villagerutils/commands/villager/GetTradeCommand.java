package io.github.redwallhp.villagerutils.commands.villager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.redwallhp.villagerutils.TradeDraft;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;
import io.github.redwallhp.villagerutils.helpers.VillagerHelper;

public class GetTradeCommand extends AbstractCommand implements TabCompleter {

    public GetTradeCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    @Override
    public String getName() {
        return "gettrade";
    }

    @Override
    public String getUsage() {
        return "/villager gettrade <position>";
    }

    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot edit villagers.");
            return false;
        }

        Player player = (Player) sender;
        AbstractVillager target = VillagerHelper.getAbstractVillagerInLineOfSight(player);
        if (!(target instanceof Villager)) {
            player.sendMessage(ChatColor.RED + "You're not looking at a villager.");
            return false;
        }

        Villager villager = (Villager) target;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Invalid arguments. Usage: " + getUsage());
            return false;
        }

        List<MerchantRecipe> recipes = villager.getRecipes();
        try {
            int position = Integer.parseInt(args[0]);
            if (position >= 1 && position <= recipes.size()) {
                MerchantRecipe recipe = recipes.get(position - 1);

                // Convert MerchantRecipe into a TradeDraft
                TradeDraft draft = new TradeDraft(recipe.getMaxUses());
                draft.setResult(recipe.getResult());
                if (!recipe.getIngredients().isEmpty()) {
                    draft.setBuyItems(
                            recipe.getIngredients().get(0),
                            recipe.getIngredients().size() > 1 ? recipe.getIngredients().get(1) : null
                    );
                }
                draft.setGivesXp(recipe.hasExperienceReward());

                // Save to workspace
                plugin.getWorkspaceManager().setWorkspace(player, draft, villager);

                player.sendMessage(ChatColor.DARK_AQUA + "Villager trade " + position + " copied to your workspace.");
                return true;
            }
        } catch (NumberFormatException ex) {
        }
        player.sendMessage(ChatColor.RED + "The position must be between 1 and the number of trades.");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender instanceof Player) {
            Player player = (Player) sender;
            AbstractVillager target = VillagerHelper.getAbstractVillagerInLineOfSight(player);
            if (target instanceof Villager) {
                Villager villager = (Villager) target;
                return IntStream.rangeClosed(1, villager.getRecipeCount())
                        .mapToObj(Integer::toString)
                        .filter(completion -> completion.startsWith(args[0]))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

}
