package prototype.model;

import java.util.HashMap;
import java.util.Map;

public class RumMessage{
	
	Map<ModelComponent, RPM> currentState;
	Map<String, Double> vars = new HashMap<>();
	

	public Map<ModelComponent, RPM> getCurrentState() {
		return currentState;
	}

	public void setCurrentState(Map<ModelComponent, RPM> currentState) {
		this.currentState = currentState;
	}

	public Map<String, Double> getVars() {
		return vars;
	}
	
	public void setVars(Map<String, Double> vars) {
		this.vars = vars;
	}
	
	
}