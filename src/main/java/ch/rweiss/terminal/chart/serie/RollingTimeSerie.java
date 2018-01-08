package ch.rweiss.terminal.chart.serie;

import java.util.concurrent.TimeUnit;

import ch.rweiss.terminal.chart.format.ValueFormat;
import ch.rweiss.terminal.chart.unit.Unit;
import ch.rweiss.terminal.Color;

public class RollingTimeSerie extends DataSerie
{
  private long timeSpan;
  private TimeUnit timeSpanUnit;

  public RollingTimeSerie(Axis yAxis, long timeSpan, TimeUnit timeSpanUnit, Color color)
  {
    super(new Axis("t", Unit.DAY_TIME, "", ValueFormat.DAY_TIME), yAxis, color);
    this.timeSpan = timeSpan;
    this.timeSpanUnit = timeSpanUnit;
  }
  
  public void addDataPoint(long value)
  {
    removeToOldDataPoints(); 
    addDataPoint(System.currentTimeMillis(), value);
  }

  private void removeToOldDataPoints()
  {
    long minTime = System.currentTimeMillis() - timeSpanUnit.toMillis(timeSpan);
    while (size() > 0)
    {
      DataPoint dp = getDataPoint(0);
      if (dp.x() < minTime)
      {
        removeDataPoint(0);
      }
      else
      {
        break;
      }
    }
  }
}
