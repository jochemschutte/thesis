package prototype.model.requirements;

import java.util.LinkedList;
import java.util.List;

import prototype.model.Resource;

public class OfferConsumeRequirementGTE extends Requirement{
	
	public OfferConsumeRequirementGTE(Resource resource) {
		super(resource);
	}

	@Override
	public boolean isValid() throws IllegalStateException {
		if(!isResolved())
			throw new IllegalStateException(String.format("isValid() was called on a non-resolved resource (%s)", resource.getIdentifier()));
		return Resource.collect(resource.offered()) >= Resource.collect(resource.consumed());
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
				return String.format("[valid] '%s' consumed value (%.2f) was smaller or equal than offered value (%.2f)", //
						resource.getIdentifier(), Resource.collect(resource.consumed()), Resource.collect(resource.offered()));
			}else {
				return String.format("[invalid] '%s' consumed value (%.2f) was larger than offered value (%.2f)", //
						resource.getIdentifier(), Resource.collect(resource.consumed()), Resource.collect(resource.offered()));
			}
		}else {
			return String.format("OfferConsumeGTE '%s' unresolved %s is/were unknown", resource.getIdentifier(), unres.toString());
		}
			
	}
	
}