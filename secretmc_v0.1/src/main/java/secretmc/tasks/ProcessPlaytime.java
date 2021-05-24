package secretmc.tasks;

import secretmc.data.PlayerMeta;
import secretmc.commands.VoteMute;
import secretmc.events.ChatListener;
import secretmc.events.ChunkManager;

import secretmc.backend.Config;
import secretmc.backend.Scheduler;
import secretmc.backend.ServerMeta;
import secretmc.backend.utils.Restart;
import secretmc.backend.LagProcessor;

import java.util.TimerTask;
import org.bukkit.Bukkit;

// Playtime processor (every 20 ticks)
public class ProcessPlaytime extends TimerTask {
	
	public static long lowTpsCounter = 0;
	
	private static long lastTime = 0;
	private static long lastHour = 0;
	private static long timeTillReset = 3600000;

	private static int lastNewChunks = 0;
	private static double lastTPS = 0.00;

	@Override
	public void run() {

		int currentNewChunks = ChunkManager.newCount;
		double onlinePlayers = Bukkit.getOnlinePlayers().size();
		
		if (onlinePlayers != 0 && (currentNewChunks - lastNewChunks) / onlinePlayers > 160.0) {
			System.out.println(
					"WARN more than 8 chunks per tick per player in last second");
			Analytics.capture();
		}
		
		lastNewChunks = currentNewChunks;
		double currentTPS = LagProcessor.getTPS();

		double difference;
		if (lastTPS == 0.00) {
			difference = 0.00;}
		else {
			difference = lastTPS - currentTPS;}
		
		if (difference > (lastTPS*0.5)) {
			System.out.println("WARN 50+% tps drop in 20t");
			Analytics.capture();
		}
		
		lastTPS = currentTPS;
		
		// get time since last tick in milliseconds
		if (lastTime == 0) {
			lastTime = System.currentTimeMillis();
			lastHour = System.currentTimeMillis();
			return;
		}
		
		long sinceLast = System.currentTimeMillis() - lastTime;		
		if (sinceLast > 3000) Analytics.capture();

		// Tick playtime and temporary mutes
		Bukkit.getOnlinePlayers().forEach(p -> PlayerMeta.tickPlaytime(p, sinceLast));
		PlayerMeta.tickTempMutes(sinceLast);

		// Tick server uptime and reconnect delays
		ServerMeta.tickUptime(sinceLast);
		ServerMeta.tickRcDelays(sinceLast);

		if (System.currentTimeMillis() - lastHour >= 3600000) {
			lastHour = System.currentTimeMillis();

			ChatListener.violationLevels.clear();
			VoteMute.clear();
		}

		// Check if we need a restart		
		double rThreshold = Double.parseDouble(Config.getValue("restart.threshold"));
		if (currentTPS < rThreshold) {
			lowTpsCounter += sinceLast;
			if (lowTpsCounter >= 300000) {
				Restart.restart(true);
			}
		}

		timeTillReset = timeTillReset - sinceLast;

		if (timeTillReset <= 0) {
			lowTpsCounter = 0;
			timeTillReset = 3600000;
		}

		lastTime = System.currentTimeMillis();

		// Log this
		Scheduler.setLastTaskId("oneSecondTasks");
	}
}
