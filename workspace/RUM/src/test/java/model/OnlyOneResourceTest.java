package model;

import java.io.IOException;
import java.util.HashSet;

import org.junit.Ignore;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import prototype.main.RumEngine;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.ResourceFunction;
import prototype.model.ResourceInterface;
import prototype.model.ResourceInterface.InterfaceType;
import prototype.model.RumMessage;
import prototype.model.optimize.MinMaxOptimizer;
import prototype.model.optimize.Optimizer;

public class OnlyOneResourceTest extends TestTest{

	@Override
	public String getTitle() {
		return "OnlyOneResourceTest";
	}
	
	@Ignore
	@Override
	public void runTimerNtimesAndMeasure() throws IOException {
		//non
	}
	
	@Override
	public void prepare() {
		Resource r = new Resource("resource", "credits");
		
		ModelComponent p1 = produceProducer("p1",r);
		ModelComponent p2 = produceProducer("p2",r);
		ModelComponent p3 = produceProducer("p3",r);
		
		Component c1 = produceConsumer("c1", r);
		Component c2 = produceConsumer("c2", r);
		Component c3 = produceConsumer("c3", r);
		Component c4 = produceConsumer("c4", r);
		
		RPM rpm11 = produceRpm("rpm11", r);
		RPM rpm12 = produceRpm("rpm12", r);
		RPM rpm13 = produceRpm("rpm13", r);
		RPM rpm21 = produceRpm("rpm21", r);
		RPM rpm22 = produceRpm("rpm22", r);
		RPM rpm23 = produceRpm("rpm23", r);
		RPM rpm31 = produceRpm("rpm31", r);
		RPM rpm32 = produceRpm("rpm32", r);
		RPM rpm33 = produceRpm("rpm33", r);
		
		qos = produceOptimizer(new Component[] {c1,c2,c3,c4});
		
		resources = ImmutableSet.of(r, qos);
		
		components = new HashSet<>();
		components.add(p1);
		components.add(p2);
		components.add(p3);
		components.add(c1);
		components.add(c2);
		components.add(c3);
		components.add(c4);

		models = HashMultimap.create();
		models.putAll(p1, ImmutableSet.of(rpm11, rpm12, rpm13));
		models.putAll(p2, ImmutableSet.of(rpm21, rpm22, rpm23));
		models.putAll(p3, ImmutableSet.of(rpm31, rpm32, rpm33));
		
		message = new RumMessage();
		message.setCurrentState(ImmutableMap.of(p1,rpm11,p2,rpm21,p3,rpm31));
		
		engine = new RumEngine(components, qos, models);

		
	}
	
	private Optimizer produceOptimizer(Component... cs) {
		Optimizer o = new MinMaxOptimizer("score", "points", MinMaxOptimizer.MinMax.MAX);
		for(Component c: cs) {
			new ResourceInterface(o, c, InterfaceType.OFFERS);
			c.getResourceFunctions().put(o, new ResourceFunction("resource"));
		}
		return o;
	}
	
	private ModelComponent produceProducer(String name, Resource r) {
		ModelComponent c = new ModelComponent(name);
		new ResourceInterface(r, c, InterfaceType.OFFERS);
		return c;
	}
	
	private Component produceConsumer(String name, Resource r) {
		Component c = new Component(name);
		new ResourceInterface(r, c, InterfaceType.CONSUMES);
		c.getResourceFunctions().put(r, new ResourceFunction("resource/4"));
		return c;
	}
	
	private RPM produceRpm(String name, Resource r) {
		RPM result = new RPM(name);
		result.getResourceFunctions().put(r, new ResourceFunction("1"));
		return result;
	}
//	
//	@Ignore
//	@Test
//	public void make() {
//		String var = "components";
//		String prefix = "c";
//		String result = var + " = new HashSet<>();\n";
//		for(int i = 1; i <= 3; i++) {
//			result += String.format("%s.add(%s%d);\n", var, prefix, i);
//		}
//		System.out.println(result);
//	}
	
	
}