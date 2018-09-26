package prototype.factory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.ResourceFunction;

public class RpmBuilder{
	
	ModelComponent component;
	Resource[] resources;
	Map<String, ResourceFunction[]> functions = new HashMap<>();
	
	public RpmBuilder(ModelComponent component, Resource... resources) {
		if(resources.length == 0) {
			throw new IllegalArgumentException(String.format("ModelComponent '%s' should have at least one resource associated", component.getIdentifier()));
		}
		this.component = component;
		this.resources = resources;
	}
	
	public void add(String name, ResourceFunction... functions) {
		if(resources.length != functions.length) {
			throw new IllegalArgumentException(String.format("RpmBuilder: nr resources wasn't equal to nr functions (%d != %d)", resources.length, functions.length));
		}
		ResourceFunction[] prevValue = this.functions.put(name, functions);
		if(prevValue != null) {
			throw new IllegalArgumentException(String.format("Duplicate RPM with name '%s' for ModelComponent '%s'", name, component.getIdentifier()));
		}
	}
	
	public List<RPM> buildRpms() {
		if(functions.size() == 0) {
			throw new IllegalStateException(String.format("ModelComponent '%s' should have at least one RPM associated", component.getIdentifier()));
		}
		List<RPM> result = new LinkedList<>();
		for(Map.Entry<String, ResourceFunction[]> entry : functions.entrySet()) {
			RPM rpm = new RPM(entry.getKey());
			result.add(rpm);
			for(int i = 0; i < resources.length; i++) {			
				rpm.getResourceFunctions().put(resources[i], entry.getValue()[i]);
			}
		}
		return result;
	}
	
	public ModelComponent getComponent() {
		return component;
	}
	
	@Override
	public String toString() {
		List<String> result = functions.entrySet().stream().map(e -> String.format("    %s = {%s}", e.getKey(), String.join(", ", Arrays.stream(e.getValue()).map(f->f.toString()).toArray(String[]::new)))).collect(Collectors.toList());
		result.add(0, String.format("ModelComponent: %s, Resources: {%s}", component.getIdentifier(), String.join(", ", Arrays.stream(resources).map(r->r.getIdentifier()).collect(Collectors.toSet()))));
		return String.join("\n", result);
	}
}