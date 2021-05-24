package secretmc.commands;

import secretmc.backend.HelpPages;
import secretmc.tasks.Analytics;
import secretmc.data.PlayerMeta;

import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Help implements CommandExecutor {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		Player player = (Player)sender;

		if (!PlayerMeta.isAdmin(player)) Analytics.help_cmd++;
		
		try {
			HelpPages.helpGeneral(player, Integer.parseInt(args[0]));
		} catch (Exception ex) {
			HelpPages.helpGeneral(player, 1);
		}
		return true;
	}
}
