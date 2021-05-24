package secretmc.data;

/* *
 * 
 *  About: Stores and mutates `StatsContainer` objects in memory
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

import secretmc.data.objects.StatsContainer;

import java.util.*;
import java.io.*;

import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

@SuppressWarnings("SpellCheckingInspection")
public class StatsManager {
	
	public static Map <UUID, StatsContainer> sPVPStats = new HashMap<>();
	
	public static void incKillTotal(Player p, int inc) {
		if (sPVPStats.containsKey(p.getUniqueId())) {
			
			StatsContainer stats = sPVPStats.get(p.getUniqueId());
			stats.killTotal += inc;
			
		} else {
			
			StatsContainer stats = new StatsContainer(p.getUniqueId(), 1, 0, "null", 0);
			sPVPStats.put(p.getUniqueId(), stats);
		}
	}
	
	public static void incDeathTotal(Player p, int inc) {
		if (sPVPStats.containsKey(p.getUniqueId())) {
			
			StatsContainer stats = sPVPStats.get(p.getUniqueId());
			stats.deathTotal += inc;
			
		} else {
			
			StatsContainer stats = new StatsContainer(p.getUniqueId(), 0, 1, "0.00", 0);
			sPVPStats.put(p.getUniqueId(), stats);
		}
	}
	
	public static void incSpawnKill (Player p, int inc) {
		if (sPVPStats.containsKey(p.getUniqueId())) {
			
			StatsContainer stats = sPVPStats.get(p.getUniqueId());
			stats.spawnKills += inc;
			
		} else {
			
			StatsContainer stats = new StatsContainer(p.getUniqueId(), 1, 0, "0.00", 0);
			sPVPStats.put(p.getUniqueId(), stats);
		}
	}
	
	public static StatsContainer getNewStats(OfflinePlayer p) {
		return new StatsContainer(p.getUniqueId(), 0, 0, "null", 0);
	}
	
	public static StatsContainer getStats(OfflinePlayer p) {
		StatsContainer stats = sPVPStats.get(p.getUniqueId());
		
		if (stats != null && sPVPStats.containsKey(p.getUniqueId())) {
			
			double kills = stats.killTotal;
			double deaths = stats.deathTotal;
			
			if (deaths < 0.710) {
				stats.kd = "Unkillable!";
			} else {
				stats.kd = Double.toString(kills / deaths);
			}
			
			return stats;
			
		} else {
			
			StatsContainer new_stats = getNewStats(p);
			sPVPStats.put(p.getUniqueId(), new_stats);
			
			return new_stats;
		}
	}

	public static void writePVPStats() throws IOException {
		
		BufferedWriter w = new BufferedWriter(new FileWriter("plugins/core/pvpstats.txt"));
		
		for (StatsContainer object: sPVPStats.values()) {
			try {
				w.write(object.toString() + "\n");
				w.flush();
				
			  } catch (IOException e) {
				  throw new UncheckedIOException(e);
			  }
		}
		w.close();
	}
}
