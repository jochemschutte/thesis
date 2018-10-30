package prototype.main;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.SetMultimap;

import prototype.graph.TopologyResolver;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.ResourceInterface;
import prototype.model.RumMessage;
import prototype.model.optimize.Optimizer;
import prototype.model.requirements.Requirement;

public class RumEngine implements Serializable{
	
	private static final long serialVersionUID = 3555244378926064165L;

	TopologyResolver<Component> topology;
	
	Map<String, Component> components;
	Map<String, Resource> resources;
	Optimizer optimizer;
	SetMultimap<ModelComponent, RPM> models;
	final Map<ModelComponent, RPM> emptyModelMap;
	
	Map<ModelComponent, RPM> currentModels;
	RumMessage currentMessage;
	
	public RumEngine(Collection<Component> components, Optimizer optimizer, SetMultimap<ModelComponent, RPM> models) {
		topology = new TopologyResolver<Component>(components);
		resources = components.stream().map(c -> c.getInterfaces()).flatMap(s -> s.stream()).map(ri -> ri.getResource()).distinct() //
				.collect(Collectors.toMap(r->r.getIdentifier(), r->r));
		this.components= components.stream().collect(Collectors.toMap(c->c.getIdentifier(), c->c));
		this.optimizer = optimizer;
		this.models = models;
		emptyModelMap = new HashMap<>();
		models.keySet().forEach(k -> emptyModelMap.put(k, null));
	}
	
	public Map<ModelComponent, RPM> run(RumMessage m){
		Set<Requirement> constraints = resources.values().stream().map(r->r.getRequirements()).flatMap(s->s.stream()).collect(Collectors.toSet());
		
		Map<Map<ModelComponent, RPM>, Double> scores = null;
		
		if(models.keySet().size() > 2) {
			provision(emptyModelMap, m);
			scores = calculateScoresRecursively(m, models, constraints);
		} else {
			scores = calculateScoresFlat(m, constraints);
		}
		Map<ModelComponent, RPM> result = optimizer.rank(scores);
		if(result != null)
			provision(result, m);
		return result;
	}
	
	public Map<Map<ModelComponent, RPM>, Double> calculateScoresFlat(RumMessage m, Set<Requirement> constraints){
		Map<Map<ModelComponent, RPM>, Double> result = new HashMap<>();
		for(Map<ModelComponent, RPM> model : powerset(models)) {
			provision(model, m);
			if(constraints.stream().filter(c->!c.isValid()).count() == 0)
				result.put(model, optimizer.score());
		}
		return result;
	}
	
	public Map<Map<ModelComponent, RPM>, Double> calculateScoresRecursively(RumMessage m, SetMultimap<ModelComponent, RPM> modelSet, Set<Requirement> constraints){
		Set<Requirement> unresolvedConstraints = constraints.stream().filter(c->!c.isResolved()).collect(Collectors.toSet());		
		if(constraints.stream().filter(c->c.isResolved() && !c.isValid()).count() > 0) {
			//a constraint was violated
			return new HashMap<>();
		}
		if(modelSet.isEmpty()) {
			//model is fully provisioned
			if(unresolvedConstraints.size() > 0) { 
				throw new IllegalArgumentException("not all constraints are resolvable, modelset was empty. Constraints: " + unresolvedConstraints);
			}
			Map<ModelComponent, RPM> key = models.keySet().stream().collect(Collectors.toMap(c->c, c->c.getModel()));
			return ImmutableMap.of(key, optimizer.score());
		}
		Map<Map<ModelComponent, RPM>, Double> result = new HashMap<>();
		SetMultimap<ModelComponent, RPM> modelsLeft = HashMultimap.create(modelSet);
		ModelComponent component = modelsLeft.keySet().stream().findAny().get();
		Set<RPM> rpms = modelsLeft.removeAll(component);
		for(RPM model : rpms) {
			component.loadModel(model);
			component.setValuesforInterfaces(m);
			topology.getFollowers(component).forEach(c->c.setValuesforInterfaces(m));
			result.putAll(calculateScoresRecursively(m, modelsLeft, unresolvedConstraints));
		}
		component.loadModel(null);
		component.setValuesforInterfaces(m);
		topology.getFollowers(component).forEach(c->c.setValuesforInterfaces(m));
		return result;
	}
	
	public boolean validateStructure(RumMessage exampleMessage) {
		Map<ModelComponent, RPM> exampleModel = new HashMap<>();
		for(ModelComponent key : models.keySet()) {
			exampleModel.put(key, models.get(key).stream().findAny() //
					.orElseThrow(()->new IllegalStateException(String.format("No RPM's supplied for component '%s'", key.getIdentifier()))));
		}
		provision(exampleModel, exampleMessage);
		Set<ResourceInterface> interfaces = resources.values().stream().map(r->r.getInterfaces()).flatMap(s->s.stream()).collect(Collectors.toSet());
		for(ResourceInterface interf : interfaces) {
			if(interf.getValue() == null) {
				throw new IllegalArgumentException(String.format("Value of %s interface between '%s' and '%s' could not be calculated", interf.getType(), interf.getComponent().getIdentifier(), interf.getResource().getIdentifier()));
			}
		}
		return true;
	}
	
	public void provision(Map<ModelComponent, RPM> models, RumMessage message) {
//		if(models != currentModels || message != currentMessage) {
		components.values().forEach(c -> c.reset());
		models.entrySet().forEach(e -> e.getKey().loadModel(e.getValue()));
		for(Component c : topology.getFullOrder()) {
			c.setValuesforInterfaces(message);
		}
		this.currentMessage = message;
		this.currentModels = models;
//		}
	}
	
	public static Set<Map<ModelComponent, RPM>> powerset(SetMultimap<ModelComponent, RPM> models){
		Stack<ModelComponent> stack = new Stack<>();
		models.keySet().forEach(m -> stack.push(m));
		return powerset(stack, models);
	}
	
	public static Set<Map<ModelComponent, RPM>> powerset(Stack<ModelComponent> stack, SetMultimap<ModelComponent, RPM> models){
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
	
	public SetMultimap<ModelComponent, RPM> getModels(){
		return this.models;
	}
	
	public Map<String, Component> getComponents(){
		return this.components;
	}
	
	public Map<String,Resource> getResources(){
		return this.resources;
	}

	public Map<ModelComponent, RPM> getEmptyModel() {
		return this.emptyModelMap;
	}
	
}