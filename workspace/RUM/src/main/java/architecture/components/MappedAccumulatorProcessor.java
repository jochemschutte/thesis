package architecture.components;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import com.google.common.collect.ImmutableList;

import io.message.IOMessage;

public class MappedAccumulatorProcessor extends AccumulatorProcessor{
	
	private static final long serialVersionUID = -5304644497500382937L;
	List<MapReducer> mapReducers;
	MapCollector collector;

	public MappedAccumulatorProcessor(long millis, MapReducer mapReducer, MapCollector collector) {
		this(millis, ImmutableList.of(mapReducer), collector);
	}
	
	public MappedAccumulatorProcessor(Amount<Duration> time, MapReducer mapReducer, MapCollector collector) {
		this(time, ImmutableList.of(mapReducer), collector);
	}
	
	public MappedAccumulatorProcessor(Amount<Duration> time, List<MapReducer> mapReducers, MapCollector collector) {
		this(time.to(SI.MILLI(SI.SECOND)).getExactValue(), mapReducers, collector);
	}
	
	public MappedAccumulatorProcessor(long millis, List<MapReducer> mapReducers, MapCollector collector) {
		super(millis);
		if(mapReducers == null || mapReducers.size() < 1)
			throw new IllegalArgumentException("MappedAccumulator requires at least one MapReducer to function");
		this.mapReducers = mapReducers;
		this.collector = collector;
	}
	
	@Override
	public void runForRange(List<IOMessage> range) {
		Iterator<MapReducer> mapIter = mapReducers.iterator();
		MapReducer reducer = mapIter.next();
		Map<String, List<IOMessage>> mapped = map(range, reducer);
		Map<String, IOMessage> reduced = reduce(mapped, reducer); 
		while(mapIter.hasNext()) {
			reducer = mapIter.next();
			mapped = map(reduced.values(), reducer); 
			reduced = reduce(mapped, reducer); 
		}
		Map<String, IOMessage> exports = collector.collect(reduced);
		for(Map.Entry<String, IOMessage> entry: exports.entrySet()) {
//			System.out.println(entry.getKey() + " : " + entry.getValue().getVars());
			publish(entry.getKey(), entry.getValue());
		}
//		exports.entrySet().forEach(e -> publish(e.getKey(), e.getValue()));
	}
	

	private static Map<String, List<IOMessage>> map(Collection<IOMessage> range, MapReducer mapReducer){
		Stream<IOMessage> stream = range.stream();
		return stream.collect(Collectors.groupingBy(m -> mapReducer.map(m)));
	}
	
	private static Map<String, IOMessage> reduce(Map<String, List<IOMessage>> range, MapReducer mapReducer){
		return range.entrySet().stream().collect(Collectors.toMap(e->e.getKey(), e -> reduce(e.getKey(), e.getValue(), mapReducer)));
	}
	
	private static IOMessage reduce(String key, List<IOMessage> range, MapReducer mapReducer) {
		List<IOMessage> sorted = range;
		if(mapReducer.getComparator() != null)
			sorted = sorted.stream().sorted(mapReducer.getComparator()).collect(Collectors.toList());
		return mapReducer.reduce(key, sorted);
	}
	//implementors of actual behavior
	
	public interface MapReducer extends Serializable{
		public String map(IOMessage message);
		public Comparator<IOMessage> getComparator(); 
		public IOMessage reduce(String key, List<IOMessage> range);
		
		
	}
	
	public interface MapCollector extends Serializable{
		public Map<String, IOMessage> collect(Map<String, IOMessage> reduced);
	}


	@Override
	public void sortRange(List<IOMessage> range) {
		//inherited dummy. sorting is done by mapreducers		
	}

}