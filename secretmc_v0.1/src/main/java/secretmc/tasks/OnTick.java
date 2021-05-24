package secretmc.tasks;

import secretmc.backend.Scheduler;
import secretmc.commands.VoteMute;

import java.util.TimerTask;

// Tps processor
public class OnTick extends TimerTask {

	@Override
	public void run() {
		VoteMute.processVoteCooldowns();
		Scheduler.setLastTaskId("tickTasks");
	}
}
