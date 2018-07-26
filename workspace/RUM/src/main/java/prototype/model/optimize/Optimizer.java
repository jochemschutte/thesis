package prototype.model.optimize;

import java.util.Map;

import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;

public abstract class Optimizer extends Resource{
	
	public Optimizer(String identifier, String unit) {
		super(identifier, unit);
	}
	
	public abstract double score();
	public abstract Map<ModelComponent, RPM> rank(Map<Map<ModelComponent, RPM>, Double> scores);
}