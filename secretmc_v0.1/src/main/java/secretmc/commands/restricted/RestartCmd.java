package secretmc.commands.restricted;

import secretmc.data.PlayerMeta;
import secretmc.backend.utils.Restart;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

// INTERNAL USE ONLY

public class RestartCmd implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

		if (!PlayerMeta.isOp(sender)) {
			sender.sendMessage("\u00A7cYou can't run this.");
			return true;
		}
		Restart.restart(args.length != 0);
		return true;
	}
}
