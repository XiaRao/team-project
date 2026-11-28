package entity;

import java.util.List;

public class StudentTaskCompletionClassification extends Classification<StudentTask, TaskCompletion> {
  public StudentTaskCompletionClassification(List<StudentTask> tasks){
    super(TaskCompletion.class);
  }
}
