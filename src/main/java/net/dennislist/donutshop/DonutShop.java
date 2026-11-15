package net.dennislist.donutshop;

import net.dennislist.donutshop.commands.SellCommand;
import net.dennislist.donutshop.commands.ShopAdminCommand;
import net.dennislist.donutshop.commands.ShopCommand;
import net.dennislist.donutshop.gui.ShopListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class DonutShop extends JavaPlugin {

    private static DonutShop instance;
    private Economy economy;
    private ShopManager shopManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Khong tim thay Vault + plugin economy (Essentials, CMI, ...)!");
            getLogger().severe("DonutShop se tu tat vi khong the mua/ban item.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.shopManager = new ShopManager(this, economy);

        getCommand("shop").setExecutor(new ShopCommand(shopManager));
        getCommand("sell").setExecutor(new SellCommand(shopManager));
        getCommand("shopadmin").setExecutor(new ShopAdminCommand(shopManager));

        getServer().getPluginManager().registerEvents(new ShopListener(shopManager), this);

        getLogger().info("DonutShop da bat - " + shopManager.getCategories().size() + " danh muc da load.");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> provider = getServer()
                .getServicesManager()
                .getRegistration(Economy.class);
        if (provider == null) {
            return false;
        }
        economy = provider.getProvider();
        return economy != null;
    }

    public static DonutShop getInstance() {
        return instance;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }
}
