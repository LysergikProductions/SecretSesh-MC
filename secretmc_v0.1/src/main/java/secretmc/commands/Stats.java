package secretmc.commands;

import secretmc.Main;
import secretmc.backend.*;
import secretmc.data.PlayerMeta;
import secretmc.data.SettingsManager;
import secretmc.data.objects.*;
import secretmc.tasks.Analytics;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Stats implements CommandExecutor {
	public static int sessionUses = 0;

	public static TextComponent allTimezoneIDs = new TextComponent(
			ChatColor.AQUA + "Click here to see all timezone IDs!"); static {
				allTimezoneIDs.setItalic(true);
				allTimezoneIDs.setClickEvent(new ClickEvent(
						ClickEvent.Action.OPEN_URL,
						"https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/"));
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		sessionUses++;
		
		Player player = (Player) sender;
		UUID playerid = player.getUniqueId();
		
		if (!PlayerMeta.isAdmin(player)) Analytics.stats_total++;
		
		SettingsContainer targetSettings = PlayerMeta.sPlayerSettings.get(playerid);
		if (targetSettings == null) {
			
			SettingsContainer newSettings = SettingsManager.getNewSettings(Bukkit.getOfflinePlayer(playerid));
			PlayerMeta.sPlayerSettings.put(playerid, newSettings);
		}
		
		// top player by playtime
		if (Main.Top == null) {
			double largest = 0;
			
			for (UUID u : PlayerMeta.Playtimes.keySet()) {
				
				if (PlayerMeta.Playtimes.get(u) > largest) {
					largest = PlayerMeta.Playtimes.get(u);
					Main.Top = Bukkit.getOfflinePlayer(u);
				}
			}
		}
		
		// check args
		if (args.length != 0) {
			int leaderLimit;

			try {
				leaderLimit = Integer.parseInt(args[0]);
			} catch (Exception e) {
				leaderLimit = -1;
			}

			if (leaderLimit == -1) {
				switch (args[0]) {
					case "top":

						assert Main.Top != null;
						ChatPrint.printStats(player, Main.Top);
						return true;

					case "lb":
					case "leaderboard":
					case "leaderboards":

						ChatPrint.printLeaders(player, 5);
						return true;

					case "help":

						HelpPages.helpStats(player);
						if (!PlayerMeta.isAdmin(player)) Analytics.stats_help++;
						return true;

					case "kill":
					case "kills":

						assert targetSettings != null;
						targetSettings.show_kills = !targetSettings.show_kills;

						if (targetSettings.show_kills) {
							player.sendMessage("Your kills are now public.");

						} else {
							player.sendMessage("Your kills are now hidden.");
						}
						return true;

					case "death":
					case "deaths":

						assert targetSettings != null;
						targetSettings.show_deaths = !targetSettings.show_deaths;

						if (targetSettings.show_deaths) {
							player.sendMessage("Your deaths are now public.");

						} else {
							player.sendMessage("Your deaths are now hidden.");
						}
						return true;

					case "kd":
					case "k/d":

						assert targetSettings != null;
						targetSettings.show_kd = !targetSettings.show_kd;

						if (targetSettings.show_kd) {
							player.sendMessage("Your k/d ratio is now public.");

						} else {
							player.sendMessage("Your k/d ratio is now hidden.");
						}
						return true;

					case "tz":
					case "timezone":

						if (args.length != 2) {
							player.sendMessage(ChatColor.GRAY +
									"Correct Syntax: /stats timezone EST | America/Phoenix | GMT | etc");
							player.sendMessage(allTimezoneIDs);
							return false;
						}

						String current_tz = Objects.requireNonNull(targetSettings).timezone;
						targetSettings.timezone = args[1];

						player.sendMessage(
								"You changed your set timezone from " + current_tz + " to " + targetSettings.timezone
						);
						return true;

					case "mc":

						ChatPrint.printMcStats(player, Bukkit.getOfflinePlayer(player.getUniqueId()));
						return true;

					case "info":
					case "setting":
					case "settings":

						ChatPrint.printPlayerSettings(player);
						if (!PlayerMeta.isAdmin(player)) Analytics.stats_info++;
						return true;
				}
			} else { // arg was an integer
				ChatPrint.printLeaders(player, leaderLimit);
				return true;
			}

			// user has submitted a probable username argument
			OfflinePlayer offline_player = Bukkit.getServer().getOfflinePlayer(args[0]);
			
			if (!offline_player.hasPlayedBefore()) {
				
				player.sendMessage("This player has never joined.");
				return true;
			}
			
			ChatPrint.printStats(player, offline_player);

		} else { // user supplied no arguments
			
			OfflinePlayer target = Bukkit.getOfflinePlayer(player.getUniqueId());
			
			ChatPrint.printStats(player, target);
		}
		return true;
	}
}
