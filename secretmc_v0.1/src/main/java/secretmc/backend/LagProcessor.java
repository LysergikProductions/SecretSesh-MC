package secretmc.backend;

/* Calculate the exponentially weighted moving average of TPS */
public class LagProcessor implements Runnable {

	public static long LAST_TICK_TS = -1;
	public static double AVERAGE_TICK = 50.0; // 20 TPS = 50 ms tick len
	public static double ALPHA = 0.01; // TODO make this a config option

	public static double getTPS() {
		return 1000.0 / AVERAGE_TICK; // one second / tick len == tps
	}

	@Override
	public void run() {
		long now = System.currentTimeMillis();

		if (LAST_TICK_TS < 0)
			LAST_TICK_TS = now - 50; // exactly one tick ago

		// at 20 TPS tick len can be anywhere from 48 - 52 ms
		long tick_len = now - LAST_TICK_TS;
		if (tick_len <= 52)
			tick_len = 50;

		AVERAGE_TICK = ALPHA * tick_len + (1 - ALPHA) * AVERAGE_TICK;
		LAST_TICK_TS = now;
	}
}
