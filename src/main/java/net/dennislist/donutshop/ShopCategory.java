package net.dennislist.donutshop;

import org.bukkit.Material;

import java.util.List;

/**
 * Mot danh muc trong main menu (vd: Blocks, Ores, Combat ...) chua danh sach ShopItem.
 */
public class ShopCategory {

    private final String key;
    private final String displayName;
    private final Material icon;
    private final int slot;
    private final List<ShopItem> items;

    public ShopCategory(String key, String displayName, Material icon, int slot, List<ShopItem> items) {
        this.key = key;
        this.displayName = displayName;
        this.icon = icon;
        this.slot = slot;
        this.items = items;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public int getSlot() {
        return slot;
    }

    public List<ShopItem> getItems() {
        return items;
    }
}
