package benchmark;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import prototype.main.RumEngine;
import prototype.model.ModelComponent;
import prototype.model.RPM;
import prototype.model.Resource;
import prototype.model.ResourceFunction;
import prototype.model.RumMessage;
import prototype.model.requirements.Requirement;

public class Benchmark{
	
	@Ignore
	@Test
	public void singleBenchmark() {
		int length = 4;
		int nrComponents = 2;
		int nrRpms = 3;
		double succChance = 0.5;
		RumEngine engine = buildEngine(expand(length, nrComponents), expand(length, nrRpms), getFailChances(succChance, length));
		Set<Requirement> constraints = engine.getResources().values().stream().map(r->r.getRequirements()).flatMap(s->s.stream()).collect(Collectors.toSet());
		print(expand(length, nrComponents));
		print(expand(length, nrRpms));
		print(getFailChances(succChance, length));
		long milis = System.currentTimeMillis();
		System.out.println(engine.calculateScoresFlat(new RumMessage(), constraints));
		System.out.println(System.currentTimeMillis()-milis);
		milis = System.currentTimeMillis();
		engine.provision(engine.getEmptyModel(), new RumMessage());
		System.out.println(String.join("\n", engine.getResources().values().stream().map(r->r.toString()).collect(Collectors.toSet())));
		System.out.println(engine.calculateScoresRecursively(new RumMessage(), engine.getModels(), constraints));
		System.out.println(String.join("\n", engine.getResources().values().stream().map(r->r.toString()).collect(Collectors.toSet())));
		System.out.println(System.currentTimeMillis()-milis);
	}
	
	private static void print(int[] s) {
		System.out.println(String.join(", ", Arrays.stream(s).mapToObj(i->Integer.toString(i)).collect(Collectors.toList())));
	}
	
	private static void print(double[] s) {
		System.out.println(String.join(", ", Arrays.stream(s).mapToObj(i->Double.toString(i)).collect(Collectors.toList())));
	}
	
//	@Ignore
	@Test
	public void runBenchmark() throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(new File("benchmark.csv")));
		out.write("Length;#components;#rpms;succChance;avg flat;avg constraint;compare\n");
		int maxNrComponents = 2;
		int maxNrRpms = 4;
		int maxLength = 4;
		int avg = 5;
		double[] succChances = new double[] {0.5,0.70,1};
		int rowNr = 2;
		for(int l = 1; l <= maxLength; l++) {
			for(int i = 1; i <= maxNrComponents; i++) {
				for(int j = 2; j <= maxNrRpms; j++) {
					for(double succChance : succChances) {				
						int nrComponents = i;
						int nrRpms = j;
						System.out.println(String.format("Length: %d, nr components: %d, nr rpms: %d, succChance: %f", l, nrComponents, nrRpms, succChance));
						double avgFlat = 0;
						RumEngine engine = buildEngine(expand(nrComponents, l), expand(nrRpms, l), getFailChances(succChance, l));
						Set<Requirement> constraints = engine.getResources().values().stream().map(r->r.getRequirements()).flatMap(s->s.stream()).collect(Collectors.toSet());
						Map<Map<ModelComponent, RPM>, Double> flatResult = null;
						for(int k = 0; k < avg; k++) {
							long milis = System.currentTimeMillis();
							flatResult = engine.calculateScoresFlat(new RumMessage(), constraints);
							avgFlat += System.currentTimeMillis()-milis;
						}
						engine.provision(engine.getEmptyModel(), new RumMessage());
						
						double avgCon = 0;
						Map<Map<ModelComponent, RPM>, Double> conResult = null;
						for(int k = 0; k < avg; k++) {
							long milis = System.currentTimeMillis();
							conResult = engine.calculateScoresRecursively(new RumMessage(), engine.getModels(), constraints);
							avgCon += System.currentTimeMillis()-milis;
						}
						String output = String.format("%d;%d;%d;%f;%f;%f;=E%d/F%d;=G%d>1\n", l, nrComponents, nrRpms, succChance, avgFlat/avg, avgCon/avg, rowNr, rowNr, rowNr);
						out.write(output);
						if(!flatResult.equals(conResult)) {
							for(ModelComponent c: engine.getModels().keySet()) {
								System.out.println(c.getIdentifier());
								for(RPM rpm : engine.getModels().get(c)) {
									System.out.print(rpm.getIdentifier() + ": ");
									for(Map.Entry<Resource, ResourceFunction> entry: rpm.getResourceFunctions().entrySet()) {
										System.out.print(entry.getKey().getIdentifier() + "="+entry.getValue()+", ");
										
									}
									System.out.println();
								}
							}
						}
							
						assertEquals(output, flatResult, conResult);
						rowNr++;
					}
				}
			}
			out.write("\n");
			rowNr++;
		}
		out.flush();
		out.close();
		System.out.println("_done");
	}
	
	public double[] getFailChances(double successChance, int n) {
		double individualSuccChance = Math.pow(successChance,1.0/(double)n);
		double[] result = new double[n];
		for(int i = 0; i < n; i++) {
			result[i] = 1-individualSuccChance;
		}
		return result;
	}
	
	public int[] expand(int val, int n) {
		int[] result = new int[n];
		for(int i = 0; i < n; i++) {
			result[i] = val;
		}
		return result;
	}
	
	public RumEngine buildEngine(int[] subComponents, int[] rpms, double[] failChances) {
		BenchmarkBuilder b = new BenchmarkBuilder();
		for(int i = 0; i < subComponents.length; i++) {
			b.addModelComponent(rpms[i], subComponents[i], failChances[i], i == subComponents.length-1);
		}
		return b.build();
		
	}
}