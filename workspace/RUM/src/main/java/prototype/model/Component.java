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
	Set<ResourceInterface> resourcesInterfaces = new HashSet<>();
	
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
	
	@Override
	public Set<GraphNode<Component>> followings(){
		Set<GraphNode<Component>> result = new HashSet<>();
		for(ResourceInterface ri : offers()) {
			result.addAll(ri.getResource().consumed().stream().map(r->r.getComponent()).collect(Collectors.toSet()));
			result.addAll(ri.getResource().calcs().stream().map(r->r.getComponent()).collect(Collectors.toSet()));
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
		return resourcesInterfaces;
	}
	
//	public void calculateUpwards(RumMessage m) {
//		setValuesforInterfaces(m);
//		//TODO rekening houden met topsort
//		Set<ResourceInterface> interfs = new HashSet<>();
//		for(ResourceInterface ri : offers()) {
//			interfs.addAll(ri.getResource().calcs());
//			interfs.addAll(ri.getResource().consumed());
//		}
//		interfs.forEach(ri->ri.getComponent().calculateUpwards(m));
//	}
	
	public void setValuesforInterfaces(RumMessage m){
		//incoming resources
		Map<String, Double> availableResources = new TreeMap<>();
		Iterable<ResourceInterface> preceeding = Iterables.concat(consumes(), calcs());
		for(ResourceInterface interf : preceeding) {
			Double value = Resource.collect(interf.getResource().offered());
			if(value != null)
				availableResources.put(interf.getResource().getIdentifier(), value);
		}
		for(ResourceInterface interf : preceeding) {
			setValueForInterface(interf, m, availableResources);
		}
		//outgoing resources
		availableResources = new TreeMap<>();
		for(ResourceInterface interf : preceeding) {
			availableResources.put(interf.getResource().getIdentifier(), interf.getValue());
		}
		for(ResourceInterface interf : offers()) {
			setValueForInterface(interf, m, availableResources);
		}
		
	}
	
	public void setValueForInterface(ResourceInterface iface, RumMessage m, Map<String, Double> values) {
		Double value = null;
		try{
			ResourceFunction func = getResourceFunctions().get(iface.getResource());
			if(func != null)
				value = func.execute(m, values);
		}catch(EvalError e) {
			//not all variables were known.
		}
		iface.setValue(value);
	}
	
	public void reset() {
		super.reset();
		this.resourcesInterfaces.stream().forEach(ri->ri.setValue(null));
	}
	
	public Set<ResourceInterface> offers(){
		return resourcesInterfaces.stream().filter(ri -> ri.getType() == InterfaceType.OFFERS).collect(Collectors.toSet());
	}
	
	public Set<ResourceInterface> consumes(){
		return resourcesInterfaces.stream().filter(ri -> ri.getType() == InterfaceType.CONSUMES).collect(Collectors.toSet());
	}
	
	public Set<ResourceInterface> calcs(){
		return resourcesInterfaces.stream().filter(ri -> ri.getType() == InterfaceType.CALC).collect(Collectors.toSet());
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Component && identifier.equals(((Component) o).getIdentifier());
	}
	
	@Override
	public String toString() {
		return this.identifier;
	}
	
	
}