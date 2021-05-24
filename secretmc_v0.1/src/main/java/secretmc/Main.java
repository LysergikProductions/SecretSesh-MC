package secretmc;

import secretmc.data.*;
import secretmc.events.*;
import secretmc.tasks.*;
import secretmc.backend.*;
import secretmc.commands.*;
import secretmc.commands.restricted.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.OfflinePlayer;
import org.bukkit.GameMode;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("SpellCheckingInspection")
public class Main extends JavaPlugin {
	public static Plugin instance;

	public static final String version = "0.3.0"; public static final int build = 264;
	public static long worldAge_atStart; public static boolean isNewWorld;

	public static OfflinePlayer Top = null;
	public DiscordBot DiscordHandler;

	@Override
	public void onEnable() {
		instance = this;

		System.out.println("[core.main] _____________________________");
		System.out.println("[core.main] --- Initializing SecretMC ---");
		System.out.println("[core.main] _____________________________");

		System.out.println("forcing default gamemode..");
		getServer().setDefaultGameMode(GameMode.SURVIVAL);

		System.out.println("[core.main] _____________");
		System.out.println("[core.main] Loading files");
		System.out.println("[core.main] _____________");

		try {
			FileManager.setup();
		} catch (IOException e) {
			System.out.println("[core.main] An error occured in FileManager.setup()");
		}

		System.out.println("[core.main] __________________");
		System.out.println("[core.main] Loading more files");
		System.out.println("[core.main] __________________");

		try {
			PlayerMeta.loadDonators();
			PlayerMeta.loadMuted();
			PlayerMeta.loadPrisoners();

		} catch (IOException e) {
			System.out.println("[core.main] An error occured loading files..");
			System.out.println("[core.main] " + e);
		}

		System.out.println("[core.main] _________________");
		System.out.println("[core.main] Enabling commands");
		System.out.println("[core.main] _________________");

		System.out.println("/mute");
		Objects.requireNonNull(this.getCommand("mute")).setExecutor(new Mute());

		System.out.println("/ninjatp");
		Objects.requireNonNull(this.getCommand("ninjatp")).setExecutor(new NinjaTP());

		System.out.println("/vm");
		Objects.requireNonNull(this.getCommand("vm")).setExecutor(new VoteMute());

		System.out.println("/msg");
		Objects.requireNonNull(this.getCommand("msg")).setExecutor(new Message());

		System.out.println("/w");
		Objects.requireNonNull(this.getCommand("w")).setExecutor(new Message());

		System.out.println("/r");
		Objects.requireNonNull(this.getCommand("r")).setExecutor(new Reply());

		System.out.println("/say");
		Objects.requireNonNull(this.getCommand("say")).setExecutor(new Say());

		System.out.println("/discord");
		Objects.requireNonNull(this.getCommand("discord")).setExecutor(new Discord());

		System.out.println("/tps");
		Objects.requireNonNull(this.getCommand("tps")).setExecutor(new Tps());

		System.out.println("/kill");
		Objects.requireNonNull(this.getCommand("kill")).setExecutor(new Kill());

		System.out.println("/about");
		Objects.requireNonNull(this.getCommand("about")).setExecutor(new About());

		System.out.println("/restart");
		Objects.requireNonNull(this.getCommand("restart")).setExecutor(new RestartCmd());

		System.out.println("/sign");
		Objects.requireNonNull(this.getCommand("sign")).setExecutor(new Sign());

		System.out.println("/admin");
		Objects.requireNonNull(this.getCommand("admin")).setExecutor(new Admin());

		System.out.println("/stats");
		Objects.requireNonNull(this.getCommand("stats")).setExecutor(new Stats());

		System.out.println("/redeem");
		Objects.requireNonNull(this.getCommand("redeem")).setExecutor(new Redeem());

		System.out.println("/tjm");
		Objects.requireNonNull(this.getCommand("tjm")).setExecutor(new ToggleJoinMessages());

		System.out.println("/server");
		Objects.requireNonNull(this.getCommand("server")).setExecutor(new Server());

		System.out.println("/help");
		Objects.requireNonNull(this.getCommand("help")).setExecutor(new Help());

		System.out.println("/repair");
		Objects.requireNonNull(this.getCommand("repair")).setExecutor(new Repair());

		System.out.println("/slowchat");
		Objects.requireNonNull(this.getCommand("slowchat")).setExecutor(new SlowChat());

		System.out.println("/backup");
		Objects.requireNonNull(this.getCommand("backup")).setExecutor(new Backup());

		System.out.println("/info");
		Objects.requireNonNull(this.getCommand("info")).setExecutor(new Info());

		System.out.println("/global");
		Objects.requireNonNull(this.getCommand("global")).setExecutor(new Global());

		System.out.println("/ignore");
		Objects.requireNonNull(this.getCommand("ignore")).setExecutor(new Ignore());

		System.out.println("/afk");
		Objects.requireNonNull(this.getCommand("afk")).setExecutor(new AFK());

		System.out.println("/last");
		Objects.requireNonNull(this.getCommand("last")).setExecutor(new Last());

		System.out.println("/fig");
		Objects.requireNonNull(this.getCommand("fig")).setExecutor(new ConfigCmd());

		System.out.println("/check");
		Objects.requireNonNull(this.getCommand("check")).setExecutor(new Check());

		System.out.println("[core.main] _______________________");
		System.out.println("[core.main] Scheduling synced tasks");
		System.out.println("[core.main] _______________________");

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagProcessor(), 1L, 1L);
		} catch (Exception e) { e.printStackTrace(); }

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new OnTick(), 1L, 1L);
		} catch (Exception e) { e.printStackTrace(); }

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new ProcessPlaytime(), 20L, 20L);
		} catch (Exception e) { e.printStackTrace(); }

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagManager(), 1200L, 1200L);
		} catch (Exception e) { e.printStackTrace(); }

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new Analytics(), 6000L, 6000L);
		} catch (Exception e) { e.printStackTrace(); }

		try { getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoAnnouncer(), 15000L, 15000L);
		} catch (Exception e) { e.printStackTrace(); }

		System.out.println("[core.main] _______________________");
		System.out.println("[core.main] Loading event listeners");
		System.out.println("[core.main] _______________________");

		PluginManager core_pm = getServer().getPluginManager();

		try { core_pm.registerEvents(new ChatListener(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new ConnectionManager(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new PVP(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new MoveListener(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new SpawnController(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new LagManager(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new SpeedLimiter(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new ItemCheckTriggers(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new BlockListener(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new ChunkManager(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try { core_pm.registerEvents(new OpListener(), this);
		} catch (Exception e) { e.printStackTrace(); }

		try {
			PacketListener.C2S_AnimationPackets();

			PacketListener.S2C_MapChunkPackets();
			PacketListener.S2C_WitherSpawnSound();

		} catch (Exception e) { e.printStackTrace(); }
		
		System.out.println("[core.main] ..finishing up..");
		
		// Define banned & special blocks
		ItemCheck.Banned.addAll(Arrays.asList(Material.BARRIER, Material.COMMAND_BLOCK,
				Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
				Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK));
		
		// Items that need to be specially rebuilt.
		ItemCheck.Special.addAll(Arrays.asList(Material.ENCHANTED_BOOK, Material.POTION, Material.LINGERING_POTION,
				Material.TIPPED_ARROW, Material.SPLASH_POTION, Material.WRITTEN_BOOK, Material.FILLED_MAP,
				Material.PLAYER_WALL_HEAD, Material.PLAYER_HEAD, Material.WRITABLE_BOOK, Material.BEEHIVE,
				Material.BEE_NEST, Material.RESPAWN_ANCHOR, Material.FIREWORK_ROCKET, Material.FIREWORK_STAR,
				Material.SHIELD));
		
		ItemCheck.LegalHeads.addAll(Arrays.asList(Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL,
				Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD));

		// Enable speed limit
		SpeedLimiter.scheduleSlTask();
		
		// Enable discord notifications for this instance
		DiscordHandler = new DiscordBot();
		getServer().getPluginManager().registerEvents(DiscordHandler, this);

		// Load chunk at 0,0 to test for world age
		for (World thisWorld: getServer().getWorlds()) {
			System.out.println("[core.main] Checking world age..");
			
			if (thisWorld.getEnvironment().equals(Environment.NORMAL)) {
				
				thisWorld.getChunkAt(0, 0).load(true);
				
				worldAge_atStart = thisWorld.getChunkAt(0, 0)
						.getChunkSnapshot().getCaptureFullTime();

				if (worldAge_atStart < 710) {
					
					System.out.println("[core.main] This world is NEW! World Ticks: " + worldAge_atStart);
					isNewWorld = true;

				} else {
					
					System.out.println("[core.main] This world is not new! World Ticks: " + worldAge_atStart);
					isNewWorld = false;
				}
				break; // <- only check first normal dimension found
			}
		}
		
		System.out.println("[core.main] ________________________________");
		System.out.println("[core.main] -- Finished loading RVAS-Core --");
		System.out.println("[core.main] ________________________________");
	}

	@Override
	public void onDisable() {
		System.out.println("[core.main] _____________________________");
		System.out.println("[core.main] --- RVAS-Core : Disabling ---");
		System.out.println("[core.main] _____________________________");
		
		// final analytics capture for this session
		Analytics.capture();
		
		System.out.println("[core.main] ________________");
		System.out.println("[core.main] Creating backups");
		System.out.println("[core.main] ________________");
		
		try {
			FileManager.backupData(FileManager.pvpstats_user_database, "pvpstats-backup-", ".txt");
			FileManager.backupData(FileManager.playtime_user_database, "playtimes-backup-", ".db");
			FileManager.backupData(FileManager.settings_user_database, "player_settings-backup-", ".txt");
			FileManager.backupData(FileManager.muted_user_database, "muted-backup-", ".db");
			
		} catch (IOException ex) {
			System.out.println("[core.main] WARNING - Failed to save one or more backup files.");
			System.out.println("[core.main] " + ex);
		}
		
		System.out.println("[core.main] ______________________");
		System.out.println("[core.main] Overwriting save files");
		System.out.println("[core.main] ______________________");
		
		try {
			PlayerMeta.saveDonators();
			PlayerMeta.saveMuted();
			PlayerMeta.savePrisoners();
			
			PlayerMeta.writePlaytime();
			SettingsManager.writePlayerSettings();
			StatsManager.writePVPStats();
			
		} catch (IOException ex) {
			System.out.println("[core.main] WARNING - Failed to write one or more files.");
			System.out.println("[core.main] " + ex);
		}
		
		System.out.println("[core.main] __________________");
		System.out.println("[core.main] Collecting garbage");
		System.out.println("[core.main] __________________");

		int max_age = Integer.parseInt(Config.getValue("wither.skull.max_ticks"));
		int removed_skulls = LagManager.removeSkulls(max_age);

		System.out.println("Found " + removed_skulls + " remaining skull/s to trash..");
		
		System.out.println("[core.main] ______________________");
		System.out.println("[core.main] Printing session stats");
		System.out.println("[core.main] ______________________");
		
		System.out.println("New Chunks Generated: " + ChunkManager.newCount);
		System.out.println("New Unique Players: " + SpawnController.sessionNewPlayers);
		System.out.println("Total Respawns: " + SpawnController.sessionTotalRespawns);
		System.out.println("Bedrock Placed: " + BlockListener.placedBedrockCounter);
		System.out.println("Bedrock Broken: " + BlockListener.brokenBedrockCounter);
		
		System.out.println("[core.main] ____________________________");
		System.out.println("[core.main] --- RVAS-Core : Disabled ---");
		System.out.println("[core.main] ____________________________");
	}
}
