package net.dennislist.donutshop;

import org.bukkit.Material;

/**
 * Mot dong hang trong shop: material + gia mua + gia ban.
 * buyPrice/sellPrice = -1 nghia la khong ho tro chieu do.
 */
public class ShopItem {

    private final Material material;
    private final double buyPrice;
    private final double sellPrice;

    public ShopItem(Material material, double buyPrice, double sellPrice) {
        this.material = material;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public Material getMaterial() {
        return material;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public boolean canBuy() {
        return buyPrice > 0;
    }

    public boolean canSell() {
        return sellPrice > 0;
    }
}
