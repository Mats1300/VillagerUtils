package io.github.redwallhp.villagerutils.commands.vtrade;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import io.github.redwallhp.villagerutils.TradeDraft;
import io.github.redwallhp.villagerutils.VillagerUtils;
import io.github.redwallhp.villagerutils.commands.AbstractCommand;

/**
 * Command to open a GUI for editing the items of a villager trade.
 * <p>
 * Usage: <code>/vtrade items</code>
 * </p>
 * <p>
 * This command requires that a {@link TradeDraft} is already loaded in the
 * player's workspace (via '/vtrade new'). It opens a custom inventory GUI
 * where the player can view or modify the buy and result items for the trade.
 * </p>
 * <p>
 * Permissions required: <code>villagerutils.editvillager</code>
 * </p>
 */
public class ItemsVtradeCommand extends AbstractCommand {

    /**
     * Constructs the command with the plugin instance and required permission.
     *
     * @param plugin the main {@link VillagerUtils} plugin instance
     */
    public ItemsVtradeCommand(VillagerUtils plugin) {
        super(plugin, "villagerutils.editvillager");
    }

    /**
     * Gets the name of this command.
     *
     * @return the name of the command ("items")
     */
    @Override
    public String getName() {
        return "items";
    }

    /**
     * Gets the usage message for this command.
     *
     * @return a string describing the command usage
     */
    @Override
    public String getUsage() {
        return "/vtrade items";
    }

    /**
     * Executes the command action when a player issues it.
     * <p>
     * Opens the villager trade editor GUI for the player if a {@link TradeDraft}
     * exists in their workspace. If no trade is loaded, the player is notified
     * to create a new trade first.
     * </p>
     *
     * @param sender the command sender
     * @param args   command arguments (not used in this command)
     * @return true if the GUI was successfully opened, false otherwise
     */
    @Override
    public boolean action(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Console cannot edit villagers.", NamedTextColor.RED));
            return false;
        }

        TradeDraft draft = plugin.getWorkspaceManager().getWorkspace(player);
        if (draft == null) {
            player.sendMessage(Component.text("No trade loaded. Use '/vtrade new' first.", NamedTextColor.RED));
            return false;
        }

        player.openInventory(getTradeInventory(draft));
        return true;
    }

    /**
     * Creates a custom inventory representing the trade editor for a {@link TradeDraft}.
     * <p>
     * Slots layout:
     * <ul>
     *     <li>0: First buy item</li>
     *     <li>1: Second buy item</li>
     *     <li>2–7: Decorative white glass panes</li>
     *     <li>8: Result item</li>
     * </ul>
     * </p>
     *
     * @param draft the {@link TradeDraft} to display
     * @return a {@link Inventory} representing the trade editor GUI
     */
    private Inventory getTradeInventory(TradeDraft draft) {
        Inventory inventory = Bukkit.createInventory(
                null,
                9,
                Component.text("Edit Villager Trade")
        );

        // Fill decorative slots
        for (int i = 2; i <= 7; i++) {
            inventory.setItem(i, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
        }

        // Set buy items (slots 0 & 1)
        if (Objects.nonNull(draft.getBuyOne())) inventory.setItem(0, draft.getBuyOne());
        if (Objects.nonNull(draft.getBuyTwo())) inventory.setItem(1, draft.getBuyTwo());

        // Set result (slot 8)
        if (Objects.nonNull(draft.getResult())) inventory.setItem(8, draft.getResult());

        return inventory;
    }
}

