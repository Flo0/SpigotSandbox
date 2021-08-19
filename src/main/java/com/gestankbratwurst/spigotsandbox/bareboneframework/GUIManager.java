package com.gestankbratwurst.spigotsandbox.bareboneframework;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class GUIManager {

  private final Map<Inventory, GUIHandler> guiHandlerMap = new HashMap<>();
  private final Map<String, GUIFactory> guiFactoryMap = new HashMap<>();

  public void registerFactory(final GUIFactory factory) {
    this.guiFactoryMap.put(factory.getKey(), factory);
  }

  public void openGUI(final Player player, final String guiKey) {
    final GUIFactory factory = this.guiFactoryMap.get(guiKey);
    if (factory == null) {
      throw new IllegalStateException("Factory of type " + guiKey + " not registered.");
    }
    final Inventory inventory = factory.createInventory(player);
    final GUIHandler handler = factory.createHandler(player);
    this.guiHandlerMap.put(inventory, handler);
    handler.prepareInv(inventory);
    player.openInventory(inventory);
  }

  protected void handleEvent(final InventoryClickEvent event) {
    final Inventory inventory = event.getInventory();
    final GUIHandler handler = this.guiHandlerMap.get(inventory);
    if (handler == null) {
      return;
    }
    handler.handleClick(event);
  }

  protected void handleEvent(final InventoryCloseEvent event) {
    this.guiHandlerMap.remove(event.getInventory());
  }

}
