package secretmc.commands.restricted;

/* *
 * 
 *  About: A command for ops to backup vital rvas-core data files
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
import secretmc.data.FileManager;

import java.io.IOException;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class Backup implements CommandExecutor {
	
	public static int opBackupCounter = 0;
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		
		Player player = (Player) sender;
		if (!player.isOp() || args.length != 0) return false;
		
		opBackupCounter++;
		
		if (!PlayerMeta.isAdmin(player) && opBackupCounter > 10) {
			player.sendMessage(
					"Sorry, there have already been at least 10 backups this session.");
			return false;
		}
		
		try {
			player.sendMessage("saving pvpstats..");
			FileManager.backupData(FileManager.pvpstats_user_database, "pvpstats-backup-", ".txt");
			
			player.sendMessage("saving playtimes..");
			FileManager.backupData(FileManager.playtime_user_database, "playtime-backup-", ".db");
			
			player.sendMessage("saving player-settings..");
			FileManager.backupData(FileManager.settings_user_database, "player_settings-backup-", ".txt");
			
			player.sendMessage("saving muted players..");
			FileManager.backupData(FileManager.muted_user_database, "muted-backup-", ".db");
			
		} catch (IOException e) {
			
			System.out.println("Could not backup one or more files..");
			if (Config.debug) e.printStackTrace();
			
			player.sendMessage("There was an exception while trying to backup one or more files :(");
			return false;
		}
		return true;
	}
}
