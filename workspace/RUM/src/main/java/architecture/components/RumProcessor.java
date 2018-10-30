package architecture.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import io.message.IOMessage;
import io.message.RumExporter;
import prototype.main.RumEngine;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.RumMessage;

public class RumProcessor extends SingleMessageProcessor{
	
	private static final long serialVersionUID = -8441811495864715725L;
	public static final String CURRENT_MODEL = "CURRENT_MODEL";
	public static final String NEXT_MODEL = "NEXT_MODEL";

	Set<RumExporter> changeRumMessageExporters = new HashSet<>();
	Set<RumExporter> noValidRumExporters = new HashSet<>();
	SetMultimap<String, RumExporter> exporters =  HashMultimap.create();
	
	private RumEngine engine;
	private Set<String> changeRumTopics;
	private Set<String> noValidRumTopics;
	
	public RumProcessor(RumEngine engine, Set<String> changeRumTopics, Set<String> noValidRumTopics) {
		super();
		this.engine = engine;
		this.changeRumTopics = changeRumTopics;
		this.noValidRumTopics = noValidRumTopics;
	}
	
	public Set<RumExporter> getChangeRumMessageExporters() {
		return changeRumMessageExporters;
	}

	public Set<RumExporter> getNoValidRumExporters() {
		return noValidRumExporters;
	}

	public SetMultimap<String, RumExporter> getExporters() {
		return exporters;
	}

	@Override
	public void runForMessage(IOMessage m) {
		RumMessage rm = extractRumMessage(m);
		Map<ModelComponent, RPM> currentState = rm.getCurrentState();
		Map<ModelComponent, RPM> nextState = engine.run(rm);
		if(nextState == null) {
			//No valid model -> continue with current model
			nextState = currentState;
			IOMessage noValidModel = exportNoValidModel(m);
			noValidRumTopics.forEach(t->publish(t, noValidModel));
		}
		else if(!nextState.equals(currentState)) {
			IOMessage changeRpm = exportChangeRpm(nextState, m);
			changeRumTopics.forEach(t->publish(t, changeRpm));
		}
		engine.provision(nextState, rm);
		for(String topic : exporters.keySet()) {
			publish(topic, new IOMessage(exporters.get(topic).stream().collect(Collectors.toMap(e->e.getExportAs(),  e->e.export(m, engine)))));
		}
	}
	
	private RumMessage extractRumMessage(IOMessage m) {
		RumMessage result = new RumMessage();
		for(Map.Entry<String, String> entry : m.getVars().entrySet()) {
			try {
				result.getVars().put(entry.getKey(), Double.parseDouble(entry.getValue()));
			}catch(NumberFormatException e) {}
		}		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(m.getVars().get(CURRENT_MODEL));
			Iterator<Map.Entry<String, JsonNode>> iter = root.getFields();
			Map<ModelComponent, RPM> rpms = new HashMap<>();
			while(iter.hasNext()) {
				Map.Entry<String, JsonNode> entry = iter.next();
				ModelComponent mc = (ModelComponent)engine.getComponents().get(entry.getKey());
				RPM rpm = engine.getModels().get(mc).stream().filter(r->r.getIdentifier().equals(entry.getValue().asText())).findAny().get();
				rpms.put(mc,  rpm);
			}
			result.setCurrentState(rpms);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} 
		return result;
	}
	
	public IOMessage exportNoValidModel(IOMessage in) {
		return new IOMessage(noValidRumExporters.stream().collect(Collectors.toMap(e->e.getExportAs(), e->e.export(in, engine))));
	}
	
	public IOMessage exportChangeRpm(Map<ModelComponent, RPM> models, IOMessage in) {
		IOMessage result = new IOMessage(changeRumMessageExporters.stream().collect(Collectors.toMap(e->e.getExportAs(), e->e.export(in, engine))));
		result.getVars().put(CURRENT_MODEL, in.getVars().get(CURRENT_MODEL));
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		models.entrySet().forEach(e->root.put(e.getKey().getIdentifier(), e.getValue().getIdentifier()));
		result.getVars().put(NEXT_MODEL, root.toString());
		return result;
	}
		
}