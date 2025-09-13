package io.github.redwallhp.villagerutils.helpers;

import io.github.redwallhp.villagerutils.TradeDraft;
import org.bukkit.ChatColor;
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

        if (buyOne != null) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Ingredient #1: " +
                    ChatColor.WHITE + ItemHelper.getItemDescription(buyOne));
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Ingredient #1: " + ChatColor.RED + "(not set)");
        }

        if (buyTwo != null) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Ingredient #2: " +
                    ChatColor.WHITE + ItemHelper.getItemDescription(buyTwo));
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Ingredient #2: " + ChatColor.RED + "(not set)");
        }

        // Result
        if (result != null) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Result: " +
                    ChatColor.WHITE + ItemHelper.getItemDescription(result));
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Result: " + ChatColor.RED + "(not set)");
        }

        // Max uses
        sender.sendMessage(ChatColor.DARK_AQUA + "Max Uses: " + ChatColor.WHITE + draft.getMaxUses());

        // Gives XP
        sender.sendMessage(ChatColor.DARK_AQUA + "Gives XP: " + ChatColor.WHITE + draft.getGivesXp());
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
            sender.sendMessage(ChatColor.DARK_AQUA + "Ingredient #" + ingredientIndex + ": " +
                    ChatColor.WHITE + ItemHelper.getItemDescription(item));
            ingredientIndex++;
        }

        sender.sendMessage(ChatColor.DARK_AQUA + "Result: " + ChatColor.WHITE + ItemHelper.getItemDescription(recipe.getResult()));
        sender.sendMessage(ChatColor.DARK_AQUA + "Uses: " + ChatColor.WHITE + recipe.getUses() + " / " + recipe.getMaxUses());
        sender.sendMessage(ChatColor.DARK_AQUA + "Gives XP: " + ChatColor.WHITE + recipe.hasExperienceReward());
    }
}

