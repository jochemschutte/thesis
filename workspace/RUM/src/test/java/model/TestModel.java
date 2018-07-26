package model;

import java.util.HashSet;

import org.junit.Before;

import com.google.common.collect.HashMultimap;

import prototype.main.Engine;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.ResourceFunction;
import prototype.model.ResourceInterface;
import prototype.model.ResourceInterface.InterfaceType;
import prototype.model.optimize.MinMaxOptimizer;
import prototype.model.optimize.MinMaxOptimizer.MinMax;

public class TestModel extends TestTest{
	
	@Before
	public void prepare() {
		Component clock = new Component("clock");
		ModelComponent cpu = new ModelComponent("cpu");
		Component battery = new Component("battery");
		ModelComponent radio = new ModelComponent("radio");
		Component serviceProvider = new Component("service provider");
		components = new HashSet<>();
		components.add(clock);
		components.add(cpu);
		components.add(battery);
		components.add(radio);
		components.add(serviceProvider);
		
		Resource cycles = new Resource("cycles", "nr/s");
		Resource computations = new Resource("computations", "nr/s");
		Resource power = new Resource("power", "mW");
		Resource bandwidth = new Resource("bandwidth", "B/s");
		resources = new HashSet<>();
		resources.add(cycles);
		resources.add(computations);
		resources.add(power);
		resources.add(bandwidth);
		
		qos = new MinMaxOptimizer("qos", "QoS level", MinMax.MAX);
		
		connect(clock, cpu, cycles);
		connect(cpu, serviceProvider, computations);
		connect(battery, radio, power);
		connect(radio, serviceProvider, bandwidth);
		new ResourceInterface(qos, serviceProvider, InterfaceType.OFFERS);
		
		clock.getResourceFunctions().put(cycles, new ResourceFunction("10"));
		battery.getResourceFunctions().put(power, new ResourceFunction("15"));
		//model goes here
		serviceProvider.getResourceFunctions().put(computations, new ResourceFunction("computations"));
		serviceProvider.getResourceFunctions().put(bandwidth, new ResourceFunction("bandwidth"));
		serviceProvider.getResourceFunctions().put(qos, new ResourceFunction("computations*bandwidth"));
		
		RPM lowCpu = new RPM("lowCpu");
		lowCpu.getResourceFunctions().put(cycles, new ResourceFunction("8"));
		lowCpu.getResourceFunctions().put(computations, new ResourceFunction("4"));
		
		RPM highCpu = new RPM("highCpu");
		highCpu.getResourceFunctions().put(cycles, new ResourceFunction("16"));
		highCpu.getResourceFunctions().put(computations, new ResourceFunction("8"));
		
		RPM lowRadio = new RPM("lowRadio");
		lowRadio.getResourceFunctions().put(power, new ResourceFunction("8"));
		lowRadio.getResourceFunctions().put(bandwidth, new ResourceFunction("4"));
		
		RPM highRadio = new RPM("highRadio");
		highRadio.getResourceFunctions().put(power, new ResourceFunction("14"));
		highRadio.getResourceFunctions().put(bandwidth, new ResourceFunction("7"));
		
		models  = HashMultimap.create();
		models.put(cpu, lowCpu);
		models.put(cpu, highCpu);
		models.put(radio, lowRadio);
		models.put(radio, highRadio);
		
		engine = new Engine(components, qos, models);
	}
	
}