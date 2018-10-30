package architecture.factory;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.TopologyBuilder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import architecture.components.Processor;
import architecture.components.SingleMessageProcessor;
import architecture.components.Spout;

public class TopicTopologyBuilder{
	
	private static final String DECOUPLE_MSG = "[WARN] '%s' %s on topic '%s' but is never %s.";
	Map<Processor, TopologyHelper> processors = new HashMap<>();
	
	public Map<Processor, TopologyHelper> getHelpers(){
		return processors;
	}
	
	public TopologyHelper addProcessor(Processor p) {
		//TODO check doubles
		TopologyHelper helper = new TopologyHelper();
		processors.put(p, helper);
		return helper;
	}
	
	public TopologyHelper addProcessor(Processor p, TopologyHelper h) {
		//TODO check doubles
		processors.put(p,  h);
		return h;
	}
	
	public TopologyBuilder build() {
		//TODO check double name
		SetMultimap<String, Processor> producers = compileProducers(processors);
		SetMultimap<String, Processor> consumers = compileConsumers(processors);
		crossCheck(producers, consumers, processors);
		TopologyBuilder b = new TopologyBuilder();
		for(Map.Entry<Processor, TopologyHelper> entry : processors.entrySet()) {
			if(entry.getValue().getName() == null) {
				throw new IllegalArgumentException("Spout or bolt doesn't have a name.");
			}
			TopologyHelper h = entry.getValue();
			Processor p = entry.getKey();
			if(p instanceof Spout) {
				Spout spout = (Spout)p;
				if(h.getConsumerTopics().size() > 0) {
					throw new IllegalArgumentException("Spout has subscriptions to topics. Spouts can only produce.");
				}
				if(h.getParallelHint() == null) {
					b.setSpout(h.getName(), spout);
				}else {
					b.setSpout(h.getName(), spout, h.getParallelHint());
				}
			}else {
				SingleMessageProcessor bolt = (SingleMessageProcessor)p;
				BoltDeclarer decl;
				if(h.getParallelHint() == null) {
					decl = b.setBolt(h.getName(), bolt);
				}else {
					decl = b.setBolt(h.getName(), bolt, h.getParallelHint());
				}
				for(String consumeTopic : h.getConsumerTopics()) {
//					producers.get(consumeTopic).forEach(prod->System.out.println(String.format("prod %s, con %s, topic %s", processors.get(prod).getName(), h.getName(), consumeTopic)));
					producers.get(consumeTopic).forEach(prod->decl.shuffleGrouping(processors.get(prod).getName(), consumeTopic));
				}
			}
			p.setProduceTopics(h.getProducerTopics());
		}
		return b;
	}
	
	private boolean crossCheck(SetMultimap<String, Processor> produce, SetMultimap<String, Processor> consume, Map<Processor, TopologyHelper> helpers) {
		SetMultimap<String, Processor> produceCp = HashMultimap.create(produce);
		SetMultimap<String, Processor> consumeCp = HashMultimap.create(consume);
		produce.keySet().forEach(k -> consumeCp.removeAll(k));
		consume.keySet().forEach(k -> produceCp.removeAll(k));
		for(String topic : consumeCp.keySet()) {
			consumeCp.get(topic).forEach(p -> System.out.println(String.format(DECOUPLE_MSG, helpers.get(p).getName(), "consumes", topic, "produced")));
		}
		for(String topic : produceCp.keySet()) {
			produceCp.get(topic).forEach(p -> System.out.println(String.format(DECOUPLE_MSG, helpers.get(p).getName(), "produces", topic, "consumed")));
		}
		return !produceCp.isEmpty() || !consumeCp.isEmpty();
	}
	
	private static SetMultimap<String, Processor> compileProducers(Map<Processor, TopologyHelper> processors) {
		SetMultimap<String, Processor> result = HashMultimap.create();
		for(Map.Entry<Processor, TopologyHelper> entry : processors.entrySet()) {
			entry.getValue().getProducerTopics().forEach(t->result.put(t, entry.getKey()));
		}
		return result;
	}
	
	private static SetMultimap<String, Processor> compileConsumers(Map<Processor, TopologyHelper> processors) {
		SetMultimap<String, Processor> result = HashMultimap.create();
		for(Map.Entry<Processor, TopologyHelper> entry : processors.entrySet()) {
			entry.getValue().getConsumerTopics().forEach(t->result.put(t, entry.getKey()));
		}
		return result;
	}
		
}