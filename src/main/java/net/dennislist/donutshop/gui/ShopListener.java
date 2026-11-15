package net.dennislist.donutshop.gui;

import net.dennislist.donutshop.ShopCategory;
import net.dennislist.donutshop.ShopItem;
import net.dennislist.donutshop.ShopManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class ShopListener implements Listener {

    private final ShopManager manager;

    public ShopListener(ShopManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof MainMenu) {
            event.setCancelled(true);
            handleMainMenuClick(event, (MainMenu) holder);
            return;
        }

        if (holder instanceof CategoryMenu) {
            event.setCancelled(true);
            handleCategoryMenuClick(event, (CategoryMenu) holder);
        }
    }

    private void handleMainMenuClick(InventoryClickEvent event, MainMenu menu) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getInventory().getSize()) {
            return;
        }
        String categoryKey = menu.getCategoryKeyAt(slot);
        if (categoryKey == null) {
            return;
        }
        ShopCategory category = manager.getCategory(categoryKey);
        if (category == null) {
            return;
        }
        player.openInventory(new CategoryMenu(manager, category).getInventory());
    }

    private void handleCategoryMenuClick(InventoryClickEvent event, CategoryMenu menu) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == CategoryMenu.BACK_SLOT) {
            player.openInventory(new MainMenu(manager).getInventory());
            return;
        }
        if (slot < 0 || slot >= event.getInventory().getSize()) {
            return;
        }

        ShopItem item = menu.getItemAt(slot);
        if (item == null) {
            return;
        }

        ClickType click = event.getClick();
        switch (click) {
            case LEFT -> manager.buy(player, item, 1);
            case SHIFT_LEFT -> manager.buy(player, item, 64);
            case RIGHT -> manager.sell(player, item, 1);
            case SHIFT_RIGHT -> manager.sell(player, item, 64);
            default -> {
                // bo qua cac kieu click khac (vd. double click, drop...)
            }
        }

        // ve lai menu de cap nhat so luong hien thi khong doi, nhung refresh phong truong hop
        // sau nay them lore dong (vd. hien stock). Hien tai khong can rebuild de tranh giat UI.
    }
}
