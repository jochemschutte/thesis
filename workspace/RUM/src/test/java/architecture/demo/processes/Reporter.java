package architecture.demo.processes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import architecture.components.KafkaEmitterProcessor;
import io.message.IOMessage;

public class Reporter extends KafkaEmitterProcessor{

	private static final long serialVersionUID = -7348313219531277546L;
	String format;
	String[] args;
	
	public Reporter(String topic, String producerName, String kafkaHost, String format, String... args) {
		super(topic, producerName, kafkaHost);
		this.format = format;
		this.args = args;
	}
	
	public IOMessage prepareMessage(IOMessage m) {
		Object[] vars = Arrays.stream(args).map(v->m.getVars().get(v)).toArray();
		Map<String, String> varMap = new HashMap<>();
		varMap.put("message", String.format(format, vars));
		return new IOMessage(varMap);
	}
	
}