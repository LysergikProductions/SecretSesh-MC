package secretmc.events;

/* *
 *
 *  About: Do things when entities move
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
import secretmc.data.PlayerMeta;
import secretmc.backend.utils.Util;

import java.util.*;
import org.bukkit.GameMode;
import org.bukkit.Location;

import org.bukkit.World.Environment;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@SuppressWarnings("SpellCheckingInspection")
public class MoveListener implements Listener {

	static Random r = new Random();

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();

		boolean inNether = loc.getWorld().getEnvironment().equals(Environment.NETHER);
		boolean inEnd = loc.getWorld().getEnvironment().equals(Environment.THE_END);
		double yCoord = loc.getY();
		
		// This method is actually fired upon head rotate too, so skip event if the player's coords didn't change
		if (event.getFrom().getBlockX() == event.getTo().getBlockX()
				&& event.getFrom().getBlockY() == event.getTo().getBlockY()
				&& event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;

		// Ensure survival-mode players are not invulnerable
		if (player.getGameMode().equals(GameMode.SURVIVAL) && !player.isOp()) {
			player.setInvulnerable(false);
		}

		// -- ROOF AND FLOOR PATCH -- //

		// kill players on the roof of the nether
		if (inNether && yCoord > 127 && Config.getValue("movement.block.roof").equals("true"))
			player.setHealth(0);

		// kill players below ground in overworld and nether
		if (!inEnd && yCoord < 0 && Config.getValue("movement.block.floor").equals("true"))
			player.setHealth(0);
		
		// Make game unplayable for laggers
		if (PlayerMeta.isPrisoner(player)) {
			int randomNumber = r.nextInt(9);
			
			if (randomNumber == 5 || randomNumber == 6) {
				player.sendMessage("\u00A7cThis is what you get!");
				event.setCancelled(true);
				return;
			}

			randomNumber = r.nextInt(250);
			if (randomNumber == 21) {
				player.kickPlayer("\u00A76lmao -tries to move-");
			}
		}
	}
		
	@EventHandler (priority = EventPriority.LOW)
	public void onEntityPortal(EntityPortalEvent e) {
		// Prevent invulnerable end-crystals from breaking spawn chunks
		// https://github.com/PaperMC/Paper/issues/5404

		EntityType ET = e.getEntityType();

		if (ET.equals(EntityType.ENDER_CRYSTAL)) {
			EnderCrystal crystal = (EnderCrystal)e.getEntity();

			if (crystal.isShowingBottom() || crystal.isInvulnerable()) {
				Environment portalFrom = e.getFrom().getWorld().getEnvironment();

				if (portalFrom.equals(Environment.THE_END)) {
					e.setCancelled(true);

					org.bukkit.World overworld = Util.getWorldByDimension(Environment.NORMAL);
					if (overworld == null) {
						System.out.println("WARN couldn't find NORMAL dimension onEntityPortal()");
						return;
					}

					Location spawnLoc = SpawnController
							.getRandomSpawn(overworld, overworld.getSpawnLocation());

					spawnLoc.setY(spawnLoc.getY()+1);
					TP_InvulCrystal(crystal, spawnLoc);

					if (Config.debug) System.out.println("TP'd invulnerable crystal to " +
							(int)spawnLoc.getX() + " " + (int)spawnLoc.getY() + " " + (int)spawnLoc.getZ());

				} else {
					e.setCancelled(true);

					Location spawnLoc = e.getTo();
					if (spawnLoc != null) TP_InvulCrystal(crystal, spawnLoc);

					if (Config.debug) System.out.println("TP'd invulnerable crystal to " +
							(int)spawnLoc.getX() + " " + (int)spawnLoc.getY() + " " + (int)spawnLoc.getZ());
				}
			}
		}
	}

	private static void TP_InvulCrystal(EnderCrystal crystal, Location spawnLoc) {
		spawnLoc.getChunk().load();

		EnderCrystal finalCrystal = (EnderCrystal)spawnLoc.getWorld()
				.spawnEntity(spawnLoc, EntityType.ENDER_CRYSTAL);

		finalCrystal.setInvulnerable(true);
		finalCrystal.setPersistent(true);
		finalCrystal.setShowingBottom(true);
		finalCrystal.setBeamTarget(new Location(spawnLoc.getWorld(), 0.5, 128, 0.5));

		crystal.remove();
	}
}
