package com.gestankbratwurst.spigotsandbox.bareboneframework;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public record GUIListener(GUIManager manager) implements Listener {

  @EventHandler
  public void onClick(final InventoryClickEvent event) {
    this.manager.handleEvent(event);
  }

  @EventHandler
  public void onClose(final InventoryCloseEvent event) {
    this.manager.handleEvent(event);
  }

}
