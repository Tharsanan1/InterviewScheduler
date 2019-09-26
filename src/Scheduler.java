import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scheduler {
  private StudentCompany[] studentCompanyPossibilities;
  private ArrayList<Assignment> assignments;
  private HashMap<StudentCompany, ArrayList<Assignment>> possibleAssignments;
  private int maxRange;

  public Scheduler(StudentCompany[] studentCompanyPossibilities, int maxRange) {
    this.studentCompanyPossibilities = studentCompanyPossibilities;
    this.assignments = new ArrayList<>();
    this.maxRange = maxRange;
    possibleAssignments = new HashMap<>();
    generatePossibleAssignments();
  }

  public static void main(String[] args) {

    // test

    CSVReader csvReader =
        new CSVReader("/home/tharsanan/Projects/InterviewScheduler/FinalInterviewSchedule.csv");
    try {
      csvReader.readFromFile();
      ArrayList<StudentCompany> studentCompanyArrayList = new ArrayList<>();
      for (int i = 0; i < csvReader.getStudentList().size(); i++) {
        for (int j = 0; j < csvReader.getStudentList().get(i).getCompanies().length; j++) {
          studentCompanyArrayList.add(
              new StudentCompany(
                  csvReader.getStudentList().get(i),
                  csvReader.getStudentList().get(i).getCompanies()[j]));
        }
      }
      Scheduler scheduler =
          new Scheduler(studentCompanyArrayList.toArray(new StudentCompany[0]), 95);
      boolean scheduled = scheduler.schedule();
      if (scheduled) {
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
      } else {
        System.out.println("not found");
      }
    } catch (IOException e) {
      System.out.println("could not read the file");
    }
  }

  public static void printList(List list) {
    for (Object o : list) {
      System.out.println(o.toString());
    }
  }

  public void generatePossibleAssignments() {
    for (int j = 0; j < studentCompanyPossibilities.length; j++) {
      StudentCompany studentCompany = studentCompanyPossibilities[j];
      ArrayList<Assignment> assignments = new ArrayList<>();
      for (int i = 0; i < maxRange + 1; i += studentCompany.getCompany().getTimeUnits()) {
        TimeRange timeRange = new TimeRange(i, i + studentCompany.getCompany().getTimeUnits());
        for (int k = 0; k < studentCompany.getCompany().getPanels(); k++) {
          Assignment assignment = new Assignment(studentCompany, k, timeRange);
          assignments.add(assignment);
        }
      }
      possibleAssignments.put(studentCompany, assignments);
    }
  }

  public ArrayList<Assignment> getAssignments() {
    return assignments;
  }

  public boolean schedule() {
    StudentCompany studentCompany = getLeastAcceptedStudentCompanyByLeasePossibleAssignments();
    if (studentCompany == null) {
      return true;
    }
    for (int i = 0; i < maxRange + 1; i += studentCompany.getCompany().getTimeUnits()) {
      TimeRange timeRange = new TimeRange(i, i + studentCompany.getCompany().getTimeUnits());
      for (int j = 0; j < studentCompany.getCompany().getPanels(); j++) {
        Assignment assignment = new Assignment(studentCompany, j, timeRange);
        Result result = isValidBasedOnPossibleAssignmentLeft(assignment);
        if (result.isValid) {
          studentCompany.setAssigned(true);
          assignments.add(assignment);
          if (schedule()) {
            return true;
          }
          studentCompany.setAssigned(false);
          assignments.remove(assignment);
        }
        reAddRemovedAssignments(result.possibleAssignments);
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
              > studentCompanyPossibilities[i].getStudent().getCompanies().length) {
            leastAccepted = studentCompanyPossibilities[i];
          }
          if (leastAccepted.getStudent().getCompanies().length
              == studentCompanyPossibilities[i].getStudent().getCompanies().length) {
            if (studentCompanyPossibilities[i].getCompany().getPanels()
                < leastAccepted.getCompany().getPanels()) {
              leastAccepted = studentCompanyPossibilities[i];
            }
          }
        }
      }
    }
    return leastAccepted;
  }

  public StudentCompany getLeastAcceptedStudentCompanyByLeasePossibleAssignments() {
    StudentCompany leastAccepted = null;
    for (StudentCompany studentCompany : possibleAssignments.keySet()) {
      if (!studentCompany.isAssigned()) {
        if (leastAccepted == null) {
          leastAccepted = studentCompany;
        } else {
          if (possibleAssignments.get(leastAccepted).size()
              > possibleAssignments.get(studentCompany).size()) {
            leastAccepted = studentCompany;
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
        if (TimeRange.clashesStudent(assignment.getTimeRange(), assignmentLocal.getTimeRange())) {
          return false;
        }
      }
    }
    return true;
  }

  public void reAddRemovedAssignments(
      HashMap<StudentCompany, ArrayList<Assignment>> possibleAssignmentsLocal) {
    for (StudentCompany studentCompany : possibleAssignmentsLocal.keySet()) {
      for (Assignment assignment : possibleAssignmentsLocal.get(studentCompany)) {
        possibleAssignments.get(studentCompany).add(assignment);
      }
    }
  }

  public Result isValidBasedOnPossibleAssignmentLeft(Assignment assignment) {
    HashMap<StudentCompany, ArrayList<Assignment>> possibleAssignmentsLocal = new HashMap<>();
    for (Assignment assignmentLocal : assignments) {
      if (assignment
          .getStudentCompany()
          .getCompany()
          .getName()
          .equals(assignmentLocal.getStudentCompany().getCompany().getName())) {
        if (assignment.getPanelNumber() == assignmentLocal.getPanelNumber()) {
          if (TimeRange.clashes(assignment.getTimeRange(), assignmentLocal.getTimeRange())) {
            return new Result(possibleAssignmentsLocal, false);
          }
        }
      }
      if (assignment
          .getStudentCompany()
          .getStudent()
          .getName()
          .equals(assignmentLocal.getStudentCompany().getStudent().getName())) {
        if (TimeRange.clashesStudent(assignment.getTimeRange(), assignmentLocal.getTimeRange())) {
          return new Result(possibleAssignmentsLocal, false);
        }
      }
    }
    for (StudentCompany studentCompany : studentCompanyPossibilities) {
      possibleAssignmentsLocal.put(studentCompany, new ArrayList<Assignment>());
      if (!studentCompany.isAssigned()) {
        ArrayList<Assignment> assignmentArrayList = possibleAssignments.get(studentCompany);
        for (int k = 0; k < assignmentArrayList.size(); k++) {
          Assignment assignment1 = assignmentArrayList.get(k);
          if (assignment
              .getStudentCompany()
              .getCompany()
              .getName()
              .equals(assignment1.getStudentCompany().getCompany().getName())) {
            if (assignment.getPanelNumber() == assignment1.getPanelNumber()) {
              if (TimeRange.clashes(assignment.getTimeRange(), assignment1.getTimeRange())) {
                assignmentArrayList.remove(assignment1);
                possibleAssignmentsLocal.get(studentCompany).add(assignment1);
                if (assignmentArrayList.size() == 0) {
                  return new Result(possibleAssignmentsLocal, false);
                }
              }
            }
          }
          if (assignment
              .getStudentCompany()
              .getStudent()
              .getName()
              .equals(assignment1.getStudentCompany().getStudent().getName())) {
            if (TimeRange.clashesStudent(assignment.getTimeRange(), assignment1.getTimeRange())) {
              possibleAssignments.get(studentCompany).remove(assignment1);
              possibleAssignmentsLocal.get(studentCompany).add(assignment1);
              if (possibleAssignments.get(studentCompany).size() == 0) {
                return new Result(possibleAssignmentsLocal, false);
              }
            }
          }
        }
      }
    }
    return new Result(possibleAssignmentsLocal, true);
  }
}

class Result {
  HashMap<StudentCompany, ArrayList<Assignment>> possibleAssignments;
  boolean isValid;

  public Result(
      HashMap<StudentCompany, ArrayList<Assignment>> possibleAssignments, boolean isValid) {
    this.possibleAssignments = possibleAssignments;
    this.isValid = isValid;
  }
}

/**
 * Company sys = new Company("sys", 6, 3); Company wso = new Company("wso", 6, 2); Company enac =
 * new Company("ena", 3, 1);
 *
 * <p>Student thar = new Student("Tha", new Company[] {sys, wso, enac}); Student kir = new
 * Student("kir", new Company[] {sys, wso, enac}); Student chru = new Student("chr", new Company[]
 * {sys, wso, enac}); Student bra = new Student("bra", new Company[] {sys, wso, enac}); Student sar
 * = new Student("sar", new Company[] {sys, wso, enac});
 *
 * <p>ArrayList<StudentCompany> studentCompaniesList = new ArrayList<>(); for (Student student : new
 * Student[] {thar, kir, chru, bra, sar}) { for (Company company : student.getCompanies()) {
 * studentCompaniesList.add(new StudentCompany(student, company)); } } StudentCompany[]
 * studentCompanies = new StudentCompany[studentCompaniesList.size()];
 *
 * <p>for (int i = 0; i < studentCompaniesList.size(); i++) { studentCompanies[i] =
 * studentCompaniesList.get(i); }
 *
 * <p>Scheduler scheduler = new Scheduler(studentCompanies, 20); if (scheduler.schedule()) { for
 * (Assignment assignment : scheduler.getAssignments()) { System.out.println(
 * assignment.getStudentCompany().getStudent().getName() + " | " +
 * assignment.getStudentCompany().getCompany().getName() + " | " + assignment.getPanelNumber() + " |
 * " + assignment.getTimeRange().toString()); } } else{ System.out.println("not found"); }
 */
