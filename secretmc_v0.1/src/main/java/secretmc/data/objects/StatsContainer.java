package secretmc.data.objects;

/* *
 * 
 *  About: A class object that is used as the data container
 *  	for all PVP related stats while they are in memory 
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

import java.util.UUID;
import java.io.Serializable;

import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;

@SuppressWarnings("SpellCheckingInspection")
public class StatsContainer implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public UUID playerid; public int killTotal;
	public int deathTotal; public String kd;
	public int spawnKills;
	
	static boolean debug = Boolean.parseBoolean(Config.getValue("debug"));
	
	public StatsContainer(UUID playerid, int killTotal, int deathTotal, String kd, int spawnKills) {

		this.playerid = playerid; this.killTotal = killTotal; this.deathTotal = deathTotal;
		this.kd = kd; this.spawnKills = spawnKills;
	}
	
	@Override
    public String toString() {
		return playerid + ":" + killTotal + ":" + deathTotal + ":" + kd + ":" + spawnKills;
    }
	
	public static StatsContainer fromString(String line) {
		// Example of intended given line: f6c6e3a1-a1ec-4fee-9d1d-f5e495c3e9d7:4:0:Unkillable!:2
		
		String[] stats = line.split(":");
		
		UUID playerid = UUID.fromString(stats[0]);
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerid);
		String player_name = player.getName();
		
		if (debug) {
			System.out.println("Parsed ign: " + player_name);
			System.out.println("Parsed id: " + playerid);
		}
		
		int killTotal;
		
		try {killTotal = Integer.parseInt(stats[1]);} catch (Exception e) {killTotal = 0;}
		if (debug) System.out.println("Parsed kills: " + killTotal);
		
		int deathTotal;
		
		try {deathTotal = Integer.parseInt(stats[2]);} catch (Exception e) {deathTotal = 0;}
		if (debug) System.out.println("Parsed deaths: " + deathTotal);
		
		String kd;
		
		try {kd = stats[3];} catch (Exception e) {kd = "null";}
		if (debug) System.out.println("Parsed k/d: " + kd);
		
		int spawnKills;		
		try {spawnKills = Integer.parseInt(stats[4]);} catch (Exception e) {spawnKills = 0;}
		if (debug) System.out.println("Parsed spawn kills: " + spawnKills);

		return new StatsContainer(playerid, killTotal, deathTotal, kd, spawnKills);
	}
}
