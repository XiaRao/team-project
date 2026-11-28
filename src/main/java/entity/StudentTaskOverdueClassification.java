package entity;

import java.util.List;

public class StudentTaskOverdueClassification extends Classification<StudentTask, TaskDue> {
  public StudentTaskOverdueClassification(List<StudentTask> tasks) {
    super(TaskDue.class);
  }
}
