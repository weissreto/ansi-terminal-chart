package ch.weiss.terminal.chart;

public class Rectangle
{
  private int leftX;
  private int topY;
  private int width;
  private int height;
  
  public Rectangle(int leftX, int topY, int width, int height)
  {
    this.leftX = leftX;
    this.topY = topY;
    this.width = width;
    this.height = height;
  }
  
  public int width()
  {
    return width;
  }
  
  public int height()
  {
    return height;
  }
  
  public int leftX()
  {
    return leftX;
  }
  
  public int rightX()
  {
    return leftX()+width();
  }
  
  public int topY()
  {
    return topY;
  }
  
  public int bottomY()
  {
    return topY()+height();
  }
  
}
