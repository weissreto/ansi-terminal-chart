package ch.weiss.terminal.chart;

import ch.weiss.terminal.AnsiTerminal;

public class Graphic
{
  AnsiTerminal term = AnsiTerminal.get();
  public void drawText(int x, int y, String text)
  {
    int column = column(x);
    int line = line(y);
       
    term.cursor().position(line, column).write(text);
  }

  public void drawTextUnderline(int x, int y, String text)
  {
    term.style().underline();
    drawText(x, y, text);
    term.reset();
  }

  public void drawVerticalText(int x, int y, String text)
  {
    int column = column(x);
    int line = line(y);
    for (int pos = 0; pos < text.length(); pos++)
    {
      term.cursor().position(line+pos, column);
      term.write(text.charAt(pos));
    }
  }

  public void drawVerticalLine(int x, int y, int length)
  {
    int column = column(x);
    for (int line = line(y); line <= line(y+length); line++)
    {
      term.cursor().position(line, column);
      term.write("\u2502");
    }
  }
  
  public void drawHorizontalLine(int x, int y, int length)
  {
    StringBuilder line = new StringBuilder();
    for (int pos=0; pos <= length; pos++)
    {
      line.append("\u2500");
    }
    drawText(x, y, line.toString());
  }


  private int column(int x)
  {
    return x+1;
  }
  private int line(int y)
  {
    return y+1;
  }

  public void foregroundColor(Color color)
  {
    switch(color)
    {
      case BRIGHT_RED:
        term.color().brightRed();
        break;
      case BRIGHT_GREEN:
        term.color().brightGreen();
        break;
      case BRIGHT_BLUE:
        term.color().brightBlue();
        break;
      case BRIGHT_YELLOW:
        term.color().brightYellow();
        break;
    }
  }

  public void drawLine(int x1, int y1, int x2, int y2, String text)
  {
    int dx = Math.abs(x2 - x1);
    int dy = Math.abs(y2 - y1);

    int sx = (x1 < x2) ? 1 : -1;
    int sy = (y1 < y2) ? 1 : -1;

    int err = dx - dy;
    int x = x1;
    int y = y1;

    while (true) 
    {
      drawText(x, y, text);

      if (x == x2 && y == y2) 
      {
          break;
      }
      int e2 = 2 * err;
      if (e2 > -dy) 
      {
        err = err - dy;
        x = x + sx;
      }
      if (e2 < dx) 
      {
        err = err + dx;
        y = y + sy;
      }
    }
  }
}
