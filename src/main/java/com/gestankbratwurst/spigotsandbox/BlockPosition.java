package com.gestankbratwurst.spigotsandbox;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of SpigotSandbox and was created at the 08.09.2021
 *
 * SpigotSandbox can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public record BlockPosition(int x, int y, int z) implements Comparable<BlockPosition> {

  @Override
  public String toString() {
    return "[" + this.x + ":" + this.y + ":" + this.z + "]";
  }

  @Override
  public int compareTo(final BlockPosition other) {
    final int compX = Integer.compare(other.x, this.x);
    if (compX != 0) {
      return compX;
    }
    final int compZ = Integer.compare(other.z, this.z);
    if (compZ != 0) {
      return compZ;
    }
    return Integer.compare(other.y, this.y);
  }

}