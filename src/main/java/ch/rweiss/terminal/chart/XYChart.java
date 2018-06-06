package ch.rweiss.terminal.chart;

import java.util.Arrays;
import java.util.List;

import ch.rweiss.terminal.Color;
import ch.rweiss.terminal.FontStyle;
import ch.rweiss.terminal.Style;
import ch.rweiss.terminal.chart.serie.DataPoint;
import ch.rweiss.terminal.chart.serie.DataSerie;
import ch.rweiss.terminal.graphics.Graphics;
import ch.rweiss.terminal.graphics.LineStyle;
import ch.rweiss.terminal.graphics.Point;
import ch.rweiss.terminal.graphics.Rectangle;
import ch.rweiss.terminal.widget.Widget;

public class XYChart extends Widget
{
  private List<DataSerie> dataSeries;
  private Rectangle chart;
  private DataPoint lastDataPoint;
  private final String title;
  private final Style titleStyle = Style.create().withColor(Color.BRIGHT_GREEN).withFontStyle(FontStyle.UNDERLINE).toStyle();
  private final Style axisStyle = Style.create().withColor(Color.BRIGHT_GREEN).toStyle();
  
  public XYChart(String title, Rectangle bounds, DataSerie... dataSeries)
  {
    this.title = title;
    bounds(bounds);
    this.dataSeries = Arrays.asList(dataSeries);
  }
  
  @Override
  public void paint(Graphics graphics)
  {        
    graphics.reset();
    paintTitle(graphics);
    if (dataSeries.isEmpty())
    {
      paintNoData(graphics);
    }
    else
    {
      paintAxis(graphics);
      paintDataSeries(graphics);
      paintCurrentValues(graphics);
    }
  }

  private void paintNoData(Graphics graphics)
  {
    graphics.reset();
    String noData = "No data";
    graphics.color(Color.YELLOW);
    graphics.drawText(bounds().center().move(-noData.length()/2,0), noData);
  }
  
  @Override
  public void bounds(Rectangle bounds)
  {
    super.bounds(bounds);
    this.chart = bounds.moveTopLeft(6, 1).moveBottomRight(0, -3);
  }

  private void paintTitle(Graphics graphics)
  {
    graphics.style(titleStyle);
    String text = title;
    if (text.length() > bounds().width())
    {
      text = text.substring(0, bounds().width());
    }
    graphics.drawText(new Point(bounds().leftX()+(bounds().width()-text.length())/2, bounds().topY()), text);
  }

  private void paintAxis(Graphics graphics)
  {
    graphics.style(axisStyle);
    paintYAxis(graphics, dataSeries.get(0));
    paintXAxis(graphics, dataSeries.get(0));
  }

  private void paintYAxis(Graphics graphics, DataSerie dataSerie)
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


  private void paintXAxis(Graphics graphics, DataSerie dataSerie)
  {
    graphics.drawCharacter(chart.bottomLeft().move(-1, 1), LineStyle.SINGLE_LINE.bottomLeft());
    graphics.drawHorizontalLine(chart.bottomLeft().move(0, 1), chart.width());
    graphics.drawCharacter(chart.bottomRight().move(0, 1), '>');
    
    String centerText = dataSerie.xAxis().displayText();
    String leftText = "";
    String rightText = "";
    if (dataSerie.size() > 0)
    {
      long minXValue = dataSerie.minXValue();
      leftText = dataSerie.xAxis().format(minXValue);
    }
    if (dataSerie.size() > 1)
    {
      long maxXValue = dataSerie.maxXValue();
      rightText = dataSerie.xAxis().format(maxXValue);
    }
    
    Point center = chart.centerBottom().move(0, 2);
    Point left = chart.bottomLeft().move(-1, 2);
    Point right = chart.bottomRight().move(0, 2);
    
    if (leftText.length() + 1 + centerText.length()/2  > center.x() - left.x() ||
        centerText.length()/2 + 1 + rightText.length() > right.x() - center.x())
    {
      centerText = "";
      if (leftText.length() + rightText.length() + 1 > right.x() - left.x())
      {
        leftText = "";
        if (rightText.length() > right.x() - left.x())
        {
          rightText = "";
        }
      }
    }
    
    if (!leftText.isEmpty())
    {
      graphics.drawText(left, leftText);
    }
    if (!centerText.isEmpty())
    {
      graphics.drawText(center.move(-centerText.length()/2, 0), centerText);
    }
    if (!rightText.isEmpty())
    {
      graphics.drawText(right.move(-rightText.length()+1, 0), rightText);
    }    
  }
  
  private void paintDataSeries(Graphics graphics)
  {
    dataSeries.forEach(ds -> paintDataSerie(graphics, ds));
  }

  private void paintDataSerie(Graphics graphics, DataSerie dataSerie)
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
    double xScale = (double)deltaX / (double)width;
    
    long deltaY = maxYValue() - minYValue();
    if (deltaY == 0)
    {
      deltaY = 1;
    }
    double yScale = (double)deltaY / (double)height;
    lastDataPoint = null;
    dataSerie.getDataPointStream()
      .map(dataPoint -> transform(dataSerie, dataPoint, xScale, yScale))
      .forEach(dp -> paint(graphics, dp));
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
  
  private void paint(Graphics graphics, DataPoint dp)
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
  
  private void paintCurrentValues(Graphics graphics)
  {
    CurrentValueArea area = new CurrentValueArea(
        new Point(chart.leftX()-1, bounds().bottomY()), 
        chart.width()+2, 
        dataSeries);
    area.paint(graphics);
  }
}