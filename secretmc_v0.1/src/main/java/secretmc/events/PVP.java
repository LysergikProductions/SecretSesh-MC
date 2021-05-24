package secretmc.events;

/* *
 * 
 *  About: Listen for PVP related events to do various things,
 *  	primarily incrementing PVP related stats hash maps
 *  	stored in core.data.PlayerMeta
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

import secretmc.backend.Config;
import secretmc.data.PlayerMeta;
import secretmc.data.StatsManager;

import java.util.UUID;
import java.util.Objects;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

public class PVP implements Listener {
	
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	static boolean verbose = Boolean.parseBoolean(Config.getValue("verbose"));
	
	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		
		if (debug && verbose) System.out.println("[core.events.pvp] onKill has been called");
		
		Player killed = event.getEntity();
		UUID killedID = killed.getUniqueId();
		Player killer = killed.getKiller();

		String killerName;
		String killerLoc;

		String killedName = killed.getName();
		
		if (debug) {
			try {
				killerName = Objects.requireNonNull(killer).getName();
				killerLoc = killer.getLocation().getX() +
						", "+killer.getLocation().getY() +
						", "+killer.getLocation().getZ();

			} catch (Exception ignore) {
				System.out.println("[core.events.pvp] Killer was null!");

				killerName = "null";
				killerLoc = killed.getLocation().getX() +
						", "+killed.getLocation().getY() +
						", "+killed.getLocation().getZ();
			}
			System.out.println("[core.events.pvp] "+killerName+" "+killedName+" "+killerLoc);
		}

		// increment appropriate stats, do nothing if this was not a PVP kill
		if (killer != null) {
			StatsManager.incKillTotal(killer, 1);
			StatsManager.incDeathTotal(killed, 1);
		} else return;

		// check if victim was in the spawn region on death
		OfflinePlayer victim = Bukkit.getOfflinePlayer(killedID);
		double victim_playtime = PlayerMeta.getPlaytime(victim);
		
		double cX = killed.getLocation().getX();
		double cZ = killed.getLocation().getZ();
		
		double max_x; double max_z;
		double min_x; double min_z;
		
		double config_max_x = Double.parseDouble(Config.getValue("spawn.max.X"));
		double config_max_z = Double.parseDouble(Config.getValue("spawn.max.Z"));
		double config_min_x = Double.parseDouble(Config.getValue("spawn.min.X"));
		double config_min_z = Double.parseDouble(Config.getValue("spawn.min.Z"));
		
		if (Double.isNaN(config_max_x)) max_x = 420.0; else max_x = config_max_x;
		if (Double.isNaN(config_max_z)) max_z = 420.0; else max_z = config_max_z;
		if (Double.isNaN(config_min_x)) min_x = -420.0; else min_x = config_min_x;
		if (Double.isNaN(config_min_z)) min_z = -420.0; else min_z = config_min_z;

		if (cX < max_x && cZ < max_z && cX > min_x && cZ > min_z) {
			if (debug) System.out.println(killedName + " was killed in the spawn region!");

			if (victim_playtime < 3600.0) {

				if (debug) System.out.println(killedName + " was also a new player!");
				StatsManager.incSpawnKill(killer, 1);
			}
		}
	}
}
