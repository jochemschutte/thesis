package architecture.demo.processes;

import static architecture.demo.global.Fields.COMPOSER;
import static architecture.demo.global.Fields.SENSOR_ID;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import architecture.components.RumProcessor;
import architecture.components.SingleMessageProcessor;
import io.message.IOMessage;

public class RumActuator extends SingleMessageProcessor{
	ObjectMapper mapper = new ObjectMapper();
	public Map<Integer, Sensor> sensors;
	
	public RumActuator(Collection<Sensor> sensors) {
		super();
		this.sensors = sensors.stream().collect(Collectors.toMap(s-> s.getSensorId(), s->s));
	}
	
	@Override
	public void runForMessage(IOMessage m) {
		ObjectNode root;
		try {
			root = (ObjectNode)mapper.readTree(m.getVars().get(RumProcessor.NEXT_MODEL));
			sensors.get(Integer.parseInt(m.getVars().get(SENSOR_ID))).setCurrentModel(root.get(COMPOSER).getTextValue());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}