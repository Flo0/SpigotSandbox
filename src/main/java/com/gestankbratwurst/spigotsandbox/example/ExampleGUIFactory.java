package com.gestankbratwurst.spigotsandbox.example;

import com.gestankbratwurst.spigotsandbox.bareboneframework.GUIFactory;
import com.gestankbratwurst.spigotsandbox.bareboneframework.GUIHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ExampleGUIFactory implements GUIFactory {

  @Override
  public String getKey() {
    return "example";
  }

  @Override
  public Inventory createInventory(final Player player) {
    return Bukkit.createInventory(null, 5 * 9, "Hi " + player.getName());
  }

  @Override
  public GUIHandler createHandler(final Player player) {
    return new ExampleGUI();
  }
}
