package dev.demeng.demquests.model;

import dev.demeng.demquests.DemQuests;
import dev.demeng.pluginbase.Common;
import dev.demeng.pluginbase.Validate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

@RequiredArgsConstructor
public enum Quest {
  WALK(getRewardsFromConfig("walk")),
  BREAK(getRewardsFromConfig("break-blocks")),
  PLACE(getRewardsFromConfig("place-blocks")),
  COMMAND(getRewardsFromConfig("execute-commands")),
  KILL_MOBS(getRewardsFromConfig("kill-mobs"));

  @Getter private final Map<Integer, List<String>> rewards;

  private static Map<Integer, List<String>> getRewardsFromConfig(String key) {

    final Map<Integer, List<String>> questRewards = new HashMap<>();

    final ConfigurationSection section = Common.getOrError(
        DemQuests.getInstance().getSettings()
            .getConfigurationSection("quests." + key + ".rewards"),
        "Rewards for " + key + " not found.", false);

    for (String strThreshold : section.getKeys(false)) {

      final int threshold = Common.getOrError(Validate.checkInt(strThreshold),
          "Invalid threshold: " + strThreshold, false);

      questRewards.put(threshold, section.getStringList(strThreshold));
    }

    return questRewards;
  }
}
