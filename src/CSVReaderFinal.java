import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CSVReaderFinal {
  private String path;
  private ArrayList<Student> studentList;
  private ArrayList<String> companyNameList;
  public CSVReaderFinal(String path){
    this.path = path;
    studentList = new ArrayList<>();
    companyNameList = new ArrayList<>();
  }

  public void readFromFile() throws IOException {
    HashMap<String, ArrayList<Company>> acceptedCompaniesPerStudent = new HashMap<>();
    ArrayList<Company> companiesParticipating = new ArrayList<>();
    String row;
    int startIndex = 6;
    int panelIndex = 0;
    int timeIndex = 0;
    try (FileReader fileReader = new FileReader(path)) {
      try (BufferedReader csvReader = new BufferedReader(fileReader)) {
        boolean isFirst = true;
        while ((row = csvReader.readLine()) != null) {
          if(isFirst){
            isFirst = false;
            continue;
          }
          String[] data = row.split(",");
          Company company = new Company(data[2],Integer.parseInt(data[4])/5, Integer.parseInt(data[3]) );  // need to change.
          companiesParticipating.add(company);
          companyNameList.add(data[2]);
          for (int i = startIndex; i < data.length; i++) {
            String studentName = removeInvertedComma(data[i]).trim();
            acceptedCompaniesPerStudent.computeIfPresent(studentName, (key, value) -> {
              value.add(company);
              return value;
            });
            ArrayList<Company> companies = new ArrayList<>();
            companies.add(company);
            acceptedCompaniesPerStudent.putIfAbsent(studentName, companies);
          }
        }
      }
    }

    String[] studentNames = acceptedCompaniesPerStudent.keySet().toArray(new String[0]);
    for (int i = 0; i < studentNames.length; i++) {
      Company[] companiesForOneStudent = acceptedCompaniesPerStudent.get(studentNames[i]).toArray(new Company[0]);
      studentList.add(new Student(studentNames[i], companiesForOneStudent));
    }

  }

  private String removeInvertedComma(String s){
    final char iComma = '"';
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      if(s.charAt(i) != iComma){
        stringBuilder.append(s.charAt(i));
      }
    }
    return stringBuilder.toString();
  }

  public static void main(String[] args) throws IOException {
    CSVReaderFinal csvReaderFinal = new CSVReaderFinal("/home/tharsanan/Projects/InterviewScheduler/test2019.csv");
    csvReaderFinal.readFromFile();
  }

  public ArrayList<Student> getStudentList() {
    return studentList;
  }

  public ArrayList<String> getCompanyNameList() {
    return companyNameList;
  }
}
