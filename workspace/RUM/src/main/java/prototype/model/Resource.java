package prototype.model;

import java.util.HashSet;
import java.util.Set;

import prototype.model.Requirement.RequirementType;
import prototype.model.ResourceInterface.InterfaceType;

public class Resource{
	
	String identifier;
	String unit;
	Set<ResourceInterface> interfaces = new HashSet<>();
	Requirement req = new Requirement(RequirementType.GTE);
	
	public Resource(String identifier, String unit) {
		this.identifier = identifier;
		this.unit = unit;
	}
	
	public Resource(String identifier, String unit, RequirementType type) {
		this(identifier, unit);
		req = new Requirement(type);
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getUnit() {
		return unit;
	}
	
	public Set<ResourceInterface> getInterfaces() {
		return this.interfaces;
	}
	
	public int offered() {
		return collect(InterfaceType.OFFERS);
	}
	
	public int consumed() {
		return collect(InterfaceType.CONSUMES);
	}
	
	private int collect(InterfaceType type) {
		return interfaces.stream().filter(i -> i.getType() == type).mapToInt(i->i.getValue()).sum();
	}
	
	public boolean isValid() {
		return req.isValid(offered(), consumed());
	}
	
	@Override
	public boolean equals(Object r) {
		if(r instanceof Resource) 
			return this.getIdentifier().equals(((Resource)r).getIdentifier());
		return false;
	}
} 