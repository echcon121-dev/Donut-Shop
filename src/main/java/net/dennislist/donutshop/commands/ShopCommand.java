package net.dennislist.donutshop.commands;

import net.dennislist.donutshop.ShopManager;
import net.dennislist.donutshop.gui.MainMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private final ShopManager manager;

    public ShopCommand(ShopManager manager) {
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
        player.openInventory(new MainMenu(manager).getInventory());
        return true;
    }
}
