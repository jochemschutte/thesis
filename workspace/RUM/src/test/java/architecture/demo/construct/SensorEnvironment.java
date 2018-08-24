package architecture.demo.construct;

import static architecture.demo.global.Fields.MODEL_HIGH;

import java.util.Collection;
import java.util.HashSet;

import architecture.demo.processes.Sensor;

public class SensorEnvironment{
	
	public static Collection<Sensor> construct(int nrNodes) {
		Collection<Sensor> result = new HashSet<>();
		for(int i = 0; i < nrNodes; i++) {
			double runtimeLeft = Math.random()*5 + 5;
			Sensor s = new Sensor(i+1, 100.0*runtimeLeft/10, 0.2, (Math.random()-0.5)/5, 10-runtimeLeft, MODEL_HIGH);
			result.add(s);
		}
		return result;
	}
	
	public static void start(Collection<Sensor> sensors) {
		sensors.forEach(s-> new Thread(s).start());
	}
}