package architecture.demo.construct;

import static architecture.demo.global.Fields.MODEL_HIGH;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import architecture.demo.global.ConfigurableTimer;
import architecture.demo.processes.DebugLog;
import architecture.demo.processes.Sensor;

public class Main{
	
	
	public static void main(String[] args) {
		DebugLog log = new DebugLog();
		log.subscribe("DEBUG");
		
		// one day per second
//		run(86400, 1, 5);
		
		//~11.5 days per second
//		run(1000000, 1, 5);
		
		//2 days per second, 3 sensors
//		run(172800, 3, 5);
		
		runDebug(5, 10, 900000, 5);
	}
	
	private static void run(long speedfactor, int nrSensors, int nrSensorAnalysers) {
		ConfigurableTimer.setInstance(new ConfigurableTimer(speedfactor));
		Collection<Sensor> sensors = SensorEnvironment.construct(nrSensors);
		BasicArchitectureEnvironment.construct(sensors,nrSensorAnalysers);
		SensorEnvironment.start(sensors);
	}
	
	private static void runDebug(double percentageLeft, double yearsRunning, long speedfactor, int nrProcessors) {
//		Sensor #3 switched to 'middle. Years running: 6,4, percentage left 47,2%'
		
		ConfigurableTimer.setInstance(new ConfigurableTimer(speedfactor));
		
		Sensor s = new Sensor(1, percentageLeft, 0.2, (Math.random()-0.5)/5, yearsRunning, MODEL_HIGH);
		
		BasicArchitectureEnvironment.construct(ImmutableList.of(s), nrProcessors);
		SensorEnvironment.start(ImmutableList.of(s));
	}
}