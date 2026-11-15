package net.dennislist.donutshop.commands;

import net.dennislist.donutshop.ShopItem;
import net.dennislist.donutshop.ShopManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellCommand implements CommandExecutor {

    private final ShopManager manager;

    public SellCommand(ShopManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Lenh nay chi dung duoc trong game.");
            return true;
        }
        if (!player.hasPermission("donutshop.use")) {
            player.sendMessage(ChatColor.RED + "Ban khong co quyen dung shop.");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Dung: /sell hand | /sell all");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "hand" -> sellHand(player);
            case "all" -> sellAll(player);
            default -> player.sendMessage(ChatColor.YELLOW + "Dung: /sell hand | /sell all");
        }
        return true;
    }

    private void sellHand(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Ban dang khong cam item nao.");
            return;
        }
        ShopItem item = manager.findByMaterial(hand.getType());
        if (item == null || !item.canSell()) {
            player.sendMessage(ChatColor.RED + "Item nay khong the ban.");
            return;
        }
        manager.sell(player, item, hand.getAmount());
    }

    private void sellAll(Player player) {
        double earned = manager.sellAll(player);
        if (earned <= 0) {
            player.sendMessage(ChatColor.RED + "Ban khong co item nao de ban trong tui do.");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Da ban toan bo tui do, nhan duoc "
                + ChatColor.WHITE + manager.formatPrice(earned) + ChatColor.GREEN + ".");
    }
}
