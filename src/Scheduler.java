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
  private static ArrayList<String> companyNameList;

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
      companyNameList = csvReader.getCompanyNameList();
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
      int begin = studentCompanyArrayList.size();
      studentCompanyArrayList = modifyAssignmentPriority(studentCompanyArrayList, createOrder());      // priority modification
      if(begin != studentCompanyArrayList.size()){
        throw new IllegalStateException();
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
    ArrayList<ArrayList<Assignment>> assignmentListForStudentDataGen = new ArrayList<>();
    Collections.sort(studentArrayList, Comparator.comparing(Student::getName));
    for (int i = 0; i < studentArrayList.size(); i++) {
      companyNameForEachFiveMin.add(new String[max]);
      assignmentListForStudentDataGen.add(new ArrayList<>());
      for (int j = 0; j < assignments.size(); j++) {
        if(assignments.get(j).getStudentCompany().getStudent().getName().equals(studentArrayList.get(i).getName())){
          assignmentListForStudentDataGen.get(i).add(assignments.get(j));
          int start = assignments.get(j).getTimeRange().getTimeStart();
          int end = assignments.get(j).getTimeRange().getTimeEnd();
          for(int k = start; k < end; k++) {
            companyNameForEachFiveMin.get(i)[k] = assignments.get(j).getStudentCompany().getCompany().getName() +" "+ assignments.get(j).getPanelNumber();
          }
        }
      }
      Collections.sort(assignmentListForStudentDataGen.get(i), Comparator.comparingInt(k -> k.getTimeRange().getTimeStart()));
    }
    StringBuilder csvData = new StringBuilder();
    csvData.append("start");
    for (int i = 0; i < max; i++) {
      csvData.append(",").append(i * 5).append(" - ").append((i + 1) * 5);
    }
    csvData.append('\n');
    for (int i = 0; i < studentArrayList.size(); i++) {
      csvData.append(studentArrayList.get(i).getName());
      for (int j = 0; j < companyNameForEachFiveMin.get(i).length; j++) {
        csvData.append(",").append(companyNameForEachFiveMin.get(i)[j]);
      }
      csvData.append("\n");
    }
    if(studentArrayList.size() != assignmentListForStudentDataGen.size()){
      throw new IllegalStateException();
    }
    ArrayList<String> studentSpecificDataStrings = new ArrayList<>();
    for (int i = 0; i < studentArrayList.size(); i++) {
      StringBuilder studentSpecData = new StringBuilder();
      for (int j = 0; j < assignmentListForStudentDataGen.get(i).size(); j++) {
        Assignment localAssignment = assignmentListForStudentDataGen.get(i).get(j);
        studentSpecData.append(localAssignment.getTimeRange().getTimeStart()).append(" - ").append(localAssignment.getTimeRange().getTimeEnd()).append(",").append(localAssignment.getStudentCompany().getCompany().getName()).append(" - panel : ").append(localAssignment.getPanelNumber()).append("\n");
      }
      studentSpecificDataStrings.add(studentSpecData.toString());
    }
    for (int i = 0; i < studentArrayList.size(); i++) {
      try (PrintWriter writer =
               new PrintWriter("/home/tharsanan/Projects/InterviewScheduler/student_data/"+ studentArrayList.get(i).getName() + ".csv", "UTF-8")) {
        writer.println(studentSpecificDataStrings.get(i));
      } catch (FileNotFoundException | UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }

    ArrayList<ArrayList<Assignment>> assignmentListForCompanyDataGen = new ArrayList<>();
    for (int i = 0; i < companyNameList.size(); i++) {
      assignmentListForCompanyDataGen.add(new ArrayList<>());
      for (int j = 0; j < assignments.size(); j++) {
        if(assignments.get(j).getStudentCompany().getCompany().getName().equals(companyNameList.get(i))){
          assignmentListForCompanyDataGen.get(i).add(assignments.get(j));
        }
      }
      Collections.sort(assignmentListForCompanyDataGen.get(i), Comparator.comparingInt(k -> k.getTimeRange().getTimeStart()));
    }

    ArrayList<String> companySpecificDataStrings = new ArrayList<>();
    if(companyNameList.size() != assignmentListForCompanyDataGen.size()){
      throw new IllegalStateException();
    }
    for (int i = 0; i < companyNameList.size(); i++) {
      StringBuilder companySpecData = new StringBuilder();
      for (int j = 0; j < assignmentListForCompanyDataGen.get(i).size(); j++) {
        Assignment localAssignment = assignmentListForCompanyDataGen.get(i).get(j);
        companySpecData.append(localAssignment.getTimeRange().getTimeStart()).append(" - ").append(localAssignment.getTimeRange().getTimeEnd()).append(",").append(localAssignment.getStudentCompany().getStudent().getName()).append(" - panel : ").append(localAssignment.getPanelNumber()).append("\n");
      }
      companySpecificDataStrings.add(companySpecData.toString());
    }
    for (int i = 0; i < companyNameList.size(); i++) {
      try (PrintWriter writer =
          new PrintWriter(
              "/home/tharsanan/Projects/InterviewScheduler/company_data/"
                  + companyNameList.get(i)
                  + ".csv",
              "UTF-8")) {
        writer.println(companySpecificDataStrings.get(i));
      } catch (FileNotFoundException | UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }


    try (PrintWriter writer =
             new PrintWriter("/home/tharsanan/Projects/InterviewScheduler/companyVSstudent.csv", "UTF-8")) {
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
    StudentCompany studentCompany = getNotAcceptedStudentCompany();      // modified line
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

  public StudentCompany getNotAcceptedStudentCompany() {
    StudentCompany leastAccepted = null;
    for (int i = 0; i < studentCompanyPossibilities.length; i++) {
      if (!studentCompanyPossibilities[i].isAssigned()) {
        leastAccepted = studentCompanyPossibilities[i];
        break;
      }
    }
    return leastAccepted;
  }

  public StudentCompany getLeastAcceptedStudentCompanyModify() {
    StudentCompany leastAccepted = null;
    for (int i = 0; i < studentCompanyPossibilities.length; i++) {
      if (!studentCompanyPossibilities[i].isAssigned()) {
        if(containsUs(studentCompanyPossibilities[i].getStudent().getName())){
          return studentCompanyPossibilities[i];
        }
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

  static boolean containsUs(String name){
    if(name.contains("Tharsanan Kurukulasingam") || name.contains("Christkiran Sathiyananthadevan") || name.contains("Braveen Sritharan") || name.contains("Ramesh Kiroshkumar")){
      return true;
    }
    return false;
  }

  public boolean isValid(Assignment assignment) {
    if(assignment.getStudentCompany().getCompany().getName().contains("ACCELAERO") && assignment.getStudentCompany().getStudent().getName().toLowerCase().contains("tharsanan")){
      System.out.println("");
    }
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

  static ArrayList<StudentCompany> modifyAssignmentPriority(ArrayList<StudentCompany> originalList, ArrayList<StudentCompany> apeList){
    int count = 0;
    for (int i=0; i<apeList.size(); i++ ) {
      for (int j=0;j<originalList.size(); j++) {

        if(originalList.get(j).getCompany().getName().equals(apeList.get(i).getCompany().getName()) &&
            originalList.get(j).getStudent().getName().equals(apeList.get(i).getStudent().getName())){
          StudentCompany temp = originalList.get(j);
          originalList.remove(originalList.get(j));
          originalList.add(count, temp);
          count++;
        }
      }
    }
    return originalList;
  }

  static ArrayList<StudentCompany> createOrder(){
    String tharsanan[] = { "Enactor" ,"WSO2_Ballerina", "WSO2_Choreo", "Codify", "WSO2_OB" , "Virtusa", "Cloud Solution", "Creative Software"};
    String christkiran[] = {"Enactor", "WSO2_Choreo", "Virtusa", "WSO2_Ballerina", "Cloud Solutions International"};
    String braveen[] = {"Synergen Health", "WSO2_Ballerina", "Creative Software", "Accerlero", "Yaala Labs"};
    String kirosh[] = {"Virtusa", "WSO2_Ballerina", "Enactor", "Creative Software", "99X Technology", "WSO2_OB"};

    ArrayList<StudentCompany> apeList = new ArrayList<>();
    for (String company_name:tharsanan) {
      apeList.add(new StudentCompany(new Student("Tharsanan Kurukulasingam",null), new Company(company_name,0,0)));
    }
    for (String company_name:christkiran) {
      apeList.add(new StudentCompany(new Student("Christkiran Sathiyananthadevan",null), new Company(company_name,0,0)));
    }
    for (String company_name:braveen) {
      apeList.add(new StudentCompany(new Student("Braveen Sritharan",null), new Company(company_name,0,0)));
    }
    for (String company_name:kirosh) {
      apeList.add(new StudentCompany(new Student("Ramesh Kiroshkumar",null), new Company(company_name,0,0)));
    }
    return apeList;
  }
}
