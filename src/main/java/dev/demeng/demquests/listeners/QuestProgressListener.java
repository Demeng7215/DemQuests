package dev.demeng.demquests.listeners;

import dev.demeng.demquests.DemQuests;
import dev.demeng.demquests.model.Quest;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class QuestProgressListener implements Listener {

  private final DemQuests i;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerMove(PlayerMoveEvent e) {
    if (e.getFrom().getBlockX() != e.getTo().getBlockX()
        || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
      i.getDataManager().incrementProgress(e.getPlayer(), Quest.WALK, 1);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onBlockBreak(BlockBreakEvent e) {
    i.getDataManager().incrementProgress(e.getPlayer(), Quest.BREAK, 1);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onBlockPlace(BlockPlaceEvent e) {
    i.getDataManager().incrementProgress(e.getPlayer(), Quest.PLACE, 1);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
    i.getDataManager().incrementProgress(e.getPlayer(), Quest.COMMAND, 1);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDeath(EntityDeathEvent e) {

    if (e.getEntityType() == EntityType.PLAYER) {
      return;
    }

    final Player killer = e.getEntity().getKiller();

    if (killer != null) {
      i.getDataManager().incrementProgress(killer, Quest.KILL_MOBS, 1);
    }
  }
}
