package com.gestankbratwurst.spigotsandbox;

public class ChunkUtils {

  public static int[] getChunkCoords(final long chunkKey) {
    final int x = ((int) chunkKey);
    final int z = (int) (chunkKey >> 32);
    return new int[]{x, z};
  }

  public static long getChunkKey(final int x, final int z) {
    return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
  }

}
