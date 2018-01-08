package ch.rweiss.terminal.chart;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import ch.rweiss.terminal.chart.unit.Unit;

class TestUnit
{
  @Test
  void testToString()
  {
    assertThat(Unit.BYTES.toString()).isEqualTo("B (bytes)");
    assertThat(Unit.KILO_BYTES.toString()).isEqualTo("kB (kilo bytes)");
    assertThat(Unit.SECONDS.toString()).isEqualTo("s (seconds)");
    assertThat(Unit.MINUTES.toString()).isEqualTo("m (minutes)");
    assertThat(Unit.MILLI_SECONDS.toString()).isEqualTo("ms (milli seconds)");
  }
  
  @Test
  void symbol()
  {
    assertThat(Unit.BYTES.symbol()).isEqualTo("B");
    assertThat(Unit.KILO_BYTES.symbol()).isEqualTo("kB");
    assertThat(Unit.SECONDS.symbol()).isEqualTo("s");
    assertThat(Unit.MINUTES.symbol()).isEqualTo("m");
    assertThat(Unit.MILLI_SECONDS.symbol()).isEqualTo("ms");
  }

  @Test
  void scaleUp()
  {
    long value = Unit.BYTES.convertTo(1024*1024*1024, Unit.KILO_BYTES);
    assertThat(value).isEqualTo(1024*1024);
    value = Unit.BYTES.convertTo(1024*1024*1024, Unit.MEGA_BYTES);
    assertThat(value).isEqualTo(1024);
    value = Unit.BYTES.convertTo(1024*1024*1024, Unit.GIGA_BYTES);
    assertThat(value).isEqualTo(1);
  }
  
  @Test
  void scaleDown()
  {
    long value = Unit.MINUTES.convertTo(1, Unit.SECONDS);
    assertThat(value).isEqualTo(60);
    value = Unit.MINUTES.convertTo(1, Unit.MILLI_SECONDS);
    assertThat(value).isEqualTo(60*1000);
    value = Unit.MINUTES.convertTo(1, Unit.MICRO_SECONDS);
    assertThat(value).isEqualTo(60*1000*1000);
  }
}
