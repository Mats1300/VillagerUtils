package io.github.redwallhp.villagerutils.listeners;

import io.github.redwallhp.villagerutils.TradeDraft;
import org.bukkit.ChatColor;
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
import org.bukkit.inventory.ItemStack;

import io.github.redwallhp.villagerutils.VillagerUtils;

public class TradeListener implements Listener {

    private final VillagerUtils plugin;

    protected boolean isTradeEditingView(InventoryView view) {
        return view != null && "Edit Villager Trade".equals(view.getTitle());
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
        if (!isTradeEditingView(event.getView()) ||
                !plugin.getWorkspaceManager().hasWorkspace(player)) {
            return;
        }

        TradeDraft draft = plugin.getWorkspaceManager().getWorkspace(player);
        if (draft == null) return;

        ItemStack result = event.getInventory().getItem(8);
        ItemStack buyOne = event.getInventory().getItem(0);
        ItemStack buyTwo = event.getInventory().getItem(1);

        draft.setResult(result);
        draft.setBuyItems(buyOne, buyTwo);

        plugin.getWorkspaceManager().setWorkspace(player, draft, null);

        player.sendMessage(ChatColor.DARK_AQUA + "Trade items updated.");
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
