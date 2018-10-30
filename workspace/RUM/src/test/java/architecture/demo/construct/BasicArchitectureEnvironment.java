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

import javax.measure.unit.SI;

import org.apache.storm.topology.TopologyBuilder;
import org.jscience.physics.amount.Amount;

import architecture.components.MappedAccumulatorProcessor;
import architecture.components.RumProcessor;
import architecture.demo.processes.AccumulatorImplementation;
import architecture.demo.processes.Dumper;
import architecture.demo.processes.NoRumAdapter;
import architecture.demo.processes.Reporter;
import architecture.demo.processes.RumActuator;
import architecture.demo.processes.SensorSpout;
import architecture.demo.processes.SingleSensorProcessor;
import architecture.demo.processes.TendencyAnalyser;
import architecture.factory.TopicTopologyBuilder;

public class BasicArchitectureEnvironment{
		
	public static final String DUMP = "DUMP";
	
	public static TopologyBuilder construct(int nrSensorProcessors) {
		
		TopicTopologyBuilder b = new TopicTopologyBuilder();
		
		SensorSpout spout = new SensorSpout();
		b.addProcessor(spout)
				.setName("SensorSpout")
				.declareAsProducer(SENSOR);
		
		RumProcessor rum = SingleSensorProcessor.constructProcessor();
		b.addProcessor(rum)
				.setName("RumProcessor")
				.setParallelHint(nrSensorProcessors)
				.subscribeAsConsumer(SENSOR)
				.declareAsProducer(CHANGE_RPM, ACCUMULATOR, NO_RUM);
		
		RumActuator actuator = new RumActuator();
		b.addProcessor(actuator)
				.setName("RumActuator")
				.subscribeAsConsumer(CHANGE_RPM);
		
		Reporter changeRum = new Reporter("Sensor #%s changed to '%s'", SENSOR_ID, RumProcessor.NEXT_MODEL);
		b.addProcessor(changeRum)
				.setName("ChangeRum")
				.subscribeAsConsumer(CHANGE_RPM);
		
		NoRumAdapter noRum = new NoRumAdapter();
		b.addProcessor(noRum)//
				.setName("noRum")//
				.setParallelHint(1)//
				.declareAsProducer(LOG)//
				.subscribeAsConsumer(NO_RUM);
		
		AccumulatorImplementation impl = new AccumulatorImplementation();
		MappedAccumulatorProcessor accumulator = new MappedAccumulatorProcessor(Amount.valueOf(5, SI.SECOND), impl, impl);
		b.addProcessor(accumulator)
				.setName("Accumulator")
				.setParallelHint(1)
				.subscribeAsConsumer(ACCUMULATOR)
				.declareAsProducer(APPLICATION, ANALYSER);
		
		TendencyAnalyser analyser = new TendencyAnalyser();
		b.addProcessor(analyser)//
				.setName("Analyser")//
				.setParallelHint(1)//
				.declareAsProducer(LOG)//
				.subscribeAsConsumer(ANALYSER);
		
		Reporter alert = new Reporter("LOG[%s]: %s: %s", SEVERITY, FIELD_LABEL, MESSAGE);
		b.addProcessor(alert)//
				.setName("Alert")//
				.subscribeAsConsumer(LOG);
		
		Reporter exposer = new Reporter("Runtime: %s\nAvg throughput: %s\nnr sensors total: %s\nnr sensor < 1 year: %s\nbased on %s messages\n", //
				SYSTEM_RUNTIME, AVG_THROUGHPUT, NR_SENSORS, NR_LESS_YEAR, NR_MESSAGES);
		b.addProcessor(exposer)//
				.setName("Exposer")//
				.setParallelHint(1)
				.subscribeAsConsumer(APPLICATION);
		
		Dumper dump = new Dumper();
		b.addProcessor(dump)
				.setName("Dumper")
				.setParallelHint(1)
				.subscribeAsConsumer(APPLICATION);
		
		return b.build();
	}
}