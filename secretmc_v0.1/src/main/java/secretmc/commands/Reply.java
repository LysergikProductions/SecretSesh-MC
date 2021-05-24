package secretmc.commands;

import secretmc.commands.restricted.Admin;
import secretmc.data.PlayerMeta;
import secretmc.tasks.Analytics;

import java.util.Arrays;
import org.bukkit.entity.Player;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class Reply implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		Player p = (Player) sender;
		String sendName = p.getName();

		if (args.length < 1) {
			sender.sendMessage("\u00A7cIncorrect syntax. Syntax: /r [message]");
			return true;
		}
		if (!Message.Replies.containsKey(p.getUniqueId())) {
			sender.sendMessage("\u00A7cNobody to reply to.");
			return true;
		}
		if (Message.Replies.get(p.getUniqueId()) == null) {
			sender.sendMessage("\u00A7cCan't reply to Console.");
			return true;
		}
		
		if (!PlayerMeta.isAdmin(p)) Analytics.r_cmd++;

		// Get recipient
		Player recv = Bukkit.getPlayer(Message.Replies.get(p.getUniqueId()));
		String recvName;

		if (recv == null) {
			sender.sendMessage("\u00A7cPlayer is not online.");
			return true;
		} else if (AFK._AFKs.contains(recv.getUniqueId())) {
			sender.sendMessage("\u00A7cPlayer is currently AFK.");
			return true;
		}

		recvName = recv.getName();

		// Muted players can't send or recieve messages.
		if (PlayerMeta.isMuted(p)) {
			sender.sendMessage("\u00A7cYou can't send messages.");
			return true;
		} else if (PlayerMeta.isMuted(recv)) {
			sender.sendMessage("\u00A7cYou can't send messages to this person.");
			return true;
		}

		// Concatenate
		final String[] msg = new String[]{""};
		Arrays.asList(args).forEach( s -> msg[0] += s + " ");
		msg[0] = msg[0].trim();

		String finalRecvName = recvName;
		Bukkit.getOnlinePlayers().forEach(pl -> {
			if (Admin.Spies.contains(pl.getUniqueId())) {
				pl.sendMessage("\u00A75" + sendName + " to " + finalRecvName + ": " + msg[0]);
			}
		});

		if (!Admin.Spies.contains(recv.getUniqueId())) {
			recv.sendMessage("\u00A7dfrom " + sendName + ": " + msg[0]);
		}
		if (!Admin.Spies.contains(((Player) sender).getUniqueId())) {
			sender.sendMessage("\u00A7dto " + recvName + ": " + msg[0]);
		}
		Message.Replies.put(recv.getUniqueId(), ((Player) sender).getUniqueId());
		Message.Replies.put(((Player) sender).getUniqueId(), recv.getUniqueId());
		return true;
	}
}
