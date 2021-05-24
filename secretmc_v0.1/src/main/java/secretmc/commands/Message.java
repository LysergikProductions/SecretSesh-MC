package secretmc.commands;

import secretmc.commands.restricted.Admin;
import secretmc.tasks.Analytics;
import secretmc.data.PlayerMeta;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class Message implements CommandExecutor {

	public static ArrayList<UUID> AFK_warned = new ArrayList<>();
	public static HashMap<UUID, UUID> Replies = new HashMap<>();
	public static HashMap<UUID, ArrayList<String>> recentWhispers = new HashMap<>();

	private static void addRecentWhisper(UUID thisUUID, String thisMessage) {
		String whisp_last; String whisp_1last; String whisp_2last;

		if (!recentWhispers.containsKey(thisUUID)) {

			ArrayList<String> newList = new ArrayList<>(); { newList.add(thisMessage); }
			recentWhispers.put(thisUUID, newList);
			return;
		}

		ArrayList<String> thisList = Message.recentWhispers.get(thisUUID);

		try { whisp_last = thisList.get(0); }
		catch (Exception ignore) { whisp_last = null; }

		try { whisp_1last = thisList.get(1); }
		catch (Exception ignore) { whisp_1last = null; }

		try { whisp_2last = thisList.get(2); }
		catch (Exception ignore) { whisp_2last = null; }

		if (whisp_last == null) {
			thisList.add(0, thisMessage);
			return;
		}

		if (whisp_1last == null && !whisp_last.equals(thisMessage)) {

			thisList.add(1, thisList.get(0));
			thisList.set(0, thisMessage);
			return;
		}

		if (whisp_2last == null && whisp_1last != null && !whisp_1last.equals(whisp_last)) {

			thisList.add(2, thisList.get(1));
			thisList.set(1, thisList.get(0));
			thisList.set(0, thisMessage);
			return;
		}

		thisList.set(2, whisp_1last);
		thisList.set(1, whisp_last);
		thisList.set(0, thisMessage);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		Player p; String sendName; UUID pid;
		
		if (args.length < 2) {
			sender.sendMessage("\u00A7cIncorrect syntax. Syntax: /msg [player] [message]");
			return true;
		}

		if (sender instanceof Player) {
			p = ((Player) sender);
			sendName = p.getName();
			pid = p.getUniqueId();
		} else {
			sendName = "Console";
			pid = null;
		}

		if (!sendName.equals("Console") && !AFK_warned.contains(pid)) {
			AFK_warned.add(pid);
		}

		if (sender instanceof Player && !PlayerMeta.isAdmin((Player) sender)) Analytics.msg_cmd++;

		// Get recipient
		final Player recv = Bukkit.getPlayer(args[0]);

		if (recv == null) {
			sender.sendMessage("\u00A7cPlayer is no longer online.");
			return true;

		} else if (AFK._AFKs.contains(recv.getUniqueId())) {
			sender.sendMessage("\u00A7cThis player is currently AFK.");

			if (!AFK_warned.contains(recv.getUniqueId())) {
				recv.sendMessage("\u00A7c" + sendName + " is trying to whisper you, but you are AFK.");
				AFK_warned.add(recv.getUniqueId());
			}
			return true;
		}

		String recvName = recv.getName();

		// Concatenate all messages
		final String[] msg = {""};

		final int[] x = {0};

		Arrays.stream(args).forEach(s ->  {
			if (x[0] == 0) {
				x[0]++;
				return;
			}
			msg[0] += s + " ";
		});
		msg[0] = msg[0].trim();


		// If either player is muted, refuse message.
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (PlayerMeta.isMuted(player)) {
				sender.sendMessage("\u00A7cYou can't send messages.");
				return true;
			}

			if (PlayerMeta.isMuted(recv) || (Admin.MsgToggle.contains(recv.getUniqueId()) && !player.isOp())) {
				sender.sendMessage("\u00A7cYou can't send messages to this person.");
				return true;
			}

			if(PlayerMeta.isIgnoring(player.getUniqueId(), recv.getUniqueId())) {
				sender.sendMessage("\u00A7cYou can't send messages to this person.");
				return true;
			}

			if(PlayerMeta.isIgnoring(recv.getUniqueId(), player.getUniqueId())) {
				sender.sendMessage("\u00A7cYou can't send messages to this person.");
				return true;
			}
		}

		// Cycle through online players & if they're an admin with spy enabled, send
		// them a copy of this message
		Bukkit.getOnlinePlayers().forEach(thisPlayer -> { if (Admin.Spies.contains(thisPlayer.getUniqueId())) {
			thisPlayer.sendMessage("\u00A75" + sendName + " to " + recvName + ": " + msg[0]);
		}});

		// send the whisper
		if (!Admin.Spies.contains(recv.getUniqueId())) {
			recv.sendMessage("\u00A7dfrom " + sendName + ": " + msg[0]);
		}
		addRecentWhisper(recv.getUniqueId(), sendName + ": " + msg[0]);

		if (sender instanceof Player && !Admin.Spies.contains(((Player) sender).getUniqueId())) {
			sender.sendMessage("\u00A7dto " + recvName + ": " + msg[0]);
		}
		if (sender instanceof Player) {
			Replies.put(recv.getUniqueId(), ((Player) sender).getUniqueId());
		}
		if (sender instanceof Player) {
			Replies.put(((Player) sender).getUniqueId(), recv.getUniqueId());
		}
		return true;
	}
}
