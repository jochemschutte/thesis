package prototype.model;

import java.util.HashMap;
import java.util.Map;

public class RPM{
	
	String identifier;
	Map<Resource, ResourceFunction> resourceFunctions = new HashMap<>();
	
	public RPM(String identifier) {
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