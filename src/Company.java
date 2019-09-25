public class Company {
  private String name;
  private int timeUnits;
  private int panels;
  public Company(String name, int timeUnits, int panels){
    this.name = name;
    this.timeUnits = timeUnits;
    this.panels = panels;
  }

  public String getName() {
    return name;
  }

  public int getTimeUnits() {
    return timeUnits;
  }


  public int getPanels() {
    return panels;
  }

  @Override
  public String toString(){
    return name;
  }
}
