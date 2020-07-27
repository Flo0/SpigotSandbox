package com.gestankbratwurst.spigotsandbox;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of SpigotSandbox and was created at the 27.07.2020
 *
 * SpigotSandbox can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ProjectileTask implements Runnable {

  public ProjectileTask(JavaPlugin plugin) {
    Bukkit.getScheduler().runTaskTimer(plugin, this, 1, 1);
    this.activeProjectiles = new HashSet<>();
  }

  public void shoot(Player player) {
    this.shoot(player.getUniqueId(), player.getEyeLocation(), 0.9, 1, 60);
  }

  public void shoot(UUID shooterID, Location start, double speed, double hitBox, int maxTicksAlive) {
    Projectile projectile = new Projectile(shooterID, start, speed, hitBox, maxTicksAlive);
    activeProjectiles.add(projectile);
  }

  private final Set<Projectile> activeProjectiles;

  @Override
  public void run() {
    Iterator<Projectile> iter = activeProjectiles.iterator();
    while (iter.hasNext()) {
      Projectile next = iter.next();
      next.tick();
      if (next.isDone()) {
        iter.remove();
      }
    }
  }

}
