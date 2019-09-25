public class Assignment {
  private StudentCompany studentCompany;
  private int panelNumber;
  private TimeRange timeRange;

  public Assignment(StudentCompany student, int panelNumber, TimeRange timeRange) {
    this.studentCompany = student;
    this.panelNumber = panelNumber;
    this.timeRange = timeRange;
  }

  public StudentCompany getStudentCompany() {
    return studentCompany;
  }

  public int getPanelNumber() {
    return panelNumber;
  }

  public TimeRange getTimeRange() {
    return timeRange;
  }
}
