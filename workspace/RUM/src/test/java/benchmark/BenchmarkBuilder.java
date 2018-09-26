package benchmark;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import prototype.factory.RpmBuilder;
import prototype.factory.RumBuilder;
import prototype.main.RumEngine;
import prototype.model.Component;
import prototype.model.ModelComponent;
import prototype.model.Resource;
import prototype.model.ResourceFunction;
import prototype.model.ResourceInterface;
import prototype.model.ResourceInterface.InterfaceType;
import prototype.model.optimize.MinMaxOptimizer;
import prototype.model.optimize.MinMaxOptimizer.MinMax;

public class BenchmarkBuilder{
	
	RumBuilder builder = new RumBuilder();
	int mcId = 1;
	int rId;
	ModelComponent prevMC = null;
	Resource prevR = null;
	
	public void addModelComponent(int nrRpms, int nrSubComponents, double failChance, boolean optimizer) {
		ModelComponent mc = builder.modelComponent(String.format("mc%d", mcId));
		double chance = failChance/nrSubComponents;
		int cId = 1;
		List<Resource> rs = new LinkedList<>();
		Resource nextR = null;
		if(optimizer){
			nextR = builder.optimize(new MinMaxOptimizer("mcr"+mcId, "", MinMax.MAX));
			new ResourceInterface(nextR, mc, InterfaceType.OFFERS);
		}else{
			nextR = builder.resource("mcr"+mcId, "");
		}
		for(int i = 1; i <= nrSubComponents; i++) {
			Component c = builder.component(String.format("c%d_%d", mcId, cId));
			Resource r = builder.resource(String.format("r%d_%d", mcId, cId), "");
			rs.add(r);
			cId++;
			builder.connect(c, mc, r, new ResourceFunction(1), null);
		}
		
		List<Resource> allRs = new LinkedList<>(rs);
		
		if(prevMC != null) {
			builder.connect(prevMC, mc, prevR);
			allRs.add(prevR);
		}
		allRs.add(nextR);

		RpmBuilder rpm = builder.rpmBuilder(mc, allRs.toArray(new Resource[0]));
		for(int i = 1; i <= nrRpms; i++) {
			List<ResourceFunction> values = rs.stream().map(r->Double.toString(Math.random() < chance ? 2.0 : 0.5)).map(v->new ResourceFunction(v)).collect(Collectors.toList());
			if(prevR != null) {
				values.add(new ResourceFunction(prevR.getIdentifier()));
			}
			values.add(new ResourceFunction(x->Arrays.stream(x).mapToDouble(y->y).sum(), rs.stream().map(y->y.getIdentifier()).toArray(String[]::new)));
			rpm.add(String.format("rpm_mc%d_%d", mcId, i), values.toArray(new ResourceFunction[0]));
		}
		prevMC = mc;
		prevR = nextR;
		mcId++;
	}
	
	public static Function<Double[], Double> getFunction(List<String> values){
		return x->Arrays.stream(x).mapToDouble(y->y).sum();
	}


	
	public RumEngine build() {
		return builder.build();
	}
}