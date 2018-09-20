package model.small;

import com.google.common.collect.HashMultimap;

import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.ResourceFunction;

public class TestModelSecondInvalid extends TestModel{
	
	@Override
	public HashMultimap<ModelComponent, RPM> getModels(){
		RPM lowRadio = new RPM("lowRadio");
		lowRadio.getResourceFunctions().put(power, new ResourceFunction("9"));
		lowRadio.getResourceFunctions().put(bandwidth, new ResourceFunction("4"));
		
		RPM highRadio = new RPM("highRadio");
		highRadio.getResourceFunctions().put(power, new ResourceFunction("14"));
		highRadio.getResourceFunctions().put(bandwidth, new ResourceFunction("7"));
		
		RPM lowCpu = new RPM("lowCpu");
		lowCpu.getResourceFunctions().put(cycles, new ResourceFunction("1000"));
		lowCpu.getResourceFunctions().put(computations, new ResourceFunction("4"));
		
		RPM highCpu = new RPM("highCpu");
		highCpu.getResourceFunctions().put(cycles, new ResourceFunction("8"));
		highCpu.getResourceFunctions().put(computations, new ResourceFunction("8"));
		
		HashMultimap<ModelComponent, RPM> result = HashMultimap.create();
		result.put(radio, lowRadio);
		result.put(radio, highRadio);
		result.put(cpu, lowCpu);
		result.put(cpu, highCpu);
		
		return result;
	}

	@Override
	protected String getTitle() {
		return "TestModel second invalid";
	}
}