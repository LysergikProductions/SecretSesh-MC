package secretmc.commands.restricted;

import java.util.Arrays;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

// OP-only say command
public class Say implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

		if (!sender.isOp() && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage("\u00A7cUnknown command.");
			return true;
		}

		final String[] data = {""};
		Arrays.stream(args).forEach(arg -> data[0] += arg + " ");
		data[0] = data[0].trim();
		data[0] = data[0].replace("\u00A7", "");

		if (data[0].isEmpty()) {
			sender.sendMessage("\u00A7cNo message specified.");
			return true;
		}

		Bukkit.spigot().broadcast(new TextComponent("\u00A7d[Server] " + data[0]));
		System.out.println("\u00A7d[Server] " + data[0]);
		return true;
	}
}
