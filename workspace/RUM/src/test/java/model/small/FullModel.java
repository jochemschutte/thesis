package model.small;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;

import bsh.EvalError;
import model.TestTest;
import prototype.main.RumEngine;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.ResourceFunction;
import prototype.model.ResourceInterface;
import prototype.model.ResourceInterface.InterfaceType;
import prototype.model.RumMessage;
import prototype.model.optimize.MinMaxOptimizer;
import prototype.model.optimize.MinMaxOptimizer.MinMax;

public class FullModel extends TestTest{
	
	static final int INITIAL_BATTERY_CAPACITY = 200; //Wh
	static final int MAX_POWER = 3; //W
	static final int MIN_YEARS = 10;
	
	Resource serviceTime;

	@Override
	public String getTitle() {
		return "FullModel";
	}
	
	@Override
	public void prepare() {
		Component network = new Component("network");
		Component battery = new Component("battery");
		ModelComponent composer = new ModelComponent("composer");
		Component batteryLeft = new Component("batteryLeft");
		Component serviceTimeCalculator = new Component("serviceTimeCalculator");
		Component totalServiceTimeCalculator = new Component("totalServiceTimeCalculator");
		Component totalServiceTimeConsumer = new Component("totalServiceTimeConsumer");
		Component qosCalculator = new Component("qosCalculator");
		components = new HashSet<>();
		components.add(network);
		components.add(battery);
		components.add(batteryLeft);
		components.add(serviceTimeCalculator);
		components.add(totalServiceTimeCalculator);
		components.add(totalServiceTimeConsumer);
		components.add(qosCalculator);
		components.add(composer);
		
		Resource initialCapacity = new Resource("initialCapacity", "Wh");
		Resource capacityLeft = new Resource("capacityLeft", "Wh");
		Resource networkBandwidth = new Resource("networkBandwidth", "B/s");
		Resource powerUsageModelOut = new Resource("powerUsageModelOut", "mW");
		Resource powerUsageModelIn = new Resource("powerUsageModelIn", "mW");
		Resource throughput = new Resource("throughput", "messages/day");
		Resource totalServiceTime = new Resource("totalServiceTime", "years");
		serviceTime = new Resource("serviceTime", "years");
		qos = new MinMaxOptimizer("qos", "messages in lifetime", MinMax.MAX);
		resources = new HashSet<>();
		resources.add(initialCapacity);
		resources.add(capacityLeft);
		resources.add(networkBandwidth);
		resources.add(powerUsageModelIn);
		resources.add(powerUsageModelOut);
		resources.add(throughput);
		resources.add(serviceTime);
		resources.add(qos);
		
		connect(battery, composer, powerUsageModelIn, Integer.toString(MAX_POWER), null);
		connect(battery, batteryLeft, initialCapacity, Integer.toString(INITIAL_BATTERY_CAPACITY), "initialCapacity");
		connect(batteryLeft, serviceTimeCalculator, capacityLeft, "powerPercentageLeft/100 * initialCapacity", "capacityLeft");
		connect(composer, serviceTimeCalculator, powerUsageModelOut, null, "powerUsageModelOut");
		connect(serviceTimeCalculator, qosCalculator, serviceTime, "(capacityLeft*3600/(powerUsageModelOut/1000))/3600/24/365", "serviceTime");
		new ResourceInterface(serviceTime, totalServiceTimeCalculator, InterfaceType.CALC,new ResourceFunction("serviceTime"));
		connect(totalServiceTimeCalculator, totalServiceTimeConsumer, totalServiceTime, "serviceTime+timeRunning", "10");
		connect(composer, qosCalculator, throughput, null, "throughput");
		new ResourceInterface(qos, qosCalculator, InterfaceType.OFFERS);
		qosCalculator.getResourceFunctions().put(qos, new ResourceFunction("serviceTime*365*throughput"));
		
		models = HashMultimap.create();
		
		RPM low = new RPM("low");
		low.getResourceFunctions().put(powerUsageModelIn, new ResourceFunction("1"));
		low.getResourceFunctions().put(powerUsageModelOut, new ResourceFunction("powerUsageModelIn"));
		low.getResourceFunctions().put(throughput, new ResourceFunction("1"));
		low.getResourceFunctions().put(networkBandwidth, new ResourceFunction("3"));
		models.put(composer, low);
		
		RPM middle = new RPM("middle");
		middle.getResourceFunctions().put(powerUsageModelIn, new ResourceFunction("3"));
		middle.getResourceFunctions().put(powerUsageModelOut, new ResourceFunction("powerUsageModelIn"));
		middle.getResourceFunctions().put(throughput, new ResourceFunction("10"));
		middle.getResourceFunctions().put(networkBandwidth, new ResourceFunction("10"));
		models.put(composer, middle);
		
		RPM high = new RPM("high");
		high.getResourceFunctions().put(powerUsageModelIn, new ResourceFunction("5"));
		high.getResourceFunctions().put(powerUsageModelOut, new ResourceFunction("powerUsageModelIn"));
		high.getResourceFunctions().put(throughput, new ResourceFunction("20"));
		high.getResourceFunctions().put(networkBandwidth, new ResourceFunction("20"));
		models.put(composer, high);
		
		engine = new RumEngine(components, qos, models);
		
		message = new RumMessage();
		message.setCurrentState(null);
		message.getVars().putAll(ImmutableMap.of("timeRunning", 9.0, "powerPercentageLeft", 29.0));
	}
	
	@Ignore
	@Test
	public void runScenarioNormal() throws EvalError {
		double[] timeRunning = new double[] {0.5,1.0,1.5,2.0,2.5,3.0,3.5,4.0,4.5,5.0,5.5,6.0,6.5,7.0,7.5,8.0,8.5,9.0,9.5,10.0,11.0,12.0,15.0};
		double[] capacityLeft = new double[] {100.0,90.0,85.0,80.0,75.0,72.0,70.0,65.0,60.0,55.0,50.0,45.0,40.0,38.0,37.0,35.0,32.0,29.0,27.0,26.0,24.0,20.0,15.0};
		runForScenario(timeRunning, capacityLeft);
	}
	
	@Ignore
	@Test
	public void runScenarioLow() throws EvalError {
		double[] timeRunning = new double[] {0.0,0.0,0.0,1.0,1.0,2.0,2.0,2.0,3.0,3.0,4.0,4.0,4.0,5.0,5.0,6.0,6.0,6.0,7.0,7.0,8.0};
		double[] capacityLeft = new double[] {100.0,95.0,90.0,85.0,80.0,75.0,70.0,65.0,60.0,55.0,50.0,45.0,40.0,35.0,30.0,25.0,20.0,15.0,10.0,5.0,0.0};
		runForScenario(timeRunning, capacityLeft);
	}
	
	@Ignore
	@Test
	public void formulateMessages() {
		double n = 0.0;
		double end = 8;
		int nr = 20;
		double diff = (n-end)/nr;
		for(int i = 0; i <= nr; i++) {
			System.out.print((int)(n-diff*i) + ".0,");
		}
	}
	
	public void runForScenario(double[] timeRunning, double[] capacityLeft) throws EvalError {
		assertEquals(timeRunning.length, capacityLeft.length);
		for(int i = 0; i < timeRunning.length; i++) {
			RumMessage message = new RumMessage();
			message.setCurrentState(null);
			message.getVars().putAll(ImmutableMap.of("timeRunning",timeRunning[i],"powerPercentageLeft",capacityLeft[i]));
			Map<ModelComponent, RPM> composition = engine.run(message);
			System.out.println(String.format("%.1f timeRunning, %f battery left.", message.getVars().get("timeRunning"), message.getVars().get("powerPercentageLeft")));
			if(composition != null) {
				engine.provision(composition, message);
				System.out.println(String.format("%s: %f years, %f messages in lifetime", composition.values(), Resource.collect(serviceTime.offered()), qos.score()));
			}else {
				System.out.println("no valid model found (keep current or optimize)");
			}
			System.out.println();
		}
	}
	
}