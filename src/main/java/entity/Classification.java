package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Classification<T extends Task, S extends Enum<S>>{
  private Map<S, List<T>> groupedTasks;

  public Classification(Class<S> status) {
    this.groupedTasks = new HashMap<>();
    for (S group : status.getEnumConstants()) {
      groupedTasks.put(group, new ArrayList<>());
    }
  }

  public void setTasks(S group, List<T> tasks) {
    List<T> newTasks = new ArrayList<>();
    for (T task : tasks) {
      newTasks.add(task);
    }
    this.groupedTasks.put(group, newTasks);
  }

  public List<T> getTasks(S group) {
    return groupedTasks.get(group);
  }
}
