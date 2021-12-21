package dev.demeng.demquests.data;

import dev.demeng.demquests.DemQuests;
import dev.demeng.demquests.model.Quest;
import dev.demeng.demquests.util.Utils;
import dev.demeng.pluginbase.Common;
import dev.demeng.pluginbase.Registerer;
import dev.demeng.pluginbase.TaskUtils;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DataManager implements Listener {

  private final DemQuests i;
  private final Map<Player, Map<Quest, Integer>> progress = new HashMap<>();

  public DataManager(DemQuests i) {
    this.i = i;
    Registerer.registerListener(this);
  }

  public Map<Quest, Integer> getProgress(Player p) {
    return progress.get(p);
  }

  public int getProgress(Player p, Quest quest) {
    return progress.get(p).get(quest);
  }

  public void incrementProgress(Player p, Quest quest, int amount) {

    final int originalProgress = getProgress(p, quest);
    final int newProgress = originalProgress + amount;
    progress.get(p).put(quest, newProgress);

    TaskUtils.runAsync(task -> {
      try {
        i.getQuestsDatabase().updateProgress(p, quest, newProgress);
      } catch (SQLException ex) {
        Common.error(ex, "Failed to update player data: " + p.getName(), false);
      }
    });

    quest.getRewards().entrySet().stream()
        .filter(entry -> originalProgress < entry.getKey())
        .filter(entry -> newProgress >= entry.getKey())
        .map(Map.Entry::getValue)
        .forEachOrdered(commands -> {
          for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                command.replace("%player%", p.getName()));
          }
        });
  }

  public void loadData(Player p) {
    TaskUtils.runAsync(task -> {
      try {
        progress.put(p, Utils.progressMap(i.getQuestsDatabase().getProgress(p)));
      } catch (SQLException ex) {
        Common.error(ex, "Failed to get player data: " + p.getName(), false);
      }
    });
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent e) {
    loadData(e.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent e) {
    progress.remove(e.getPlayer());
  }
}
