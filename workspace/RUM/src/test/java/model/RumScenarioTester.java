package model;

import static architecture.demo.global.Fields.PERCENTAGE_LEFT;
import static architecture.demo.global.Fields.SENSOR_ID;
import static architecture.demo.global.Fields.SERVICE_TIME;
import static architecture.demo.global.Fields.TIMESTAMP;
import static architecture.demo.global.Fields.YEARS_RUNNING;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import architecture.demo.construct.RumEngineConstructor;
import prototype.main.RumEngine;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.RumMessage;

public class RumScenarioTester{

	
	static double percentageLeft = 61.0;
	static double yearsRunning = 3.9;
	static String currentModelId = "low";
	
	
	@Test
	public void run() {
		RumEngine engine = RumEngineConstructor.constructEngine();
		Map<String, Double> vars = ImmutableMap.of(SENSOR_ID, 1.0, TIMESTAMP, 1.0, PERCENTAGE_LEFT, percentageLeft, YEARS_RUNNING, yearsRunning);
		Set<ModelComponent> modelComponents = engine.getComponents().values().stream().filter(m -> m instanceof ModelComponent).map(c->(ModelComponent)c).collect(Collectors.toSet());
		for(String model : new String[] {"low", "middle", "high"}){
			Map<ModelComponent, RPM> currentModel = modelComponents.stream().collect(Collectors.toMap(c->c, c->extractRpm(engine, c.getIdentifier(), model)));
			RumMessage m = new RumMessage();
			m.setCurrentState(currentModel);
			m.setVars(vars);
			engine.provision(currentModel, m);
			Resource serviceTime = engine.getResources().get(SERVICE_TIME);
			System.out.println(model + ": "+Resource.collect(serviceTime.offered())+", "+engine.isValid());
		}
		Map<ModelComponent, RPM> currentModel = modelComponents.stream().collect(Collectors.toMap(c->c, c->extractRpm(engine, c.getIdentifier(), currentModelId)));
		RumMessage m = new RumMessage();
		m.setCurrentState(currentModel);
		m.setVars(vars);
		long start = System.currentTimeMillis();
		Map<ModelComponent, RPM> nextModel = engine.run(m);
		System.out.println(System.currentTimeMillis() - start);
		print(nextModel);
	}
	
	private RPM extractRpm(RumEngine engine, String componentName, String rumName){
		Set<RPM> rpms = engine.getModels().get((ModelComponent)engine.getComponents().get(componentName));
		return rpms.stream().filter(m->m.getIdentifier().equals(rumName)).findAny()//
				.orElseThrow(()->new NoSuchElementException(String.format("Rum for component '%s' with name '%s'", componentName, rumName)));
	}
	
	private void print(Map<ModelComponent, RPM> models) {
		if(models == null)
			System.out.println("null");
		else
			System.out.println(models.entrySet().stream().collect(Collectors.toMap(e->e.getKey().getIdentifier(), e->e.getValue().getIdentifier())));
	}
}