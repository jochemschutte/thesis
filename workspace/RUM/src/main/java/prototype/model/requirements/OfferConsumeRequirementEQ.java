package prototype.model.requirements;

import java.util.LinkedList;
import java.util.List;

import prototype.model.Resource;

public class OfferConsumeRequirementEQ extends Requirement{

	public OfferConsumeRequirementEQ(Resource resource) {
		super(resource);
	}
	
	@Override
	public boolean isValid() throws IllegalStateException {
		if(!isResolved())
			throw new IllegalStateException("isValid() was called on a non-resolved resource");
		return Resource.collect(resource.offered()) == Resource.collect(resource.consumed());
	}
	
	@Override
	public boolean isResolved() {
		return Resource.collect(resource.offered()) != null && Resource.collect(resource.consumed()) != null;
	}
	
	@Override
	public String toString() {
		List<String> unres = new LinkedList<>();
		if(Resource.collect(resource.offered()) == null)
			unres.add("offered");
		if(Resource.collect(resource.consumed()) == null)
			unres.add("consumed");
		if(isResolved()) {
			if(isValid()) {
				return String.format("[valid] '%s' offered and consumed values (%.2f and %.2f) '%s' were equal", // 
						resource.getIdentifier(), Resource.collect(resource.offered()), Resource.collect(resource.consumed()));
			}else {
				return String.format("[invalid] '%s' offered and consumed values (%.2f and %.2f) '%s' were not equal", // 
						resource.getIdentifier(), Resource.collect(resource.offered()), Resource.collect(resource.consumed()));
			}
		}else {
			return String.format("OfferConsumeEQ '%s' unresolved %s is/were unknown for resource '%s'", resource.getIdentifier(), unres.toString());
		}
			
	}
	
}