package dev.demeng.demquests.util;

import dev.demeng.demquests.model.Quest;
import java.util.HashMap;
import java.util.Map;

public class Utils {

  public static Map<Quest, Integer> progressMap(Map<Quest, Integer> current) {

    final Map<Quest, Integer> map = new HashMap<>(current);

    for (Quest quest : Quest.values()) {
      if (!current.containsKey(quest)) {
        map.put(quest, 0);
      }
    }

    return map;
  }
}
