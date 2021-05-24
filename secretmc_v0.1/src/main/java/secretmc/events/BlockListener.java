package secretmc.events;

/* *
 * 
 *  About: Use block-related events to enforce
 *  	configured restrictions in RVAS-core
 * 
 *  LICENSE: AGPLv3 (https://www.gnu.org/licenses/agpl-3.0.en.html)
 *  Copyright (C) 2021  Lysergik Productions (https://github.com/LysergikProductions)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * */

import secretmc.backend.Config;
import secretmc.backend.ItemCheck;
import secretmc.backend.utils.*;
import secretmc.data.PlayerMeta;
import secretmc.commands.restricted.Repair;

import java.util.*;
import java.text.DecimalFormat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

@SuppressWarnings({"SpellCheckingInspection", "deprecation"})
public class BlockListener implements Listener {

	static boolean roofProt = Boolean.parseBoolean(Config.getValue("protect.bedrock.roof"));
	static boolean floorProt = Boolean.parseBoolean(Config.getValue("protect.bedrock.floor"));
	static boolean modeOnPlace = Boolean.parseBoolean(Config.getValue("protect.gamemode.onplace"));
	static boolean modeOnBreak = Boolean.parseBoolean(Config.getValue("protect.gamemode.onbreak"));
	static boolean consumeCreativeBlocks = Boolean.parseBoolean(Config.getValue("consume.creative.blocks"));
	
	public static int brokenBedrockCounter = 0;
	public static int placedBedrockCounter = 0;
	
	public static ArrayList<Material> BreakBanned = new ArrayList<>(); static {
		BreakBanned.addAll(Arrays.asList(Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK,
				Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
				Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID));
	}
	
	public static ArrayList<Material> PlacementBanned = new ArrayList<>(); static {
		PlacementBanned.addAll(Arrays.asList(Material.BARRIER, Material.COMMAND_BLOCK,
				Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
				Material.WATER, Material.LAVA, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID));
	}
	
	public static ArrayList<Material> LagMats = new ArrayList<>(); static {
		LagMats.addAll(Arrays.asList(Material.REDSTONE, Material.REDSTONE_WIRE, Material.REDSTONE_BLOCK,
				Material.REDSTONE_TORCH, Material.REDSTONE_WALL_TORCH, Material.ACTIVATOR_RAIL, Material.POWERED_RAIL,
				Material.LEVER, Material.PISTON, Material.STICKY_PISTON, Material.REDSTONE_LAMP, Material.GLOWSTONE,
				Material.OBSERVER, Material.HOPPER, Material.DROPPER, Material.REPEATER, Material.COMPARATOR,
				Material.DISPENSER, Material.GRAVEL, Material.DRAGON_EGG, Material.TRIPWIRE_HOOK, Material.TRIPWIRE));
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		
		//long startTime = System.nanoTime();		
		Player breaker = event.getPlayer();
		
		// prevent creative players from breaking certain blocks but completely ignore admin account
		if (!PlayerMeta.isAdmin(breaker)) {
			
			Block block = event.getBlock();
			Location block_loc = block.getLocation();
			
			int x = (int)block_loc.getX();
			int y = (int)block_loc.getY();
			int z = (int)block_loc.getZ();

			String breaker_name = breaker.getName();
			Material blockType = block.getType();
			Environment dimension = block.getWorld().getEnvironment();
			
			TextComponent cancelPos = new TextComponent(
					breaker_name + "'s BlockBreakEvent was cancelled: " + blockType);
			
			if (!breaker.isOp() && modeOnBreak) breaker.setGameMode(GameMode.SURVIVAL);
			
			if (BreakBanned.contains(blockType)) {

				System.out.println("WARN " + breaker_name + " tried to break a protected admin block!");
				if (Config.debug && Config.verbose) Bukkit.spigot().broadcast(cancelPos);

				event.setCancelled(true);
				
			// do things if block == bedrock
			} else if (blockType.equals(Material.BEDROCK)) {
				
				// protect bedrock floor
				if (y < 1 && floorProt) {

					System.out.println("WARN " + breaker_name + " tried to break a protected floor block!");
					if (Config.debug && Config.verbose) Bukkit.spigot().broadcast(cancelPos);

					event.setCancelled(true);
					return;
					
				// protect nether roof	
				} else if (y == 127 && roofProt &&
						dimension.equals(Environment.NETHER)) {

					System.out.println("WARN " + breaker_name + " tried to break a protected roof block!");
					if (Config.debug && Config.verbose) Bukkit.spigot().broadcast(cancelPos);

					event.setCancelled(true);
					return;
				
				// protect exit portal in the end
				} else if (dimension.equals(Environment.THE_END) &&
						y == Repair.y_low || y == Repair.y_low+1) {
					
					if (x < 4 && x > -4) {
						if (z < 4 && z > -4) {
							
							System.out.println("WARN " + breaker_name + " tried to break a protected exit portal block!");
							if (Config.debug && Config.verbose) Bukkit.spigot().broadcast(cancelPos);

							event.setCancelled(true);
							return;
						}
					}
				}
				
				if (Config.debug && !Config.verbose) Bukkit.spigot().broadcast(
						new TextComponent(breaker_name + " just broke BEDROCK!"));
				brokenBedrockCounter++;
				
			// protect natural The_End entry and exit portals
			} else if (blockType.equals(Material.END_PORTAL)) {
				
				if (dimension.equals(Environment.THE_END) &&
						y == Repair.y_low || y == Repair.y_low+1) {
					
					if (x < 4 && x > -4) {
						if (z < 4 && z > -4) {
							
							event.setCancelled(true);
							if (Config.debug && !Config.verbose) Bukkit.spigot().broadcast(cancelPos);
						}
					}
				}
			}
			
			//long endTime = System.nanoTime();
			//long duration = (endTime - startTime);
			//System.out.println("BreakTime: " + new DecimalFormat("#.###").format((double)duration/1000000.0) + " ms");
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlace(BlockPlaceEvent event) {
		
		long startTime = System.nanoTime();		
		if (Config.debug) System.out.println("onPlace triggered.." + startTime);
		
		Player placer = event.getPlayer();
		if (PlayerMeta.isAdmin(placer)) return;
		String placer_name = placer.getName();
		
		Block block = event.getBlockPlaced();
		Location block_loc = block.getLocation();
		String env = Util.getDimensionName(block_loc);

		Material blockType = block.getType();
		String mat = blockType.toString();

		// prevent lag-prisoners from placing things that can cause lag
		if (PlayerMeta.isPrisoner(placer)) {

			if (LagMats.contains(blockType)) {event.setCancelled(true);
				return;
			} else if (
					mat.endsWith("SAND") ||
					mat.contains("POWDER") ||
					mat.contains("BUTTON") ||
					mat.contains("PRESSURE_PLATE") ||
					mat.contains("MINECART") ||
					mat.contains("DOOR")
					) {
				
				event.setCancelled(true);
				return;
			}
		}

		// Watchdog for potential lag machines
		if (LagMats.contains(blockType) || mat.contains("DOOR")) {
			int counter = 0;

			for (Material thisMat: LagMats) {
				if (thisMat != Material.GRAVEL) {
					counter += Chunks.blockCounter(block.getChunk(), thisMat);
				}
			}

			TextComponent warn = new TextComponent("WARN "); warn.setBold(true);
			warn.setColor(ChatColor.RED);

			TextComponent msg = new TextComponent("Potential lag-machine at " +
					block.getX() + ", " + block.getY() + ", " + block.getZ() + " in " + env +
					" by " + placer_name + " with UUID: " + placer.getUniqueId());

			String location = block.getX() + " " + block.getY() + " " + block.getZ();
			ClickEvent thisEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					"/ninjatp " + env + " " + location);

			msg.setClickEvent(thisEvent);

			if (counter > 256) {
				Util.notifyOps(new TextComponent(warn, msg));
			}
		}

		// destroy illegal items on shulker placement | prevent Ops duping shulkers in creative mode
		if (mat.contains("SHULKER_BOX")) {

			if (!placer.getGameMode().equals(GameMode.SURVIVAL)) {
				event.setCancelled(true);

				placer.sendMessage(new TextComponent(
						ChatColor.RED + "You can only place shulkers in survival mode").toLegacyText());
			}

			ShulkerBox thisShulk;
			try {
				thisShulk = (ShulkerBox)event.getBlockReplacedState();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				thisShulk = null;
			}

			if (thisShulk != null) {
				for (ItemStack thisStack: thisShulk.getInventory().getContents()) {
					ItemCheck.IllegalCheck(thisStack, "Placed Shulker", placer);
				}
			}
		}
		
		// anti roof-placement
		if (Config.getValue("protect.roof.noplacement").equals("true")) {
			if(block_loc.getY() > 127 && block_loc.getWorld().getName().endsWith("the_nether")) {
				
				event.setCancelled(true);
			}
		}
		
		// do nothing if user is placing an ender eye into an end portal frame
		if (!event.getItemInHand().getType().equals(Material.ENDER_EYE)
				&& Config.getValue("protect.banned.place").equals("true")) {
			
			// prevent all players from placing blocks totally unobtainable in survival mode, but ignore admin account					
			if (Config.getValue("protect.banned.place.ops").equals("false") && placer.isOp()) {
				return;
			}
			
			if (PlacementBanned.contains(blockType)) {
				event.setCancelled(true);
				
				if (Config.debug) Bukkit.spigot().broadcast(
						new TextComponent(placer_name + "'s BlockPlaceEvent was cancelled."));
				return;
			} else if (blockType.equals(Material.BEDROCK)) {
				placedBedrockCounter++;
				System.out.println("WARN: " + placer_name + " just placed bedrock at " + block_loc);
			}
		}
		
		if (Config.debug && Config.verbose) {
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			
			placer.sendMessage(new TextComponent(
					"PlaceTime: " + new DecimalFormat("0.000").format((double)duration/1000000.0) + " ms")
					.toLegacyText());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public static void onCreativePlace(BlockPlaceEvent event) {
		Player thisPlayer = event.getPlayer();

		if (thisPlayer.getGameMode().equals(GameMode.SURVIVAL) ||
				PlayerMeta.isAdmin(thisPlayer)) return;

		int stackCount = event.getItemInHand().getAmount();

		ItemStack realStack = thisPlayer.getActiveItem();
		if (consumeCreativeBlocks) {
			// TODO: figure out why these don't work:
			// Objects.requireNonNull(realStack).setAmount(stackCount-1);
			// usedItemStack.setAmount(stackCount-1);
		}

		if (!thisPlayer.isOp() && modeOnPlace) thisPlayer.setGameMode(GameMode.SURVIVAL);
	}
	
	public static boolean updateConfigs() {
		
		try {
			roofProt = Boolean.parseBoolean(Config.getValue("protect.bedrock.roof"));
			floorProt = Boolean.parseBoolean(Config.getValue("protect.bedrock.floor"));
			modeOnPlace = Boolean.parseBoolean(Config.getValue("protect.gamemode.onplace"));
			modeOnBreak = Boolean.parseBoolean(Config.getValue("protect.gamemode.onbreak"));
			consumeCreativeBlocks = Boolean.parseBoolean(Config.getValue("consume.creative.blocks"));
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
