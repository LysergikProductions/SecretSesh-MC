package secretmc.commands.restricted;

import secretmc.data.PlayerMeta;
import secretmc.data.PlayerMeta.MuteType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class Mute implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		String name;

		if (sender instanceof Player) name = sender.getName();
		else name = "CONSOLE";

		if (!PlayerMeta.isOp(sender)) {
			sender.sendMessage("\u00A7cYou can't use this.");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage("\u00A7cInvalid syntax. Syntax: /mute <perm/temp/none/all> [player]");
			return true;
		}

		String mode = args[0];
		if (mode.equals("all")) {
			PlayerMeta.MuteAll = !PlayerMeta.MuteAll;
			Bukkit.getServer().spigot()
					.broadcast(PlayerMeta.MuteAll ?
							new TextComponent("\u00A74\u00A7l" + name + " \u00A7r\u00A74has silenced the chat.") :
							new TextComponent("\u00A7a\u00A7l" + name + " \u00A7r\u00A7ahas un-silenced the chat."));
			return true;
		}

		Player toMute = null;
		try {
			if (args[1] != null) toMute = Bukkit.getPlayer(args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("/mute probably entered incorrectly..");
			sender.sendMessage("Syntax: /mute [type] [player]");
		} catch (NullPointerException e) {
			System.out.println("/mute target is null");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (toMute == null) {
			sender.sendMessage("Player is not online.");
			return true;
		}
		if (toMute.isOp()) {
			sender.sendMessage("You can't mute this person.");
			return true;
		}

		switch (mode.toUpperCase()) {
			case "PERM":
				if(PlayerMeta.isMuted(toMute)) {
					sender.sendMessage("\u00A7cPlayer is already muted.");
					break;
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent(
						"\u00A74\u00A7l" + name + " \u00A7r\u00A74has permanently muted \u00A74\u00A7l" +
								toMute.getName() + " \u00A7r\u00A74."));
				PlayerMeta.setMuteType(toMute, MuteType.PERMANENT);
				break;

			case "TEMP":
				if(PlayerMeta.isMuted(toMute)) {
					sender.sendMessage("\u00A7cPlayer is already muted.");
					break;
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent(
						"\u00A7c\u00A7l" + name + " \u00A7r\u00A7chas temporarily muted \u00A7c\u00A7l" +
								toMute.getName() + " \u00A7r\u00A7c."));
				PlayerMeta.setMuteType(toMute, MuteType.TEMPORARY);
				break;

			case "NONE":
				if(!PlayerMeta.isMuted(toMute)) {
					sender.sendMessage("\u00A7cPlayer isn't muted.");
					break;
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent(
						"\u00A7a\u00A7l" + name + " \u00A7r\u00A7ahas un-muted \u00A7a\u00A7l" +
								toMute.getName() + "\u00A7r\u00A7a."));
				PlayerMeta.setMuteType(toMute, MuteType.NONE);
				break;

			case "IP":
				if(PlayerMeta.isMuted(toMute)) {
					sender.sendMessage("\u00A7cIP is already muted.");
					break;
				}
				Bukkit.getServer().spigot().broadcast(new TextComponent(
						"\u00A74\u00A7l" + name + " \u00A7r\u00A74has IP muted \u00A74\u00A7l" +
								toMute.getName() + "\u00A7r\u00A74."));
				PlayerMeta.setMuteType(toMute, MuteType.IP);

			default:
				sender.sendMessage("\u00A7cInvalid syntax. Syntax: /mute <perm/temp/ip/none/all> [player]");
				return true;
		}
		return true;
	}
}
