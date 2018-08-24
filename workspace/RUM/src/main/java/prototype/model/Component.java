package prototype.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;

import bsh.EvalError;
import prototype.graph.GraphNode;
import prototype.model.ResourceInterface.InterfaceType;

public class Component extends GraphNode<Component>{
	
	String identifier;
	Map<Resource,ResourceFunction> resourceFunctions = new HashMap<>();
	Set<ResourceInterface> resources = new HashSet<>();
	
	public Component(String identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public String getName() {
		return getIdentifier();
	}

	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public Set<GraphNode<Component>> preceedings(){
		Set<GraphNode<Component>> result = new HashSet<>();
		for(ResourceInterface cons : Iterables.concat(consumes(), calcs())) {
			for(ResourceInterface off : cons.getResource().offered()) {
				result.add(off.getComponent());
			}
		}
		return result;
	}
	
	public Map<Resource, ResourceFunction> getResourceFunctions() {
		return resourceFunctions;
	}

	@Override
	public List<GraphNode<Component>> visit() {
		return super.visit();
	}

	public Set<ResourceInterface> getInterfaces() {
		return resources;
	}
	
	public void setValuesforInterfaces(RumMessage m) throws EvalError{
		//consumed resources
		Map<String, Double> availableResources = new TreeMap<>();
		Iterable<ResourceInterface> preceeding = Iterables.concat(consumes(), calcs());
		for(ResourceInterface interf : preceeding) {
			availableResources.put(interf.getResource().getIdentifier(), Resource.collect(interf.getResource().offered()));
		}
		for(ResourceInterface interf : preceeding) {
			setValueForInterface(interf, m, availableResources);
		}
		
		//offered resources
		availableResources = new TreeMap<>();
		for(ResourceInterface interf : preceeding) {
			availableResources.put(interf.getResource().getIdentifier(), interf.getValue());
		}
		for(ResourceInterface interf : offers()) {
			setValueForInterface(interf, m, availableResources);
		}
	}
	
	public void setValueForInterface(ResourceInterface iface, RumMessage m, Map<String, Double> values) throws EvalError {
		iface.setValue(getResourceFunctions().get(iface.getResource()).execute(m, values));
	}
	
	public void reset() {
		super.reset();
		offers().stream().forEach(r -> r.setValue(null));
	}
	
	public Set<ResourceInterface> offers(){
		return resources.stream().filter(ri -> ri.getType() == InterfaceType.OFFERS).collect(Collectors.toSet());
	}
	
	public Set<ResourceInterface> consumes(){
		return resources.stream().filter(ri -> ri.getType() == InterfaceType.CONSUMES).collect(Collectors.toSet());
	}
	
	public Set<ResourceInterface> calcs(){
		return resources.stream().filter(ri -> ri.getType() == InterfaceType.CALC).collect(Collectors.toSet());
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Component && identifier.equals(((Component) o).getIdentifier());
	}
	
	
}