package com.gestankbratwurst.spigotsandbox;

import com.gestankbratwurst.spigotsandbox.bareboneframework.GUIListener;
import com.gestankbratwurst.spigotsandbox.bareboneframework.GUIManager;
import com.gestankbratwurst.spigotsandbox.example.ExampleGUIFactory;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotCore extends JavaPlugin implements Listener {

  @Getter
  private GUIManager guiManager;

  @Override
  public void onEnable() {
    this.guiManager = new GUIManager();
    Bukkit.getPluginManager().registerEvents(new GUIListener(this.guiManager), this);
    Bukkit.getPluginManager().registerEvents(this, this);
    this.guiManager.registerFactory(new ExampleGUIFactory());
  }

  @EventHandler
  public void onSneak(final PlayerToggleSneakEvent event) {
    if (event.isSneaking()) {
      this.guiManager.openGUI(event.getPlayer(), "example");
    }
  }

}