package io.github.redwallhp.villagerutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

/**
 * Manages the association between players and their trade drafts, and the villager being edited.
 */
public class WorkspaceManager {
    private final HashMap<UUID, TradeDraft> uuidToTrade = new HashMap<>();
    private final HashMap<UUID, Villager> uuidToVillager = new HashMap<>();

    /** Check if the player currently has a draft workspace */
    public boolean hasWorkspace(Player player) {
        return uuidToTrade.containsKey(player.getUniqueId());
    }

    /** Get the player's current TradeDraft, or null if none exists */
    public TradeDraft getWorkspace(Player player) {
        return uuidToTrade.get(player.getUniqueId());
    }

    /** Set or replace a player's current TradeDraft */
    public void setWorkspace(Player player, TradeDraft draft, Villager villager) {
        uuidToTrade.put(player.getUniqueId(), draft);
        uuidToVillager.put(player.getUniqueId(), villager);
    }

    /** Remove a player's draft workspace */
    public void clearWorkspace(Player player) {
        uuidToTrade.remove(player.getUniqueId());
    }
    /** Finalize the draft into a MerchantRecipe and clear the workspace */
    public MerchantRecipe finalizeDraft(Player player) {
        TradeDraft draft = uuidToTrade.get(player.getUniqueId());
        if (draft == null) return null;
        if (!draft.isComplete()) {
            throw new IllegalStateException("TradeDraft is incomplete.");
        }

        MerchantRecipe recipe = draft.toRecipe();
        Villager villager = uuidToVillager.get(player.getUniqueId());
        if (villager != null) {
            List<MerchantRecipe> recipes = new ArrayList<>(villager.getRecipes());
            recipes.add(recipe);
            villager.setRecipes(recipes);
        }

        clearWorkspace(player);
        return recipe;
    }
}
