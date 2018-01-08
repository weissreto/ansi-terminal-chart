package ch.rweiss.terminal.chart.serie;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import ch.rweiss.terminal.Color;

public class DataSerie
{
  private Axis xAxis;
  private Axis yAxis;
  private List<DataPoint> dataPoints = new ArrayList<>();
  private Color color;
  
  public DataSerie(Axis xAxis, Axis yAxis, Color color)
  {
    this.xAxis = xAxis;
    this.yAxis = yAxis;
    this.color = color;
  }
  
  public DataPoint getDataPoint(int index)
  {
    return dataPoints.get(index);
  }
  
  public void addDataPoint(long x, long y)
  {
    dataPoints.add(new DataPoint(x, y));
  }
  
  public void addDataPoint(DataPoint dataPoint)
  {
    dataPoints.add(dataPoint);
  }
  
  
  public void removeDataPoint(int index)
  {
    dataPoints.remove(index);
  }
  
  public int size()
  {
    return dataPoints.size();
  }
  
  public long minXValue()
  {
    return xValues()
        .min()
        .orElseThrow(this::emptyDataSeriesException);
  }

  public long maxXValue()
  {
    return xValues()
        .max()
        .orElseThrow(this::emptyDataSeriesException);
  }

  public long minYValue()
  {
    return yValues()
        .min()
        .orElseThrow(this::emptyDataSeriesException);
  }

  public long maxYValue()
  {
    return yValues()
        .max()
        .orElseThrow(this::emptyDataSeriesException);
  }

  private LongStream xValues()
  {
    return dataPoints
        .stream()
        .mapToLong(dataPoint -> dataPoint.x());
  }

  private LongStream yValues()
  {
    return dataPoints
        .stream()
        .mapToLong(dataPoint -> dataPoint.y());
  }
  
  private NoSuchElementException emptyDataSeriesException()
  {
    return new NoSuchElementException("The data series is empty");
  }

  public Axis xAxis()
  {
    return xAxis;
  }

  public Axis yAxis()
  {
    return yAxis;
  }

  public Stream<DataPoint> getDataPointStream()
  {
    return dataPoints.stream();
  }

  public Color getColor()
  {
    return color;
  }
}
