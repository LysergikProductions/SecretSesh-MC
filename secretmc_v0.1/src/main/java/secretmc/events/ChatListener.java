package secretmc.events;

import secretmc.backend.Config;
import secretmc.commands.AFK;
import secretmc.commands.restricted.Admin;
import secretmc.data.PlayerMeta;
import secretmc.data.PlayerMeta.MuteType;

import java.util.*;
import java.util.logging.Level;

import secretmc.commands.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

@SuppressWarnings("SpellCheckingInspection")
public class ChatListener implements Listener {

	private static final Set<String> allUserCommands = new HashSet<>(Arrays.asList(
		"about", "admin", "discord", "dupehand", "help", "kill", "kit", "kys", "msg", "w", "r",
		"redeem", "stats", "tdm", "tjm", "tps", "vm", "vote", "ignore", "server", "sign", "afk", "last"
	));
	
	private HashMap<UUID, Long> lastChatTimes = new HashMap<>();
	private HashMap<UUID, String> lastChatMessages = new HashMap<>();
	
	public static HashMap<UUID, Integer> violationLevels = new HashMap<>();
	public static boolean slowChatEnabled = false;

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		
		// Cancel this event so we can override vanilla chat
		e.setCancelled(true);
		
		Player player = e.getPlayer();

		// Don't execute if the player is muted
		if (PlayerMeta.isMuted(player) || (PlayerMeta.MuteAll && !player.isOp()))
			return;

		// remove AFK statuses
		UUID playerid = player.getUniqueId();
		if (AFK._AFKs.contains(playerid)) {

			Message.AFK_warned.remove(playerid);
			AFK._AFKs.remove(playerid);

			player.sendMessage(new TextComponent(ChatColor.GREEN +
					"You are no longer AFK!").toLegacyText());
		}

		// -- CREATE PROPERTIES -- \\
		
		boolean doSend = true;
		String finalMessage = e.getMessage();
		String color;
		String usernameColor;

		// -- SET CHAT COLORS -- //

		switch (e.getMessage().charAt(0)) {
			case '>':
				color = "\u00A7a"; // Greentext
				break;
			case '$':
				if (PlayerMeta.isDonator(player)) {
					color = "\u00A76"; // Donator text
					break;
				}
			default:
				color = "\u00A7f"; // Normal text
				break;
		}

		if (PlayerMeta.isDonator(player) && !Admin.UseRedName.contains(player.getUniqueId())) {
			usernameColor = "\u00A76";
		} else if (Admin.UseRedName.contains(player.getUniqueId())) {
			usernameColor = "\u00A7c";
		} else {
			usernameColor = "\u00A7f";
		}

		// -- STRING MODIFICATION -- //

		// Remove section symbols
		finalMessage = finalMessage.replace('\u00A7', ' ');

		// -- CHECKS -- //

		if (isBlank(finalMessage)) doSend = false;
		else if (PlayerMeta.isPrisoner(player)) finalMessage = ":'(";
		
		// Don't send any message if slow-chat configs dictate it
		if (slowChatEnabled && !player.isOp()) {
			
			try {
				TextComponent msg = new TextComponent(
						"Slow chat is currently enabled. You can chat once every " +
						Integer.parseInt(Config.getValue("chat.slow.time")) / 1000 + " seconds");
				
				msg.setColor(ChatColor.RED);
				
				if(lastChatTimes.get(player.getUniqueId()) + Integer.parseInt(Config.getValue("chat.slow.time")) > System.currentTimeMillis()) {
					
					doSend = false;
					player.sendMessage(msg.toLegacyText());
					
				} else doSend = true;
				
			} catch (Exception ex) {
				ex.printStackTrace();
				lastChatTimes.put(player.getUniqueId(), System.currentTimeMillis());
			}
		}
		
		// -- SEND FINAL MESSAGE -- //

		if (doSend) {
			String username = e.getPlayer().getName();

			TextComponent finalCom = new TextComponent("\u00A7f<" + usernameColor + username +
					"\u00A7f> " + color + finalMessage);
			
			if(Config.getValue("spam.enable").equals("true")) {	
				boolean censored = false;
				
				if(lastChatTimes.containsKey(e.getPlayer().getUniqueId())) {
					
					if(lastChatTimes.get(e.getPlayer().getUniqueId()) +
							Integer.parseInt(Config.getValue("spam.wait_time")) > System.currentTimeMillis()) {
						
						censored = true;
						
						if(violationLevels.containsKey(e.getPlayer().getUniqueId())) {
							violationLevels.put(e.getPlayer().getUniqueId(),
									violationLevels.get(e.getPlayer().getUniqueId()) + 1);
						}
						else {
							violationLevels.put(e.getPlayer().getUniqueId(), 1);
						}
					}
				}
				
				if(lastChatMessages.containsKey(e.getPlayer().getUniqueId())) {
					
					// slow chat is off, but how similar are the messages?
					if(similarity(lastChatMessages.get(e.getPlayer().getUniqueId()), finalMessage)
							* 100 > Integer.parseInt(Config.getValue("spam.min_similarity"))) {
						
						censored = true;
						
						if(violationLevels.containsKey(e.getPlayer().getUniqueId())) {
							violationLevels.put(e.getPlayer().getUniqueId(), violationLevels.get(e.getPlayer().getUniqueId()) + 1);
						}
						else {
							violationLevels.put(e.getPlayer().getUniqueId(), 1);
						}
					}
				}
				
				lastChatTimes.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
				lastChatMessages.put(e.getPlayer().getUniqueId(), finalMessage);
				
				if(violationLevels.containsKey(e.getPlayer().getUniqueId())) {
					if(violationLevels.get(e.getPlayer().getUniqueId()) == Integer.parseInt(Config.getValue("spam.minimum_vl"))) {
						PlayerMeta.setMuteType(e.getPlayer(), MuteType.TEMPORARY);
						return;
					}
				}
				
				// op bypass
				if(e.getPlayer().isOp() && Config.getValue("spam.ops").equals("true")) {					
					if(censored) {
						
						e.getPlayer().sendMessage(
								"\u00A7cYour message was flagged as spam, but since you are an OP, it was not filtered.");
						censored = false;
					}
					violationLevels.remove(e.getPlayer().getUniqueId());
				}
			
				if(censored) {
					Bukkit.getLogger().log(Level.INFO, "\u00A74<" + username + "> " + finalMessage + " [deleted, vl="
							+ violationLevels.get(e.getPlayer().getUniqueId())+"]");
					return;
				}
			}
			
			Bukkit.getLogger().log(Level.INFO, "\u00A7f<" + usernameColor + username + "\u00A7f> " + color + finalMessage);
			Bukkit.getServer().spigot().broadcast(finalCom);
		}
	}
	
	public double similarity(String s1, String s2) {
		
		    String longer = s1, shorter = s2;
		    if (s1.length() < s2.length()) { // longer should always have greater length
		      longer = s2; shorter = s1;
		    }
		    
		    int longerLength = longer.length();
		    if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
		    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
		    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
		    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
		    return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
	}
	
	public int editDistance(String s1, String s2) {
		
	    s1 = s1.toLowerCase();
	    s2 = s2.toLowerCase();

	    int[] costs = new int[s2.length() + 1];
	    for (int i = 0; i <= s1.length(); i++) {
	      int lastValue = i;
	      for (int j = 0; j <= s2.length(); j++) {
	        if (i == 0)
	          costs[j] = j;
	        else {
	          if (j > 0) {
	            int newValue = costs[j - 1];
	            if (s1.charAt(i - 1) != s2.charAt(j - 1))
	              newValue = Math.min(Math.min(newValue, lastValue),
	                  costs[j]) + 1;
	            costs[j - 1] = lastValue;
	            lastValue = newValue;
	          }
	        }
	      }
	      if (i > 0)
	        costs[s2.length()] = lastValue;
	    }
	    return costs[s2.length()];
	  }

	@EventHandler
	public boolean onCommand(PlayerCommandPreprocessEvent e) {
		
		if (e.getMessage().split(" ")[0].contains(":") && !e.getPlayer().isOp()) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("\u00A7cUnknown command.");
			
		} else if (e.getMessage().split("")[1].contains(Config.getValue("admin")) && !e.getPlayer().isOp()) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("\u00A7cCannot target admin account.");
		}
		return true;
	}

	@EventHandler
	public void onPlayerTab(PlayerCommandSendEvent e) {
		
		if (!e.getPlayer().isOp()) {
			e.getCommands().clear();
			e.getCommands().addAll(allUserCommands);
		}
	}

	private boolean isBlank(String check) {
		return check == null || check.isEmpty() || check.trim().isEmpty();
	}
}
