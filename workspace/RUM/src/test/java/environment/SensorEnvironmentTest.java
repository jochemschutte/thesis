package environment;

import java.util.Collection;

import org.junit.Test;

import architecture.demo.construct.SensorEnvironment;
import architecture.demo.processes.Sensor;

public class SensorEnvironmentTest{
	
	@Test
	public void run() {
		Collection<Sensor> sensors = SensorEnvironment.construct(100);
		sensors.forEach(s->System.out.println(s.getPercentageLeft()));
	}
}