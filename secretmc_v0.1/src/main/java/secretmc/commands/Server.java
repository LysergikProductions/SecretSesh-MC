package secretmc.commands;

import secretmc.backend.Config;
import secretmc.backend.LagProcessor;
import secretmc.data.PlayerMeta;
import secretmc.backend.ServerMeta;
import secretmc.backend.utils.Restart;
import secretmc.backend.utils.Util;

import secretmc.tasks.Analytics;
import secretmc.tasks.LagManager;
import secretmc.tasks.ProcessPlaytime;
import secretmc.events.SpeedLimiter;
import secretmc.events.ChatListener;

import java.util.Arrays;
import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class Server implements CommandExecutor {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		
		if (!PlayerMeta.isAdmin((Player)sender)) Analytics.server_cmd++;
		
		int witherCount = LagManager.getWithers();
		int witherLimit = Integer.parseInt(Config.getValue("wither.limit"));
		
		double tier1 = Double.parseDouble(Config.getValue("speedlimit.tier_one"));
		double tier2 = Double.parseDouble(Config.getValue("speedlimit.tier_two"));
		double tier3 = Double.parseDouble(Config.getValue("speedlimit.tier_three"));
		double tier4 = Double.parseDouble(Config.getValue("speedlimit.tier_four"));
		double tier5 = Double.parseDouble(Config.getValue("speedlimit.tier_five"));
		
		final double speed_limit;
		double tps = LagProcessor.getTPS();
		
		if (tps >= 16.0) {
			speed_limit = tier1;
			
		} else if (tps < 16.0 && tps >= 14.0) {
			speed_limit = tier2;
			
		} else if (tps < 14.0 && tps >= 10.0) {
			speed_limit = tier3;
			
		} else if (tps < 10.0 && tps >= 7.0) {
			speed_limit = tier4;
			
		} else if (tps < 7) {
			speed_limit = tier5;
		} else {
			speed_limit = tier1;
		}
		
		TextComponent title_sep = new TextComponent("===========");
		TextComponent title_name = new TextComponent(" SERVER HEALTH ");
		TextComponent player_head_name = new TextComponent(" PLAYERS ");
		TextComponent debug_head_name = new TextComponent(" DEBUG ");
		TextComponent moreinfo_head_name = new TextComponent(" MORE INFO ");
		
		//GENERAL
		TextComponent players_a = new TextComponent("Connected Players: ");
		TextComponent players_b = new TextComponent("" + Bukkit.getOnlinePlayers().size());
		TextComponent tps_a = new TextComponent("Current TPS: ");
		TextComponent tps_b = new TextComponent(new DecimalFormat("0.00").format(LagProcessor.getTPS()));
		TextComponent slimit_a = new TextComponent("Current Speed Limit: ");
		TextComponent slimit_b = new TextComponent(speed_limit + " bps");
		TextComponent skicks_a = new TextComponent("Speed Limit Kicks: ");
		TextComponent skicks_b = new TextComponent("" + SpeedLimiter.totalKicks);
		TextComponent acr_a = new TextComponent("Anti-Cheat Enabled: ");
		
		// PLAYERS
		TextComponent ujoins_a = new TextComponent("Unique Joins: ");
		TextComponent ujoins_b = new TextComponent("" + PlayerMeta.Playtimes.keySet().size());
		TextComponent donos_a = new TextComponent("Donators: ");
		TextComponent donos_b = new TextComponent("" + PlayerMeta._donatorList.size());
		TextComponent laggers_a = new TextComponent("Lag-Prisoners: ");
		TextComponent laggers_b = new TextComponent("" + PlayerMeta._prisonerList.size());
		TextComponent pmutes_a = new TextComponent("Permanent Mutes: ");
		TextComponent pmutes_b = new TextComponent("" + PlayerMeta._permanentMutes.size());
		TextComponent ops_a = new TextComponent("OP Accounts: ");
		TextComponent ops_b = new TextComponent("" + Bukkit.getOperators().size());
		
		// DEBUG
		TextComponent restart_a = new TextComponent("Server restarting: ");
		TextComponent restart_b = new TextComponent(Restart.restarting ? "True" : "False");
		TextComponent rtrig_a = new TextComponent("Time below threshold: ");
		TextComponent rtrig_b = new TextComponent("" + ProcessPlaytime.lowTpsCounter);
		TextComponent rtrig_c = new TextComponent("ms (600000ms required to restart)");
		TextComponent withers_a = new TextComponent("Loaded Withers: ");
		TextComponent withers_b = new TextComponent("" + witherCount);
		TextComponent slowMode_a = new TextComponent("Slow chat enabled: ");
		TextComponent slowMode_b = new TextComponent(ChatListener.slowChatEnabled ? "True" : "False");
		TextComponent uptime_a = new TextComponent("Server Uptime: ");
		TextComponent uptime_b = new TextComponent(Util.timeToString(ServerMeta.getUptime()));
		
		// style individual components //
		title_sep.setColor(ChatColor.GRAY);
		
		players_a.setColor(ChatColor.RED); players_a.setBold(true);
		
		tps_a.setColor(ChatColor.RED); tps_a.setBold(true);
		
		slimit_a.setColor(ChatColor.GRAY); slimit_a.setBold(true);
		
		skicks_a.setColor(ChatColor.RED); skicks_a.setBold(true);
		
		withers_a.setColor(ChatColor.GRAY); withers_a.setBold(true);
		acr_a.setColor(ChatColor.RED); acr_a.setBold(true);
		
		ujoins_a.setColor(ChatColor.RED); ujoins_a.setBold(true);
		
		donos_a.setColor(ChatColor.RED); donos_a.setBold(true);
		
		laggers_a.setColor(ChatColor.RED); laggers_a.setBold(true);
		
		pmutes_a.setColor(ChatColor.RED); pmutes_a.setBold(true);
		
		ops_a.setColor(ChatColor.RED); ops_a.setBold(true);
		
		restart_a.setColor(ChatColor.RED); restart_a.setBold(true);
		restart_b.setColor(ChatColor.GRAY);
		
		rtrig_a.setColor(ChatColor.RED); rtrig_a.setBold(true);
		rtrig_b.setColor(ChatColor.GRAY); rtrig_c.setColor(ChatColor.GRAY);
		
		if ((double)witherCount >= ((double)witherLimit * 0.8)) {			
			withers_b.setColor(ChatColor.RED);
			
		} else if ((double)witherCount >= ((double)witherLimit * 0.5)
				&& witherCount < ((double)witherLimit * 0.8)) {
			
			withers_b.setColor(ChatColor.GOLD);
			
		} else if (witherCount < ((double)witherLimit * 0.5)) {
			withers_b.setColor(ChatColor.GREEN);
		}
		
		slowMode_a.setColor(ChatColor.RED); slowMode_a.setBold(true);
		slowMode_b.setColor(ChatColor.GRAY);
		uptime_a.setColor(ChatColor.RED); uptime_a.setBold(true);
		
		// parse components into 1-line components
		TextComponent title = new TextComponent(title_sep, title_name, title_sep);
		TextComponent player_head = new TextComponent(title_sep, player_head_name, title_sep);
		TextComponent debug_head = new TextComponent(title_sep, debug_head_name, title_sep);
		TextComponent more_info_head = new TextComponent(title_sep, moreinfo_head_name, title_sep);
		
		TextComponent players = new TextComponent(players_a, players_b);
		TextComponent tpsText = new TextComponent(tps_a, tps_b);
		TextComponent slimit = new TextComponent(slimit_a, slimit_b);
		TextComponent skicks = new TextComponent(skicks_a, skicks_b);
		// players
		TextComponent ujoins = new TextComponent(ujoins_a, ujoins_b);
		TextComponent donos = new TextComponent(donos_a, donos_b);
		TextComponent laggers = new TextComponent(laggers_a, laggers_b);
		TextComponent pmutes = new TextComponent(pmutes_a, pmutes_b);
		TextComponent ops = new TextComponent(ops_a, ops_b);
		// debug
		TextComponent restart = new TextComponent(restart_a, restart_b);
		TextComponent rtrig = new TextComponent(rtrig_a, rtrig_b, rtrig_c);
		TextComponent withers = new TextComponent(withers_a, withers_b);
		TextComponent slowMode = new TextComponent(slowMode_a, slowMode_b);
		TextComponent uptime = new TextComponent(uptime_a, uptime_b);
		// more info
		TextComponent moreInfo = new TextComponent("Click here to see more info..");
		moreInfo.setColor(ChatColor.BLUE); moreInfo.setItalic(true);
		
		// add functionality to components
		laggers_a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Trying to lag the server will result in severe consequences.")));
		moreInfo.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server 2 "));
		
		// define second page
		if (args.length != 0) {
			switch (args[0]) {
				case "2":
			
					Arrays.asList(new TextComponent(""), more_info_head, uptime, ujoins, ops, debug_head, slowMode, restart, rtrig)
					.forEach(ln -> sender.sendMessage(ln.toLegacyText()));
			
					return true;
					
				default:
					return true;
			}
		}
		
		// create output structure and send to chat
		Arrays.asList(new TextComponent(""), title, tpsText, slimit, skicks, withers, player_head, players, donos, laggers, pmutes, moreInfo)
		.forEach(ln -> sender.sendMessage(ln));
		
		return true;
	}
}
