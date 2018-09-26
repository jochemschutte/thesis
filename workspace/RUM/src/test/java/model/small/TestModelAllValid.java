package model.small;

import prototype.factory.RpmBuilder;
import prototype.model.ResourceFunction;

public class TestModelAllValid extends TestModel{
	
	@Override
	public void buildModels(RpmBuilder cpu, RpmBuilder radio) {		
		radio.add("lowRadio", new ResourceFunction(9), new ResourceFunction(4));
		radio.add("highRadio", new ResourceFunction(14), new ResourceFunction(7));
		cpu.add("lowCpu", new ResourceFunction(9), new ResourceFunction(4));
		cpu.add("highCpu", new ResourceFunction(8), new ResourceFunction(8));
	}
	
//	@Override
//	public HashMultimap<ModelComponent, RPM> getModels(){
//		RPM lowRadio = new RPM("lowRadio");
//		lowRadio.getResourceFunctions().put(power, new ResourceFunction("9"));
//		lowRadio.getResourceFunctions().put(bandwidth, new ResourceFunction("4"));
//		
//		RPM highRadio = new RPM("highRadio");
//		highRadio.getResourceFunctions().put(power, new ResourceFunction("14"));
//		highRadio.getResourceFunctions().put(bandwidth, new ResourceFunction("7"));
//		
//		RPM lowCpu = new RPM("lowCpu");
//		lowCpu.getResourceFunctions().put(cycles, new ResourceFunction("9"));
//		lowCpu.getResourceFunctions().put(computations, new ResourceFunction("4"));
//		
//		RPM highCpu = new RPM("highCpu");
//		highCpu.getResourceFunctions().put(cycles, new ResourceFunction("8"));
//		highCpu.getResourceFunctions().put(computations, new ResourceFunction("8"));
//		
//		HashMultimap<ModelComponent, RPM> result = HashMultimap.create();
//		result.put(radio, lowRadio);
//		result.put(radio, highRadio);
//		result.put(cpu, lowCpu);
//		result.put(cpu, highCpu);
//		
//		return result;
//	}

	@Override
	protected String getTitle() {
		return "TestModel all valid";
	}
	
}