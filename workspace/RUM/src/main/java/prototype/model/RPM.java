package prototype.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RPM implements Serializable{
	
	private static final long serialVersionUID = -4808143849245670856L;
	String identifier;
	Map<Resource, ResourceFunction> resourceFunctions = new HashMap<>();
	
	public RPM(String identifier) {
		if(!identifier.matches("[A-Za-z][A-Za-z0-9_]*"))
			throw new IllegalArgumentException(String.format("'%s' is not a valid identifier. Should match '[A-Za-z][A-Za-z0-9_]*'", identifier));
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Map<Resource, ResourceFunction> getResourceFunctions() {
		return resourceFunctions;
	}
	
	@Override
	public String toString() {
		return this.identifier;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof RPM) {
			RPM rpm2 = (RPM)o;
			return this.identifier.equals(rpm2.getIdentifier());
		}
		return false;
			
	}
}