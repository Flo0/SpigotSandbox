package com.gestankbratwurst.spigotsandbox;

import java.util.EnumSet;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of SpigotSandbox and was created at the 07.09.2021
 *
 * SpigotSandbox can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class OreLocatorConfig {

  public static OreLocatorConfig load(final OreLocator plugin) {
    final OreLocatorConfig config = new OreLocatorConfig();

    plugin.saveDefaultConfig();
    plugin.reloadConfig();
    final FileConfiguration configuration = plugin.getConfig();

    config.worldName = configuration.getString("WorldName");
    config.worldMiddleUsed = configuration.getBoolean("Middle.use-spawn-as-middle");
    config.alternateMiddle = new double[]{
        configuration.getDouble("Middle.x"),
        configuration.getDouble("Middle.y"),
        configuration.getDouble("Middle.z")
    };
    config.radius = configuration.getInt("Radius");

    final EnumSet<Material> materials = EnumSet.noneOf(Material.class);
    for (final String val : configuration.getStringList("Types")) {
      materials.add(Material.matchMaterial(val));
    }
    config.materialList = materials;

    config.saveFreqSeconds = configuration.getInt("SaveFrequencySeconds");
    config.logFreqSeconds = configuration.getInt("ConsoleDisplaySeconds");
    config.logFormat = configuration.getString("ConsoleFormat");
    config.chunkThreadsNum = configuration.getInt("NumberOfChunkGenThreads");
    config.analyzeThreadsNum = configuration.getInt("NumberOfAnalyzingThreads");

    return config;
  }

  @Getter
  private String worldName;
  @Getter
  private boolean worldMiddleUsed;
  @Getter
  private double[] alternateMiddle;
  @Getter
  private int radius;
  @Getter
  private EnumSet<Material> materialList;
  @Getter
  private int saveFreqSeconds;
  @Getter
  private int logFreqSeconds;
  @Getter
  private String logFormat;
  @Getter
  private int chunkThreadsNum;
  @Getter
  private int analyzeThreadsNum;

}
