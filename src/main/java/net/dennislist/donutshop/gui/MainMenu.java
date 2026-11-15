package net.dennislist.donutshop.gui;

import net.dennislist.donutshop.ShopCategory;
import net.dennislist.donutshop.ShopManager;
import net.dennislist.donutshop.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Menu chinh: moi item la mot danh muc (category). InventoryHolder rieng
 * giup ShopListener nhan dang day la menu cua plugin minh, khong dung title string.
 */
public class MainMenu implements InventoryHolder {

    private final Inventory inventory;
    private final Map<Integer, String> slotToCategory = new HashMap<>();

    public MainMenu(ShopManager manager) {
        int rows = Math.max(1, Math.min(6, manager.getMainMenuRows()));
        this.inventory = Bukkit.createInventory(this, rows * 9, manager.getMainMenuTitle());
        build(manager);
    }

    private void build(ShopManager manager) {
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }

        for (ShopCategory category : manager.getCategories().values()) {
            int slot = category.getSlot();
            if (slot < 0 || slot >= inventory.getSize()) {
                continue;
            }
            ItemStack icon = new ItemBuilder(category.getIcon())
                    .name(category.getDisplayName())
                    .lore("&7Nhap de xem mat hang")
                    .lore("&8» " + category.getItems().size() + " mat hang")
                    .build();
            inventory.setItem(slot, icon);
            slotToCategory.put(slot, category.getKey());
        }
    }

    /**
     * Tra ve key cua category dat o slot nay, hoac null neu slot do la o trong/filler.
     */
    public String getCategoryKeyAt(int slot) {
        return slotToCategory.get(slot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
