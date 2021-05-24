package secretmc.commands;

import secretmc.tasks.Analytics;
import secretmc.data.PlayerMeta;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.jetbrains.annotations.NotNull;

public class Kill implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		
		if (!PlayerMeta.isAdmin((Player)sender)) Analytics.kill_cmd++;
		
		if (!(sender instanceof ConsoleCommandSender) && args.length == 0) {
			
			((Player) sender).setHealth(0);
			return true;
			
		} else {
			if (args.length == 1 && Bukkit.getPlayer(args[0]) != null) {
				if (sender instanceof ConsoleCommandSender) {
					
					Objects.requireNonNull(Bukkit.getPlayer(args[0])).setHealth(0);
					
				} else {
					
					if ((sender).isOp()) {
						Objects.requireNonNull(Bukkit.getPlayer(args[0])).setHealth(0);
					}
				}
			}
		}
		return true;
	}
}
