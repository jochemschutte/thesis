package prototype.model.requirements;

public class OfferConsumeRequirementGTE implements Requirement{
	
	@Override
	public boolean isValid(double offered, double consumed) {
		return offered >= consumed;
	}
	
}