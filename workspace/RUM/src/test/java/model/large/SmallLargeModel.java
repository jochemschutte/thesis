package model.large;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.HashMultimap;

import model.TestTest;
import prototype.main.RumEngine;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.ResourceFunction;
import prototype.model.ResourceInterface;
import prototype.model.RumMessage;
import prototype.model.optimize.MinMaxOptimizer;

public class SmallLargeModel extends TestTest{
	
	public void makeMessage() {
		Map<ModelComponent, RPM> modelMap = new HashMap<>();
		for(ModelComponent c : models.keySet()) {
			modelMap.put(c, models.get(c).stream().findFirst().get());
		}
		message = new RumMessage();
		message.setCurrentState(modelMap);
		message.setVars(new HashMap<>());
		
	}
	
	@Override
	public void prepare() {
		Component c1 = new Component("c1");
		Component c2 = new Component("c2");
		Component c3 = new Component("c3");
		Component c4 = new Component("c4");
		Component c5 = new Component("c5");
		Component c6 = new Component("c6");
		Component c7 = new Component("c7");
		
		components = new HashSet<>();
		components.add(c1);
		components.add(c2);
		components.add(c3);
		components.add(c4);
		components.add(c5);
		components.add(c6);
		components.add(c7);

		ModelComponent m1 = new ModelComponent("m1");
		ModelComponent m2 = new ModelComponent("m2");
		ModelComponent m3 = new ModelComponent("m3");
		ModelComponent m4 = new ModelComponent("m4");
		ModelComponent m5 = new ModelComponent("m5");
		
		components.add(m1);
		components.add(m2);
		components.add(m3);
		components.add(m4);
		components.add(m5);

		Resource r1 = new Resource("r1", "u1");
		Resource r2 = new Resource("r2", "u2");
		Resource r31 = new Resource("r31", "u31");
		Resource r32 = new Resource("r32", "u32");
		Resource r4 = new Resource("r4", "u4");
		Resource r5 = new Resource("r5", "u5");
		Resource r6 = new Resource("r6", "u6");
		Resource r7 = new Resource("r7", "u7");

		resources = new HashSet<>();
		resources.add(r1);
		resources.add(r2);
		resources.add(r31);
		resources.add(r32);
		resources.add(r4);
		resources.add(r5);
		resources.add(r6);
		resources.add(r7);
		
		Resource mr1 = new Resource("mr1", "mu1");
		Resource mr2 = new Resource("mr2", "mu2");
		Resource mr3 = new Resource("mr3", "mu3");
		Resource mr4 = new Resource("mr4", "mu4");
		
		resources.add(mr1);
		resources.add(mr2);
		resources.add(mr3);
		resources.add(mr4);
		
		qos = new MinMaxOptimizer("qos", "score", MinMaxOptimizer.MinMax.MAX);

		resources.add(qos);
		
		connect(c1, m1, r1, "5", null);
		connect(c2, m1, r2, "5", null);
		connect(c3, m1, r31, "5", null);
		connect(c3, m2, r32, "5", null);
		connect(c4, m3, r4, "5", null);
		connect(c5, m4, r5, "5", null);
		connect(c6, m4, r6, "5", null);
		connect(c7, m5, r7, "5", null);
//		connect(c1, c2, r1, "5", null);
		
		connect(m1, m2, mr1);
		connect(m2, m3, mr2);
		connect(m3, m4, mr3);
		connect(m4, m5, mr4);
		
		new ResourceInterface(qos, m5, ResourceInterface.InterfaceType.OFFERS);
		
		RPM rpm_m1_1 = new RPM("rpm_m1_1");
		rpm_m1_1.getResourceFunctions().put(r1, new ResourceFunction("3.6"));
		rpm_m1_1.getResourceFunctions().put(r2, new ResourceFunction("4.7"));
		rpm_m1_1.getResourceFunctions().put(r31, new ResourceFunction("4.1"));
		rpm_m1_1.getResourceFunctions().put(mr1, new ResourceFunction("r1+r2+r31"));

		RPM rpm_m1_2 = new RPM("rpm_m1_2");
		rpm_m1_2.getResourceFunctions().put(r1, new ResourceFunction("3.9"));
		rpm_m1_2.getResourceFunctions().put(r2, new ResourceFunction("5.6"));
		rpm_m1_2.getResourceFunctions().put(r31, new ResourceFunction("4.9"));
		rpm_m1_2.getResourceFunctions().put(mr1, new ResourceFunction("r1+r2+r31"));

		RPM rpm_m1_3 = new RPM("rpm_m1_3");
		rpm_m1_3.getResourceFunctions().put(r1, new ResourceFunction("1.7"));
		rpm_m1_3.getResourceFunctions().put(r2, new ResourceFunction("3.3"));
		rpm_m1_3.getResourceFunctions().put(r31, new ResourceFunction("1.9"));
		rpm_m1_3.getResourceFunctions().put(mr1, new ResourceFunction("r1+r2+r31"));

		RPM rpm_m1_4 = new RPM("rpm_m1_4");
		rpm_m1_4.getResourceFunctions().put(r1, new ResourceFunction("3.2"));
		rpm_m1_4.getResourceFunctions().put(r2, new ResourceFunction("4.3"));
		rpm_m1_4.getResourceFunctions().put(r31, new ResourceFunction("2.0"));
		rpm_m1_4.getResourceFunctions().put(mr1, new ResourceFunction("r1+r2+r31"));

		RPM rpm_m1_5 = new RPM("rpm_m1_5");
		rpm_m1_5.getResourceFunctions().put(r1, new ResourceFunction("0.4"));
		rpm_m1_5.getResourceFunctions().put(r2, new ResourceFunction("2.5"));
		rpm_m1_5.getResourceFunctions().put(r31, new ResourceFunction("3.5"));
		rpm_m1_5.getResourceFunctions().put(mr1, new ResourceFunction("r1+r2+r31"));

		// rm2
		RPM rpm_m2_1 = new RPM("rpm_m2_1");
		rpm_m2_1.getResourceFunctions().put(r32, new ResourceFunction("4.8"));
		rpm_m2_1.getResourceFunctions().put(mr1, new ResourceFunction("mr1"));
		rpm_m2_1.getResourceFunctions().put(mr2, new ResourceFunction("mr1+r32"));

		RPM rpm_m2_2 = new RPM("rpm_m2_2");
		rpm_m2_2.getResourceFunctions().put(r32, new ResourceFunction("4.3"));
		rpm_m2_2.getResourceFunctions().put(mr1, new ResourceFunction("mr1"));
		rpm_m2_2.getResourceFunctions().put(mr2, new ResourceFunction("mr1+r32"));

		RPM rpm_m2_3 = new RPM("rpm_m2_3");
		rpm_m2_3.getResourceFunctions().put(r32, new ResourceFunction("3.6"));
		rpm_m2_3.getResourceFunctions().put(mr1, new ResourceFunction("mr1"));
		rpm_m2_3.getResourceFunctions().put(mr2, new ResourceFunction("mr1+r32"));

		RPM rpm_m2_4 = new RPM("rpm_m2_4");
		rpm_m2_4.getResourceFunctions().put(r32, new ResourceFunction("4.3"));
		rpm_m2_4.getResourceFunctions().put(mr1, new ResourceFunction("mr1"));
		rpm_m2_4.getResourceFunctions().put(mr2, new ResourceFunction("mr1+r32"));

		RPM rpm_m2_5 = new RPM("rpm_m2_5");
		rpm_m2_5.getResourceFunctions().put(r32, new ResourceFunction("2.8"));
		rpm_m2_5.getResourceFunctions().put(mr1, new ResourceFunction("mr1"));
		rpm_m2_5.getResourceFunctions().put(mr2, new ResourceFunction("mr1+r32"));

		//m3
		RPM rpm_m3_1 = new RPM("rpm_m3_1");
		rpm_m3_1.getResourceFunctions().put(r4, new ResourceFunction("6.5"));
		rpm_m3_1.getResourceFunctions().put(mr2, new ResourceFunction("mr2"));
		rpm_m3_1.getResourceFunctions().put(mr3, new ResourceFunction("mr2+r4"));

		RPM rpm_m3_2 = new RPM("rpm_m3_2");
		rpm_m3_2.getResourceFunctions().put(r4, new ResourceFunction("3.1"));
		rpm_m3_2.getResourceFunctions().put(mr2, new ResourceFunction("mr2"));
		rpm_m3_2.getResourceFunctions().put(mr3, new ResourceFunction("mr2+r4"));

		RPM rpm_m3_3 = new RPM("rpm_m3_3");
		rpm_m3_3.getResourceFunctions().put(r4, new ResourceFunction("3.9"));
		rpm_m3_3.getResourceFunctions().put(mr2, new ResourceFunction("mr2"));
		rpm_m3_3.getResourceFunctions().put(mr3, new ResourceFunction("mr2+r4"));

		RPM rpm_m3_4 = new RPM("rpm_m3_4");
		rpm_m3_4.getResourceFunctions().put(r4, new ResourceFunction("3.5"));
		rpm_m3_4.getResourceFunctions().put(mr2, new ResourceFunction("mr2"));
		rpm_m3_4.getResourceFunctions().put(mr3, new ResourceFunction("mr2+r4"));

		RPM rpm_m3_5 = new RPM("rpm_m3_5");
		rpm_m3_5.getResourceFunctions().put(r4, new ResourceFunction("0.3"));
		rpm_m3_5.getResourceFunctions().put(mr2, new ResourceFunction("mr2"));
		rpm_m3_5.getResourceFunctions().put(mr3, new ResourceFunction("mr2+r4"));
		
		//m4
		RPM rpm_m4_1 = new RPM("rpm_m4_1");
		rpm_m4_1.getResourceFunctions().put(r5, new ResourceFunction("4.9"));
		rpm_m4_1.getResourceFunctions().put(r6, new ResourceFunction("3.1"));
		rpm_m4_1.getResourceFunctions().put(mr3, new ResourceFunction("mr3"));
		rpm_m4_1.getResourceFunctions().put(mr4, new ResourceFunction("mr3+r5+r6"));

		RPM rpm_m4_2 = new RPM("rpm_m4_2");
		rpm_m4_2.getResourceFunctions().put(r5, new ResourceFunction("3.7"));
		rpm_m4_2.getResourceFunctions().put(r6, new ResourceFunction("3.8"));
		rpm_m4_2.getResourceFunctions().put(mr3, new ResourceFunction("mr3"));
		rpm_m4_2.getResourceFunctions().put(mr4, new ResourceFunction("mr3+r5+r6"));

		RPM rpm_m4_3 = new RPM("rpm_m4_3");
		rpm_m4_3.getResourceFunctions().put(r5, new ResourceFunction("3.5"));
		rpm_m4_3.getResourceFunctions().put(r6, new ResourceFunction("1.7"));
		rpm_m4_3.getResourceFunctions().put(mr3, new ResourceFunction("mr3"));
		rpm_m4_3.getResourceFunctions().put(mr4, new ResourceFunction("mr3+r5+r6"));

		RPM rpm_m4_4 = new RPM("rpm_m4_4");
		rpm_m4_4.getResourceFunctions().put(r5, new ResourceFunction("2.0"));
		rpm_m4_4.getResourceFunctions().put(r6, new ResourceFunction("3.7"));
		rpm_m4_4.getResourceFunctions().put(mr3, new ResourceFunction("mr3"));
		rpm_m4_4.getResourceFunctions().put(mr4, new ResourceFunction("mr3+r5+r6"));

		RPM rpm_m4_5 = new RPM("rpm_m4_5");
		rpm_m4_5.getResourceFunctions().put(r5, new ResourceFunction("5.2"));
		rpm_m4_5.getResourceFunctions().put(r6, new ResourceFunction("4.7"));
		rpm_m4_5.getResourceFunctions().put(mr3, new ResourceFunction("mr3"));
		rpm_m4_5.getResourceFunctions().put(mr4, new ResourceFunction("mr3+r5+r6"));

		//m4
		RPM rpm_m5_1 = new RPM("rpm_m5_1");
		rpm_m5_1.getResourceFunctions().put(r7, new ResourceFunction("4.1"));
		rpm_m5_1.getResourceFunctions().put(mr4, new ResourceFunction("mr4"));
		rpm_m5_1.getResourceFunctions().put(qos, new ResourceFunction("mr4+r7"));

		RPM rpm_m5_2 = new RPM("rpm_m5_2");
		rpm_m5_2.getResourceFunctions().put(r7, new ResourceFunction("8.6"));
		rpm_m5_2.getResourceFunctions().put(mr4, new ResourceFunction("mr4"));
		rpm_m5_2.getResourceFunctions().put(qos, new ResourceFunction("mr4+r7"));

		RPM rpm_m5_3 = new RPM("rpm_m5_3");
		rpm_m5_3.getResourceFunctions().put(r7, new ResourceFunction("4.7"));
		rpm_m5_3.getResourceFunctions().put(mr4, new ResourceFunction("mr4"));
		rpm_m5_3.getResourceFunctions().put(qos, new ResourceFunction("mr4+r7"));

		RPM rpm_m5_4 = new RPM("rpm_m5_4");
		rpm_m5_4.getResourceFunctions().put(r7, new ResourceFunction("2.1"));
		rpm_m5_4.getResourceFunctions().put(mr4, new ResourceFunction("mr4"));
		rpm_m5_4.getResourceFunctions().put(qos, new ResourceFunction("mr4+r7"));

		RPM rpm_m5_5 = new RPM("rpm_m5_5");
		rpm_m5_5.getResourceFunctions().put(r7, new ResourceFunction("2.7"));
		rpm_m5_5.getResourceFunctions().put(mr4, new ResourceFunction("mr4"));
		rpm_m5_5.getResourceFunctions().put(qos, new ResourceFunction("mr4+r7"));

		//set in models
		models = HashMultimap.create();
		
		models.put(m1, rpm_m1_1);
		models.put(m1, rpm_m1_2);
		models.put(m1, rpm_m1_3);
//		models.put(m1, rpm_m1_4);
//		models.put(m1, rpm_m1_5);

		models.put(m2, rpm_m2_1);
		models.put(m2, rpm_m2_2);
		models.put(m2, rpm_m2_3);
//		models.put(m2, rpm_m2_4);
//		models.put(m2, rpm_m2_5);

		models.put(m3, rpm_m3_1);
		models.put(m3, rpm_m3_2);
		models.put(m3, rpm_m3_3);
//		models.put(m3, rpm_m3_4);
//		models.put(m3, rpm_m3_5);

		models.put(m4, rpm_m4_1);
//		models.put(m4, rpm_m4_2);
//		models.put(m4, rpm_m4_3);
//		models.put(m4, rpm_m4_4);
//		models.put(m4, rpm_m4_5);

		models.put(m5, rpm_m5_1);
//		models.put(m5, rpm_m5_2);
//		models.put(m5, rpm_m5_3);
//		models.put(m5, rpm_m5_4);
//		models.put(m5, rpm_m5_5);

		makeMessage();

		engine = new RumEngine(components, qos, models);

	}
	
	@Ignore
	@Test
	public void gen() {
		int iterations = 5;
		int m = 5;
		
		String result = "SetMultimap models = HashMultimap.create();\n\n";
		for(int i = 1; i <= iterations; i++) {
//			for(int j = 1; j <= iterations; j++) {
//				result += String.format("models.put(m%d, rpm_m%d_%d);\n", i, i, j);
//			}
//			result += "\n";
			result += String.format("RPM rpm_m%d_%d = new RPM(\"rpm_m%d_%d\");\n", m, i, m, i);
			result += String.format("rpm_m%d_%d.getResourceFunctions().put(r7, new ResourceFunction(\"%.1f\"));\n", m, i, Math.random()*10);
			
			result += String.format("rpm_m%d_%d.getResourceFunctions().put(mr4, new ResourceFunction(\"mr4\"));\n", m, i);
			result += String.format("rpm_m%d_%d.getResourceFunctions().put(qos, new ResourceFunction(\"mr4+r7\"));\n", m, i);
			result += "\n";
		}
		System.out.println(result);
	}

	@Override
	protected String getTitle() {
		return "Small large test";
	}

	
}