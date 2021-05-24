package secretmc.commands.restricted;

/* *
 *
 *  About: Check a 3x3 chunk grid around each online player for lag block count
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

import secretmc.backend.utils.Chunks;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Check implements CommandExecutor {

    public static Map<UUID, Integer> lagList = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        Player player = (Player)sender;
        if (!player.isOp()) { player.sendMessage("You can't use this!"); return false; }

        for (Player thisPlayer: Bukkit.getServer().getOnlinePlayers()) {
            int thisCount = 0;

            if (!thisPlayer.isOp()) {
                thisCount = Chunks.countChunkLagBlocks(thisPlayer);
                if (thisCount > 256) lagList.put(thisPlayer.getUniqueId(), thisCount);
            }
        }

        // convert to and send message in human-readable form
        for (Map.Entry thisEntry: lagList.entrySet()) {
            player.sendMessage(
                    Objects.requireNonNull(Bukkit.getPlayer((UUID)thisEntry.getKey()))
                            .getName() + " | " + thisEntry.getValue() + " lag blocks");
        }
        return true;
    }
}
