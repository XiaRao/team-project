package entity;

public enum SortingStrategy {

  BY_DUE_DATE("By Due Date"),
  BY_SUGGESTED_HOURS_TO_WORK_PER_DAY("By Suggested Hours To Work Per Day"),
  BY_DIFFICULTY_LEVEL("BY Difficulty Level");

  private final String displayName;

  SortingStrategy(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}