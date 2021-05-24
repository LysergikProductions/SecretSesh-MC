package secretmc.commands.restricted;

/* *
 *
 *  About: Allow owner to modify configs
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
import secretmc.backend.utils.Restart;
import secretmc.data.PlayerMeta;

import java.io.IOException;
import org.bukkit.entity.Player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ConfigCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player player = (Player) sender;

        if (!player.isOp() || !PlayerMeta.isAdmin(player)) {
            player.sendMessage("You can't use this!");
            return false;

        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            try {
                Config.load();
                sender.sendMessage("\u00A7aSuccessfully reloaded.");

            } catch (IOException e) {
                sender.sendMessage("\u00A74Failed to reload.");
                if (Config.debug) Restart.restart();
            }
            return true;

        } else if (args.length != 2) {
            player.sendMessage("Correct syntax: /fig [key] [value]");
            return false;
        }

        String thisKey = args[0]; String thisValue = args[1];

        if (!Config.isRealConfig(thisKey)) {
            player.sendMessage("This is not a recognized config key!");
            return false;
        }

        if (thisValue.equalsIgnoreCase("true") || thisValue.equalsIgnoreCase("false")) {

            if (thisKey.equalsIgnoreCase("debug")) Config.debug = Boolean.parseBoolean(thisValue);
            else if (thisKey.equalsIgnoreCase("verbose")) Config.verbose = Boolean.parseBoolean(thisValue);

        } else Config.modifyConfig(thisKey, thisValue);

        player.sendMessage(thisKey + " is now set to " + Config.getValue(thisKey));
        return true;
    }
}
