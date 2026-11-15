package net.dennislist.donutshop.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper nho de tao ItemStack co ten + lore nhanh, tu dong doi ma mau '&'.
 */
public class ItemBuilder {

    private final ItemStack stack;
    private final ItemMeta meta;
    private final List<String> lore = new ArrayList<>();

    public ItemBuilder(Material material) {
        this.stack = new ItemStack(material);
        this.meta = stack.getItemMeta();
    }

    public ItemBuilder amount(int amount) {
        stack.setAmount(Math.max(1, amount));
        return this;
    }

    public ItemBuilder name(String name) {
        if (meta != null) {
            meta.setDisplayName(color(name));
        }
        return this;
    }

    public ItemBuilder lore(String line) {
        lore.add(color(line));
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        for (String line : lines) {
            lore.add(color(line));
        }
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
