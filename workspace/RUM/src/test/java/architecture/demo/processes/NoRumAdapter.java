package architecture.demo.processes;

import static architecture.demo.global.Fields.PERCENTAGE_LEFT;
import static architecture.demo.global.Fields.SENSOR_ID;
import static architecture.demo.global.Fields.YEARS_RUNNING;
import static architecture.demo.global.Topics.DEBUG;
import static architecture.demo.global.Topics.FIELD_LABEL;
import static architecture.demo.global.Topics.LOG;
import static architecture.demo.global.Topics.MESSAGE;
import static architecture.demo.global.Topics.SEVERITY;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableMap;

import architecture.components.SingleMessageProcessor;
import io.message.IOMessage;

public class NoRumAdapter extends SingleMessageProcessor{

	public NoRumAdapter() {
		super();
	}
	
	Set<Integer> reportedSensorIds = new TreeSet<>();
	
	@Override
	public void runForMessage(IOMessage m) {
		if(reportedSensorIds.add(Integer.parseInt(m.getVars().get(SENSOR_ID)))){
			Map<String, String> vars = ImmutableMap.of(SEVERITY, "WARN", FIELD_LABEL, "NO_RUM", // 
					MESSAGE, String.format("No valid rum found for sensor #%s after %.1f years (%.1f%% left)",m.getVars().get(SENSOR_ID), Double.parseDouble(m.getVars().get(YEARS_RUNNING)), Double.parseDouble(m.getVars().get(PERCENTAGE_LEFT))));
			publish(LOG, new IOMessage(vars));
			publish(DEBUG, new IOMessage(vars));
		}
	}
	
}