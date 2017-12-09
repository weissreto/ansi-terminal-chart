package ch.weiss.terminal.chart;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachineDescriptor;

import ch.weiss.terminal.AnsiTerminal;
import ch.weiss.terminal.Color;
import ch.weiss.terminal.chart.serie.Axis;
import ch.weiss.terminal.chart.serie.RollingTimeSerie;
import ch.weiss.terminal.chart.unit.Unit;
import ch.weiss.terminal.graphics.Point;
import ch.weiss.terminal.graphics.Rectangle;

@SuppressWarnings("restriction")
public class Test
{
  private AnsiTerminal term = AnsiTerminal.get();
  private Jmx jmx = new Jmx(); 
  
  public static void main(String[] args) throws Exception
  { 
    new Test().main();    
  }

  private void main() throws Exception
  {
    connectToJavaVirtualMaschine();
    drawCharts();
  }

  private void drawCharts() throws Exception
  {
    term.clear();
    RollingTimeSerie heapUsed = new RollingTimeSerie(new Axis("Used", Unit.BYTES, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_RED);
    RollingTimeSerie heapCommitted = new RollingTimeSerie(new Axis("Committed", Unit.BYTES, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_BLUE);
    XYChart heapMemory = new XYChart("Heap Memory", new Rectangle(new Point(0, 0), 60, 20), heapUsed, heapCommitted);
    RollingTimeSerie nonHeapUsed = new RollingTimeSerie(new Axis("Used", Unit.BYTES, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_RED);
    RollingTimeSerie nonHeapCommitted = new RollingTimeSerie(new Axis("Commited", Unit.BYTES, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_BLUE);
    XYChart nonHeapMemory = new XYChart("Non Heap Memory", new Rectangle(new Point(62, 0), 60, 20), nonHeapUsed, nonHeapCommitted);

    RollingTimeSerie threads = new RollingTimeSerie(new Axis("Threads", Unit.NONE, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_RED);
    XYChart thread = new XYChart("Threads", new Rectangle(new Point(0, 22), 60, 20), threads);

    RollingTimeSerie systemCpuUsage = new RollingTimeSerie(new Axis("System CPU Load", Unit.PERCENTAGE, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_RED);
    RollingTimeSerie processCpuUsage = new RollingTimeSerie(new Axis("Process CPU Load", Unit.PERCENTAGE, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_BLUE);
    XYChart cpu = new XYChart("CPU", new Rectangle(new Point(62, 22), 60, 20), systemCpuUsage, processCpuUsage);

    RollingTimeSerie oldGcTime = new RollingTimeSerie(new Axis("Old", Unit.MILLI_SECONDS, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_RED);
    RollingTimeSerie newGcTime = new RollingTimeSerie(new Axis("New", Unit.MILLI_SECONDS, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_BLUE);
    XYChart gc = new XYChart("Garbage Collector", new Rectangle(new Point(124, 0), 60, 20), oldGcTime, newGcTime);

    RollingTimeSerie totalClassLoaded = new RollingTimeSerie(new Axis("Total Loaded", Unit.NONE, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_RED);
    RollingTimeSerie classLoaded = new RollingTimeSerie(new Axis("Loaded", Unit.NONE, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_BLUE);
    RollingTimeSerie classUnloaded = new RollingTimeSerie(new Axis("Unloaded", Unit.NONE, ""), 10, TimeUnit.MINUTES, Color.BRIGHT_YELLOW);
    XYChart classLoading = new XYChart("Class Loading", new Rectangle(new Point(124, 22), 60, 20), totalClassLoaded, classLoaded, classUnloaded);

    while (true)
    {      
      term.clear();
      heapUsed.addDataPoint(jmx.getHeapUsed());
      heapCommitted.addDataPoint(jmx.getCommitted());
      nonHeapUsed.addDataPoint(jmx.getNonHeapUsed());
      nonHeapCommitted.addDataPoint(jmx.getNonHeapCommited());
      threads.addDataPoint(jmx.getThreads());
      processCpuUsage.addDataPoint(jmx.getProcessCpuLoad());
      systemCpuUsage.addDataPoint(jmx.getSystemCpuLoad());
      oldGcTime.addDataPoint(jmx.getOldGcTime());
      newGcTime.addDataPoint(jmx.getNewGcTime());
      totalClassLoaded.addDataPoint(jmx.getTotalClassLoaded());
      classLoaded.addDataPoint(jmx.getClassLoaded());
      classUnloaded.addDataPoint(jmx.getClassUnloaded());
      heapMemory.paint();
      nonHeapMemory.paint();
      thread.paint();
      cpu.paint();
      gc.paint();
      classLoading.paint();
      Thread.sleep(1000);
    }
  }

  private void connectToJavaVirtualMaschine() throws AttachNotSupportedException, IOException
  {
    term.clear();
    term.cursor().position(1,1);
    term.color().brightGreen().newLine().newLine();
    term.write(" -----------").newLine();
    term.write("| ConsoleVM |").newLine();
    term.write(" -----------").newLine();
    term.newLine();
    term.newLine();
    term.color().brightGreen().fontStyle().underline().write("Available Virtual Maschines:").newLine().newLine().reset();
    int pos = 0;
    List<VirtualMachineDescriptor> vmDescriptors = jmx.getAvailableVirtualMaschines();
    for (VirtualMachineDescriptor vmDescriptor : vmDescriptors)
    {
      term.color().brightRed().write("[").write(pos++).write("]").write(" ")
          .color().brightYellow().write(vmDescriptor.displayName())
          .write(" (").write(vmDescriptor.id()).write(")").newLine();
    }
    term.newLine();
    term.color().brightGreen().write("Enter the number of the virtual maschine you want to connect to: ");
    term.color().brightRed();
    try(Scanner scanner = new Scanner(System.in))
    {
      pos = scanner.nextInt();
      jmx.connect(vmDescriptors.get(pos));
    }
  }
}
