package architecture.demo.construct;

import static architecture.demo.global.Fields.BATTERY;
import static architecture.demo.global.Fields.BATTERY_LEFT;
import static architecture.demo.global.Fields.CAPACITY_LEFT;
import static architecture.demo.global.Fields.COMPOSER;
import static architecture.demo.global.Fields.INITIAL_CAPACITY;
import static architecture.demo.global.Fields.NETWORK_BANDWIDTH;
import static architecture.demo.global.Fields.PERCENTAGE_LEFT;
import static architecture.demo.global.Fields.POWER_USAGE_MODEL_IN;
import static architecture.demo.global.Fields.POWER_USAGE_MODEL_OUT;
import static architecture.demo.global.Fields.QOS;
import static architecture.demo.global.Fields.QOS_CALCULATOR;
import static architecture.demo.global.Fields.RADIO;
import static architecture.demo.global.Fields.SERVICE_TIME;
import static architecture.demo.global.Fields.SERVICE_TIME_CALCULATOR;
import static architecture.demo.global.Fields.THROUGHPUT;
import static architecture.demo.global.Fields.TOTAL_SERVICE_TIME;
import static architecture.demo.global.Fields.TOTAL_SERVICE_TIME_CALCULATOR;
import static architecture.demo.global.Fields.TOTAL_SERVICE_TIME_CONSUMER;
import static architecture.demo.global.Fields.YEARS_RUNNING;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import prototype.main.RumEngine;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.ResourceFunction;
import prototype.model.ResourceInterface;
import prototype.model.ResourceInterface.InterfaceType;
import prototype.model.optimize.MinMaxOptimizer;
import prototype.model.optimize.MinMaxOptimizer.MinMax;
import prototype.model.optimize.Optimizer;

public class RumEngineConstructor{
	

	static final int RADIO_MAX_BANDWIDTH = 15; //kb/s
	static final int INITIAL_BATTERY_CAPACITY = 200; //Wh
	static final int MAX_POWER = 3; //W
	static final int MIN_YEARS = 10;
	
	
	public static RumEngine constructEngine() {
		Component radio = new Component(RADIO);
		Component battery = new Component(BATTERY);
		ModelComponent composer = new ModelComponent(COMPOSER);
		Component batteryLeft = new Component(BATTERY_LEFT);
		Component serviceTimeCalculator = new Component(SERVICE_TIME_CALCULATOR);
		Component totalServiceTimeCalculator = new Component(TOTAL_SERVICE_TIME_CALCULATOR);
		Component totalServiceTimeConsumer = new Component(TOTAL_SERVICE_TIME_CONSUMER);
		Component qosCalculator = new Component(QOS_CALCULATOR);
		Set<Component> components = new HashSet<>();
		components.add(radio);
		components.add(battery);
		components.add(batteryLeft);
		components.add(serviceTimeCalculator);
		components.add(totalServiceTimeCalculator);
		components.add(totalServiceTimeConsumer);
		components.add(qosCalculator);
		components.add(composer);
		
		Resource initialCapacity = new Resource(INITIAL_CAPACITY, "Wh");
		Resource capacityLeft = new Resource(CAPACITY_LEFT, "Wh");
		Resource networkBandwidth = new Resource(NETWORK_BANDWIDTH, "B/s");
		Resource powerUsageModelOut = new Resource(POWER_USAGE_MODEL_OUT, "mW");
		Resource powerUsageModelIn = new Resource(POWER_USAGE_MODEL_IN, "mW");
		Resource throughput = new Resource(THROUGHPUT, "messages/day");
		Resource totalServiceTime = new Resource(TOTAL_SERVICE_TIME, "years");
		Resource serviceTime = new Resource(SERVICE_TIME, "years");
		Optimizer qos = new MinMaxOptimizer(QOS, "messages in lifetime", MinMax.MAX);
		Set<Resource> resources = new HashSet<>();
		resources.add(initialCapacity);
		resources.add(capacityLeft);
		resources.add(networkBandwidth);
		resources.add(powerUsageModelIn);
		resources.add(powerUsageModelOut);
		resources.add(throughput);
		resources.add(serviceTime);
		resources.add(qos);
		
		ResourceInterface.connect(radio, composer, networkBandwidth, Integer.toString(RADIO_MAX_BANDWIDTH), null);
		ResourceInterface.connect(battery, composer, powerUsageModelIn, Integer.toString(MAX_POWER), null);
		ResourceInterface.connect(battery, batteryLeft, initialCapacity, Integer.toString(INITIAL_BATTERY_CAPACITY), INITIAL_CAPACITY);
		ResourceInterface.connect(batteryLeft, serviceTimeCalculator, capacityLeft, PERCENTAGE_LEFT+"/100 * "+INITIAL_CAPACITY, CAPACITY_LEFT);
		ResourceInterface.connect(composer, serviceTimeCalculator, powerUsageModelOut, null, POWER_USAGE_MODEL_OUT);
		ResourceInterface.connect(serviceTimeCalculator, qosCalculator, serviceTime, "("+CAPACITY_LEFT+"/"+POWER_USAGE_MODEL_OUT+")/24/365.26", SERVICE_TIME);
		new ResourceInterface(serviceTime, totalServiceTimeCalculator, InterfaceType.CALC,new ResourceFunction(SERVICE_TIME));
		ResourceInterface.connect(totalServiceTimeCalculator, totalServiceTimeConsumer, totalServiceTime, SERVICE_TIME+"+"+YEARS_RUNNING, "10");
		ResourceInterface.connect(composer, qosCalculator, throughput, null, THROUGHPUT);
		new ResourceInterface(qos, qosCalculator, InterfaceType.OFFERS);
		qosCalculator.getResourceFunctions().put(qos, new ResourceFunction(SERVICE_TIME+"*365.26*"+THROUGHPUT));
		
		SetMultimap<ModelComponent, RPM>models = HashMultimap.create();
		
		RPM low = new RPM("low");
		low.getResourceFunctions().put(powerUsageModelIn, new ResourceFunction("0.001667"));
		low.getResourceFunctions().put(powerUsageModelOut, new ResourceFunction("powerUsageModelIn"));
		low.getResourceFunctions().put(throughput, new ResourceFunction("1"));
		low.getResourceFunctions().put(networkBandwidth, new ResourceFunction("3"));
		models.put(composer, low);
		
		RPM middle = new RPM("middle");
		middle.getResourceFunctions().put(powerUsageModelIn, new ResourceFunction("0.003333"));
		middle.getResourceFunctions().put(powerUsageModelOut, new ResourceFunction("powerUsageModelIn"));
		middle.getResourceFunctions().put(throughput, new ResourceFunction("10"));
		middle.getResourceFunctions().put(networkBandwidth, new ResourceFunction("10"));
		models.put(composer, middle);
		
		RPM high = new RPM("high");
		high.getResourceFunctions().put(powerUsageModelIn, new ResourceFunction("0.005556"));
		high.getResourceFunctions().put(powerUsageModelOut, new ResourceFunction("powerUsageModelIn"));
		high.getResourceFunctions().put(throughput, new ResourceFunction("20"));
		high.getResourceFunctions().put(networkBandwidth, new ResourceFunction("20"));
		models.put(composer, high);
		
		return new RumEngine(components, qos, models);
	}
}