public class Student {
  private String name;
  private Company[] companies;
  public Student(String name, Company[] companies) {
    this.name = name;
    this.companies = companies;
  }

  public String getName() {
    return name;
  }

  public Company[] getCompanies() {
    return companies;
  }



  @Override
  public String toString(){
    return name;
  }
}
