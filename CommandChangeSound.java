package c.Test;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import c.Main;
import c.Messages;

public class CommandChangeSound implements CommandExecutor {
	File file = new File(Main.getInstance().getDataFolder() + File.separator + "test.yml");
	FileConfiguration data = YamlConfiguration.loadConfiguration(file);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Messages.only_for_player);
			return true;
		}
		Player target = sender.getServer().getPlayerExact(sender.getName());
		
		if(target.getWorld() == Bukkit.getServer().getWorld("Test")) {
			c.Test.Scripts.sound(target);
		}
		else {
			target.sendMessage(Messages.no_this_world + target.getWorld().getName());
			target.playSound(target.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 2);
		}
		return true;
	}
}
