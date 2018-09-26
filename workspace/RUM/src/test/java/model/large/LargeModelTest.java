package model.large;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import model.TestTest;
import prototype.factory.RpmBuilder;
import prototype.factory.RumBuilder;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.ResourceFunction;
import prototype.model.ResourceInterface;
import prototype.model.RumMessage;
import prototype.model.optimize.MinMaxOptimizer;

public class LargeModelTest extends TestTest{
	
	@Override
	public String getTitle() {
		return "LargeModel";
	}
			
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
		RumBuilder b = new RumBuilder();
		
		Component c1 = b.component("c1");
		Component c2 = b.component("c2");
		Component c3 = b.component("c3");
		Component c4 = b.component("c4");
		Component c5 = b.component("c5");
		Component c6 = b.component("c6");
		Component c7 = b.component("c7");
		

		ModelComponent m1 = b.modelComponent("m1");
		ModelComponent m2 = b.modelComponent("m2");
		ModelComponent m3 = b.modelComponent("m3");
		ModelComponent m4 = b.modelComponent("m4");
		ModelComponent m5 = b.modelComponent("m5");
		
		Resource r1 = b.resource("r1", "u1");
		Resource r2 = b.resource("r2", "u2");
		Resource r31 = b.resource("r31", "u31");
		Resource r32 = b.resource("r32", "u32");
		Resource r4 = b.resource("r4", "u4");
		Resource r5 = b.resource("r5", "u5");
		Resource r6 = b.resource("r6", "u6");
		Resource r7 = b.resource("r7", "u7");

		Resource mr1 = b.resource("mr1", "mu1");
		Resource mr2 = b.resource("mr2", "mu2");
		Resource mr3 = b.resource("mr3", "mu3");
		Resource mr4 = b.resource("mr4", "mu4");
		
		qos = b.optimize(new MinMaxOptimizer("qos", "score", MinMaxOptimizer.MinMax.MAX));
		
		b.connect(c1, m1, r1, new ResourceFunction(5.0), null);
		b.connect(c2, m1, r2, new ResourceFunction(5.0), null);
		b.connect(c3, m1, r31, new ResourceFunction(5.0), null);
		b.connect(c3, m2, r32, new ResourceFunction(5.0), null);
		b.connect(c4, m3, r4, new ResourceFunction(5.0), null);
		b.connect(c5, m4, r5, new ResourceFunction(5.0), null);
		b.connect(c6, m4, r6, new ResourceFunction(5.0), null);
		b.connect(c7, m5, r7, new ResourceFunction(5.0), null);
		
		b.connect(m1, m2, mr1);
		b.connect(m2, m3, mr2);
		b.connect(m3, m4, mr3);
		b.connect(m4, m5, mr4);
		
		new ResourceInterface(qos, m5, ResourceInterface.InterfaceType.OFFERS);
		
		RpmBuilder rpm1 = b.rpmBuilder(m1, r1, r2, r31, mr1);
		rpm1.add("rpm_m1_1", new ResourceFunction(3.6), new ResourceFunction(4.7), new ResourceFunction(4.1), new ResourceFunction(x->x[0]+x[1]+x[2], "r1", "r2", "r31"));
		rpm1.add("rpm_m1_2", new ResourceFunction(3.9), new ResourceFunction(5.6), new ResourceFunction(4.9), new ResourceFunction(x->x[0]+x[1]+x[2], "r1", "r2", "r31"));
		rpm1.add("rpm_m1_3", new ResourceFunction(1.7), new ResourceFunction(3.3), new ResourceFunction(2.9), new ResourceFunction(x->x[0]+x[1]+x[2], "r1", "r2", "r31"));
		rpm1.add("rpm_m1_4", new ResourceFunction(3.2), new ResourceFunction(4.3), new ResourceFunction(2.0), new ResourceFunction(x->x[0]+x[1]+x[2], "r1", "r2", "r31"));
		rpm1.add("rpm_m1_5", new ResourceFunction(0.4), new ResourceFunction(2.5), new ResourceFunction(3.5), new ResourceFunction(x->x[0]+x[1]+x[2], "r1", "r2", "r31"));

		RpmBuilder rpm2 = b.rpmBuilder(m2, r32, mr1, mr2);
		rpm2.add("rpm_m2_1", new ResourceFunction(4.8), new ResourceFunction("mr1"), new ResourceFunction(x->x[0]+x[1], "mr1", "r32"));
		rpm2.add("rpm_m2_2", new ResourceFunction(4.3), new ResourceFunction("mr1"), new ResourceFunction(x->x[0]+x[1], "mr1", "r32"));
		rpm2.add("rpm_m2_3", new ResourceFunction(3.6), new ResourceFunction("mr1"), new ResourceFunction(x->x[0]+x[1], "mr1", "r32"));
		rpm2.add("rpm_m2_4", new ResourceFunction(4.3), new ResourceFunction("mr1"), new ResourceFunction(x->x[0]+x[1], "mr1", "r32"));
		rpm2.add("rpm_m2_5", new ResourceFunction(2.8), new ResourceFunction("mr1"), new ResourceFunction(x->x[0]+x[1], "mr1", "r32"));

		RpmBuilder rpm3 = b.rpmBuilder(m3, r4, mr2, mr3);
		rpm3.add("rpm_m3_1", new ResourceFunction(6.5), new ResourceFunction("mr2"), new ResourceFunction(x->x[0]+x[1], "mr2", "r4"));
		rpm3.add("rpm_m3_2", new ResourceFunction(3.1), new ResourceFunction("mr2"), new ResourceFunction(x->x[0]+x[1], "mr2", "r4"));
		rpm3.add("rpm_m3_3", new ResourceFunction(3.9), new ResourceFunction("mr2"), new ResourceFunction(x->x[0]+x[1], "mr2", "r4"));
		rpm3.add("rpm_m3_4", new ResourceFunction(3.5), new ResourceFunction("mr2"), new ResourceFunction(x->x[0]+x[1], "mr2", "r4"));
		rpm3.add("rpm_m3_5", new ResourceFunction(0.3), new ResourceFunction("mr2"), new ResourceFunction(x->x[0]+x[1], "mr2", "r4"));

		RpmBuilder rpm4 = b.rpmBuilder(m4, r5, r6, mr3, mr4);
		rpm4.add("rpm_m4_1", new ResourceFunction(4.9), new ResourceFunction(3.1), new ResourceFunction("mr3"), new ResourceFunction(x->x[0]+x[1]+x[2], "mr3", "r5", "r6"));
		rpm4.add("rpm_m4_2", new ResourceFunction(3.7), new ResourceFunction(3.8), new ResourceFunction("mr3"), new ResourceFunction(x->x[0]+x[1]+x[2], "mr3", "r5", "r6"));
		rpm4.add("rpm_m4_3", new ResourceFunction(3.5), new ResourceFunction(1.7), new ResourceFunction("mr3"), new ResourceFunction(x->x[0]+x[1]+x[2], "mr3", "r5", "r6"));
		rpm4.add("rpm_m4_4", new ResourceFunction(2.0), new ResourceFunction(3.7), new ResourceFunction("mr3"), new ResourceFunction(x->x[0]+x[1]+x[2], "mr3", "r5", "r6"));
		rpm4.add("rpm_m4_5", new ResourceFunction(5.2), new ResourceFunction(4.7), new ResourceFunction("mr3"), new ResourceFunction(x->x[0]+x[1]+x[2], "mr3", "r5", "r6"));

		RpmBuilder rpm5 = b.rpmBuilder(m5, r7, mr4, qos);
		rpm5.add("rpm_m5_1", new ResourceFunction(4.1), new ResourceFunction("mr4"), new ResourceFunction(x->x[0]+x[1], "mr4", "r7"));
		rpm5.add("rpm_m5_2", new ResourceFunction(8.6), new ResourceFunction("mr4"), new ResourceFunction(x->x[0]+x[1], "mr4", "r7"));
		rpm5.add("rpm_m5_3", new ResourceFunction(4.7), new ResourceFunction("mr4"), new ResourceFunction(x->x[0]+x[1], "mr4", "r7"));
		rpm5.add("rpm_m5_4", new ResourceFunction(2.1), new ResourceFunction("mr4"), new ResourceFunction(x->x[0]+x[1], "mr4", "r7"));
		rpm5.add("rpm_m5_5", new ResourceFunction(2.7), new ResourceFunction("mr4"), new ResourceFunction(x->x[0]+x[1], "mr4", "r7"));

		engine = b.build();
		
		components = engine.getComponents().values();
		resources = engine.getResources().values();
		models = engine.getModels();

		makeMessage();
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

	}