package ch.rweiss.terminal.chart.serie;

import ch.rweiss.terminal.chart.format.ValueFormat;
import ch.rweiss.terminal.chart.unit.Unit;

public class Axis
{
  private final String name;
  private final Unit unit;
  private Unit scaledUnit;
  private final String description;
  private ValueFormat format;
    
  public Axis(String name, Unit unit)
  {
    this(name, unit, "");
  } 
  
  public Axis(String name, Unit unit, String description)
  {
    this(name, unit, description, ValueFormat.NUMBER);
  }
  
  public Axis(String name, Unit unit, String description, ValueFormat format)
  {
    this.name = name;
    this.unit = unit;
    this.scaledUnit = unit;
    this.description = description;
    this.format = format;
  }
  
  public String description()
  {
    return description;
  }
  
  public String symbol()
  {
    return name;
  }
  
  public Unit unit()
  {
    return unit;
  }
  
  public Unit scaledUnit()
  {
    return scaledUnit;
  }
  
  public String format(long value)
  {
    long scaledValue = unit.convertTo(value, scaledUnit);
    return format.format(scaledValue);
  }

  public String displayText()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(name);
    if (!unit.symbol().isEmpty())
    {
      builder.append(" [");
      builder.append(scaledUnit.symbol());
      builder.append("]");
    }
    if (!description.isEmpty())
    {
      builder.append(" (");      
      builder.append(description);
      builder.append(")");
    }
    return builder.toString();
  }

  public void scale(long value)
  {
    Unit newScaledUnit = scaledUnit;
    do
    {
      scaledUnit = newScaledUnit;
      String scaledValueStr = format(value);
      int length = scaledValueStr.length();
      if (length > 4)
      {
        newScaledUnit = scaledUnit.scaleUp();
      }
      if (length < 2)
      {
        newScaledUnit = scaledUnit.scaleDown();
      }
    } while (newScaledUnit != scaledUnit);    
  }
}
