package net.dennislist.donutshop.gui;

import net.dennislist.donutshop.ShopCategory;
import net.dennislist.donutshop.ShopItem;
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
 * Menu hien thi tat ca ShopItem cua mot category, voi lore huong dan mua/ban.
 * Slot 49 (hang cuoi, giua) la nut "Quay lai".
 */
public class CategoryMenu implements InventoryHolder {

    public static final int BACK_SLOT = 49;

    private final Inventory inventory;
    private final ShopCategory category;
    private final Map<Integer, ShopItem> slotToItem = new HashMap<>();

    public CategoryMenu(ShopManager manager, ShopCategory category) {
        this.category = category;
        this.inventory = Bukkit.createInventory(this, 54, category.getDisplayName());
        build(manager);
    }

    private void build(ShopManager manager) {
        ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }

        int slot = 0;
        for (ShopItem item : category.getItems()) {
            // bo qua cac o danh cho nut back / trang trai
            while (slot == BACK_SLOT) {
                slot++;
            }
            if (slot >= inventory.getSize()) {
                break;
            }

            ItemBuilder builder = new ItemBuilder(item.getMaterial())
                    .name("&f" + prettyName(item.getMaterial()));

            if (item.canBuy()) {
                builder.lore("&aMua: &f" + manager.formatPrice(item.getBuyPrice()) + " &7/ 1 item");
            } else {
                builder.lore("&7Khong the mua");
            }
            if (item.canSell()) {
                builder.lore("&6Ban: &f" + manager.formatPrice(item.getSellPrice()) + " &7/ 1 item");
            } else {
                builder.lore("&7Khong the ban");
            }
            builder.lore(" ");
            builder.lore("&eClick trai: &7mua 1");
            builder.lore("&eShift+trai: &7mua 1 chong (64)");
            builder.lore("&eClick phai: &7ban 1");
            builder.lore("&eShift+phai: &7ban 1 chong (64)");

            inventory.setItem(slot, builder.build());
            slotToItem.put(slot, item);
            slot++;
        }

        ItemStack back = new ItemBuilder(Material.ARROW)
                .name("&c« Quay lai")
                .lore("&7Tro ve menu chinh")
                .build();
        inventory.setItem(BACK_SLOT, back);
    }

    private String prettyName(Material material) {
        String[] parts = material.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part.charAt(0)).append(part.substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
    }

    public ShopItem getItemAt(int slot) {
        return slotToItem.get(slot);
    }

    public ShopCategory getCategory() {
        return category;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
