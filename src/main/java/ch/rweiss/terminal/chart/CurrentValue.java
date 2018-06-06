package ch.rweiss.terminal.chart;

import ch.rweiss.terminal.Color;
import ch.rweiss.terminal.chart.serie.DataSerie;
import ch.rweiss.terminal.graphics.Graphics;
import ch.rweiss.terminal.graphics.Point;

class CurrentValue
{
  private final String serie;
  private final String value;
  private final String unit;
  private final Color color;
  private int width;
  
  private CurrentValue(String serie, String value, String unit, Color color)
  {
    this.serie = serie;
    this.value = value;
    this.unit = unit;
    this.width = serie.length()+1+value.length()+unit.length();
    this.color = color;
  }

  void decreaseWidth()
  {
    width = width - 1;
  }

  static CurrentValue create(DataSerie dataSerie)
  {
    long currentValue = dataSerie.getDataPoint(dataSerie.size()-1).y();
    String currentValueStr = dataSerie.yAxis().format(currentValue);

    return new CurrentValue(
        dataSerie.yAxis().symbol(), 
        currentValueStr, 
        dataSerie.yAxis().scaledUnit().symbolWithBracesOrEmpty(), 
        dataSerie.getColor());
  }
  
  @Override
  public String toString()
  {
    if (value.length() > width)
    {
      return "";
    }
    if (value.length()+unit.length() > width)
    {
      return value;
    }
    if (value.length()+unit.length() + 2 > width)
    {
      return value+unit;
    }
    String serieAbbreviated = serie;
    if (serie.length()+1+value.length()+unit.length() > width)
    {
      serieAbbreviated = serie.substring(0, width - 1- value.length() - unit.length());
      serieAbbreviated = serieAbbreviated.trim();
    }
    return serieAbbreviated+"="+value+unit;
  }
  
  int width()
  {
    return toString().length();
  }

  Point paint(Graphics graphics, Point textStartPoint)
  {
    graphics.color(color);
    String valueStr = toString();
    graphics.drawText(textStartPoint, valueStr);
    return textStartPoint.move(valueStr.length()+1, 0);
  }
}