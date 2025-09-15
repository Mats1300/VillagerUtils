package io.github.redwallhp.villagerutils;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 * Manages the association between players and their active TradeDrafts.
 * <p>
 * This class is used for keeping track of trades being edited via /vtrade commands.
 * </p>
 */
public class WorkspaceManager {

    /**
     * Maps a player's UUID to their current TradeDraft.
     */
    private final HashMap<UUID, TradeDraft> uuidToTrade = new HashMap<>();

    /**
     * Checks if a player has a trade draft loaded in their workspace.
     *
     * @param player the player to check
     * @return true if a draft is loaded, false otherwise
     */
    public boolean hasWorkspace(Player player) {
        return uuidToTrade.containsKey(player.getUniqueId());
    }

    /**
     * Gets the TradeDraft currently in the player's workspace.
     *
     * @param player the player whose draft to retrieve
     * @return the TradeDraft, or null if none exists
     */
    public TradeDraft getWorkspace(Player player) {
        return uuidToTrade.get(player.getUniqueId());
    }

    /**
     * Sets or updates the TradeDraft for a player.
     *
     * @param player the player whose workspace to update
     * @param draft  the TradeDraft to store
     */
    public void setWorkspace(Player player, TradeDraft draft) {
        uuidToTrade.put(player.getUniqueId(), draft);
    }

    /**
     * Clears the TradeDraft from a player's workspace.
     *
     * @param player the player whose workspace to clear
     */
    public void clearWorkspace(Player player) {
        uuidToTrade.remove(player.getUniqueId());
    }
}