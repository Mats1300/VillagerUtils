package io.github.redwallhp.villagerutils.listeners;

import io.github.redwallhp.villagerutils.TradeDraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import io.github.redwallhp.villagerutils.VillagerUtils;

public class TradeListener implements Listener {

    private final VillagerUtils plugin;

    protected boolean isTradeEditingView(InventoryView view) {
        if (view == null) return false;
        String plainTitle = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(view.title());
        return "Edit Villager Trade".equals(plainTitle);
    }

    public TradeListener() {
        plugin = VillagerUtils.instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Update the trade recipe being edited when the trade editor inventory is
     * closed
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        // Check if this inventory is a trade editing GUI and if the player has a draft
        if (!isTradeEditingView(event.getView()) || !plugin.getWorkspaceManager().hasWorkspace(player)) {
            return;
        }

        TradeDraft draft = plugin.getWorkspaceManager().getWorkspace(player);
        if (draft == null) return;

        // Update the draft with the current inventory items
        draft.setResult(event.getInventory().getItem(8));
        draft.setBuyItems(event.getInventory().getItem(0), event.getInventory().getItem(1));

        // Notify the player that changes are saved in the draft
        player.sendMessage(Component.text("Trade draft updated. Use /vtrade givesxp or /villager addtrade to finalize.", NamedTextColor.DARK_AQUA));
    }

    /**
     * Block glass panes from being removed from the trade editor UI
     */
    @EventHandler
    public void onInventoryMoveItem(InventoryClickEvent event) {
        if (event.getClickedInventory() == null ||
            !isTradeEditingView(event.getView())) {
            return;
        }

        if (event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.WHITE_STAINED_GLASS_PANE)) {
            event.setCancelled(true);
        }
    }

    /**
     * Stop villagers from acquiring new trades if the villager is a server
     * merchant
     */
    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        String villagerId = event.getEntity().getUniqueId().toString();
        if (plugin.getVillagerMeta().STATIC_MERCHANTS.contains(villagerId)) {
            event.setCancelled(true);
        }
    }

    /**
     * Clean up after server merchant villagers so we don't have an
     * ever-expanding UUID list
     */
    @EventHandler
    public void onVillagerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof AbstractVillager) {
            String villagerId = event.getEntity().getUniqueId().toString();
            if (plugin.getVillagerMeta().STATIC_MERCHANTS.contains(villagerId)) {
                plugin.getVillagerMeta().STATIC_MERCHANTS.remove(villagerId);
                plugin.getVillagerMeta().save();
            }
        }
    }
}
