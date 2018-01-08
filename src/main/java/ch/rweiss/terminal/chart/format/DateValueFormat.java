package ch.rweiss.terminal.chart.format;

import java.text.DateFormat;
import java.util.Date;

class DateValueFormat implements ValueFormat
{

  @Override
  public String format(long value)
  {
    Date time = new Date(value);
    DateFormat timeInstance = DateFormat.getTimeInstance(DateFormat.MEDIUM);
    return timeInstance.format(time);
  }

}
