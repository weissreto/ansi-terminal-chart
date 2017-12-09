package ch.weiss.terminal.chart;

import java.io.IOException;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

@SuppressWarnings("restriction")
public class Jmx
{
  private MBeanServerConnection mBeanServer;
  private long newGcTime = -1;
  private long oldGcTime = -1;

  public Jmx()
  {
  }

  public long getHeapUsed() throws Exception
  {
    CompositeDataSupport memoryUsage = (CompositeDataSupport) mBeanServer.getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
    long used = (Long) memoryUsage.get("used");
    return used;
  }
  
  public long getCommitted() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException
  {
    CompositeDataSupport memoryUsage = (CompositeDataSupport) mBeanServer.getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
    long committed = (Long) memoryUsage.get("committed");
    return committed;
  }

  public long getNonHeapUsed() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException
  {
    CompositeDataSupport memoryUsage = (CompositeDataSupport) mBeanServer.getAttribute(new ObjectName("java.lang:type=Memory"), "NonHeapMemoryUsage");
    long used = (Long) memoryUsage.get("used");
    return used;
  }

  public long getNonHeapCommited() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException
  {
    CompositeDataSupport memoryUsage = (CompositeDataSupport) mBeanServer.getAttribute(new ObjectName("java.lang:type=Memory"), "NonHeapMemoryUsage");
    long used = (Long) memoryUsage.get("committed");
    return used;
  }

  public long getThreads() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException
  {
    return (Integer) mBeanServer.getAttribute(new ObjectName("java.lang:type=Threading"), "ThreadCount");
  }

  public long getSystemCpuLoad() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException
  {
    Double value = (Double) mBeanServer.getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "SystemCpuLoad");
    value = value * 100;
    return value.longValue();
  }

  public long getProcessCpuLoad() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException
  {
    Double value = (Double) mBeanServer.getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "ProcessCpuLoad");
    value = value * 100;
    return value.longValue();
  }

  public long getOldGcTime() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException
  {
    long time = (long) mBeanServer.getAttribute(new ObjectName("java.lang:type=GarbageCollector,name=G1 Old Generation"), "CollectionTime");
    long delta = 0;
    if (oldGcTime >= 0)
    {
      delta = time-oldGcTime;
    }
    oldGcTime = time;
    return delta;
  }

  public long getNewGcTime() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException
  {
    long time = (long) mBeanServer.getAttribute(new ObjectName("java.lang:type=GarbageCollector,name=G1 Young Generation"), "CollectionTime");
    long delta = 0;
    if (newGcTime >= 0)
    {
      delta = time - newGcTime;
    }
    newGcTime = time;
    return delta;
  }

  public long getTotalClassLoaded() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException
  {
    return (long) mBeanServer.getAttribute(new ObjectName("java.lang:type=ClassLoading"), "TotalLoadedClassCount");
  }

  public long getClassLoaded() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException
  {
    return (int) mBeanServer.getAttribute(new ObjectName("java.lang:type=ClassLoading"), "LoadedClassCount");
  }

  public long getClassUnloaded() throws AttributeNotFoundException, InstanceNotFoundException, MalformedObjectNameException, MBeanException, ReflectionException, IOException
  {
    return (long) mBeanServer.getAttribute(new ObjectName("java.lang:type=ClassLoading"), "UnloadedClassCount");
  }

  public List<VirtualMachineDescriptor> getAvailableVirtualMaschines()
  {
    return VirtualMachine.list();
  }
  
  public void connect(VirtualMachineDescriptor vmDescriptor) throws AttachNotSupportedException, IOException
  {    
    VirtualMachine vm = VirtualMachine.attach(vmDescriptor.id());
    String jmx = vm.startLocalManagementAgent();
    @SuppressWarnings("resource")
    JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(jmx));
    mBeanServer = connector.getMBeanServerConnection();
  }
}
