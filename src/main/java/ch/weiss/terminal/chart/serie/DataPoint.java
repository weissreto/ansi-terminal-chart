package ch.weiss.terminal.chart.serie;

public class DataPoint
{
  private final long x;
  private final long y;
  
  public DataPoint(long x, long y)
  {
    this.x = x;
    this.y = y;
  }
  
  public long x()
  {
    return x;
  }
  
  public long y()
  {
    return y;
  }
}
