package com.gestankbratwurst.spigotsandbox;


import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class OreLocator extends JavaPlugin implements Listener {

  private OreLocatorConfig config;
  private ChunkLoader chunkLoader;

  @Override
  public void onEnable() {
    this.config = OreLocatorConfig.load(this);
    Bukkit.getScheduler().runTaskLater(this, this::startLoader, 20L);
  }

  private void startLoader() {
    this.chunkLoader = new ChunkLoader(this.config, this);
  }

  @Override
  public void onDisable() {
    this.chunkLoader.terminateThreads();
  }

  public static int[] getChunkCoords(final long chunkKey) {
    final int x = ((int) chunkKey);
    final int z = (int) (chunkKey >> 32);
    return new int[]{x, z};
  }

  public static long getChunkKey(final int x, final int z) {
    return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
  }

}