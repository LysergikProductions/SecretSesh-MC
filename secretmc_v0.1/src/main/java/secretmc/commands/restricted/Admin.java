package secretmc.commands.restricted;

import secretmc.backend.*;
import secretmc.backend.utils.Restart;
import secretmc.backend.utils.Util;
import secretmc.data.Aliases;
import secretmc.data.PlayerMeta;
import secretmc.tasks.Analytics;
import secretmc.events.SpeedLimiter;

import java.util.*;
import java.io.IOException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class Admin implements CommandExecutor {

	public static List<UUID> Spies = new ArrayList<>();
	public static List<UUID> MsgToggle = new ArrayList<>();
	public static List<UUID> UseRedName = new ArrayList<>();
	public static Map<String, Location> LogOutSpots = new HashMap<>();
	public static boolean disableWarnings = false;

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		Player player = (Player) sender;
		
		if (!player.isOp()) Analytics.admin_cmd++;
		
		if (args.length == 1) {
			if (!PlayerMeta.isOp(sender)) {
				
				sender.sendMessage(new TextComponent("\u00A7cYou can't use this.").toLegacyText());
				return true;
			}
			
			switch (args[0].toUpperCase()) {
				case "COLOR":
					if (UseRedName.contains(player.getUniqueId())) {
						player.sendMessage("\u00A76Disabled red name.");
						UseRedName.remove(player.getUniqueId());
					} else {
						player.sendMessage("\u00A76Enabled red name.");
						UseRedName.add(player.getUniqueId());
					}
					return true;
					
				case "SPY":
					if (Spies.contains(player.getUniqueId())) {
						player.sendMessage("\u00A76Disabled spying on player messages.");
						Spies.remove(player.getUniqueId());
					} else {
						player.sendMessage("\u00A76Enabled spying on player messages.");
						Spies.add(player.getUniqueId());
					}
					return true;
					
				case "MSGTOGGLE":
					if (MsgToggle.contains(player.getUniqueId())) {
						player.sendMessage("\u00A76Enabled recieving player messages.");
						MsgToggle.remove(player.getUniqueId());
					} else {
						player.sendMessage("\u00A76Disabled recieving player messages.");
						MsgToggle.add(player.getUniqueId());
					}
					return true;

				case "RELOAD":
					try {
						Config.load();
						sender.sendMessage("\u00A7aSuccessfully reloaded.");

					} catch (IOException e) {
						sender.sendMessage("\u00A74Failed to reload.");
						Restart.restart();
					}
					return true;
					
				case "SPEED":
					player.sendMessage("\u00A76Player speeds:");
					List< Pair<Double, String> > speeds = SpeedLimiter.getSpeeds();
					
					for (Pair<Double, String> speedEntry : speeds) {
						double speed = speedEntry.getLeft();
						if(speed == 0) continue;
						String playerName = speedEntry.getRight();
						String color = "\u00A7";
						if (speed >= 64.0)
							color += "c"; // red
						else if (speed >= 48.0)
							color += "e"; // yellow
						else
							color += "a"; // green
						player.sendMessage(new TextComponent(color
								+ String.format("%4.1f: %s", speed, playerName)).toLegacyText());
					}
					player.sendMessage("\u00A76End of speed list.");
					return true;
					
				case "AGRO":
					disableWarnings = !disableWarnings;
					if(disableWarnings) {
						sender.sendMessage("\u00A76Enabled aggressive speed limit.");
					}
					else {
						sender.sendMessage("\u00A76Disabled aggressive speed limit.");
					}
					return true;

				case "CRYSTAL":
					player.chat(Aliases.invulCrystal);
					return true;

				case "ILLEGALS":
				case "ILLEGAL":
					player.chat(Aliases.illegals_kit);
					return true;
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("spot")) {
				
				Location loc = LogOutSpots.get(args[1]);
				
				if (loc == null) {
					sender.sendMessage("\u00A76No logout spot logged for " + args[1]);
				} else {
					
					String dimension = Util.getDimensionName(loc);
					String location = (int)loc.getX() + " " + (int)loc.getY() + " " + (int)loc.getZ();

					TextComponent logSpot = new TextComponent("\u00A76"+args[1] + " logged out at " + location);

					logSpot.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/ninjatp " + dimension + " " + location));
					
					sender.spigot().sendMessage(logSpot);
				}
				return true;
			} else if (args[0].equalsIgnoreCase("debug")) {
				if (args[1].equalsIgnoreCase("normal")) {
					Config.debug = true;
					Config.verbose = false;

					player.sendMessage("Config.debug is now true");
					player.sendMessage("Config.verbose is now false");

				} else if (args[1].equalsIgnoreCase("verbose")) {
					Config.debug = true;
					Config.verbose = true;

					player.sendMessage("Config.debug is now true");
					player.sendMessage("Config.verbose is now true");

				} else if (args[1].equalsIgnoreCase("off")) {
					Config.debug = false;
					Config.verbose = false;

					player.sendMessage("Config.debug is now false");
					player.sendMessage("Config.verbose is now false");

				} else {
					Config.debug = !Config.debug;
					player.sendMessage("Config.debug is now " + Config.debug);
				}
			}
		}
		
		TextComponent ops_a = new TextComponent("OP Accounts: ");
		TextComponent ops_b = new TextComponent("" + Bukkit.getOperators().size());
		
		ops_a.setColor(ChatColor.RED); ops_b.setColor(ChatColor.GRAY);
		TextComponent ops = new TextComponent(ops_a, ops_b);
		
		player.sendMessage("");
		player.sendMessage("\u00A7csinse420: \u00A77Server Admin, Developer, Founder");
		player.sendMessage(ops.toLegacyText());
		return true;
	}
}
