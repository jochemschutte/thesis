package architecture.demo.processes;

import static architecture.demo.global.Fields.AVG_THROUGHPUT;
import static architecture.demo.global.Fields.NETWORK_BANDWIDTH;
import static architecture.demo.global.Fields.NR_LESS_YEAR;
import static architecture.demo.global.Fields.NR_MESSAGES;
import static architecture.demo.global.Fields.SENSOR_ID;
import static architecture.demo.global.Fields.SERVICE_TIME;
import static architecture.demo.global.Fields.SUM_THROUGHPUT;
import static architecture.demo.global.Fields.SYSTEM_RUNTIME;
import static architecture.demo.global.Fields.THROUGHPUT;
import static architecture.demo.global.Fields.TOTAL_USED_BANDWIDTH;
import static architecture.demo.global.Topics.ANALYSER;
import static architecture.demo.global.Topics.APPLICATION;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import architecture.components.MappedAccumulatorProcessor;
import architecture.demo.global.ConfigurableTimer;
import architecture.demo.helpers.TimestampComparator;
import io.message.IOMessage;

public class AccumulatorImplementation implements MappedAccumulatorProcessor.MapReducer, MappedAccumulatorProcessor.MapCollector{

	long startMillis;
	
	public AccumulatorImplementation() {
		startMillis = ConfigurableTimer.getInstance().getTimeElapsedMillis();
	}
	
	@Override
	public String map(IOMessage message) {
		return message.getVars().get(SENSOR_ID);
	}

	@Override
	public IOMessage reduce(String key, List<IOMessage> range) {
		Map<String, String> result = new HashMap<>();
		boolean lessThenYear = Double.parseDouble(range.get(range.size()-1).getVars().get(SERVICE_TIME)) < 1.0;
		result.put(NR_LESS_YEAR, lessThenYear ? "1" : "0");
		result.put(SUM_THROUGHPUT, Double.toString(range.stream().mapToDouble(m -> Double.parseDouble(m.getVars().get(THROUGHPUT))).sum()));
		result.put(TOTAL_USED_BANDWIDTH, Double.toString(range.stream().mapToDouble(m->Double.parseDouble(m.getVars().get(NETWORK_BANDWIDTH))).sum()));
		result.put(NR_MESSAGES, Integer.toString(range.size()));
		return new IOMessage(result);
	}
	
	public Comparator<IOMessage> getComparator(){
		return new TimestampComparator();
	}

	@Override
	public Map<String, IOMessage> collect(Map<String, IOMessage> reduced) {
		float totalThroughput = reduced.values().stream()
				.map(v -> Float.parseFloat(v.getVars().get(SUM_THROUGHPUT))).reduce((float)0.0, (a,b)->a+b);
		long nrMessages = reduced.values().stream()
				.mapToLong(m -> Long.parseLong(m.getVars().get(NR_MESSAGES))).sum();
		float avgThroughput = totalThroughput == 0 ? 0 : totalThroughput/nrMessages;
		long nrLessThenYear = reduced.values().stream()
				.mapToLong(m -> Long.parseLong(m.getVars().get(NR_LESS_YEAR))).sum();
		float totalBandwidthUsed = reduced.values().stream()
				.map(m -> Float.parseFloat(m.getVars().get(TOTAL_USED_BANDWIDTH))).reduce((float)0.0, (a,b)->a+b);
		long millisRunning = (ConfigurableTimer.getInstance().getTimeElapsedMillis()-startMillis);
		long daysRunning = millisRunning/86400000;
		
		long yearsRunning = daysRunning/365;
		daysRunning %= 365;
		long monthsRunning = daysRunning/30;
		daysRunning %= 30;
		String timeRunning = String.format("y:%d m:%d d:%d", yearsRunning, monthsRunning, daysRunning);
		Map<String, IOMessage> exports = new HashMap<>();
		IOMessage message = new IOMessage(ImmutableMap.of(SYSTEM_RUNTIME, timeRunning, AVG_THROUGHPUT, avgThroughput+"", NR_LESS_YEAR, nrLessThenYear+"", NR_MESSAGES, nrMessages+""));
		exports.put(APPLICATION, message);
		exports.put(ANALYSER, new IOMessage(ImmutableMap.of(TOTAL_USED_BANDWIDTH, totalBandwidthUsed+"")));
		return exports;
	}
	
	
}