package model.small;

import prototype.factory.RpmBuilder;
import prototype.model.ResourceFunction;

public class TestModelSecondInvalid extends TestModel{
	
	@Override
	public void buildModels(RpmBuilder cpu, RpmBuilder radio) {		
		radio.add("lowRadio", new ResourceFunction(9), new ResourceFunction(4));
		radio.add("highRadio", new ResourceFunction(14), new ResourceFunction(7));
		cpu.add("lowCpu", new ResourceFunction(1000), new ResourceFunction(4));
		cpu.add("highCpu", new ResourceFunction(8), new ResourceFunction(8));
	}

	@Override
	protected String getTitle() {
		return "TestModel second invalid";
	}
}