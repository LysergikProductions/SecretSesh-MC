package secretmc.commands;

import secretmc.backend.Config;
import secretmc.data.PlayerMeta;
import secretmc.data.PlayerMeta.MuteType;
import secretmc.tasks.Analytics;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class VoteMute implements CommandExecutor {
	
	static HashMap<UUID, Integer> _votes = new HashMap<>();
	static HashMap<UUID, List<UUID>> _voters = new HashMap<>();
	static HashMap<String, List<UUID>> _voterIps = new HashMap<>();
	
	public static int cooldown = 0;

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		
		if (args.length != 1) {
			sender.sendMessage("\u00A7cInvalid syntax. Syntax: /vm [player]");
			return true;
		}

		if (Bukkit.getPlayer(args[0]) == null) {
			sender.sendMessage("\u00A7cPlayer is not online.");
			return true;
		}
		
		if(Config.getValue("mute.enabled").equals("false")) {
			sender.sendMessage("\u00A7cThis command has been disabled by your server administrator.");
			return true;
		}

		Player voter = (Player) sender;
		Player toMute = Bukkit.getPlayer(args[0]);
		
		if (!PlayerMeta.isAdmin(voter)) Analytics.vm_cmd++;

		if (PlayerMeta.getPlaytime(Bukkit.getOfflinePlayer(voter.getUniqueId())) < 1800) {

			sender.sendMessage("\u00A7cYou can vote after playing for 30 minutes or more!");
			return false;
		}

		int popNeeded = (int) (Bukkit.getOnlinePlayers().size()
				* (Float.parseFloat(Config.getValue("mute.pop")) / 100.0f));

		if (popNeeded <= 1) {
			int rnd = (int) ((Math.random() * (5)) + 0);
			if (rnd == 3) {
				voter.sendMessage("\u00A7cno votey not enough");
			} else {
				voter.sendMessage("\u00A7cThere are not enough players online to conduct a vote.");
			}
			return true;
		}

		// Can't vote on an already-muted person.
		assert toMute != null;
		if (PlayerMeta.isMuted(toMute)) {
			voter.sendMessage("\u00A7cPlayer is already muted.");
			return true;
		}

		// Muted people can't vote.
		if ((PlayerMeta.isMuted(voter) && Config.getValue("mute.hypocrisy").equals("1")) || PlayerMeta.isPrisoner(voter)) {
			voter.sendMessage("\u00A7cYou can't vote.");
			return true;
		}

		// Can't mute ops
		if (toMute.isOp()) {
			voter.sendMessage("\u00A7cYou can't vote to mute this person.");
			return true;
		}

		// List of previous votes to be filled.
		List<UUID> previousVotes;

		// List of previous IP votes
		List<UUID> previousIpVotes;

		// Load previous votes
		if (_voters.containsKey(voter.getUniqueId())) {
			previousVotes = _voters.get(voter.getUniqueId());
			if (previousVotes.contains(toMute.getUniqueId())) {
				int votes = _votes.get(toMute.getUniqueId());
				int need = popNeeded - votes;
				if (need <= 0) {
					Bukkit.spigot().broadcast(new TextComponent(
							"\u00A76The vote to mute " + toMute.getName() + " passed after " + popNeeded + " votes."));
					_votes.remove(toMute.getUniqueId());
					previousVotes.remove(toMute.getUniqueId());
					PlayerMeta.setMuteType(toMute, MuteType.TEMPORARY);
					return true;
				} else {
					voter.sendMessage(
							"\u00A7cYou've already voted for " + toMute.getName() + ". You need " + need + " more votes.");
				}
				return true;
			}
		}
		// Create new list of votes
		else {
			previousVotes = new ArrayList<>();
		}

		// Load previous IP votes
		if (_voterIps.containsKey(Objects.requireNonNull(voter.getAddress()).toString())) {
			previousIpVotes = _voterIps.get(voter.getAddress().toString());
			if (previousIpVotes.contains(toMute.getUniqueId())) {
				int votes = _votes.get(toMute.getUniqueId());
				int need = popNeeded - votes;
				if (need <= 0) {
					Bukkit.spigot().broadcast(new TextComponent(
							"\u00A76The vote to mute " + toMute.getName() + " passed after " + popNeeded + " votes."));
					_votes.remove(toMute.getUniqueId());
					previousVotes.remove(toMute.getUniqueId());
					PlayerMeta.setMuteType(toMute, MuteType.TEMPORARY);
					return true;
				} else {
					voter.sendMessage(
							"\u00A7cYou've already voted for " + toMute.getName() + ". You need " + need + " more votes.");
				}
				return true;
			}
		}

		// Create new list of IP votes
		else {
			previousIpVotes = new ArrayList<>();
		}

		// If our candidate has been voted on before
		if (_votes.containsKey(toMute.getUniqueId())) {
			// New votes value
			int votes = _votes.get(toMute.getUniqueId()) + 1;
			// Put our vote into repository
			_votes.put(toMute.getUniqueId(), votes);
			// Flag that we've voted for this person and can't vote again.
			previousVotes.add(toMute.getUniqueId());
			_voters.put(voter.getUniqueId(), previousVotes);
			// Flag that this IP has voted for this person and can't vote again.
			previousIpVotes.add(toMute.getUniqueId());
			_voterIps.put(voter.getAddress().toString(), previousIpVotes);
			if (popNeeded - votes <= 0) {
				PlayerMeta.setMuteType(toMute, MuteType.TEMPORARY);
				Bukkit.spigot().broadcast(
						new TextComponent("\u00A76The vote to mute " + args[0] + " passed after " + popNeeded + " votes."));
				_votes.remove(toMute.getUniqueId());
				previousVotes.remove(toMute.getUniqueId());

			} else {
				voter.sendMessage("\u00A7aSuccessfully submitted vote for " + toMute.getName()
						+ ". " + (popNeeded - votes) + " more needed for mute.");
			}
		}
		// If our candidate has not been voted on before
		else {
			if (cooldown != 0) {
				sender.sendMessage(
						"\u00A7cServer must wait " + (cooldown / 20) + " more seconds to vote mute again.");
				return true;
			}
			_votes.put(toMute.getUniqueId(), 1);
			previousVotes.add(toMute.getUniqueId());
			_voters.put(voter.getUniqueId(), previousVotes);
			previousIpVotes.add(toMute.getUniqueId());
			_voterIps.put(voter.getAddress().toString(), previousIpVotes);
			Bukkit.spigot()
					.broadcast(new TextComponent("\u00A76" + voter.getName() + " has started a vote to mute "
							+ toMute.getName() + ". " + (popNeeded - 1) + " more votes are needed to mute. \u00A7lUse /vm "
							+ toMute.getName() + " to vote."));
			cooldown = 1500;
		}
		return true;
	}

	public static void clear() {
		_votes.clear();
		_voters.clear();
		_voterIps.clear();
	}

	public static void processVoteCooldowns() {
		if (cooldown != 0) {
			cooldown = cooldown - 1;
		}
	}
}
