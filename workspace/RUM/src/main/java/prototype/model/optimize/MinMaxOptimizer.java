package prototype.model.optimize;

import java.util.Map;
import java.util.function.BiFunction;

import prototype.model.ModelComponent;
import prototype.model.RPM;

public class MinMaxOptimizer extends Optimizer{

	public enum MinMax {MIN, MAX}
	
	MinMax type;
	
	public MinMaxOptimizer(String identifier, String unit, MinMax type) {
		super(identifier, unit);
		this.type = type;
	}

	@Override
	public double score() {
		return collect(offered());
	}

	@Override
	public Map<ModelComponent, RPM> rank(Map<Map<ModelComponent,RPM>, Double> scores) {
		if(scores.size() == 0)
			return null;
		Map.Entry<Map<ModelComponent, RPM>, Double> result = scores.entrySet().stream().findAny().get();
		for(Map.Entry<Map<ModelComponent, RPM>, Double> iter : scores.entrySet()) {
			BiFunction<Double,Double, Double> f = null;
			if(type == MinMax.MIN) 
				f = Math::min;
			else
				f = Math::max;
			if(f.apply(result.getValue(), iter.getValue()).equals(iter.getValue()))
				result = iter;
		}
		return result.getKey();
	}
	
}