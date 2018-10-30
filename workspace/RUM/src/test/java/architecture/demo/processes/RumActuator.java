package architecture.demo.processes;

import static architecture.demo.global.Fields.COMPOSER;
import static architecture.demo.global.Fields.SENSOR_ID;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import architecture.components.RumProcessor;
import architecture.components.SingleMessageProcessor;
import io.message.IOMessage;

public class RumActuator extends SingleMessageProcessor{
//	public Map<Integer, Sensor> sensors;
	
	
	@Override
	public void runForMessage(IOMessage m) {
		ObjectNode root;
		try {
			ObjectMapper mapper = new ObjectMapper();
			root = (ObjectNode)mapper.readTree(m.getVars().get(RumProcessor.NEXT_MODEL));
			Sensor.sensorMap.get(Integer.parseInt(m.getVars().get(SENSOR_ID))).setCurrentModel(root.get(COMPOSER).getTextValue());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}