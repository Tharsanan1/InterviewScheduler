import java.util.ArrayList;

public class Scheduler {
  private StudentCompany[] studentCompanyPossibilities;
  private ArrayList<Assignment> assignments;
  private int maxRange;

  public Scheduler(StudentCompany[] studentCompanyPossibilities, int maxRange) {
    this.studentCompanyPossibilities = studentCompanyPossibilities;
    this.assignments = new ArrayList<>();
    this.maxRange = maxRange;
  }

  public static void main(String[] args) {
    Company sys = new Company("sys", 6, 3);
    Company wso = new Company("wso", 6, 2);
    Company enac = new Company("ena", 3, 1);

    Student thar = new Student("Tha", new Company[] {sys, wso, enac});
    Student kir = new Student("kir", new Company[] {sys, wso, enac});
    Student chru = new Student("chr", new Company[] {sys, wso, enac});
    Student bra = new Student("bra", new Company[] {sys, wso, enac});
    Student sar = new Student("sar", new Company[] {sys, wso, enac});

    ArrayList<StudentCompany> studentCompaniesList = new ArrayList<>();
    for (Student student : new Student[] {thar, kir, chru, bra, sar}) {
      for (Company company : student.getCompanies()) {
        studentCompaniesList.add(new StudentCompany(student, company));
      }
    }
    StudentCompany[] studentCompanies = new StudentCompany[studentCompaniesList.size()];

    for (int i = 0; i < studentCompaniesList.size(); i++) {
      studentCompanies[i] = studentCompaniesList.get(i);
    }

    Scheduler scheduler = new Scheduler(studentCompanies, 20);
    if (scheduler.schedule()) {
      for (Assignment assignment : scheduler.getAssignments()) {
        System.out.println(
            assignment.getStudentCompany().getStudent().getName()
                + " | "
                + assignment.getStudentCompany().getCompany().getName()
                + " | "
                + assignment.getPanelNumber()
                + " | "
                + assignment.getTimeRange().toString());
      }
    }
    else{
      System.out.println("not found");
    }
  }

  public ArrayList<Assignment> getAssignments() {
    return assignments;
  }

  public boolean schedule() {
    StudentCompany studentCompany = getLeastAcceptedStudentCompany();
    if (studentCompany == null) {
      return true;
    }
    for (int i = 0; i < maxRange + 1; i += studentCompany.getCompany().getTimeUnits()) {
      TimeRange timeRange = new TimeRange(i, i + studentCompany.getCompany().getTimeUnits());
      for (int j = 0; j < studentCompany.getCompany().getPanels(); j++) {
        Assignment assignment = new Assignment(studentCompany, j, timeRange);
        if (isValid(assignment)) {
          studentCompany.setAssigned(true);
          assignments.add(assignment);
          if (schedule()) {
            return true;
          }
          studentCompany.setAssigned(false);
          assignments.remove(assignment);
        }
      }
    }
    return false;
  }

  public StudentCompany getLeastAcceptedStudentCompany() {
    StudentCompany leastAccepted = null;
    for (int i = 0; i < studentCompanyPossibilities.length; i++) {
      if (!studentCompanyPossibilities[i].isAssigned()) {
        if (leastAccepted == null) {
          leastAccepted = studentCompanyPossibilities[i];
        } else {
          if (leastAccepted.getStudent().getCompanies().length
              < studentCompanyPossibilities[i].getStudent().getCompanies().length) {
            leastAccepted = studentCompanyPossibilities[i];
          }
        }
      }
    }
    return leastAccepted;
  }

  public boolean isValid(Assignment assignment) {
    for (Assignment assignmentLocal : assignments) {
      if (assignment
          .getStudentCompany()
          .getCompany()
          .getName()
          .equals(assignmentLocal.getStudentCompany().getCompany().getName())) {
        if (assignment.getPanelNumber() == assignmentLocal.getPanelNumber()) {
          if (TimeRange.clashes(assignment.getTimeRange(), assignmentLocal.getTimeRange())) {
            return false;
          }
        }
      }
      if (assignment
          .getStudentCompany()
          .getStudent()
          .getName()
          .equals(assignmentLocal.getStudentCompany().getStudent().getName())) {
        if (TimeRange.clashes(assignment.getTimeRange(), assignmentLocal.getTimeRange())) {
          return false;
        }
      }
    }
    return true;
  }
}
