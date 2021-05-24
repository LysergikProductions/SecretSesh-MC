package secretmc.backend;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class ServerMeta {
	// -- SERVER STATISTICS -- //

	private static double _uptimeInSeconds = 0;

	public static void tickUptime(double msToAdd)
	{
		_uptimeInSeconds += msToAdd / 1000;
	}
	public static double getUptime()
	{
		return _uptimeInSeconds;
	}

	// -- RC BLOCKING -- //

	private static HashMap<UUID, Double> Delays = new HashMap<>();
	private static HashMap<UUID, Double> InitialDelays = new HashMap<>();

	@SuppressWarnings("deprecation")
	public static void kickWithDelay(Player p, double delay) {
		Delays.put(p.getUniqueId(), 0D);
		InitialDelays.put(p.getUniqueId(), delay);
		p.kickPlayer("\u00A76You have lost connection to the server.");
	}
	
	public static void preventReconnect(Player p, double delay) {
		Delays.put(p.getUniqueId(), 0D);
		InitialDelays.put(p.getUniqueId(), delay);
	}

	public static boolean canReconnect(Player p) {
		if (Delays.containsKey(p.getUniqueId())) {
			Delays.put(p.getUniqueId(), 0D);
			return false;
		}
		return true;
	}

	public static void tickRcDelays(double elapsed) {
		HashMap<UUID, Double> temp = Delays;
		for (UUID u : temp.keySet()) {
			double oldValue = Delays.get(u);
			Delays.put(u, oldValue + elapsed);
			if (oldValue + elapsed >= InitialDelays.get(u)) {
				Delays.remove(u);
				InitialDelays.remove(u);
			}
		}
	}
}
