package architecture.demo.processes;

import static architecture.demo.global.Fields.COMPOSER;
import static architecture.demo.global.Fields.MODEL_HIGH;
import static architecture.demo.global.Fields.MODEL_LOW;
import static architecture.demo.global.Fields.MODEL_MIDDLE;
import static architecture.demo.global.Fields.PERCENTAGE_LEFT;
import static architecture.demo.global.Fields.SENSOR_ID;
import static architecture.demo.global.Fields.TIMESTAMP;
import static architecture.demo.global.Fields.YEARS_RUNNING;
import static architecture.demo.global.Topics.DEBUG;
import static architecture.demo.global.Topics.MESSAGE;
import static architecture.demo.global.Topics.SEVERITY;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.collect.ImmutableMap;

import architecture.components.RumProcessor;
import architecture.demo.global.ConfigurableTimer;
import io.message.IOMessage;
import mock.kafka.MockKafkaProducer;

public class Sensor extends MockKafkaProducer implements Runnable{
	
	public static ConcurrentLinkedQueue<IOMessage> sensorQueue = new ConcurrentLinkedQueue<>();
	public static Map<Integer, Sensor> sensorMap = new TreeMap<>();
	
//	public static double PERCENTAGE_USED_PER_DAY = 0.027; //equivalent to 10 years + a bit
	public static double PERCENTAGE_USED_PER_SEND = 0.02; //equivalent with 13.7 years on 'low'
	public ObjectMapper mapper = new ObjectMapper();
	
	int sensorId;
	double batteryPercentage;
	double percentageUsedPerDayDeviation;
	double standardDeviation;
	double yearsRunning;
	String currentModel;
	double wait;
	
	public Sensor(int sensorId, double batteryPercentage, double percentageUsedPerDayDeviation, double standardDeviation,
			double yearsRunning, String currentModel) {
		super();
		this.sensorId = sensorId;
		this.batteryPercentage = batteryPercentage;
		this.standardDeviation = standardDeviation;
		this.percentageUsedPerDayDeviation = percentageUsedPerDayDeviation;
		this.yearsRunning = yearsRunning;
		this.currentModel = currentModel;
	}
	
	public int getSensorId() {
		return this.sensorId;
	}
	
	public String getCurrentModel() {
		return this.currentModel;
	}
	
	public double getPercentageLeft() {
		return this.batteryPercentage;
	}
	
	public void setCurrentModel(String nextModel) {
		if(!this.currentModel.equals(nextModel)) {
			this.currentModel = nextModel;
			publish("DEBUG", new IOMessage(ImmutableMap.of(SEVERITY, "INFO", "LABEL", "CHANGE_RUM", MESSAGE, //
					String.format("Sensor #%d switched to '%s'. Years running: %.1f, percentage left %.1f%%'", sensorId, nextModel,yearsRunning, batteryPercentage))));
		}
	}
	
	@Override
	public void run() {
		long initialWait = (long)(86400000*Math.random()/ConfigurableTimer.getInstance().getSpeedFactor());
		Map<String, String> debug = ImmutableMap.of(MESSAGE, 
				String.format("Sensor #%s initiated. starting percentage: %.1f%%, years running: %.1f, waiting %.1f (simulated) seconds", sensorId, batteryPercentage, yearsRunning, (double)initialWait/1000.0));
		publish(DEBUG, new IOMessage(debug));
		try {
			Thread.sleep(initialWait);
		}catch(InterruptedException e) {}
		publish(DEBUG, new IOMessage(ImmutableMap.of(MESSAGE, String.format("Sensor #%d started.", sensorId))));
		while(batteryPercentage > 0) {
			switch(currentModel) {
			case MODEL_HIGH:
				wait = 0.3*(1+(Math.random()-0.5)*0.2);
				break;
			case MODEL_MIDDLE:
				wait = 0.5;
				break;
			case MODEL_LOW:
				wait = 1;
				break;
			}
			// IMPORTANT: $wait is now in days
			try {
				Thread.sleep((long)(wait*86400000)/ConfigurableTimer.getInstance().getSpeedFactor());
			}catch(InterruptedException e) {}

			recalc();
			if(batteryPercentage > 0) {
				send();
			}
		}
//		publish(DEBUG, new IOMessage(ImmutableMap.of(SEVERITY, "INFO", "LABEL", "SENSOR_STOPPED", MESSAGE, String.format("Sensor #%d has stopped. Years running %.1f", sensorId, yearsRunning))));
		System.out.println(String.format("Sensor #%d has stopped. Years running %.1f", sensorId, yearsRunning));
	}
	
	private void recalc() {
		batteryPercentage -= PERCENTAGE_USED_PER_SEND*(1+(percentageUsedPerDayDeviation*((Math.random()-0.5)*2)));
		yearsRunning += wait/365;
	}
	
	private void send() {
		Map<String, String> vars = new HashMap<>();
		vars.put(SENSOR_ID, Integer.toString(sensorId));
		vars.put(TIMESTAMP, Long.toString(System.nanoTime()*ConfigurableTimer.getInstance().getSpeedFactor()));
		vars.put(PERCENTAGE_LEFT, Double.toString(batteryPercentage));
		ObjectNode root = mapper.createObjectNode();
		root.put(COMPOSER, currentModel);
		vars.put(RumProcessor.CURRENT_MODEL, root.toString());
		vars.put(YEARS_RUNNING, Double.toString(yearsRunning));
		
		Sensor.sensorQueue.add(new IOMessage(vars));				
//		publish(SENSOR, new IOMessage(vars));
	}
	
}