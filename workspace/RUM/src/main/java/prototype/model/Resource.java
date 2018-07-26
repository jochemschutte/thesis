package prototype.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import prototype.model.ResourceInterface.InterfaceType;
import prototype.model.requirements.OfferConsumeRequirementGTE;
import prototype.model.requirements.Requirement;

public class Resource{
	
	String identifier;
	String unit;
	Set<ResourceInterface> interfaces = new HashSet<>();
	Set<Requirement> reqs = new HashSet<>();
	
	public Resource(String identifier, String unit) {
		this.identifier = identifier;
		this.unit = unit;
		reqs.add(new OfferConsumeRequirementGTE());
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
	
	public Set<ResourceInterface> offered() {
		return interfaces.stream().filter(i -> i.getType() == InterfaceType.OFFERS).collect(Collectors.toSet());
	}
	
	public Set<ResourceInterface> consumed() {
		return interfaces.stream().filter(i -> i.getType() == InterfaceType.CONSUMES).collect(Collectors.toSet());
	}
	
	public Set<ResourceInterface> calcs(){
		return interfaces.stream().filter(i -> i.getType() == InterfaceType.CALC).collect(Collectors.toSet());
	}
	
	public Set<Requirement> getRequirements(){
		return this.reqs;
	}
	
	public boolean isValid() {
		return reqs.stream().map(r -> r.isValid(collect(offered()),collect(consumed()))).reduce(true, (a,b) -> a && b);
	}
	
	public static Double collect(Collection<ResourceInterface> interfaces) {
		return interfaces.stream().map(i->i.getValue()).reduce(0.0, (a,b)->a+b);
	}
	
	@Override
	public boolean equals(Object r) {
		return r instanceof Resource && this.getIdentifier().equals(((Resource)r).getIdentifier());
	}

	@Override
	public String toString() {
		return this.identifier;
	}
} 