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
				
		builder.connect(radio, composer, networkBandwidth, new ResourceFunction(RADIO_MAX_BANDWIDTH), null);
		builder.connect(battery, composer, powerUsageModelIn, new ResourceFunction(MAX_POWER), null);
		builder.connect(battery, batteryLeft, initialCapacity, new ResourceFunction(INITIAL_BATTERY_CAPACITY), new ResourceFunction(INITIAL_CAPACITY));
		
		builder.connect(batteryLeft, serviceTimeCalculator, capacityLeft, new ResourceFunction(x->x[0]/100*x[1], PERCENTAGE_LEFT, INITIAL_CAPACITY), new ResourceFunction(CAPACITY_LEFT));
		builder.connect(composer, serviceTimeCalculator, powerUsageModelOut, null, new ResourceFunction(POWER_USAGE_MODEL_OUT));
		builder.connect(serviceTimeCalculator, qosCalculator, serviceTime, new ResourceFunction(x->(x[0]/x[1])/24/265.26, CAPACITY_LEFT, POWER_USAGE_MODEL_OUT), new ResourceFunction(SERVICE_TIME));
		new ResourceInterface(serviceTime, totalServiceTimeCalculator, InterfaceType.CALC, new ResourceFunction(SERVICE_TIME));
		builder.connect(totalServiceTimeCalculator, totalServiceTimeConsumer, totalServiceTime, new ResourceFunction(x->x[0]+x[1], SERVICE_TIME, YEARS_RUNNING), new ResourceFunction(10));
		builder.connect(composer, qosCalculator, throughput, null, new ResourceFunction(THROUGHPUT));
		new ResourceInterface(qos, qosCalculator, InterfaceType.OFFERS);
		qosCalculator.getResourceFunctions().put(qos, new ResourceFunction(x->x[0]*265.26*x[1], SERVICE_TIME, THROUGHPUT));
		
		RpmBuilder rpm = builder.rpmBuilder(composer, powerUsageModelIn, powerUsageModelOut, throughput, networkBandwidth);
		rpm.add("low", new ResourceFunction(0.001667), new ResourceFunction("powerUsageModelIn"), new ResourceFunction(1), new ResourceFunction(3));
		rpm.add("middle", new ResourceFunction(0.003333), new ResourceFunction("powerUsageModelIn"), new ResourceFunction(2), new ResourceFunction(10));
		rpm.add("high", new ResourceFunction(0.005556), new ResourceFunction("powerUsageModelIn"), new ResourceFunction(5), new ResourceFunction(20));
				
		return builder.build();
	}
	
}