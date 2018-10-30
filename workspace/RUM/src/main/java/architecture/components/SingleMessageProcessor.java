package architecture.components;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import io.message.IOMessage;

public abstract class SingleMessageProcessor extends BaseRichBolt implements Processor{

	private static final long serialVersionUID = 1077641428863982083L;
	private OutputCollector collector;
	Iterable<String> produceTopics;
	
	@Override
	@SuppressWarnings("rawtypes")
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }
	
	@Override
	public void execute(Tuple in) {
		runForMessage((IOMessage)in.getValueByField("message"));
	}
		
	public abstract void runForMessage(IOMessage m);
	
	@Override
	public void publish(String topic, IOMessage message) {
		if(collector != null) {
			collector.emit(topic, new Values(message));
		}
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		produceTopics.forEach(t->declarer.declareStream(t, new Fields("message")));
	}
	
	@Override
	public void setProduceTopics(Iterable<String> topics) {
		this.produceTopics = topics;
	}
	
}