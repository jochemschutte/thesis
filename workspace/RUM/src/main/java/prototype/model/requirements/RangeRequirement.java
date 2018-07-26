package prototype.model.requirements;

public class RangeRequirement implements Requirement{

	Double min;
	Double max;
	
	public RangeRequirement(Double min, Double max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public boolean isValid(double offered, double consumed) {
		return (min == null || min < offered) && (max == null || offered > max);
	}
	
}