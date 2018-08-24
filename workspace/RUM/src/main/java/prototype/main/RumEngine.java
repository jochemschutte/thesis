package prototype.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import com.google.common.collect.SetMultimap;

import bsh.EvalError;
import prototype.graph.TopologyResolver;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.RumMessage;
import prototype.model.optimize.Optimizer;

public class RumEngine{
	
	List<Component> topologicalOrder;
	
	Map<String, Component> components;
	Map<String, Resource> resources;
	Optimizer optimizer;
	SetMultimap<ModelComponent, RPM> models;
	
	Map<ModelComponent, RPM> currentModels;
	RumMessage currentMessage;
	
	public RumEngine(Set<Component> components, Optimizer optimizer, SetMultimap<ModelComponent, RPM> models) {
		topologicalOrder = new TopologyResolver<Component>().resolve(components);
		resources = components.stream().map(c -> c.getInterfaces()).flatMap(s -> s.stream()).map(ri -> ri.getResource()).distinct() //
				.collect(Collectors.toMap(r->r.getIdentifier(), r->r));
		this.components= components.stream().collect(Collectors.toMap(c->c.getIdentifier(), c->c));
		this.optimizer = optimizer;
		this.models = models;
	}
	
	public Map<ModelComponent, RPM> run(RumMessage m){
		Map<Map<ModelComponent, RPM>, Double> scores = new HashMap<>();		
		
		for(Map<ModelComponent, RPM> modelComposition: powerset(models)) {
			provision(modelComposition, m);
			if(this.isValid())
				scores.put(modelComposition, optimizer.score());
		}
		return optimizer.rank(scores);
	}
	
	public static Set<Map<ModelComponent, RPM>> powerset(SetMultimap<ModelComponent, RPM> models){
		Stack<ModelComponent> stack = new Stack<>();
		models.keySet().forEach(m -> stack.push(m));
		return powerset(stack, models);
	}
	
	private static Set<Map<ModelComponent, RPM>> powerset(Stack<ModelComponent> stack, SetMultimap<ModelComponent, RPM> models){
		Set<Map<ModelComponent, RPM>> result = new HashSet<>();
		if(stack.isEmpty()) {
			result.add(new HashMap<>());
		}else {
			ModelComponent key = stack.pop();
			Set<Map<ModelComponent, RPM>> sub = powerset(stack, models);
			for(RPM appendix : models.get(key)) {
				for(Map<ModelComponent, RPM> subResult : sub) {
					Map<ModelComponent, RPM> newElem = new HashMap<>(subResult);
					newElem.put(key, appendix);
					result.add(newElem);
				}
			}
		}
		return result;
	}
	
	public boolean isValid() {
		return resources.values().stream().map(Resource::isValid).reduce(true, (a,b)->a && b);
	}
	
	public void provision(Map<ModelComponent, RPM> models, RumMessage message) {
		if(models != currentModels || message != currentMessage) {
			try{
				components.values().forEach(c -> c.reset());
				models.entrySet().forEach(e -> e.getKey().loadModel(e.getValue()));
				for(Component c : topologicalOrder) {
					c.setValuesforInterfaces(message);
				}
			}catch(EvalError e) {
				throw new IllegalArgumentException(e);
			}
			this.currentMessage = message;
			this.currentModels = models;
		}
	}
	
	public SetMultimap<ModelComponent, RPM> getModels(){
		return this.models;
	}
	
	public Map<String, Component> getComponents(){
		return this.components;
	}
	
	public Map<String,Resource> getResources(){
		return this.resources;
	}
	
}