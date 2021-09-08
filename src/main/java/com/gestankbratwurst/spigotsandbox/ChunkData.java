package com.gestankbratwurst.spigotsandbox;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of SpigotSandbox and was created at the 30.08.2021
 *
 * SpigotSandbox can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ChunkData {

  private final Logger logger = OreLocator.getPlugin(OreLocator.class).getLogger();
  private final BlockingQueue<ChunkSnapshot> snapshotQueue = new LinkedBlockingQueue<>();
  private final ExecutorService executorService;
  private final ConcurrentLinkedDeque<Future<?>> executingFutures = new ConcurrentLinkedDeque<>();

  private final Map<Material, ObjectBigArrayBigList<BlockPosition>> positionMap = Collections.synchronizedMap(
      new EnumMap<>(Material.class));
  private final EnumSet<Material> targetMaterials;
  private final Semaphore semaphore;
  private final int threadCount;

  public ChunkData(final OreLocatorConfig config) {
    this.threadCount = config.getAnalyzeThreadsNum();
    this.executorService = Executors.newFixedThreadPool(this.threadCount);
    this.targetMaterials = config.getMaterialList();
    this.semaphore = new Semaphore(this.threadCount);
  }

  public int getAnalysingTaskCount() {
    this.executingFutures.removeIf(Future::isDone);
    return this.executingFutures.size();
  }

  public void addSnapshotToAnalyse(final ChunkSnapshot snapshot) {
    this.snapshotQueue.add(snapshot);
    this.executingFutures.add(this.executorService.submit(this::analyse));
  }

  private void analyse() {
    final ChunkSnapshot snapshot;
    try {
      snapshot = this.snapshotQueue.poll(1000, TimeUnit.MILLISECONDS);
      if (snapshot == null) {
        return;
      }
    } catch (final InterruptedException e) {
      System.out.println(e.getMessage());
      return;
    }
    this.loadData(snapshot);
  }

  private void loadData(final ChunkSnapshot snapshot) {
    try {
      this.semaphore.acquire();
      for (int x = 0; x < 16; x++) {
        for (int z = 0; z < 16; z++) {
          for (int y = 0; y < 256; y++) {
            final Material material = snapshot.getBlockType(x, y, z);
            if (this.targetMaterials.contains(material)) {
              final int gx = snapshot.getX() * 16;
              final int gz = snapshot.getZ() * 16;
              this.analysePositionData(material, x + gx, y, z + gz);
            }
          }
        }
      }
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      this.semaphore.release();
    }
  }

  private void analysePositionData(final Material material, final int x, final int y, final int z) {
    this.positionMap.computeIfAbsent(material, key -> new ObjectBigArrayBigList<>()).add(new BlockPosition(x, y, z));
  }

  public void terminate() {
    this.logger.info("§eAwaiting Termination...");
    this.executorService.shutdown();
    try {
      this.executorService.awaitTermination(5, TimeUnit.MINUTES);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
    this.logger.info("§aAll pending tasks done.");
  }


  public JsonObject asJson() {
    final JsonObject jsonObject = new JsonObject();
    try {
      this.semaphore.acquire(this.threadCount);
      for (final Entry<Material, ObjectBigArrayBigList<BlockPosition>> entry : this.positionMap.entrySet()) {
        final String key = entry.getKey().toString();
        final JsonArray array = new JsonArray();
        entry.getValue().stream().filter(Objects::nonNull).sorted().map(BlockPosition::toString).forEach(array::add);
        jsonObject.add(key, array);
      }
    } catch (final Exception exception) {
      exception.printStackTrace();
    } finally {
      this.semaphore.release(this.threadCount);
    }
    return jsonObject;
  }

}