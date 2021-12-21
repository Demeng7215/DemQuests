package dev.demeng.demquests;

import dev.demeng.demquests.data.DataManager;
import dev.demeng.demquests.data.QuestsDatabase;
import dev.demeng.demquests.listeners.QuestProgressListener;
import dev.demeng.pluginbase.Common;
import dev.demeng.pluginbase.Registerer;
import dev.demeng.pluginbase.YamlConfig;
import dev.demeng.pluginbase.chat.ChatUtils;
import dev.demeng.pluginbase.plugin.BasePlugin;
import java.io.IOException;
import java.sql.SQLException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class DemQuests extends BasePlugin {

  @Getter @Setter(AccessLevel.PRIVATE) private static DemQuests instance;

  private YamlConfig settingsFile;

  @Getter private QuestsDatabase questsDatabase;
  @Getter private DataManager dataManager;

  @Override
  public void enable() {

    setInstance(this);

    getLogger().info("Loading configuration files...");
    if (!loadFiles()) {
      return;
    }

    getLogger().info("Connecting to database...");
    if (!connectDatabase()) {
      return;
    }

    getLogger().info("Loading quests progress...");
    dataManager = new DataManager(this);

    for (Player p : Bukkit.getOnlinePlayers()) {
      dataManager.loadData(p);
    }

    getLogger().info("Registering listeners...");
    Registerer.registerListener(dataManager);
    Registerer.registerListener(new QuestProgressListener(this));

    ChatUtils.console("&aDemQuests v" + Common.getVersion() + " has been enabled.");
  }

  @Override
  public void disable() {
    ChatUtils.console("&cDemQuests v" + Common.getVersion() + " has been disabled.");
  }

  public boolean loadFiles() {

    try {
      settingsFile = new YamlConfig("settings.yml");
    } catch (IOException | InvalidConfigurationException ex) {
      Common.error(ex, "Failed to load settings.yml.", true);
      return false;
    }

    return true;
  }

  public boolean connectDatabase() {

    try {
      questsDatabase = new QuestsDatabase(this);
    } catch (SQLException ex) {
      Common.error(ex, "Failed to connect to database.", true);
      return false;
    }

    return true;
  }

  public FileConfiguration getSettings() {
    return settingsFile.getConfig();
  }
}
