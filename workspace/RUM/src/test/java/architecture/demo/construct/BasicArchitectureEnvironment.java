package architecture.demo.construct;

import static architecture.demo.global.Fields.AVG_THROUGHPUT;
import static architecture.demo.global.Fields.NR_LESS_YEAR;
import static architecture.demo.global.Fields.NR_MESSAGES;
import static architecture.demo.global.Fields.NR_SENSORS;
import static architecture.demo.global.Fields.SENSOR_ID;
import static architecture.demo.global.Fields.SYSTEM_RUNTIME;
import static architecture.demo.global.Topics.ACCUMULATOR;
import static architecture.demo.global.Topics.ANALYSER;
import static architecture.demo.global.Topics.APPLICATION;
import static architecture.demo.global.Topics.CHANGE_RPM;
import static architecture.demo.global.Topics.FIELD_LABEL;
import static architecture.demo.global.Topics.LOG;
import static architecture.demo.global.Topics.MESSAGE;
import static architecture.demo.global.Topics.NO_RUM;
import static architecture.demo.global.Topics.SENSOR;
import static architecture.demo.global.Topics.SEVERITY;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import architecture.components.MappedAccumulatorProcessor;
import architecture.components.RumProcessor;
import architecture.demo.processes.AccumulatorImplementation;
import architecture.demo.processes.NoRumAdapter;
import architecture.demo.processes.Reporter;
import architecture.demo.processes.RumActuator;
import architecture.demo.processes.Sensor;
import architecture.demo.processes.SingleSensorProcessor;
import architecture.demo.processes.TendencyAnalyser;
import mock.kafka.MockKafkaConsumerGroup;

public class BasicArchitectureEnvironment{
		
	public static void construct(Collection<Sensor> sensors, int nrSensorProcessors) {		
		Set<RumProcessor> ssprocessors = new HashSet<>();
		for(int i = 0; i < nrSensorProcessors; i++) {
			ssprocessors.add(SingleSensorProcessor.constructProcessor());
		}
		MockKafkaConsumerGroup sensorGroup = new MockKafkaConsumerGroup(ssprocessors);
		sensorGroup.subscribe(SENSOR);
		
		NoRumAdapter noRum = new NoRumAdapter();
		noRum.subscribe(NO_RUM);		
		
		Reporter changeRum = new Reporter("Sensor #%s changed to '%s'", SENSOR_ID, RumProcessor.NEXT_MODEL);
		changeRum.subscribe(CHANGE_RPM);
		
		RumActuator actuator = new RumActuator(sensors);
		actuator.subscribe(CHANGE_RPM);
		
		Reporter exposer = new Reporter("Runtime: %s\nAvg throughput: %s\nnr sensors total: %s\nnr sensor < 1 year: %s\nbased on %s messages\n", //
				SYSTEM_RUNTIME, AVG_THROUGHPUT, NR_SENSORS, NR_LESS_YEAR, NR_MESSAGES);
		exposer.subscribe(APPLICATION);
		
		TendencyAnalyser analyser = new TendencyAnalyser();
		analyser.subscribe(ANALYSER);
		
		Reporter alert = new Reporter("LOG[%s]: %s: %s", SEVERITY, FIELD_LABEL, MESSAGE);
		alert.subscribe(LOG);
		
		sensorGroup.start();
		
		AccumulatorImplementation impl = new AccumulatorImplementation();
		MappedAccumulatorProcessor coll = new MappedAccumulatorProcessor(Amount.valueOf(5, SI.SECOND), impl, impl);
		coll.subscribe(ACCUMULATOR);
		
	}
}