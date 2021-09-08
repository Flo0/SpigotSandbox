package com.gestankbratwurst.spigotsandbox;

import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of SpigotSandbox and was created at the 30.08.2021
 *
 * SpigotSandbox can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ChunkLoader {

  private final LongArrayFIFOQueue coordinates = new LongArrayFIFOQueue();
  private final World world;
  private final int count;
  private final AtomicInteger current = new AtomicInteger();
  private final ChunkData chunkData;
  private final String logFormat;
  private boolean done = false;
  private int last = 0;
  private final Logger logger = OreLocator.getPlugin(OreLocator.class).getLogger();
  private long lastTime = System.currentTimeMillis();

  public ChunkLoader(final OreLocatorConfig config, final JavaPlugin plugin) {
    final int radius = config.getRadius();
    final int flushFrequencySeconds = config.getSaveFreqSeconds();
    final int logFrequencySeconds = config.getLogFreqSeconds();
    final String worldName = config.getWorldName();
    this.world = Bukkit.getWorld(worldName);

    if (this.world == null) {
      throw new IllegalStateException("World " + worldName + " does not exist.");
    }

    final double[] xyz;

    if (config.isWorldMiddleUsed()) {
      final Location spawn = this.world.getSpawnLocation();
      xyz = new double[]{spawn.getX(), spawn.getY(), spawn.getZ()};
    } else {
      xyz = config.getAlternateMiddle();
    }

    this.logFormat = config.getLogFormat();

    final int cMidX = (int) xyz[0] / 16;
    final int cMidZ = (int) xyz[2] / 16;
    final int cRad = radius / 16;

    for (int x = cMidX - cRad; x <= cMidX + cRad; x++) {
      for (int z = cMidZ - cRad; z <= cMidZ + cRad; z++) {
        this.coordinates.enqueue(OreLocator.getChunkKey(x, z));
      }
    }

    this.chunkData = new ChunkData(config);
    this.count = this.coordinates.size();

    for (int i = 0; i < config.getChunkThreadsNum(); i++) {
      CompletableFuture.runAsync(this::startLoaderThread);
    }

    Bukkit.getScheduler().runTaskTimer(plugin, this::logProgress, logFrequencySeconds * 20L, logFrequencySeconds * 20L);
    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::save, flushFrequencySeconds * 20L, flushFrequencySeconds * 20L);
  }

  private void startLoaderThread() {
    if (this.coordinates.isEmpty()) {
      return;
    }
    final long next = this.coordinates.dequeueLong();
    final int[] coords = OreLocator.getChunkCoords(next);
    this.world.getChunkAtAsyncUrgently(coords[0], coords[1]).thenAccept(chunk -> {
      this.chunkData.addSnapshotToAnalyse(chunk.getChunkSnapshot());
      this.startLoaderThread();
    }).thenRun(() -> {
      if (this.current.incrementAndGet() == this.count) {
        Bukkit.getScheduler().runTask(OreLocator.getPlugin(OreLocator.class), this::onEnd);
      }
    });
  }

  private void logProgress() {
    if (this.done) {
      return;
    }
    final int currentCount = this.current.get();
    final int delta = currentCount - this.last;
    final long deltaTime = System.currentTimeMillis() - this.lastTime;
    this.lastTime = System.currentTimeMillis();
    this.last = currentCount;

    final double deltaSeconds = deltaTime / 1000.0;
    double cps = delta / deltaSeconds;
    cps = (int) (cps * 100) / 100.0;

    double percent = 100.0 / this.count * currentCount;
    percent = (int) (percent * 100) / 100.0;

    String message = this.logFormat.replaceFirst("%percent%", "" + percent);
    message = message.replaceFirst("%current%", "" + currentCount);
    message = message.replaceFirst("%max%", "" + this.count);
    message = message.replaceFirst("%cps%", "" + cps);
    message = message.replaceFirst("%atc%", "" + this.chunkData.getAnalysingTaskCount());

    this.logger.info(message);
  }

  private void onEnd() {
    this.logProgress();

    this.chunkData.terminate();
    this.save();

    this.done = true;
    this.logger.info("§a[Done]");
  }

  private void save() {
    final String json = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(this.chunkData.asJson());
    final OreLocator plugin = OreLocator.getPlugin(OreLocator.class);
    final File folder = plugin.getDataFolder();
    if (folder.mkdirs()) {
      this.logger.info("Plugin folder was created.");
    }
    final File dataFile = new File(folder + File.separator + "data.json");
    this.logger.info("§eSaving current data into §f" + dataFile);
    try {
      Files.writeString(dataFile.toPath(), json);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public void terminateThreads() {
    this.chunkData.terminate();
  }

}
