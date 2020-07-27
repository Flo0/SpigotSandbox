package com.gestankbratwurst.spigotsandbox;

import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of SpigotSandbox and was created at the 27.07.2020
 *
 * SpigotSandbox can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class Projectile {

  public Projectile(UUID shooterID, Location start, double speed, double hitBox, int maxTicksAlive) {
    this.direction = start.getDirection().normalize();
    this.maxTicksAlive = maxTicksAlive;
    this.speed = speed;
    this.size = hitBox;
    this.currentLocation = start.clone();
    this.shooterID = shooterID;
    this.traceFilter = entity -> !entity.getUniqueId().equals(this.shooterID);
  }

  private final UUID shooterID;
  private final double size;
  private final double speed;
  private final Vector direction;
  private final int maxTicksAlive;
  private final Predicate<Entity> traceFilter;

  private final Location currentLocation;
  private int ticksAlive;
  private boolean done = false;

  public void tick() {

    if (ticksAlive++ == maxTicksAlive) {
      this.done = true;
      return;
    }

    RayTraceResult rayTraceResult = currentLocation
        .getWorld()
        .rayTrace(currentLocation, direction, speed, FluidCollisionMode.NEVER, false, size, traceFilter);
    if (rayTraceResult != null) {
      Entity hitEntity = rayTraceResult.getHitEntity();
      Block hitBlock = rayTraceResult.getHitBlock();

      if (hitEntity != null) {
        this.onEntityHit(hitEntity);
        this.done = true;
        return;
      } else if (hitBlock != null) {
        this.onBlockHit(hitBlock);
        this.done = true;
        return;
      }
    }

    currentLocation.getWorld().spawnParticle(Particle.CRIT, currentLocation, 1, 0, 0, 0, 0);

    this.fly();
  }

  private void fly() {
    currentLocation.add(direction.clone().multiply(speed));
    int cx = currentLocation.getBlockX() >> 4;
    int cz = currentLocation.getBlockZ() >> 4;
    this.done = !currentLocation.getWorld().isChunkGenerated(cx, cz);
  }

  private void onEntityHit(Entity entity) {
    entity.getLocation().getWorld().createExplosion(entity.getLocation(), 1F);
  }

  private void onBlockHit(Block block) {
    block.getLocation().getWorld().createExplosion(block.getLocation(), 1F);
  }

  public boolean isDone() {
    return done;
  }

}
