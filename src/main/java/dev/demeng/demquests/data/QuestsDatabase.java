package dev.demeng.demquests.data;

import dev.demeng.demquests.DemQuests;
import dev.demeng.demquests.model.Quest;
import dev.demeng.pluginbase.mysql.DatabaseCredentials;
import dev.demeng.pluginbase.mysql.SqlDatabase;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.entity.Player;

public class QuestsDatabase extends SqlDatabase {

  public QuestsDatabase(DemQuests i) throws SQLException {
    super(DriverType.MYSQL,
        new DatabaseCredentials(
            i.getSettings().getString("storage.host"),
            i.getSettings().getInt("storage.port"),
            i.getSettings().getString("storage.database"),
            i.getSettings().getString("storage.user"),
            i.getSettings().getString("storage.password")),
        Objects.requireNonNull(i.getSettings().getString("storage.additional-options")));

    initialize();
  }

  public void initialize() throws SQLException {

    final StringBuilder questColumns = new StringBuilder();

    for (Quest quest : Quest.values()) {
      questColumns.append("`").append(quest.name()).append("` INT DEFAULT 0, ");
    }

    execute("CREATE TABLE IF NOT EXISTS `quests_playerdata` ("
            + "`uuid` VARCHAR(36) NOT NULL, "
            + questColumns
            + " PRIMARY KEY (`uuid`));",
        null);
  }

  public Map<Quest, Integer> getProgress(Player p) throws SQLException {
    return query("SELECT * FROM quests_playerdata WHERE uuid = ?;", statement ->
        statement.setString(1, p.getUniqueId().toString()), rs -> {

      if (!rs.next()) {
        execute("INSERT INTO quests_playerdata (uuid) VALUES (?);",
            statement -> statement.setString(1, p.getUniqueId().toString()));
        return null;
      }

      final Map<Quest, Integer> playerProgress = new EnumMap<>(Quest.class);

      for (Quest quest : Quest.values()) {
        playerProgress.put(quest, rs.getInt(quest.name()));
      }

      return playerProgress;
    }).orElse(Collections.emptyMap());
  }

  public void updateProgress(Player p, Quest quest, int progress) throws SQLException {
    execute("UPDATE quests_playerdata SET " + quest.name() + " = ? WHERE uuid = ?;", statement -> {
      statement.setDouble(1, progress);
      statement.setString(2, p.getUniqueId().toString());
    });
  }
}
