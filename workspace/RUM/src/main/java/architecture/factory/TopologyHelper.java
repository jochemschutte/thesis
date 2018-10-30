package architecture.factory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TopologyHelper{
	
	Integer nrParallel = null;
	String name = null;
	List<String> producerTopics = new LinkedList<>();
	List<String> consumerTopics = new LinkedList<>();
	

	public TopologyHelper setName(String name) {
		this.name = name;
		return this;
	}
	
	public TopologyHelper setParallelHint(int nrParallel) {
		this.nrParallel = nrParallel;
		return this;
	}
	
	public TopologyHelper subscribeAsConsumer(String... topics) {
		this.consumerTopics.addAll(Arrays.asList(topics));
		return this;
	}
	
	public TopologyHelper declareAsProducer(String... topics) {
		this.producerTopics.addAll(Arrays.asList(topics));
		return this;
	}
	
	public Integer getParallelHint() {
		return this.nrParallel;
	}
	
	List<String> getProducerTopics(){
		return producerTopics;
	}
	
	List<String> getConsumerTopics(){
		return consumerTopics;
	}
	
	public String getName() {
		return this.name;
	}
}