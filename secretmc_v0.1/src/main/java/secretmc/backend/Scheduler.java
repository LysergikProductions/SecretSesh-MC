package secretmc.backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Scheduler {
	private static String lastTaskId = "";
	private static LocalDateTime lastTaskTime = null;

	public static void setLastTaskId(String task) {
		lastTaskId = task;
		lastTaskTime = LocalDateTime.now();
	}

	@Deprecated
	public static String getLastTaskId() {
		return lastTaskId;
	}

	@Deprecated
	public static String getLastTaskTime() {
		// 2011-12-03 @ 10:15:30
		return lastTaskTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " @ ");
	}
}
