package storm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.utils.Utils;

import architecture.factory.TopicTopologyBuilder;

public class StormTest{
	
	public static BufferedWriter out;
	
	public static void main(String[] args) throws IOException{
		out = new BufferedWriter(new FileWriter(new File("target/storm-test.txt")));
		
		TopicTopologyBuilder b = new TopicTopologyBuilder();
		int ss = 3;
		int ps = 3;
		
		List<Starter> starters = IntStream.range(0, ss).mapToObj(i->new Starter(i, ps)).collect(Collectors.toList());
		starters.forEach(s->b.addProcessor(s));
		starters.forEach(s->b.getHelpers().get(s).setName("s"+s.id).setParallelHint(1));
		List<Printer> printers = IntStream.range(0, ps).mapToObj(i->new Printer(i)).collect(Collectors.toList());
		printers.forEach(p->b.addProcessor(p));
		printers.forEach(p->b.getHelpers().get(p).setName("p"+p.id).setParallelHint(1));
		for(Starter s : starters) {
			b.getHelpers().get(s).declareAsProducer(IntStream.range(0,  ps).mapToObj(i->"c"+i).toArray(String[]::new));
		}
		printers.forEach(p->b.getHelpers().get(p).subscribeAsConsumer("c"+p.id));
		
		
		
		Config conf = new Config();
		conf.setDebug(false);
		conf.setNumWorkers(2);

		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("test", conf, b.build().createTopology());
		Utils.sleep(10000);
		cluster.killTopology("test");
		cluster.shutdown();
		
		out.flush();
		out.close();
		
	}
}