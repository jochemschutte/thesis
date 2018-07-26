package prototype.model;

import java.util.Map;

public class ModelComponent extends Component{
	
	RPM performanceModel;
	
	public ModelComponent(String identifier) {
		super(identifier);
	}
	
	public void loadModel(RPM performanceModel){
		this.performanceModel = performanceModel;
	}
	
	@Override
	public Map<Resource, ResourceFunction> getResourceFunctions(){
		return performanceModel.getResourceFunctions();
	}
}