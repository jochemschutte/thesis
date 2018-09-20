package prototype.model.requirements;

import prototype.model.Resource;

public class RangeRequirement extends Requirement{

	Double min;
	Double max;
	
	public RangeRequirement(Resource resource, Double min, Double max) {
		super(resource);
		this.min = min;
		this.max = max;
	}
	
	@Override
	public boolean isValid() throws IllegalStateException {
		if(!isResolved())
			throw new IllegalStateException("isValid() was called on a non-resolved resource");
		return (min == null || min < Resource.collect(resource.offered())) && (max == null || Resource.collect(resource.offered()) > max);
	}
	
	@Override
	public boolean isResolved() {
		return Resource.collect(resource.offered()) != null;
	}
	
	@Override
	public String toString() {
		if(isResolved()) {
			if(isValid()) {
				return String.format("[valid] '%s' offered value (%.2f) is within bounds [%.2f, %.2f]", //
						resource.getIdentifier(), Resource.collect(resource.offered()),  min, max);
			}else {
				return String.format("[invalid] '%s' offered value (%.2f) was not within bounds [%.2f, %.2f]", //
						resource.getIdentifier(), Resource.collect(resource.offered()),  min, max);
			}
		}else {
			return String.format("RangeReq '%s' unresolved. Offered unknown'", resource.getIdentifier());
		}
	}
	
}