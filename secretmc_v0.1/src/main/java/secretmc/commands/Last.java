package secretmc.commands;

/* *
 *
 *  About: Displays last three whispers the command-sender received
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

import secretmc.backend.utils.Reversed;

import java.util.UUID;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Last implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player player = (Player)sender; UUID playerid;
        playerid = player.getUniqueId();

        if (!Message.recentWhispers.containsKey(playerid)) player.sendMessage(
                "\u00A76You don't have any recent whispers!");
        else {
            for (String thisWhisper: Reversed.reverse(Message.recentWhispers.get(playerid))) {
                player.sendMessage("\u00A75" + thisWhisper);
            }
        }
        return true;
    }
}
