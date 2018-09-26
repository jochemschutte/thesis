package model;

import static javax.measure.unit.NonSI.HOUR;
import static javax.measure.unit.NonSI.MINUTE;
import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.SECOND;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.measure.quantity.Duration;

import org.jscience.physics.amount.Amount;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import prototype.main.RumEngine;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.ResourceInterface;
import prototype.model.RumMessage;
import prototype.model.optimize.Optimizer;
import prototype.model.requirements.Requirement;

public abstract class TestTest{

	protected RumEngine engine = null;
	protected Collection<Component> components = null;
	protected Collection<Resource> resources = null;
	protected Optimizer qos = null;
	protected SetMultimap<ModelComponent, RPM> models = null;
	
	protected RumMessage message = null;
	
	private static BufferedWriter out = null;
	private static boolean writerOpenedBefore = false;
	
	@BeforeClass
	public static void openIO() throws IOException{
		out = new BufferedWriter(new FileWriter("test-times.csv", writerOpenedBefore));
		writerOpenedBefore = true;
	}
	
	@AfterClass
	public static void closeIO() throws IOException{
		out.flush();
		out.close();
	}
	
	@Before
	public abstract void prepare();
	
	protected abstract String getTitle();

	protected void validatePrepare() {
		assertNotNull(engine);
		assertNotNull(components);
		assertNotNull(resources);
		assertNotNull(qos);
		assertNotNull(models);
		assertNotNull(message);
	}
	
	@Ignore
	@Test
	public void run() { 
		validatePrepare();
		
		engine.validateStructure(message);
		
		Set<Map<ModelComponent, RPM>> powerset = powerset(models);
		for(Map<ModelComponent, RPM> composition : powerset) {
			engine.provision(composition, message);
			Set<Requirement> invalid = resources.stream().map(r->r.getRequirements()).flatMap(s->s.stream()).filter(r->!r.isResolved() || !r.isValid()).collect(Collectors.toSet());
			System.out.print(String.format("%s: %f", composition.values(), qos.score()));
			if(invalid.isEmpty())
				System.out.println();
			else
				System.out.println(String.format(","+ invalid));
		}
		System.out.println();
		
		Map<ModelComponent, RPM> result = engine.run(message);
		System.out.println("Models:");
		SetMultimap<ModelComponent, RPM> target = HashMultimap.create();
		for(Map.Entry<ModelComponent, RPM> entry : result.entrySet()) {
			System.out.println(String.format("%s: %s", entry.getKey().getIdentifier(), entry.getValue().getIdentifier()));
			target.put(entry.getKey(), entry.getValue());
		}
		System.out.println();
//		engine = new RumEngine(components, qos, target);
		engine.run(message);
		
		for(Component component : components) {
			System.out.println(component.getIdentifier());
			for(ResourceInterface ri: component.consumes()) {
				System.out.println(String.format("[consumes] %s: %.2f %s", ri.getResource().getIdentifier(), ri.getValue(), ri.getResource().getUnit()));
			}
			for(ResourceInterface ri : component.offers()) {	
				System.out.println(String.format("[offers] %s: %.2f %s", ri.getResource().getIdentifier(), ri.getValue(), ri.getResource().getUnit()));
			}
			System.out.println();
		}
	}

//	@Ignore
	@Test
	public void runTimerOnetime() {
		compare();
	}
	
	private static int n = 5;
	private static int rowPointer = 1;
	
	@Ignore
	@Test
	public void runTimerNtimesAndMeasure() throws IOException {
		//1 = brute force (ms), 2 = constraint (ms), 3 = valid (nr), 4 = total size (nr)
		compare();
		long milis = System.currentTimeMillis();
		long[] times = new long[4];
		for(int i = 0; i < n; i++) {
			long[] sub = compare();
			times[0] += sub[0];
			times[1] += sub[1];
			times[2] = sub[2];
			times[3] = sub[3];
		}
		times[0] /= n;
		times[1] /= n;
		
		Amount<Duration> time = Amount.valueOf(System.currentTimeMillis()-milis, MILLI(SECOND));
		if(time.getExactValue()/1000 > 1){
			time = time.to(SECOND);
			if(time.getEstimatedValue()/60 > 1) {
				time = time.to(MINUTE);
				if(time.getEstimatedValue()/60 > 1) {
					time = time.to(HOUR);
				}
			}
		}
		out.write(String.format("%s\nNr valid:;%d\nNr total:;%d\nBrute force:;%d;ms\nConstraint:;%d;ms\nfactor;=B%d/B%d;;\"=IF(B%d>=1;\"\"Pass\"\";\"\"Fail\"\")\"\nTotal:;%.1f;%s\n\n", //
				getTitle(), times[2], times[3], times[0], times[1], rowPointer+3, rowPointer+4, rowPointer+5, time.getEstimatedValue(), time.getUnit().toString()));
		rowPointer += 8;
	}
	
	public long[] compare() {
		long[] result = new long[4];
		
		validatePrepare();
		engine.validateStructure(message);
		Set<Map<ModelComponent, RPM>> powerset = powerset(models);
		Set<Requirement> constraints = engine.getResources().values().stream().map(r->r.getRequirements()).flatMap(s->s.stream()).collect(Collectors.toSet());
		Map<ModelComponent, RPM> emptyMap = new HashMap<>();
		for(ModelComponent c: engine.getModels().keySet()) {
			emptyMap.put(c, null);
		}
		
		engine.provision(emptyMap, message);
		System.out.println("Starting brute force");
		long milis = System.currentTimeMillis();
		Map<Map<ModelComponent, RPM>, Double> flat = engine.calculateScoresFlat(message, constraints);
		Amount<Duration> time = Amount.valueOf(System.currentTimeMillis()-milis, MILLI(SECOND));
		
		result[0] = time.getExactValue();
		
		System.out.println(String.format("Ran for %f seconds", time.to(SECOND).getEstimatedValue()));
		System.out.println();
		
		engine = new RumEngine(components, qos, models);
		engine.provision(emptyMap, message);
		System.out.println("Starting constraints run...");
		milis = System.currentTimeMillis();
		Map<Map<ModelComponent, RPM>, Double> hierarchical = engine.calculateScoresRecursively(message, engine.getModels(), constraints);
		time = Amount.valueOf(System.currentTimeMillis()-milis, MILLI(SECOND));
		
		System.out.println(String.format("Ran for %f seconds", time.to(SECOND).getEstimatedValue()));
		System.out.println(String.format("%d valid, %d invalid, %d total", flat.size(), powerset.size()-flat.size(), powerset.size()));
		System.out.println();
		result[1] = time.getExactValue();
		result[2] = flat.size();
		result[3] = powerset.size();
		return result;
		
		
	}
	
//	public long[] runTimer() {
//		long[] result = new long[4];
//		validatePrepare();
//		
//		engine.validateStructure(message);
//		
//		Set<Map<ModelComponent, RPM>> powerset = powerset(models);
//		System.out.println("------------------");
//		System.out.println(String.format("Running for %d configurations...", powerset.size()));
//		System.out.println(models.keySet().size() == 1 ? "Running flat brute force" : "Running hierarchical constraint algorithm");
//		int printInterval = 500;
//		long milis = System.currentTimeMillis();
//		Set<Requirement> constraints = engine.getResources().values().stream().map(r->r.getRequirements()).flatMap(s->s.stream()).collect(Collectors.toSet());
//		int valid = 0;
//		int i = 0;
//		for(Map<ModelComponent, RPM> model : powerset) {
//			i++;
//			if(i % printInterval == 0)
//				System.out.println(String.format("Processed %d configurations", i));
//			engine.provision(model, message);
//			boolean invalid = constraints.stream().filter(c-> !c.isResolved() || !c.isValid()).count() > 0;
//			if(!invalid)
//				valid++;
//		}
//		//set "best" (any) model as normal execution also does
//		engine.provision(powerset.stream().findAny().orElseThrow(()->new NullPointerException()), message);
//		Amount<Duration> time = Amount.valueOf(System.currentTimeMillis()-milis, MILLI(SECOND));
//		result[0] = time.getExactValue();
//		
//		System.out.println(String.format("Ran for %f seconds", time.to(SECOND).getEstimatedValue()));
//		System.out.println(String.format("%d valid, %d invalid, %d total", valid, powerset.size()-valid, powerset.size()));
//		System.out.println();
//		
//		engine = new RumEngine(components, qos, models);
//		System.out.println("Starting constraints run...");
//		milis = System.currentTimeMillis();
//		Map<ModelComponent, RPM> winner = engine.run(message);
//		time = Amount.valueOf(System.currentTimeMillis()-milis, MILLI(SECOND));
//		result[1] = time.getExactValue();
//		System.out.println(String.format("Ran for %f seconds", time.to(SECOND).getEstimatedValue()));
//		System.out.println(winner);
//		System.out.println();
//		result[2] = valid;
//		result[3] = powerset.size();
//		return result;
//	}
	
	protected static Set<Map<ModelComponent, RPM>> powerset(SetMultimap<ModelComponent, RPM> models){
		Stack<ModelComponent> stack = new Stack<>();
		models.keySet().forEach(m -> stack.push(m));
		return powerset(stack, models);
	}
	
	private static Set<Map<ModelComponent, RPM>> powerset(Stack<ModelComponent> stack, SetMultimap<ModelComponent, RPM> models){
		Set<Map<ModelComponent, RPM>> result = new HashSet<>();
		if(stack.isEmpty()) {
			result.add(new HashMap<>());
		}else {
			ModelComponent key = stack.pop();
			Set<Map<ModelComponent, RPM>> sub = powerset(stack, models);
			for(RPM appendix : models.get(key)) {
				for(Map<ModelComponent, RPM> subResult : sub) {
					Map<ModelComponent, RPM> newElem = new HashMap<>(subResult);
					newElem.put(key, appendix);
					result.add(newElem);
				}
			}
		}
		return result;
	}
}