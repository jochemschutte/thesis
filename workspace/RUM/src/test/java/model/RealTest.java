package model;

import static architecture.demo.global.Fields.PERCENTAGE_LEFT;
import static architecture.demo.global.Fields.SENSOR_ID;
import static architecture.demo.global.Fields.TIMESTAMP;
import static architecture.demo.global.Fields.YEARS_RUNNING;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import architecture.demo.construct.RumEngineConstructor;
import prototype.main.RumEngine;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.RumMessage;

public class RealTest{

	@Test
	public void runCustom() {
		RumEngine engine = RumEngineConstructor.constructEngine();
		RumMessage message = new RumMessage();
		ModelComponent c = engine.getModels().keys().stream().findAny().get();
		RPM rpm = engine.getModels().get(c).stream().findAny().get();
		message.setCurrentState(ImmutableMap.of(c, rpm));
		message.setVars(ImmutableMap.of(SENSOR_ID, 1.0, TIMESTAMP, 1.0, PERCENTAGE_LEFT, 78.32273171697526, YEARS_RUNNING, 2.165083023505142));
		
		for(int i = 0; i < 2; i++) {			
			System.out.println(engine.run(message));
			System.out.println(engine.getResources().get("totalServiceTime"));
			
//			for(Component component : engine.getComponents().values()) {
//				System.out.println(component.getIdentifier());
//				for(ResourceInterface ri: component.consumes()) {
//					System.out.println(String.format("[consumes] %s: %.2f %s", ri.getResource().getIdentifier(), ri.getValue(), ri.getResource().getUnit()));
//				}
//				for(ResourceInterface ri : component.offers()) {	
//					System.out.println(String.format("[offers] %s: %.2f %s", ri.getResource().getIdentifier(), ri.getValue(), ri.getResource().getUnit()));
//				}
//				System.out.println();
//			}
		}
		
	}
	
	
	@Ignore
	@Test
	public void testClear() {
		RumEngine engine = RumEngineConstructor.constructEngine();
		RumMessage message = new RumMessage();
		ModelComponent c = engine.getModels().keys().stream().findAny().get();
		RPM rpm = engine.getModels().get(c).stream().findAny().get();
		message.setCurrentState(ImmutableMap.of(c, rpm));
		message.setVars(ImmutableMap.of(SENSOR_ID, 1.0, TIMESTAMP, 1.0, PERCENTAGE_LEFT, 78.32273171697526, YEARS_RUNNING, 2.165083023505142));
		Map<ModelComponent, RPM> empty = new HashMap<>();
		empty.put(c, null);
		ModelComponent mc = (ModelComponent)engine.getComponents().get("composer");
		
		System.out.println(String.join("\n", mc.getInterfaces().stream().map(i->i.toString()).collect(Collectors.toList())));
		engine.provision(message.getCurrentState(), message);
		System.out.println(String.join("\n", mc.getInterfaces().stream().map(i->i.toString()).collect(Collectors.toList())));
		engine.provision(empty, message);
		System.out.println(String.join("\n", mc.getInterfaces().stream().map(i->i.toString()).collect(Collectors.toList())));
		engine.provision(message.getCurrentState(), message);
		System.out.println(String.join("\n", mc.getInterfaces().stream().map(i->i.toString()).collect(Collectors.toList())));
		
	}
}