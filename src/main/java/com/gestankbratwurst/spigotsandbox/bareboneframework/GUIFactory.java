package com.gestankbratwurst.spigotsandbox.bareboneframework;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface GUIFactory {

  String getKey();

  Inventory createInventory(Player player);

  GUIHandler createHandler(Player player);

}
