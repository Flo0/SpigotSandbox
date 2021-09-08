package com.gestankbratwurst.spigotsandbox;

import java.util.EnumSet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OreLocatorConfig {

  public static OreLocatorConfig load(final OreLocator plugin) {
    plugin.saveDefaultConfig();
    plugin.reloadConfig();
    final FileConfiguration yamlConfig = plugin.getConfig();

    final double[] alternateMiddle = new double[]{
        yamlConfig.getDouble("Middle.x"),
        yamlConfig.getDouble("Middle.y"),
        yamlConfig.getDouble("Middle.z")
    };

    final EnumSet<Material> materials = EnumSet.noneOf(Material.class);
    for (final String val : yamlConfig.getStringList("Types")) {
      materials.add(Material.matchMaterial(val));
    }

    return new OreLocatorConfigBuilder()
        .worldName(yamlConfig.getString("WorldName"))
        .worldMiddleUsed(yamlConfig.getBoolean("Middle.use-spawn-as-middle"))
        .alternateMiddle(alternateMiddle)
        .radius(yamlConfig.getInt("Radius"))
        .materialList(materials)
        .saveFreqSeconds(yamlConfig.getInt("SaveFrequencySeconds"))
        .logFreqSeconds(yamlConfig.getInt("ConsoleDisplaySeconds"))
        .logFormat(yamlConfig.getString("ConsoleFormat"))
        .chunkThreadsNum(yamlConfig.getInt("NumberOfChunkGenThreads"))
        .analyzeThreadsNum(yamlConfig.getInt("NumberOfAnalyzingThreads"))
        .build();
  }

  private final String worldName;
  private final boolean worldMiddleUsed;
  private final double[] alternateMiddle;
  private final int radius;
  private final EnumSet<Material> materialList;
  private final int saveFreqSeconds;
  private final int logFreqSeconds;
  private final String logFormat;
  private final int chunkThreadsNum;
  private final int analyzeThreadsNum;

}