import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

public class CSVReader {
  private String path;
  private ArrayList<Student> studentList;
  private ArrayList<String> companyNameList;
  public CSVReader(String path){
    this.path = path;
    studentList = new ArrayList<>();
    companyNameList = new ArrayList<>();
  }

  public void readFromFile() throws IOException {
    String row = null;
    boolean isFirstRow = true;
    try(FileReader fileReader = new FileReader(path)){
      try(BufferedReader csvReader = new BufferedReader(fileReader)){
        while ((row = csvReader.readLine()) != null) {
          if(isFirstRow){
            isFirstRow = false;
            continue;
          } else {
            String[] data = row.split(",");
            ArrayList<Company> companies = new ArrayList<>();
            for(int i = 1; i < data.length; i++) {
              if(!data[i].equals("")){
                if(data[i].toLowerCase().contains("sysco")){
                  companies.add(new Company(data[i], 12, 10));
                } else {
                  companies.add(new Company(data[i], 3, 2));
                }
                if(!companyNameList.contains(data[i])){
                  companyNameList.add(data[i]);
                }
              }
            }
            Company[] companiesArray = companies.toArray(new Company[0]);
            Student student = new Student(data[0], companiesArray);
            studentList.add(student);
          }
        }
      }
    }
  }

  public ArrayList<Student> getStudentList() {
    return studentList;
  }

  public ArrayList<String> getCompanyNameList() {
    return companyNameList;
  }
}
