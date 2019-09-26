import java.util.Comparator;

public class SortAssignmentByStudentName implements Comparator<Assignment> {
  @Override
  public int compare(Assignment assignment, Assignment t1) {
    return assignment.getStudentCompany().getStudent().getName().compareTo(t1.getStudentCompany().getStudent().getName());
  }
}
