package net.dennislist.donutshop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Doc config.yml thanh cac ShopCategory, giu tham chieu Vault Economy,
 * va xu ly logic mua / ban / ban-tat-ca.
 */
public class ShopManager {

    private final DonutShop plugin;
    private final Economy economy;
    private final Map<String, ShopCategory> categories = new LinkedHashMap<>();

    private String mainMenuTitle;
    private int mainMenuRows;
    private String currencySymbol;
    private int priceDecimals;
    private boolean confirmSellAll;

    public ShopManager(DonutShop plugin, Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        categories.clear();

        ConfigurationSection settings = config.getConfigurationSection("settings");
        if (settings != null) {
            mainMenuTitle = color(settings.getString("main-menu-title", "&8Shop"));
            mainMenuRows = settings.getInt("main-menu-rows", 3);
            currencySymbol = settings.getString("currency-symbol", "$");
            priceDecimals = settings.getInt("price-decimals", 1);
            confirmSellAll = settings.getBoolean("confirm-sell-all", false);
        } else {
            mainMenuTitle = color("&8Shop");
            mainMenuRows = 3;
            currencySymbol = "$";
            priceDecimals = 1;
            confirmSellAll = false;
        }

        ConfigurationSection catSection = config.getConfigurationSection("categories");
        if (catSection == null) {
            return;
        }

        for (String key : catSection.getKeys(false)) {
            ConfigurationSection cat = catSection.getConfigurationSection(key);
            if (cat == null) {
                continue;
            }
            String displayName = color(cat.getString("display-name", key));
            Material icon = Material.matchMaterial(cat.getString("icon", "CHEST"));
            if (icon == null) {
                icon = Material.CHEST;
            }
            int slot = cat.getInt("slot", 0);

            List<ShopItem> items = new ArrayList<>();
            List<Map<?, ?>> rawItems = cat.getMapList("items");
            for (Map<?, ?> raw : rawItems) {
                Object matObj = raw.get("material");
                if (matObj == null) {
                    continue;
                }
                Material material = Material.matchMaterial(matObj.toString());
                if (material == null) {
                    plugin.getLogger().warning("Khong tim thay material '" + matObj + "' trong category '" + key + "', bo qua.");
                    continue;
                }
                double buy = toDouble(raw.get("buy"), -1);
                double sell = toDouble(raw.get("sell"), -1);
                items.add(new ShopItem(material, buy, sell));
            }

            categories.put(key, new ShopCategory(key, displayName, icon, slot, items));
        }
    }

    private double toDouble(Object obj, double def) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        return def;
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String formatPrice(double price) {
        return currencySymbol + String.format(Locale.US, "%." + priceDecimals + "f", price);
    }

    public Economy getEconomy() {
        return economy;
    }

    public Map<String, ShopCategory> getCategories() {
        return categories;
    }

    public ShopCategory getCategory(String key) {
        return categories.get(key);
    }

    public String getMainMenuTitle() {
        return mainMenuTitle;
    }

    public int getMainMenuRows() {
        return mainMenuRows;
    }

    public boolean isConfirmSellAll() {
        return confirmSellAll;
    }

    /**
     * Tim ShopItem theo material, bat ke category nao.
     */
    public ShopItem findByMaterial(Material material) {
        for (ShopCategory category : categories.values()) {
            for (ShopItem item : category.getItems()) {
                if (item.getMaterial() == material) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Mua so luong "amount" cua mot ShopItem cho player. Tra ve true neu thanh cong.
     */
    public boolean buy(Player player, ShopItem item, int amount) {
        if (!item.canBuy()) {
            player.sendMessage(color("&cMat hang nay khong the mua."));
            return false;
        }
        double total = item.getBuyPrice() * amount;
        if (economy.getBalance(player) < total) {
            player.sendMessage(color("&cBan khong du tien! Can " + formatPrice(total) + "."));
            return false;
        }
        PlayerInventory inv = player.getInventory();
        ItemStack stack = new ItemStack(item.getMaterial(), amount);
        if (inv.firstEmpty() == -1 && !inv.containsAtLeast(stack, 1)) {
            // khong con cho trong trong tui do
            player.sendMessage(color("&cTui do cua ban da day!"));
            return false;
        }
        economy.withdrawPlayer(player, total);
        Map<Integer, ItemStack> leftover = inv.addItem(stack);
        for (ItemStack extra : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), extra);
        }
        player.sendMessage(color("&aDa mua &f" + amount + "x " + item.getMaterial().name()
                + " &avoi gia &f" + formatPrice(total) + "&a."));
        return true;
    }

    /**
     * Ban so luong "amount" cua mot ShopItem tu tui do player. Tra ve true neu thanh cong.
     */
    public boolean sell(Player player, ShopItem item, int amount) {
        if (!item.canSell()) {
            player.sendMessage(color("&cMat hang nay khong the ban."));
            return false;
        }
        PlayerInventory inv = player.getInventory();
        ItemStack probe = new ItemStack(item.getMaterial(), amount);
        if (!inv.containsAtLeast(probe, amount)) {
            player.sendMessage(color("&cBan khong co du so luong de ban."));
            return false;
        }
        inv.removeItem(probe);
        double total = item.getSellPrice() * amount;
        economy.depositPlayer(player, total);
        player.sendMessage(color("&aDa ban &f" + amount + "x " + item.getMaterial().name()
                + " &avoi gia &f" + formatPrice(total) + "&a."));
        return true;
    }

    /**
     * Ban toan bo item co the ban duoc trong tui do (khong dung armor/offhand).
     */
    public double sellAll(Player player) {
        PlayerInventory inv = player.getInventory();
        double totalEarned = 0;
        ItemStack[] contents = inv.getStorageContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }
            ShopItem item = findByMaterial(stack.getType());
            if (item == null || !item.canSell()) {
                continue;
            }
            totalEarned += item.getSellPrice() * stack.getAmount();
            contents[i] = null;
        }
        if (totalEarned > 0) {
            inv.setStorageContents(contents);
            economy.depositPlayer(player, totalEarned);
        }
        return totalEarned;
    }
}
