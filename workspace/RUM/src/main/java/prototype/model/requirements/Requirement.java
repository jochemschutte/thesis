package prototype.model.requirements;

import java.io.Serializable;

import prototype.model.Resource;

public abstract class Requirement implements Serializable{
	
	private static final long serialVersionUID = 2102765019093392572L;
	protected Resource resource;
	
	public Requirement(Resource resource) {
		this.resource = resource;
	}
		
	public abstract boolean isValid() throws IllegalStateException;
	
	public abstract boolean isResolved();
	
}