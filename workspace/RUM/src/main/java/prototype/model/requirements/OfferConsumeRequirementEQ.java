package prototype.model.requirements;

public class OfferConsumeRequirementEQ implements Requirement{

	@Override
	public boolean isValid(double offered, double consumed) {
		return offered == consumed;
	}
	
	
}