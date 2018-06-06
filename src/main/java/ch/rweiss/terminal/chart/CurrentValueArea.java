package ch.rweiss.terminal.chart;

import java.util.List;
import java.util.stream.Collectors;

import ch.rweiss.terminal.chart.serie.DataSerie;
import ch.rweiss.terminal.graphics.Graphics;
import ch.rweiss.terminal.graphics.Point;

class CurrentValueArea
{
  private final Point left;
  private final int availableWidth;
  private final List<DataSerie> dataSeries;

  public CurrentValueArea(Point left, int availableWidth, List<DataSerie> dataSeries)
  {
    this.left = left;
    this.availableWidth = availableWidth;
    this.dataSeries = dataSeries;
  }

  public void paint(Graphics graphics)
  {

    List<CurrentValue> currentValues = dataSeries
        .stream()
        .filter(serie -> serie.size()>0)
        .map(CurrentValue::create)
        .collect(Collectors.toList());    
    
    int width = getWidth(currentValues);
    while (width > availableWidth)
    {
      CurrentValue widest = getWidest(currentValues);
      widest.decreaseWidth();
      width = getWidth(currentValues);
    }
    
    Point textStartPoint = left;
    for (CurrentValue value : currentValues)
    {
      textStartPoint = value.paint(graphics, textStartPoint);
    }    
  }
  
  private static int getWidth(List<CurrentValue> currentValues)
  {
    int width = currentValues
        .stream()
        .mapToInt(CurrentValue::width)
        .sum();
    if (currentValues.size() >= 2)
    {
      width = width + currentValues.size()-1;
    }
    return width;
  }
  
  private static CurrentValue getWidest(List<CurrentValue> currentValues)
  {
    CurrentValue max = currentValues.get(0);
    for (CurrentValue value : currentValues)
    {
      if (value.width() > max.width())
      {
        max = value;
      }
    }
    return max;
  }

}
