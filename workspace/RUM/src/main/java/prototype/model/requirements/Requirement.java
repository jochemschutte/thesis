package prototype.model.requirements;

import prototype.model.Resource;

public abstract class Requirement{
	
	protected Resource resource;
	
	public Requirement(Resource resource) {
		this.resource = resource;
	}
		
	public abstract boolean isValid() throws IllegalStateException;
	
	public abstract boolean isResolved();
	
}