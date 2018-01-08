package ch.rweiss.terminal.chart.unit;

import ch.rweiss.check.Check;

public class Unit
{
  private BaseUnit baseUnit;
  private Scale scale;

  public static final Unit BYTES = new Unit(BaseUnit.BYTES);
  public static final Unit KILO_BYTES = BYTES.scaleUp();
  public static final Unit MEGA_BYTES = KILO_BYTES.scaleUp();
  public static final Unit GIGA_BYTES = MEGA_BYTES.scaleUp();
  public static final Unit TERA_BYTES = GIGA_BYTES.scaleUp();
  
  public static final Unit SECONDS = new Unit(BaseUnit.SECONDS);
  public static final Unit MINUTES = SECONDS.scaleUp();
  public static final Unit HOURS = MINUTES.scaleUp();
  public static final Unit DAYS = HOURS.scaleUp();
  public static final Unit MILLI_SECONDS = SECONDS.scaleDown();
  public static final Unit MICRO_SECONDS = MILLI_SECONDS.scaleDown();
  public static final Unit NANO_SECONDS = MICRO_SECONDS.scaleDown();
  
  public static final Unit DAY_TIME = new Unit(BaseUnit.DAY_TIME);
  public static final Unit NONE = new Unit(BaseUnit.NONE);
  public static final Unit PERCENTAGE = new Unit(BaseUnit.PERCENTAGE);
  
  private Unit(BaseUnit baseUnit)
  {
    this.baseUnit = baseUnit;
    this.scale = baseUnit.getScaling().getNormal();
  }

  private Unit(BaseUnit baseUnit, Scale scale)
  {
    this.baseUnit = baseUnit;
    this.scale = scale;
  }

  public Unit scaleUp()
  {
    Scale upScale = scale.scaleUp();
    if (upScale == null)
    {
      return this;
    }
    return new Unit(baseUnit, scale.scaleUp());
  }

  public Unit scaleDown()
  {
    Scale downScale = scale.scaleDown();
    if (downScale == null)
    {
      return this;
    }
    return new Unit(baseUnit, scale.scaleDown());
  }

  public String symbol()
  {
    return scale.enhanceSymbol(baseUnit.getSymbol()); 
  }
  
  public String symbolWithBracesOrEmpty()
  {
    String symbol = symbol();
    if (symbol.isEmpty())
    {
      return symbol;
    }
    return "["+symbol+"]";
  }
  
  public long convertTo(long value, Unit toUnit)
  {
    return scale.converTo(value, toUnit.scale);
  }
  
  @Override
  public String toString()
  {
    return scale.enhanceSymbol(baseUnit.getSymbol()) +" ("+scale.enhanceName(baseUnit.getName())+")";
  }
  
  public static Unit fromSymbol(String symbol)
  {
    Check.parameter("symbol").withValue(symbol).isNotBlank();
    
    BaseUnit baseUnit  = BaseUnit.fromSymbol(symbol);
    if (baseUnit == null)
    {
      baseUnit = BaseUnit.NONE;
    }
    Scale scale = baseUnit.scaleFromSymbol(symbol);
    if (scale == null)
    {
      return null;
    }
    return new Unit(baseUnit, scale);
  }
  
}
