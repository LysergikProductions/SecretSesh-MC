package secretmc.commands;

/* *
 *
 *  About: A command for players to get the Discord invite URL
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
import secretmc.tasks.Analytics;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Discord implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		Player player = (Player) sender;
		
		if (!PlayerMeta.isAdmin(player)) Analytics.discord_cmd++;
		String link = Config.getValue("discord.link");

		TextComponent message;
		if (!link.equals("tbd") && !link.equals("")) {

			message = new TextComponent("Click this message to join the Discord.");
			message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));

		} else message = new TextComponent("Discord coming soon!");

		message.setColor(ChatColor.GOLD);
		sender.sendMessage(message);

		return true;
	}
}
