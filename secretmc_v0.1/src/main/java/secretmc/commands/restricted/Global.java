package secretmc.commands.restricted;

/* *
 * 
 *  About: Global effects for use on special occasions or during events
 * 
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021  Lysergik Productions (https://github.com/LysergikProductions)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * */

import secretmc.data.PlayerMeta;
import secretmc.backend.utils.Util;
import secretmc.events.SpawnController;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Global implements CommandExecutor {

	public static TextComponent dreamMsg; static {
		dreamMsg = new TextComponent("You wake up, confused.. was that a dream?");
		dreamMsg.setColor(ChatColor.GRAY);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

		Player op = (Player)sender;

		// check args
		if (args.length != 0) {
			
			switch (args[0].toUpperCase()) {
				case "ZAP":

					int i = 0;
					while (i < 3) { i++;
						
						for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							
							Location player_loc = p.getLocation();

							int range_max = (int)player_loc.getX() + 16;
							int range_min = (int)player_loc.getX() - 16;

							player_loc.setX(player_loc.getX() + Util.getRandomNumber(range_min, range_max));
							player_loc.setZ(player_loc.getZ() + Util.getRandomNumber(range_min, range_max));
							
							p.getWorld().spigot().strikeLightning(player_loc, false);
						}
					}	
					return true;

				case "DREAM":

					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						if (p.isOp() || PlayerMeta.isAdmin(p)) continue;

						String player_name = p.getName();
						Location playerSpawn = p.getBedSpawnLocation();
						Location baseLoc = p.getLocation();
						Location finalTP;

						if (playerSpawn == null) finalTP = SpawnController.getRandomSpawn(p.getWorld(), baseLoc);
						else finalTP = playerSpawn;

						String x = String.valueOf(finalTP.getBlockX());
						String y = String.valueOf(finalTP.getBlockY());
						String z = String.valueOf(finalTP.getBlockZ());

						op.chat("/tp " + player_name + " " + x + " " + y + " " + z);
						p.sendMessage(dreamMsg.toLegacyText());
					}
			}
		} return true;
	}
}
