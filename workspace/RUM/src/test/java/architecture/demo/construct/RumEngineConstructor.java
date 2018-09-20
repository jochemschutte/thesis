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

import prototype.factory.RpmBuilder;
import prototype.factory.RumBuilder;
import prototype.main.RumEngine;
import prototype.model.Component;
import prototype.model.ModelComponent;
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
		RumBuilder builder = new RumBuilder();
		
		Component radio = builder.component(RADIO);
		Component battery = builder.component(BATTERY);
		ModelComponent composer = builder.modelComponent(COMPOSER);
		Component batteryLeft = builder.component(BATTERY_LEFT);
		Component serviceTimeCalculator = builder.component(SERVICE_TIME_CALCULATOR);
		Component totalServiceTimeCalculator = builder.component(TOTAL_SERVICE_TIME_CALCULATOR);
		Component totalServiceTimeConsumer = builder.component(TOTAL_SERVICE_TIME_CONSUMER);
		Component qosCalculator = builder.component(QOS_CALCULATOR);
				
		Resource initialCapacity = builder.resource(INITIAL_CAPACITY, "Wh");
		Resource capacityLeft = builder.resource(CAPACITY_LEFT, "Wh");
		Resource networkBandwidth = builder.resource(NETWORK_BANDWIDTH, "B/s");
		Resource powerUsageModelOut = builder.resource(POWER_USAGE_MODEL_OUT, "mW");
		Resource powerUsageModelIn = builder.resource(POWER_USAGE_MODEL_IN, "mW");
		Resource throughput = builder.resource(THROUGHPUT, "messages/day");
		Resource totalServiceTime = builder.resource(TOTAL_SERVICE_TIME, "years");
		Resource serviceTime = builder.resource(SERVICE_TIME, "years");
		Optimizer qos = builder.optimize(new MinMaxOptimizer(QOS, "messages in lifetime", MinMax.MAX));
				
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
		
		RpmBuilder rpm = builder.rpmBuilder(composer, powerUsageModelIn, powerUsageModelOut, throughput, networkBandwidth);
		rpm.add("low", "0.001667", "powerUsageModelIn", "1", "3");
		rpm.add("middle", "0.003333", "powerUsageModelIn", "2", "10");
		rpm.add("high", "0.005556", "powerUsageModelIn", "5", "20");
				
		return builder.build();
	}
	
}