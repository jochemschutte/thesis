package architecture.demo.processes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;

import architecture.components.SingleMessageProcessor;
import io.message.IOMessage;

public class Dumper extends SingleMessageProcessor{

	BufferedWriter out;
	
	@Override
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		try{
			out = new BufferedWriter(new FileWriter(new File("log/dump.txt")));
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void runForMessage(IOMessage m) {
		try{
			out.write(m.getVars().toString()+'\n');
			out.flush();
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}