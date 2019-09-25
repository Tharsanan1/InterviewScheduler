import java.util.concurrent.ConcurrentMap;

public class StudentCompany {
  private Student student;
  private Company company;
  private boolean assigned;

  public StudentCompany(Student student, Company company) {
    this.student = student;
    this.company = company;
    this.assigned = false;
  }

  public boolean isAssigned() {
    return assigned;
  }

  public void setAssigned(boolean assigned) {
    this.assigned = assigned;
  }

  public Student getStudent() {
    return student;
  }

  public Company getCompany() {
    return company;
  }
}
