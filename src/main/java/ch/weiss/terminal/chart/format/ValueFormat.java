package ch.weiss.terminal.chart.format;

public interface ValueFormat
{
  public ValueFormat DAY_TIME = new DateValueFormat();
  public ValueFormat NUMBER = value -> Long.toString(value);
  
  public String format(long value);
}
