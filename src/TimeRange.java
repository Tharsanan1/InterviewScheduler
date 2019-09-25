public class TimeRange {
  private int timeStart;
  private int timeEnd;
  public TimeRange(int timeStart, int timeEnd){
    this.timeStart = timeStart;
    this.timeEnd = timeEnd;
  }


  public int getTimeStart() {
    return timeStart;
  }

  public int getTimeEnd() {
    return timeEnd;
  }

  public static boolean clashes(TimeRange timeRange, TimeRange timeRange1) {
    if(timeRange.getTimeStart() >= timeRange1.getTimeStart() && timeRange.getTimeStart() < timeRange1.getTimeEnd()){
      return true;
    }
    else if(timeRange.getTimeEnd() > timeRange1.getTimeStart() && timeRange.getTimeEnd() <= timeRange1.getTimeEnd()){
      return true;
    }
    return false;
  }

  @Override
  public String toString(){
    return "time between : " + getTimeStart() + " : " + getTimeEnd();
  }
}
