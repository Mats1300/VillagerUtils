package io.github.redwallhp.villagerutils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class TradeDraft {

    private int maxUses;
    private ItemStack result;
    private ItemStack buyOne;
    private ItemStack buyTwo;
    private boolean givesXp = true;

    public TradeDraft(int maxUses) {
        this.maxUses = maxUses;
    }

    /** Set the resulting item of the trade */
    public void setResult(ItemStack result) {
        this.result = result;
    }

    /** Set the required buy items (buyTwo can be null) */
    public void setBuyItems(ItemStack one, ItemStack two) {
        this.buyOne = one;
        this.buyTwo = two;
    }

    /** Set whether this trade gives player XP */
    public void setGivesXp(boolean givesXp) {
        this.givesXp = givesXp;
    }

    public boolean getGivesXp() {
        return givesXp;
    }

    /** Getters for external access */
    public ItemStack getBuyOne() {
        return buyOne;
    }

    public ItemStack getBuyTwo() {
        return buyTwo;
    }

    public ItemStack getResult() {
        return result;
    }

    /** Check if the trade draft is ready to be converted to a MerchantRecipe */
    public boolean isComplete() {
        return result != null && buyOne != null; // at least one buy item + result
    }

    /** Convert this draft into a real MerchantRecipe for a villager */
    public MerchantRecipe toRecipe() {
        if (!isComplete()) {
            throw new IllegalStateException("TradeDraft is incomplete.");
        }
        MerchantRecipe recipe = new MerchantRecipe(result, maxUses);
        if (buyOne != null) recipe.addIngredient(buyOne);
        if (buyTwo != null) recipe.addIngredient(buyTwo);
        recipe.setExperienceReward(givesXp);
        return recipe;
    }

    /** Get the maximum number of uses before the trade locks */
    public int getMaxUses() {
        return maxUses;
    }

    /** Set the maximum number of uses before the trade locks */
    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }
}
