package prototype.model;

import java.util.HashMap;
import java.util.Map;

public class ModelComponent extends Component{
	
	RPM performanceModel;
	
	public ModelComponent(String identifier) {
		super(identifier);
	}
	
	public void loadModel(RPM performanceModel){
		this.performanceModel = performanceModel;
	}
	
	public RPM getModel() {
		return this.performanceModel;
	}
	
	public void reset() {
		this.performanceModel = null;
		super.reset();
	}
	
	@Override
	public Map<Resource, ResourceFunction> getResourceFunctions(){
		if(performanceModel == null) 
			return new HashMap<>();
		return performanceModel.getResourceFunctions();
	}
	
	
}