package architecture.components;

import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import io.message.IOMessage;

public abstract class Spout extends BaseRichSpout implements Processor{

	private static final long serialVersionUID = 1500908714229106455L;
	Iterable<String> produceTopics;
	SpoutOutputCollector collector;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;	
	}

	@Override
	public abstract void nextTuple();

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		produceTopics.forEach(t->declarer.declareStream(t, new Fields("message")));
	}
	
	@Override
	public void publish(String topic, IOMessage m) {
		collector.emit(topic, new Values(m));
	}
	
	@Override
	public void setProduceTopics(Iterable<String> topics) {
		this.produceTopics = topics;
	}
}