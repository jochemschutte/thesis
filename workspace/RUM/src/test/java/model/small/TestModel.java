package model.small;

import java.util.stream.Collectors;

import model.TestTest;
import prototype.factory.RpmBuilder;
import prototype.factory.RumBuilder;
import prototype.model.Component;
import prototype.model.ModelComponent;
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
	
	protected abstract void buildModels(RpmBuilder cpu, RpmBuilder radio);
	
	@Override
	public void prepare() {
		RumBuilder b = new RumBuilder();
		Component clock = b.component("clock");
		cpu = b.modelComponent("cpu");
		Component battery = b.component("battery");
		radio = b.modelComponent("radio");
		Component serviceProvider = b.component("service_provider");
		
		cycles = b.resource("cycles", "nr/s");
		computations = b.resource("computations", "nr/s");
		power = b.resource("power", "mW");
		bandwidth = b.resource("bandwidth", "B/s");
		
		qos = b.optimize(new MinMaxOptimizer("qos", "QoS level", MinMax.MAX));
		
		b.connect(clock, cpu, cycles);
		b.connect(cpu, serviceProvider, computations);
		b.connect(battery, radio, power);
		b.connect(radio, serviceProvider, bandwidth);
		new ResourceInterface(qos, serviceProvider, InterfaceType.OFFERS);
		
		clock.getResourceFunctions().put(cycles, new ResourceFunction("10"));
		battery.getResourceFunctions().put(power, new ResourceFunction("15"));
		//model goes here
		serviceProvider.getResourceFunctions().put(computations, new ResourceFunction("computations"));
		serviceProvider.getResourceFunctions().put(bandwidth, new ResourceFunction("bandwidth"));
		serviceProvider.getResourceFunctions().put(qos, new ResourceFunction(x->x[0]*x[1], "computations", "bandwidth"));
		
		RpmBuilder cpuBuilder = b.rpmBuilder(cpu, cycles, computations);
		RpmBuilder radioBuilder = b.rpmBuilder(radio, power, bandwidth);
		buildModels(cpuBuilder, radioBuilder);
				
		engine = b.build();
		
		components = engine.getComponents().values().stream().collect(Collectors.toSet());
		resources = engine.getResources().values().stream().collect(Collectors.toSet());
		models = engine.getModels();
		
		message = new RumMessage();
		
		engine.validateStructure(message);
	}
}