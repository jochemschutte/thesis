package architecture.demo.construct;

import static architecture.demo.global.Fields.MODEL_HIGH;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.shade.com.google.common.collect.ImmutableMap;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

import com.google.common.collect.ImmutableList;

import architecture.demo.global.ConfigurableTimer;
import architecture.demo.processes.Sensor;

public class Main{
	
	static AtomicBoolean running = new AtomicBoolean(false);
	
	public static void main(String[] args) {
		
		//one message per 5 seconds
//		run(5760, 20, 5);
		
		// one day per second
//		run(86400, 1, 5);
		
		//5 days per second
//		run(432000, 1, 5);
		
//		1 days per second, 3 sensors
		run(86400, 1000, 5);
		
//		3 hours per second
//		run(60*60*3, 20, 5);
		
//		runDebug(5, 10, 900000, 5);
	}
	
	private static void run(long speedfactor, int nrSensors, int nrSensorAnalysers) {
		if(running.getAndSet(true)) 
			throw new IllegalStateException("Simulator was initiated multiple times (comment a run-call)");
		ConfigurableTimer.setInstance(new ConfigurableTimer(speedfactor));
		Collection<Sensor> sensors = SensorEnvironment.construct(nrSensors);
		Sensor.sensorMap = sensors.stream().collect(Collectors.toMap(s->s.getSensorId(), s->s));
		TopologyBuilder topBuilder = BasicArchitectureEnvironment.construct(nrSensorAnalysers);

		Config conf = new Config();
		conf.setDebug(false);
		conf.setNumWorkers(2);

		LocalCluster cluster = new LocalCluster();
		SensorEnvironment.start(sensors);
		cluster.submitTopology("test", conf, topBuilder.createTopology());
		Utils.sleep(1000000);
		cluster.killTopology("test");
		cluster.shutdown();
		
	}
	
	private static void runDebug(double percentageLeft, double yearsRunning, long speedfactor, int nrProcessors) {
		if(running.getAndSet(true)) 
			throw new IllegalStateException("Simulator was initiated multiple times (comment a run-call)");
		
//		Sensor #3 switched to 'middle. Years running: 6,4, percentage left 47,2%'
		
		ConfigurableTimer.setInstance(new ConfigurableTimer(speedfactor));
		
		Sensor s = new Sensor(1, percentageLeft, 0.2, (Math.random()-0.5)/5, yearsRunning, MODEL_HIGH);
		Sensor.sensorMap = ImmutableMap.of(s.getSensorId(), s);
		
		BasicArchitectureEnvironment.construct(nrProcessors);
		SensorEnvironment.start(ImmutableList.of(s));
	}
}