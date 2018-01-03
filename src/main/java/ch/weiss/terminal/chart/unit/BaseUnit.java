package ch.weiss.terminal.chart.unit;

public class BaseUnit
{
  private final String symbol;
  private final String name;
  private final Scaling scaling;

  static final BaseUnit SECONDS = new BaseUnit("s", "seconds", Scaling.TIME);
  static final BaseUnit BYTES = new BaseUnit("B", "bytes", Scaling.MEMORY);
  static final BaseUnit DAY_TIME = new BaseUnit("", "", Scaling.DAY_TIME);
  static final BaseUnit NONE = new BaseUnit("", "", Scaling.METRIC);
  static final BaseUnit PERCENTAGE = new BaseUnit("%", "percent", Scaling.NONE);
  
  private static final BaseUnit[] BASE_UNITS = new BaseUnit[] {SECONDS, BYTES, DAY_TIME, PERCENTAGE, NONE};

  private BaseUnit(String symbol, String name, Scaling scaling)
  {
    this.symbol = symbol;
    this.name = name;
    this.scaling = scaling;
  }

  Scaling getScaling()
  {
    return scaling;
  }

  String getSymbol()
  {    
    return symbol;
  }
  
  String getName()
  {
    return name;
  }
  
  @Override
  public String toString()
  {
    return symbol +" ("+name+")";
  }

  public static BaseUnit fromSymbol(String symbol)
  {
    for (BaseUnit baseUnit : BASE_UNITS)
    {
      if (!baseUnit.getSymbol().isEmpty() && symbol.endsWith(baseUnit.getSymbol()))
      {
        return baseUnit;
      }
    }
    return null;
  }

  public Scale scaleFromSymbol(String symbolWithScale)
  {
    for (Scale scale : getScaling().getScales())
    {
      if (scale.enhanceSymbol(symbol).equals(symbolWithScale))
      {
        return scale;
      }
    }
    return null;
  }
}
