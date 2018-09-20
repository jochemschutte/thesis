package model.small;

import java.util.HashSet;

import com.google.common.collect.HashMultimap;

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

public abstract class TestModel extends TestTest{
	
	protected ModelComponent cpu;
	protected ModelComponent radio;
	protected Resource cycles;
	protected Resource computations;
	protected Resource power;
	protected Resource bandwidth;
	
	protected abstract HashMultimap<ModelComponent, RPM> getModels();
	
	@Override
	public void prepare() {
		Component clock = new Component("clock");
		cpu = new ModelComponent("cpu");
		Component battery = new Component("battery");
		radio = new ModelComponent("radio");
		Component serviceProvider = new Component("service provider");
		components = new HashSet<>();
		components.add(clock);
		components.add(cpu);
		components.add(battery);
		components.add(radio);
		components.add(serviceProvider);
		
		cycles = new Resource("cycles", "nr/s");
		computations = new Resource("computations", "nr/s");
		power = new Resource("power", "mW");
		bandwidth = new Resource("bandwidth", "B/s");
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
		
		models = getModels();
		
		engine = new RumEngine(components, qos, models);
		
		message = new RumMessage();
		
		engine.validateStructure(message);
	}
}