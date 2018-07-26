package prototype.model.optimize;

import java.util.Map;

import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.optimize.MinMaxOptimizer.MinMax;

public class ApproxOptimizer extends Optimizer{
	
	final double approxValue;
	
	public ApproxOptimizer(String identifier, String unit, double approxValue) {
		super(identifier, unit);
		this.approxValue = approxValue;
	}

	@Override
	public double score() {
		return Math.abs(approxValue-(double)collect(offered()));
	}

	@Override
	public Map<ModelComponent, RPM> rank(Map<Map<ModelComponent, RPM>, Double> scores) {
		return new MinMaxOptimizer(null, null, MinMax.MIN).rank(scores);		
	}
	
	
}