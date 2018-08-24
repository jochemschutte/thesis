package architecture.demo.processes;

import static architecture.demo.global.Fields.NETWORK_BANDWIDTH;
import static architecture.demo.global.Fields.PERCENTAGE_LEFT;
import static architecture.demo.global.Fields.SENSOR_ID;
import static architecture.demo.global.Fields.SERVICE_TIME;
import static architecture.demo.global.Fields.THROUGHPUT;
import static architecture.demo.global.Fields.TIMESTAMP;
import static architecture.demo.global.Fields.YEARS_RUNNING;
import static architecture.demo.global.Topics.ACCUMULATOR;
import static architecture.demo.global.Topics.CHANGE_RPM;
import static architecture.demo.global.Topics.NO_RUM;

import com.google.common.collect.ImmutableSet;

import architecture.components.RumProcessor;
import architecture.demo.construct.RumEngineConstructor;
import io.message.MessageExporter;
import io.message.ResourceExporter;
import prototype.model.ResourceInterface.InterfaceType;

public class SingleSensorProcessor{
	
	
	public static RumProcessor constructProcessor() {
		RumProcessor result = new RumProcessor(RumEngineConstructor.constructEngine(), ImmutableSet.of(CHANGE_RPM), ImmutableSet.of(NO_RUM));
		result.getChangeRumMessageExporters().add(new MessageExporter(SENSOR_ID));
		result.getChangeRumMessageExporters().add(new MessageExporter(TIMESTAMP));
		
		result.getNoValidRumExporters().add(new MessageExporter(SENSOR_ID));
		result.getNoValidRumExporters().add(new MessageExporter(TIMESTAMP));
		result.getNoValidRumExporters().add(new MessageExporter(YEARS_RUNNING));
		result.getNoValidRumExporters().add(new MessageExporter(PERCENTAGE_LEFT));
		
		result.getExporters().put(ACCUMULATOR, new MessageExporter(SENSOR_ID));
		result.getExporters().put(ACCUMULATOR, new MessageExporter(TIMESTAMP));
		result.getExporters().put(ACCUMULATOR, new ResourceExporter(THROUGHPUT, InterfaceType.OFFERS));
		result.getExporters().put(ACCUMULATOR, new ResourceExporter(SERVICE_TIME, InterfaceType.OFFERS));
		result.getExporters().put(ACCUMULATOR, new ResourceExporter(NETWORK_BANDWIDTH, InterfaceType.CONSUMES));
		
		return result;
		
	}
}