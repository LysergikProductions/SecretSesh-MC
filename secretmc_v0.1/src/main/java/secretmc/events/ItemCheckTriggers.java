package secretmc.events;

import secretmc.backend.Config;
import secretmc.backend.ItemCheck;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

// trigger illegals checks from inventory related events
public class ItemCheckTriggers implements Listener {

	@EventHandler
	public void onDispense(BlockDispenseArmorEvent e) {
		ItemCheck.IllegalCheck(e.getItem(), "DISPENSED_ARMOR", null);
	}

	@EventHandler
	public void onOpenInventory(InventoryOpenEvent event) {
		event.getInventory().forEach(itemStack ->
				ItemCheck.IllegalCheck(itemStack, "INVENTORY_OPENED", (Player)event.getPlayer()));
	}

	// Prevents hopper exploits
	@EventHandler
	public void onInventoryMovedItem(InventoryMoveItemEvent event) {
		if (Config.getValue("item.illegal.inv_check").equals("true")) {

			ItemCheck.IllegalCheck(event.getItem(), "INVENTORY_MOVED_ITEM", null);

			event.getSource().forEach(itemStack ->
					ItemCheck.IllegalCheck(itemStack, "INVENTORY_MOVED_ITEM_INVENTORY", null));
		}
	}

	@EventHandler
	public void onPickupItem(EntityPickupItemEvent e) {
		if (e.getEntityType().equals(EntityType.PLAYER)) {

			Player player = (Player) e.getEntity();
			ItemCheck.IllegalCheck(e.getItem().getItemStack(), "ITEM_PICKUP", player);
			player.getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "ITEM_PICKUP_INVENTORY", null));

		} else {
			ItemCheck.IllegalCheck(e.getItem().getItemStack(), "ITEM_PICKUP", null);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		ItemCheck.IllegalCheck(e.getCurrentItem(), "INVENTORY_CLICK", (Player)e.getWhoClicked());
	}
}
