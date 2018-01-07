package ch.weiss.terminal.chart;

import java.util.Arrays;
import java.util.List;

import ch.weiss.terminal.AnsiTerminal;
import ch.weiss.terminal.Color;
import ch.weiss.terminal.FontStyle;
import ch.weiss.terminal.Style;
import ch.weiss.terminal.chart.serie.DataPoint;
import ch.weiss.terminal.chart.serie.DataSerie;
import ch.weiss.terminal.graphics.Graphics;
import ch.weiss.terminal.graphics.LineStyle;
import ch.weiss.terminal.graphics.Point;
import ch.weiss.terminal.graphics.Rectangle;

public class XYChart
{
  private List<DataSerie> dataSeries;
  private Rectangle window;
  private Rectangle chart;
  private Graphics graphics = AnsiTerminal.get().graphics();
  private DataPoint lastDataPoint;
  private String title;
  private Style titleStyle = Style.create().withColor(Color.BRIGHT_GREEN).withFontStyle(FontStyle.UNDERLINE).toStyle();
  private Style axisStyle = Style.create().withColor(Color.BRIGHT_GREEN).toStyle();
  
  public XYChart(String title, Rectangle window, DataSerie... dataSeries)
  {
    this.title = title;
    setWindow(window);
    this.dataSeries = Arrays.asList(dataSeries);
  }
  
  public void paint()
  {        
    graphics.reset();
    paintTitle();
    paintAxis();
    paintDataSeries();
    paintCurrentValues();
  }
  
  public void setWindow(Rectangle window)
  {
    this.window = window;
    this.chart = window.moveTopLeft(6, 1).moveBottomRight(0, -3);
  }

  private void paintTitle()
  {
    graphics.style(titleStyle);    
    graphics.drawText(new Point(window.leftX()+(window.width()-title.length())/2, window.topY()), title);
  }

  private void paintAxis()
  {
    graphics.style(axisStyle);
    paintYAxis(dataSeries.get(0));
    paintXAxis(dataSeries.get(0));
  }

  private void paintYAxis(DataSerie dataSerie)
  {
    graphics.drawCharacter(chart.topLeft().move(-1, 0), '^');
    graphics.drawVerticalLine(chart.topLeft().move(-1, 1), chart.height());
    
    if (dataSerie.size() > 1)
    {
      long maxYValue = maxYValue();
      scaleYAxises(maxYValue);
      String maxYValueAsText = dataSerie.yAxis().format(maxYValue);
      graphics.drawText(
          chart.topLeft().move(-1-maxYValueAsText.length(), 0), 
          maxYValueAsText);
      long minYValue = minYValue();
      String minYValueAsText = dataSerie.yAxis().format(minYValue);
      graphics.drawText(
          chart.bottomLeft().move(-1-minYValueAsText.length(), 1)
          ,minYValueAsText);
    }
    
    String displayText = dataSerie.yAxis().scaledUnit().symbolWithBracesOrEmpty();
    if (displayText.length() <= 4)
    {
      Point textStartPoint = chart.centerLeft().move(-1-displayText.length(), 0);
      graphics.drawText(textStartPoint, displayText);
    }
    else
    {
      Point textStartPoint = chart.centerLeft().move(-2, -displayText.length()/2);
      graphics.drawVerticalText(textStartPoint, displayText);
    }
  }

  private long maxYValue()
  {
    return dataSeries.stream().mapToLong(serie -> serie.maxYValue()).max().orElse(0);
  }

  private long minYValue()
  {
    return dataSeries.stream().mapToLong(serie -> serie.minYValue()).min().orElse(0);
  }

  private void scaleYAxises(long maxYValue)
  {
    dataSeries.forEach(serie->serie.yAxis().scale(maxYValue));
  }


  private void paintXAxis(DataSerie dataSerie)
  {
    graphics.drawCharacter(chart.bottomLeft().move(-1, 1), LineStyle.SINGLE_LINE.bottomLeft());
    graphics.drawHorizontalLine(chart.bottomLeft().move(0, 1), chart.width());
    graphics.drawCharacter(chart.bottomRight().move(0, 1), '>');
    
    String displayText = dataSerie.xAxis().displayText();
    Point textStartPoint = chart.centerBottom().move(-displayText.length()/2, 2);
    graphics.drawText(textStartPoint, displayText);
    
    if (dataSerie.size() > 0)
    {
      long minXValue = dataSerie.minXValue();
      String minXValueAsText = dataSerie.xAxis().format(minXValue);
      graphics.drawText(chart.bottomLeft().move(-1, 2), minXValueAsText);
    }
    if (dataSerie.size() > 1)
    {
      long maxXValue = dataSerie.maxXValue();
      String maxXValueAsText = dataSerie.xAxis().format(maxXValue);
      graphics.drawText(
          chart.bottomRight().move(-maxXValueAsText.length()+1, 2), 
          maxXValueAsText);
    }
  }
  
  private void paintDataSeries()
  {
    dataSeries.forEach(this::paintDataSerie);
  }

  private void paintDataSerie(DataSerie dataSerie)
  {
    int width;
    int height;
    graphics.lineStyle(LineStyle.DOT);
    graphics.color(dataSerie.getColor());
    width = chart.width();
    height = chart.height();
    
    long deltaX = dataSerie.maxXValue() - dataSerie.minXValue();
    if (deltaX == 0)
    {
      deltaX = 1;
    }
//    width = Math.min(width, dataSerie.size()); 
    double xScale = deltaX / width;
    
    long deltaY = maxYValue() - minYValue();
    if (deltaY == 0)
    {
      deltaY = 1;
    }
    double yScale = deltaY / height;
    lastDataPoint = null;
    dataSerie.getDataPointStream()
      .map(dataPoint -> transform(dataSerie, dataPoint, xScale, yScale))
      .forEach(this::paint);
  }

  private DataPoint transform(DataSerie dataSerie, DataPoint dataPoint, double xScale, double yScale)
  {
    long x = (long)((dataPoint.x() - dataSerie.minXValue())/xScale);
    x = Math.min(x, chart.width());
    x = chart.leftX() + x;
    
    long y = (long)((dataPoint.y() - minYValue())/yScale);
    y = Math.min(y, chart.height());
    y = chart.bottomY() - y;
    return new DataPoint(x, y);
  }
  
  private void paint(DataPoint dp)
  {
    if (lastDataPoint != null)
    {
      if (dp.equals(lastDataPoint))
      {
        return;
      }
      graphics.drawLine(
          new Point((int)lastDataPoint.x(),(int)lastDataPoint.y()), 
          new Point((int)dp.x(), (int)dp.y()));
    }
    lastDataPoint = dp;
  }
  
  private void paintCurrentValues()
  {
    Point textStartPoint = new Point(chart.leftX()-1, window.bottomY());
    for (DataSerie dataSerie: dataSeries)
    {
      if (dataSerie.size() > 0)
      {
        graphics.color(dataSerie.getColor());
        long currentValue = dataSerie.getDataPoint(dataSerie.size()-1).y();
        String currentValueStr = dataSerie.yAxis().format(currentValue);
        currentValueStr = dataSerie.yAxis().symbol()+"="+currentValueStr+dataSerie.yAxis().scaledUnit().symbolWithBracesOrEmpty();
        graphics.drawText(textStartPoint, currentValueStr);
        textStartPoint = textStartPoint.move(currentValueStr.length()+1, 0);
      }
    }    
  }
}
