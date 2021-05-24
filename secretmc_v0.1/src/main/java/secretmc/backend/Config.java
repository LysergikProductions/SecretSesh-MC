package secretmc.backend;

import secretmc.events.*;
import secretmc.tasks.Analytics;
import secretmc.tasks.AutoAnnouncer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Config {
	public static int version = 22;

	private static HashMap<String, String> _values = new HashMap<>();
	public static String getValue(String key)
	{
		return _values.getOrDefault(key, "false");
	}

	public static boolean debug = Boolean.parseBoolean(getValue("debug"));
	public static boolean verbose = Boolean.parseBoolean(getValue("verbose"));

	public static void load() throws IOException {
		Files.readAllLines(Paths.get("plugins/secretmc/configs/config.txt")).stream()
				.filter(cases -> !cases.startsWith("//"))
				.filter(cases -> !(cases.length() == 0)).forEach( val -> {

			try {
				_values.put(val.split("=")[0].trim(), val.split("=")[1].trim());
			} catch (Exception e) {
				System.out.println("Failed to store value for " + val.split("=")[0].trim());
				System.out.println(e.getMessage());
			}
		});

		Files.readAllLines(Paths.get("plugins/secretmc/configs/restrictions.txt")).stream()
				.filter(cases -> !cases.startsWith("//"))
				.filter(cases -> !(cases.length() == 0)).forEach( val -> {

			try {
				_values.put(val.split("=")[0].trim(), val.split("=")[1].trim());
			} catch (Exception e) {
				System.out.println("Failed to store value for " + val.split("=")[0].trim());
				System.out.println(e.getMessage());
			}
		});

		Files.readAllLines(Paths.get("plugins/secretmc/configs/spawn_controller.txt")).stream()
				.filter(cases -> !cases.startsWith("//"))
				.filter(cases -> !(cases.length() == 0)).forEach( val -> {

			try {
				_values.put(val.split("=")[0].trim(), val.split("=")[1].trim());
			} catch (Exception e) {
				System.out.println("Failed to store value for " + val.split("=")[0].trim());
				System.out.println(e.getMessage());
			}
		});

		debug = Boolean.parseBoolean(getValue("debug"));
		verbose = Boolean.parseBoolean(getValue("verbose"));

		OpListener.isSauceInitialized = false;

		if (BlockListener.updateConfigs() && verbose) System.out.println("BlockListener sConfigs Updated!");
		if (Analytics.updateConfigs() && verbose) System.out.println("Analytics sConfigs Updated!");
		if (SpawnController.updateConfigs() && verbose) System.out.println("SpawnController sConfigs Updated!");
		if (ItemCheck.updateConfigs() && verbose) System.out.println("Banned Block sConfigs Updated!");
		if (ConnectionManager.updateConfigs() && verbose) System.out.println("MOTDs Updated!");
		if (AutoAnnouncer.updateConfigs() && verbose) System.out.println("Announcements Updated!");

		System.out.println("Configs updated!");
	}

	public static void modifyConfig(String thisConfig, String thisValue) {
		_values.put(thisConfig, thisValue);
	}

	public static boolean isRealConfig(String thisConfig) {
		return _values.containsKey(thisConfig);
	}
}
