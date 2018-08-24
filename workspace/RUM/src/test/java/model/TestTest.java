package model;

import static org.junit.Assert.assertNotNull;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import prototype.main.RumEngine;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.ResourceFunction;
import prototype.model.ResourceInterface;
import prototype.model.ResourceInterface.InterfaceType;
import prototype.model.RumMessage;
import prototype.model.optimize.Optimizer;

public abstract class TestTest{

	protected RumEngine engine = null;
	protected Set<Component> components = null;
	protected Set<Resource> resources = null;
	protected Optimizer qos = null;
	protected HashMultimap<ModelComponent, RPM> models = null;
	
	protected RumMessage message = null;
	
	@Before
	public abstract void prepare();
	

	protected ResourceInterface[] connect(Component offering, Component consuming, Resource r, InterfaceType type) {
		ResourceInterface[] result = new ResourceInterface[2];
		result[0] = new ResourceInterface(r, offering, InterfaceType.OFFERS);
		result[1] = new ResourceInterface(r, consuming, type);
		return result;
	}
	
	protected ResourceInterface[] connect(Component offering, Component consuming, Resource r) {
		return connect(offering, consuming, r, InterfaceType.CONSUMES);
	}
	
	protected ResourceInterface[] connect(Component offering, Component consuming, Resource r, String functionOffer, String functionConsumer, InterfaceType type) {
		if(functionOffer != null)
			offering.getResourceFunctions().put(r,  new ResourceFunction(functionOffer));
		if(functionConsumer != null)
			consuming.getResourceFunctions().put(r, new ResourceFunction(functionConsumer));
		return connect(offering, consuming, r, type);
	}
	
	protected ResourceInterface[] connect(Component offering, Component consuming, Resource r, String functionOffer, String functionConsumer) {
		return connect(offering, consuming, r, functionOffer, functionConsumer, InterfaceType.CONSUMES);
	}
	
	private void validatePrepare() {
		assertNotNull(engine);
		assertNotNull(components);
		assertNotNull(resources);
		assertNotNull(qos);
		assertNotNull(models);
		assertNotNull(message);
	}
	
	@Test
	public void run() { 
		validatePrepare();
		Set<Map<ModelComponent, RPM>> powerset = RumEngine.powerset(models);
		for(Map<ModelComponent, RPM> composition : powerset) {
			engine.provision(composition, message);
			Set<Resource> invalid = resources.stream().filter(r -> !r.isValid()).collect(Collectors.toSet());
			System.out.print(String.format("%s: %f", composition.values(), qos.score()));
			if(invalid.isEmpty())
				System.out.println();
			else
				System.out.println(String.format(", invalid: %s", invalid));
		}
		System.out.println();
		
		Map<ModelComponent, RPM> result = engine.run(message);
		System.out.println("Models:");
		SetMultimap<ModelComponent, RPM> target = HashMultimap.create();
		for(Map.Entry<ModelComponent, RPM> entry : result.entrySet()) {
			System.out.println(String.format("%s: %s", entry.getKey().getIdentifier(), entry.getValue().getIdentifier()));
			target.put(entry.getKey(), entry.getValue());
		}
		System.out.println();
		engine = new RumEngine(components, qos, target);
		engine.run(message);
		
		for(Component component : components) {
			System.out.println(component.getIdentifier());
			for(ResourceInterface ri: component.consumes()) {
				System.out.println(String.format("[consumes] %s: %.2f %s", ri.getResource().getIdentifier(), ri.getValue(), ri.getResource().getUnit()));
			}
			for(ResourceInterface ri : component.offers()) {	
				System.out.println(String.format("[offers] %s: %.2f %s", ri.getResource().getIdentifier(), ri.getValue(), ri.getResource().getUnit()));
			}
			System.out.println();
		}
	}
}