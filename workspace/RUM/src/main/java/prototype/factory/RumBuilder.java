package prototype.factory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import prototype.main.RumEngine;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.optimize.Optimizer;

public class RumBuilder{
	
	String illegalState = "RumBuilder wasn't completed. %d was missing.";
	
	Set<Resource> resources = new HashSet<>();
	Set<Component> components = new HashSet<>();
//	SetMultimap<ModelComponent, RPM> models = HashMultimap.create();
	List<RpmBuilder> rpmBuilders = new LinkedList<>();
	Optimizer optimizer = null;
	
	public Resource resource(String identifier, String unit) {
		return resource(new Resource(identifier, unit));
	}
	
	public Resource resource(Resource r) {
		this.resources.add(r);
		return r;
	}
	
	public Component component(String identifier) {
		return component(new Component(identifier));
	}
	
	public Component component(Component component) {
		this.components.add(component);
		return component;
	}
	
	public ModelComponent modelComponent(String identifier) {
		return modelComponent(new ModelComponent(identifier));
	}
	
	public ModelComponent modelComponent(ModelComponent c) {
		components.add(c);
		return c;
	}
	
	public RpmBuilder rpmBuilder(ModelComponent c, Resource... resources) {
		RpmBuilder rpm = new RpmBuilder(c, resources);
		rpmBuilders.add(rpm);
		return rpm;
	}
		
	public Optimizer optimize(Optimizer optimizer) {
		this.optimizer = optimizer;
		return optimizer;
	}
	
	public RumEngine build() {
		String missing = null;
		if(resources.isEmpty()) {
			missing = "models";
		}
		if(optimizer == null) {
			missing = "optmizer";
		}
		if(components.isEmpty()) {
			missing = "resources";
		}
		if(components.isEmpty()) {
			missing = "components";
		}
		if(missing != null) {
			throw new IllegalArgumentException(String.format(illegalState, missing));
		}
		SetMultimap<ModelComponent, RPM> models = HashMultimap.create();
		rpmBuilders.forEach(b -> models.putAll(b.getComponent(), b.buildRpms()));
		return new RumEngine(components, optimizer, models);
		
	}
}