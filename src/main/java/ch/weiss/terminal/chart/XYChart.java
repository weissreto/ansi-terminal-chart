package ch.weiss.terminal.chart;

import java.util.Arrays;
import java.util.List;

import ch.weiss.terminal.chart.serie.DataPoint;
import ch.weiss.terminal.chart.serie.DataSerie;
import ch.weiss.terminal.chart.serie.RollingTimeSerie;

public class XYChart
{
  private List<DataSerie> dataSeries;
  private Rectangle window;
  private Rectangle chart;
  private Graphic graphic = new Graphic();
  private DataPoint lastDataPoint;
  private String title;
  
  public XYChart(String title, Rectangle window, DataSerie... dataSeries)
  {
    this.title = title;
    this.window = window;
    this.chart = new Rectangle(window.leftX()+6, window.topY()+1, window.width()-6, window.height()-4);
    this.dataSeries = Arrays.asList(dataSeries);
  }
  
  public void paint()
  {        
    paintTitle();
    paintAxis();
    paintDataSeries();
    paintCurrentValues();
  }
  
  private void paintTitle()
  {
    graphic.foregroundColor(Color.BRIGHT_GREEN);
    graphic.drawTextUnderline(window.leftX()+(window.width()-title.length())/2, window.topY(), title);
  }

  private void paintAxis()
  {
    graphic.foregroundColor(Color.BRIGHT_GREEN);
    paintYAxis(dataSeries.get(0));
    paintXAxis(dataSeries.get(0));
  }

  private void paintYAxis(DataSerie dataSerie)
  {
    graphic.drawText(chart.leftX()-1, chart.topY(), "^");
    graphic.drawVerticalLine(chart.leftX()-1, chart.topY()+1, chart.height());
    
    if (dataSerie.size() > 1)
    {
      long maxYValue = maxYValue();
      scaleYAxises(maxYValue);
      String maxYValueAsText = dataSerie.yAxis().format(maxYValue);
      graphic.drawText(chart.leftX()-1-maxYValueAsText.length(), chart.topY(), maxYValueAsText);
      long minYValue = minYValue();
      String minYValueAsText = dataSerie.yAxis().format(minYValue);
      graphic.drawText(chart.leftX()-1-minYValueAsText.length(), chart.bottomY()+1, minYValueAsText);
    }
    
    String displayText = dataSerie.yAxis().scaledUnit().symbolWithBracesOrEmpty();
    if (displayText.length() <= 4)
    {
      int yPos = chart.topY()+chart.height()/2;
      int xPos = chart.leftX()-1-displayText.length();
      graphic.drawText(xPos, yPos, displayText);
    }
    else
    {
      int yPos = chart.topY()+(chart.height() - displayText.length())/2;
      int xPos = chart.leftX()-2;
      graphic.drawVerticalText(xPos, yPos, displayText);
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
    graphic.drawText(chart.leftX()-1, chart.bottomY()+1, "\u2514");
    graphic.drawHorizontalLine(chart.leftX(), chart.bottomY()+1, chart.width());
    graphic.drawText(chart.rightX(), chart.bottomY()+1, ">");
    
    String displayText = dataSerie.xAxis().displayText();
    int width = chart.width();
    int xPos = chart.leftX() + (width - displayText.length())/2;
    graphic.drawText(xPos, chart.bottomY()+2, displayText);
    
    if (dataSerie.size() > 0)
    {
      long minXValue = dataSerie.minXValue();
      String minXValueAsText = dataSerie.xAxis().format(minXValue);
      graphic.drawText(chart.leftX()-1, chart.bottomY()+2, minXValueAsText);
    }
    if (dataSerie.size() > 1)
    {
      long maxXValue = dataSerie.maxXValue();
      String maxXValueAsText = dataSerie.xAxis().format(maxXValue);
      graphic.drawText(chart.rightX()-maxXValueAsText.length()+1, chart.bottomY()+2, maxXValueAsText);
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
    graphic.foregroundColor(dataSerie.getColor());
    width = chart.width();
    height = chart.height();
    
    long deltaX = dataSerie.maxXValue() - dataSerie.minXValue();
    if (deltaX == 0)
    {
      deltaX = 1;
    }
    width = Math.min(width, dataSerie.size()); 
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
      graphic.drawLine(
          (int)lastDataPoint.x(), 
          (int)lastDataPoint.y(), 
          (int)dp.x(), 
          (int)dp.y(), 
          ".");
    }
    lastDataPoint = dp;
  }
  
  private void paintCurrentValues()
  {
    int xPos = chart.leftX()-1;
    for (DataSerie dataSerie: dataSeries)
    {
      if (dataSerie.size() > 0)
      {
        graphic.foregroundColor(dataSerie.getColor());
        long currentValue = dataSerie.getDataPoint(dataSerie.size()-1).y();
        String currentValueStr = dataSerie.yAxis().format(currentValue);
        currentValueStr = dataSerie.yAxis().symbol()+"="+currentValueStr+dataSerie.yAxis().scaledUnit().symbolWithBracesOrEmpty();
        graphic.drawText(xPos, window.bottomY(), currentValueStr);
        xPos = xPos + currentValueStr.length()+1;
      }
    }
    
  }
}
