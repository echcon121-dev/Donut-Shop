package net.dennislist.donutshop.commands;

import net.dennislist.donutshop.ShopManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShopAdminCommand implements CommandExecutor {

    private final ShopManager manager;

    public ShopAdminCommand(ShopManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("donutshop.admin")) {
            sender.sendMessage(ChatColor.RED + "Ban khong co quyen dung lenh nay.");
            return true;
        }
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.YELLOW + "Dung: /shopadmin reload");
            return true;
        }
        manager.reload();
        sender.sendMessage(ChatColor.GREEN + "Da reload config.yml cua DonutShop ("
                + manager.getCategories().size() + " danh muc).");
        return true;
    }
}
