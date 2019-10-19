import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scheduler {
  private StudentCompany[] studentCompanyPossibilities;
  private ArrayList<Assignment> assignments;
  private int maxRange;
  private static int maxFind = 0;
  private static ArrayList<Student> studentArrayList;

  public Scheduler(StudentCompany[] studentCompanyPossibilities, int maxRange) {
    this.studentCompanyPossibilities = studentCompanyPossibilities;
    this.assignments = new ArrayList<>();
    this.maxRange = maxRange;
  }


  public static void main(String[] args) {
    CSVReaderFinal csvReader = new CSVReaderFinal("/home/tharsanan/Projects/InterviewScheduler/test2019.csv");
    try {
      csvReader.readFromFile();
      System.out.println("company count: " + csvReader.getCompanyNameList().size());
      for (String s : csvReader.getCompanyNameList()) {
          System.out.println(s);
      }
      studentArrayList = csvReader.getStudentList();
      ArrayList<StudentCompany> studentCompanyArrayList = new ArrayList<>();
      for (int i = 0; i < csvReader.getStudentList().size(); i++) {
        if(csvReader.getStudentList().get(i).getCompanies().length == 0){
          System.out.println(csvReader.getStudentList().get(i).getName());
        }
        for (int j = 0; j < csvReader.getStudentList().get(i).getCompanies().length; j++) {
          studentCompanyArrayList.add(
              new StudentCompany(
                  csvReader.getStudentList().get(i),
                  csvReader.getStudentList().get(i).getCompanies()[j]));
        }
      }
      Scheduler scheduler =
          new Scheduler(studentCompanyArrayList.toArray(new StudentCompany[0]), 200);
      boolean scheduled = scheduler.schedule();
      int maxTimeFound = 0;
      if (scheduled) {
        for (Assignment assignment : scheduler.getAssignments()) {
          if(maxTimeFound < assignment.getTimeRange().getTimeEnd()){
            maxTimeFound = assignment.getTimeRange().getTimeEnd();
          }
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
      System.out.println("max found: " + maxTimeFound);
      scheduler.saveAsCSV();
      createCSvForStudentCompanyTime(maxTimeFound+1, scheduler.assignments);
    } catch (IOException e) {
      System.out.println("could not read the file");
    }
  }

  public static void createCSvForStudentCompanyTime(int max, ArrayList<Assignment> assignments){
    ArrayList<String[]> companyNameForEachFiveMin = new ArrayList<>();
    Collections.sort(studentArrayList, Comparator.comparing(Student::getName));
    for (int i = 0; i < studentArrayList.size(); i++) {
      companyNameForEachFiveMin.add(new String[max]);
      for (int j = 0; j < assignments.size(); j++) {
        if(assignments.get(j).getStudentCompany().getStudent().getName().equals(studentArrayList.get(i).getName())){
          int start = assignments.get(j).getTimeRange().getTimeStart();
          int end = assignments.get(j).getTimeRange().getTimeEnd();
          for(int k = start; k < end; k++) {
            companyNameForEachFiveMin.get(i)[k] = assignments.get(j).getStudentCompany().getCompany().getName() +" "+ assignments.get(j).getPanelNumber();
          }
        }
      }
    }
    StringBuilder csvData = new StringBuilder();
    csvData.append("start");
    for (int i = 0; i < max; i++) {
      csvData.append(","+ i*5 + " - " + (i+1)*5);
    }
    csvData.append('\n');
    for (int i = 0; i < studentArrayList.size(); i++) {
      csvData.append(studentArrayList.get(i).getName());
      for (int j = 0; j < companyNameForEachFiveMin.get(i).length; j++) {
        csvData.append("," + companyNameForEachFiveMin.get(i)[j]);
      }
      csvData.append("\n");
    }
    try (PrintWriter writer =
             new PrintWriter("/home/tharsanan/Projects/InterviewScheduler/testNew.csv", "UTF-8")) {
      writer.println(csvData.toString());
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public static int countStudent(ArrayList<Assignment> assignments){
    ArrayList<Student> studentArrayList = new ArrayList<>();
    for (int i = 0; i < assignments.size(); i++) {
      Student student = assignments.get(i).getStudentCompany().getStudent();
      if(!studentArrayList.contains(student)){
        studentArrayList.add(student);
      }
    }
    return studentArrayList.size();
  }
  public static int countStudentPossibilities(StudentCompany[] studentCompanies){
    ArrayList<Student> studentArrayList = new ArrayList<>();
    for (int i = 0; i < studentCompanies.length; i++) {
      Student student = studentCompanies[i].getStudent();
      if(!studentArrayList.contains(student)){
        studentArrayList.add(student);
      }
    }
    return studentArrayList.size();
  }

  public void saveAsCSV() throws FileNotFoundException, UnsupportedEncodingException {
    ArrayList<Student> studentArrayList = new ArrayList<>();
    ArrayList<String[]> scheduleList = new ArrayList<>();
    Collections.sort(assignments, new SortAssignmentByStudentName());
    for (int i = 0; i < assignments.size(); i++) {
      Student student = assignments.get(i).getStudentCompany().getStudent();
      if(!studentArrayList.contains(student)){
        studentArrayList.add(student);
        scheduleList.add(new String[60]);
      }
      int index = studentArrayList.indexOf(student);
      scheduleList.get(index)[assignments.get(i).getTimeRange().getTimeStart()/3] = assignments.get(i).getStudentCompany().getCompany().getName() + "_" + assignments.get(i).getPanelNumber();
    }
    StringBuilder csvData = new StringBuilder();
    for (int i = 0; i < studentArrayList.size(); i++) {
      Student student = studentArrayList.get(i);
        csvData.append(student.getName());
      for (int j = 0; j < scheduleList.get(i).length; j++) {
        if (scheduleList.get(i)[j] != null) {
          csvData.append("," + scheduleList.get(i)[j]);
        }
        else{
          csvData.append(",");
        }
      }
      csvData.append("\n");
    }
    System.out.println("Student size : " + studentArrayList.size());
    try (PrintWriter writer =
        new PrintWriter("/home/tharsanan/Projects/InterviewScheduler/test.csv", "UTF-8")) {
      writer.println(csvData.toString());
    }
  }

  public static void printList(List list) {
    for (Object o : list) {
      System.out.println(o.toString());
    }
  }

  public ArrayList<Assignment> getAssignments() {
    return assignments;
  }

  public boolean schedule() {
    StudentCompany studentCompany = getLeastAcceptedStudentCompany();
    if (studentCompany == null) {
      System.out.println("assignment size: " + assignments.size());
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
              > studentCompanyPossibilities[i].getStudent().getCompanies().length) {
            leastAccepted = studentCompanyPossibilities[i];
          }
          if (leastAccepted.getStudent().getCompanies().length
              == studentCompanyPossibilities[i].getStudent().getCompanies().length) {
            if (studentCompanyPossibilities[i].getCompany().getPanels()
                > leastAccepted.getCompany().getPanels()) {
              leastAccepted = studentCompanyPossibilities[i];
            }
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
}
