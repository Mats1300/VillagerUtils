package io.github.redwallhp.villagerutils.helpers;

import io.github.redwallhp.villagerutils.TradeDraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

/**
 * Utility functions for trades.
 */
public class TradeHelper {
    /**
     * Show a description of a TradeDraft to a command sender.
     * Works even if the draft is incomplete.
     *
     * @param sender the command sender
     * @param draft the trade draft
     */
    public static void describeTrade(CommandSender sender, TradeDraft draft) {
        // Buy items
        ItemStack buyOne = draft.getBuyOne();
        ItemStack buyTwo = draft.getBuyTwo();
        ItemStack result = draft.getResult();

        sender.sendMessage(Component.text("Ingredient #1: ", NamedTextColor.DARK_AQUA)
                .append(buyOne != null
                        ? Component.text(ItemHelper.getItemDescription(buyOne), NamedTextColor.WHITE)
                        : Component.text("(not set)", NamedTextColor.RED)));


        sender.sendMessage(Component.text("Ingredient #2: ", NamedTextColor.DARK_AQUA)
                .append(buyTwo != null
                        ? Component.text(ItemHelper.getItemDescription(buyTwo), NamedTextColor.WHITE)
                        : Component.text("(not set)", NamedTextColor.RED)));

        // Result
        sender.sendMessage(Component.text("Result: ", NamedTextColor.DARK_AQUA)
                .append(result != null
                        ? Component.text(ItemHelper.getItemDescription(result), NamedTextColor.WHITE)
                        : Component.text("(not set)", NamedTextColor.RED)));

        // Max uses
        sender.sendMessage(Component.text("Max Uses: ", NamedTextColor.DARK_AQUA)
                .append(Component.text(draft.getMaxUses(), NamedTextColor.WHITE)));

        // Gives XP
        sender.sendMessage(Component.text("Gives XP: ", NamedTextColor.DARK_AQUA)
                .append(Component.text(draft.getGivesXp(), NamedTextColor.WHITE)));
    }
    /**
     * Show a description of a MerchantRecipe to a command sender.
     *
     * @param sender the command sender
     * @param recipe the trade recipe
     */
    public static void describeTrade(CommandSender sender, MerchantRecipe recipe) {
        int ingredientIndex = 1;
        for (ItemStack item : recipe.getIngredients()) {
            sender.sendMessage(Component.text("Ingredient #" + ingredientIndex + ": ", NamedTextColor.DARK_AQUA)
                    .append(Component.text(ItemHelper.getItemDescription(item), NamedTextColor.WHITE)));
            ingredientIndex++;
        }

        sender.sendMessage(Component.text("Result: ", NamedTextColor.DARK_AQUA)
                .append(Component.text(ItemHelper.getItemDescription(recipe.getResult()), NamedTextColor.WHITE)));

        sender.sendMessage(Component.text("Uses: ", NamedTextColor.DARK_AQUA)
                .append(Component.text(recipe.getUses() + " / " + recipe.getMaxUses(), NamedTextColor.WHITE)));

        sender.sendMessage(Component.text("Gives XP: ", NamedTextColor.DARK_AQUA)
                .append(Component.text(recipe.hasExperienceReward(), NamedTextColor.WHITE)));
    }
}

