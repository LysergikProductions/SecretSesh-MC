package secretmc.commands;

import secretmc.data.PlayerMeta;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class Redeem implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		Player player = (Player) sender;
		if (args.length != 1) {
			player.sendMessage("\u00A7cSyntax: /redeem [code]");
			return true;
		}

		if (PlayerMeta.isDonator(player)) {
			player.sendMessage("\u00A7cYou are already a donator. You keep it for life.");
			return true;
		}

		if (PlayerMeta.DonorCodes.contains(args[0]) && !PlayerMeta.UsedDonorCodes.contains(args[0])) {

			PlayerMeta.UsedDonorCodes.add(args[0]);
			PlayerMeta.setDonator(player, true);
			Bukkit.getServer().spigot()
					.broadcast(new TextComponent("\u00A76" + player.getName() + " just donated to the server!"));
		} else {
			player.sendMessage("\u00A7cThis code is not valid.");
		}
		return true;
	}
}
