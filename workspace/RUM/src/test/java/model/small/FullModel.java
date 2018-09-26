package model.small;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import bsh.EvalError;
import model.TestTest;
import prototype.factory.RpmBuilder;
import prototype.factory.RumBuilder;
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
	
	static final double INITIAL_BATTERY_CAPACITY = 200; //Wh
	static final double MAX_POWER = 3; //W
	static final double MIN_YEARS = 10;
	
	Resource serviceTime;

	@Override
	public String getTitle() {
		return "FullModel";
	}
	
	@Override
	public void prepare() {
		RumBuilder b = new RumBuilder();
		
		Component network = b.component("network");
		Component battery = b.component("battery");
		ModelComponent composer = b.modelComponent("composer");
		Component batteryLeft = b.component("batteryLeft");
		Component serviceTimeCalculator = b.component("serviceTimeCalculator");
		Component totalServiceTimeCalculator = b.component("totalServiceTimeCalculator");
		Component totalServiceTimeConsumer = b.component("totalServiceTimeConsumer");
		Component qosCalculator = b.component("qosCalculator");
		
		Resource initialCapacity = b.resource("initialCapacity", "Wh");
		Resource capacityLeft = b.resource("capacityLeft", "Wh");
		Resource networkBandwidth = b.resource("networkBandwidth", "B/s");
		Resource powerUsageModelOut = b.resource("powerUsageModelOut", "mW");
		Resource powerUsageModelIn = b.resource("powerUsageModelIn", "mW");
		Resource throughput = b.resource("throughput", "messages/day");
		Resource totalServiceTime = b.resource("totalServiceTime", "years");
		serviceTime = b.resource("serviceTime", "years");
		qos = b.optimize(new MinMaxOptimizer("qos", "messages in lifetime", MinMax.MAX));
				
		b.connect(battery, composer, powerUsageModelIn, new ResourceFunction(x->MAX_POWER), null);
		b.connect(battery, batteryLeft, initialCapacity, new ResourceFunction(x->INITIAL_BATTERY_CAPACITY), new ResourceFunction(x->x[0], "initialCapacity"));
		b.connect(batteryLeft, serviceTimeCalculator, capacityLeft, new ResourceFunction(x->x[0]/100*x[1], "powerPercentageLeft", "initialCapacity"), new ResourceFunction(x->x[0],"capacityLeft"));
		b.connect(composer, serviceTimeCalculator, powerUsageModelOut, null, new ResourceFunction(x->x[0], "powerUsageModelOut"));
		b.connect(serviceTimeCalculator, qosCalculator, serviceTime, new ResourceFunction(x->(x[0]*3600/(x[1]/1000))/3600/24/365, "capacityLeft", "powerUsageModelOut"), new ResourceFunction(x->x[0], "serviceTime"));
		new ResourceInterface(serviceTime, totalServiceTimeCalculator, InterfaceType.CALC,new ResourceFunction(x->x[0], "serviceTime"));
		b.connect(totalServiceTimeCalculator, totalServiceTimeConsumer, totalServiceTime, new ResourceFunction(x->x[0]+x[1], "serviceTime", "timeRunning"), new ResourceFunction(10.0));
		b.connect(composer, qosCalculator, throughput, null, new ResourceFunction("throughput"));
		new ResourceInterface(qos, qosCalculator, InterfaceType.OFFERS);
		qosCalculator.getResourceFunctions().put(qos, new ResourceFunction(x-> x[0]*365*x[1], "serviceTime", "throughput"));
		
		RpmBuilder rpm = b.rpmBuilder(composer, powerUsageModelIn, powerUsageModelOut, throughput, networkBandwidth);
		rpm.add("low", new ResourceFunction(1), new ResourceFunction("powerUsageModelIn"), new ResourceFunction(1), new ResourceFunction(3));
		rpm.add("middle", new ResourceFunction(3), new ResourceFunction("powerUsageModelIn"), new ResourceFunction(10), new ResourceFunction(10));
		rpm.add("high", new ResourceFunction(5), new ResourceFunction("powerUsageModelIn"), new ResourceFunction(20), new ResourceFunction(20));
		
		engine = b.build();
		components = engine.getComponents().values().stream().collect(Collectors.toSet());
		resources = engine.getResources().values().stream().collect(Collectors.toSet());
		models = engine.getModels();
		
		message = new RumMessage();
		message.setCurrentState(null);
		message.getVars().putAll(ImmutableMap.of("timeRunning", 9.0, "powerPercentageLeft", 29.0));
	}
	
	@Test
	public void assureEmpty() {
		engine.provision(engine.getEmptyModel(), message);
		engine.calculateScoresRecursively(message, engine.getModels(), engine.getResources().values().stream().flatMap(r->r.getRequirements().stream()).collect(Collectors.toSet()));
		for(Resource r : engine.getResources().values()) {
			System.out.println(r.getIdentifier());
			for(ResourceInterface ri : r.getInterfaces() ) {
				System.out.println(ri);
			}
		}
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