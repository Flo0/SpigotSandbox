package com.gestankbratwurst.spigotsandbox.bareboneframework;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface GUIHandler {

  void prepareInv(Inventory inventory);

  void handleClick(InventoryClickEvent event);

}
