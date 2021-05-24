package secretmc.events;

import secretmc.Main;
import secretmc.backend.*;
import secretmc.data.PlayerMeta;
import secretmc.tasks.Analytics;
import secretmc.commands.restricted.Admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@SuppressWarnings("SpellCheckingInspection")
public class SpeedLimiter implements Listener {
	private static final int GRACE_PERIOD = 5;

	private static HashMap<UUID, Location> locs = new HashMap<>();
	private static HashMap<String, Double> speeds = new HashMap<>();
	private static HashMap<UUID, Integer> gracePeriod = new HashMap<>();
	private static List<UUID> tped = new ArrayList<>();

	private static long lastCheck = -1;
	public static int totalKicks = 0;

	// Speed Monitor
	public static void scheduleSlTask() {
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, () -> {

			if (lastCheck < 0) {
				lastCheck = System.currentTimeMillis();
				return;
			}

			double tier1 = Double.parseDouble(Config.getValue("speedlimit.tier_one"));
			double tier2 = Double.parseDouble(Config.getValue("speedlimit.tier_two"));
			double tier3 = Double.parseDouble(Config.getValue("speedlimit.tier_three"));
			double tier4 = Double.parseDouble(Config.getValue("speedlimit.tier_four"));
			double tier5 = Double.parseDouble(Config.getValue("speedlimit.tier_five"));
			double thatNetherLimit = Double.parseDouble(Config.getValue("speedlimit.nether_roof"));
			/*
				default tier1 = 76.0
				default tier2 = 48.0
				default tier3 = 32.0
				default tier4 = 26.0
				default tier5 = 20.0
				default thatNetherLimit = 25.0
			*/
			double medium_kick = Integer.parseInt(Config.getValue("speedlimit.medium_kick"));
			double hard_kick = Integer.parseInt(Config.getValue("speedlimit.hard_kick"));
			final double speed_limit; final double nether_limit;

			long now = System.currentTimeMillis();
			double duration = (now - lastCheck) / 1000.0;
			lastCheck = now;

			double tps = LagProcessor.getTPS();
			
			if (tps >= 17.0) {
				speed_limit = tier1;
				
			} else if (tps < 17.0 && tps >= 14.0) {
				speed_limit = tier2;
				
			} else if (tps < 14.0 && tps >= 10.0) {
				speed_limit = tier3;
				
			} else if (tps < 10.0 && tps >= 7.0) {
				speed_limit = tier4;
				
			} else if (tps < 7.0) {
				speed_limit = tier5;

			} else {
				speed_limit = tier1;
			}

			if (thatNetherLimit == -1) {
				nether_limit = 8192.0;

			} else if (thatNetherLimit > 8192) {
				nether_limit = 8192.0;

			} else if (thatNetherLimit < 5) {
				nether_limit = 5.0;

			} else {
				nether_limit = thatNetherLimit;
			}

			speeds.clear();
			Bukkit.getOnlinePlayers().stream().filter(player ->
					!PlayerMeta.isAdmin(player)).forEach(player -> {
				
				double final_limit = speed_limit;
				
				// updated teleported player position
				if (tped.contains(player.getUniqueId())) {
					tped.remove(player.getUniqueId());
					locs.put(player.getUniqueId(), player.getLocation().clone());
					return;
				}

				// set previous location if it doesn't exist and bail
				Location previous_location = locs.get(player.getUniqueId());
				if (previous_location == null) {
					locs.put(player.getUniqueId(), player.getLocation().clone());
					return;
				}
				
				Location new_location = player.getLocation().clone();
				if (new_location.equals(previous_location)) {
					return;
				}
				
				new_location.setY(previous_location.getY()); // only consider movement in X/Z

				if (previous_location.getWorld() != new_location.getWorld()) {
					
					locs.remove(player.getUniqueId());
					return;
				}

				Integer grace = gracePeriod.get(player.getUniqueId());
				if (grace == null) {
					grace = GRACE_PERIOD;
				}
				
				// allow ops to bypass higher tier, but not the base, speed limiters
				if (player.isOp()) final_limit = 76.00;

				boolean toNetherGrace = false;

				// adjust speed limit for nether roof
				if (nether_limit < speed_limit &&
						new_location.getWorld().getEnvironment().equals(World.Environment.NETHER) &&
						new_location.getY() > 127) {

					final_limit = nether_limit;
					toNetherGrace = true;
				}
				
				Vector v = new_location.subtract(previous_location).toVector();
				double speed = Math.round(v.length() / duration * 10.0) / 10.0;
				
				if (speed > final_limit+ 1 && (Config.getValue("speedlimit.agro").equals("true") || Admin.disableWarnings)) {
					
					ServerMeta.kickWithDelay(player,
							Double.parseDouble(Config.getValue("speedlimit.rc_delay")));
					totalKicks++; Analytics.speed_kicks++;
					return;
				}

				// insta-kick above hard kick speed
				if (speed > hard_kick) {
					
					gracePeriod.put(player.getUniqueId(), GRACE_PERIOD);
					ServerMeta.kickWithDelay(player,
							Double.parseDouble(Config.getValue("speedlimit.rc_delay")));
					totalKicks++; Analytics.speed_kicks++;
					return;
				}

				// medium-kick: set grace period to 2 sec
				if (speed > medium_kick || toNetherGrace) {
					
					if (grace > 2)
						grace = 2;
				}

				// player is going too fast, warn or kick
				// +1 for leniency
				if (speed > final_limit+1) {
					if (grace == 0) {
						
						gracePeriod.put(player.getUniqueId(), GRACE_PERIOD);
						ServerMeta.kickWithDelay(player,
								Double.parseDouble(Config.getValue("speedlimit.rc_delay")));
						totalKicks++; Analytics.speed_kicks++;
						return;
						
					} else {
						Analytics.speed_warns++;
						
						// display speed with one decimal
						player.sendMessage("\u00A74Your speed is " + speed + ", speed limit is " + final_limit +
								". Slow down or be kicked in " + grace + " second" + (grace == 1 ? "" : "s"));
					}
					--grace;
					gracePeriod.put(player.getUniqueId(), grace);
					
				} else {// player isn't going too fast, reset grace period
					if (grace < GRACE_PERIOD)
						++grace;
				}

				gracePeriod.put(player.getUniqueId(), grace);
				locs.put(player.getUniqueId(), player.getLocation().clone());
				speeds.put(player.getName(), speed);
			});
		}, 20L, 20L);
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e)
	{
		tped.add(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onDeath(PlayerRespawnEvent e)
	{
		tped.add(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{
		tped.remove(e.getPlayer().getUniqueId());
		locs.remove(e.getPlayer().getUniqueId());
	}

	/* get speeds sorted from fastest to lowest */
	public static List< Pair<Double,String> > getSpeeds() {
		
		// create a list from the speeds map
		List<Map.Entry<String, Double> > list =
				new ArrayList<>(speeds.entrySet());

		list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

		// format them into speed strings
		List< Pair<Double, String> > ret = new ArrayList<>();
		for (Map.Entry<String, Double> aa : list) {
			ret.add(new Pair<>(aa.getValue(), aa.getKey()));
		}
		return ret;
	}
}
