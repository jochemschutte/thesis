package architecture.demo.construct;

import static architecture.demo.global.Fields.AVG_THROUGHPUT;
import static architecture.demo.global.Fields.NR_LESS_YEAR;
import static architecture.demo.global.Fields.NR_MESSAGES;
import static architecture.demo.global.Fields.NR_SENSORS;
import static architecture.demo.global.Fields.QOS;
import static architecture.demo.global.Fields.SENSOR_ID;
import static architecture.demo.global.Fields.SYSTEM_RUNTIME;
import static architecture.demo.global.Fields.YEARS_RUNNING;
import static architecture.demo.global.Topics.ACCUMULATOR;
import static architecture.demo.global.Topics.ANALYSER;
import static architecture.demo.global.Topics.APPLICATION;
import static architecture.demo.global.Topics.CHANGE_RPM;
import static architecture.demo.global.Topics.DEBUG;
import static architecture.demo.global.Topics.FIELD_LABEL;
import static architecture.demo.global.Topics.LOG;
import static architecture.demo.global.Topics.MESSAGE;
import static architecture.demo.global.Topics.NO_RUM;
import static architecture.demo.global.Topics.SENSOR;
import static architecture.demo.global.Topics.SEVERITY;

import java.io.File;
import java.io.IOException;

import javax.measure.unit.SI;

import org.apache.storm.topology.TopologyBuilder;
import org.jscience.physics.amount.Amount;

import architecture.components.MappedAccumulatorProcessor;
import architecture.components.RumProcessor;
import architecture.demo.processes.AccumulatorImplementation;
import architecture.demo.processes.Dumper;
import architecture.demo.processes.ExternalDebugLog;
import architecture.demo.processes.ExternalPrinter;
import architecture.demo.processes.NoRumAdapter;
import architecture.demo.processes.Reporter;
import architecture.demo.processes.RumActuator;
import architecture.demo.processes.SensorSpout;
import architecture.demo.processes.SingleSensorProcessor;
import architecture.demo.processes.TendencyAnalyser;
import architecture.factory.TopicTopologyBuilder;

public class BasicArchitectureEnvironment{

	public static final String KAFKA_HOST = "localhost:9092";
	
	public static TopologyBuilder construct(int nrSensorProcessors) throws IOException{
		
		ExternalPrinter printer = new ExternalPrinter("report", "externalPrinter", KAFKA_HOST);
		ExternalDebugLog log = new ExternalDebugLog(DEBUG, "externalDebugLog", KAFKA_HOST, new File("log/log.txt"));
		new Thread(printer).start();
		new Thread(log).start();
		
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
//		Reporter changeRum = new Reporter("report", "ChangeRumReporter", KAFKA_HOST, "Sensor #%s changed to '%s.", SENSOR_ID, RumProcessor.NEXT_MODEL);
		Reporter changeRum = new Reporter("report", "ChangeRumReporter", KAFKA_HOST, "Sensor #%s changed to '%s. Years: %s, QoS: %s	'", SENSOR_ID, RumProcessor.NEXT_MODEL, YEARS_RUNNING, QOS);
		b.addProcessor(changeRum)
				.setName("ChangeRumReporter")
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
		
		Reporter alert = new Reporter("report", "AlertReporter", KAFKA_HOST,"LOG[%s]: %s: %s", SEVERITY, FIELD_LABEL, MESSAGE);
		b.addProcessor(alert)//
				.setName("Alert")//
				.subscribeAsConsumer(LOG);
		
		Reporter exposer = new Reporter("report", "StatusReporter", KAFKA_HOST, "Runtime: %s\nAvg throughput: %s\nnr sensors total: %s\nnr sensor < 1 year: %s\nbased on %s messages\n", //
				SYSTEM_RUNTIME, AVG_THROUGHPUT, NR_SENSORS, NR_LESS_YEAR, NR_MESSAGES);
		b.addProcessor(exposer)//
				.setName("Exposer")//
				.setParallelHint(1)
				.subscribeAsConsumer(APPLICATION);
		
		Dumper dump = new Dumper();
		b.addProcessor(dump)
				.setName("Dumper")
				.setParallelHint(1)
				.subscribeAsConsumer(ACCUMULATOR);
		
		return b.build();
	}
}