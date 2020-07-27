package com.gestankbratwurst.spigotsandbox;


import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotSandbox extends JavaPlugin implements Listener {

  private ProjectileTask projectileTask;

  @Override
  public void onEnable() {
    this.projectileTask = new ProjectileTask(this);
    Bukkit.getPluginManager().registerEvents(this, this);
  }

  @Override
  public void onDisable() {

  }

  @EventHandler
  public void onPortal(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    player.playSound(player.getEyeLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1F, 1.4F);
    this.projectileTask.shoot(player);
  }

}
