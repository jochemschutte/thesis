package prototype.graph;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class GraphNode<T extends GraphNode<T>>{
	
	enum CycleDetectionState {PENDING, IN_PROGRESS, FINISHED}
	
	private CycleDetectionState state = CycleDetectionState.PENDING;
		
	public abstract String getName();
	public abstract Set<GraphNode<T>> preceedings();
	
	public List<GraphNode<T>> visit() {
		List<GraphNode<T>> result = new LinkedList<>();
		if(this.state == CycleDetectionState.FINISHED)
			return new LinkedList<GraphNode<T>>();
		if(this.state == CycleDetectionState.IN_PROGRESS)
			throw new IllegalStateException(String.format("Cycle detected at '%s'", getName()));
		this.state = CycleDetectionState.IN_PROGRESS;
		for(GraphNode<T> preceeding: preceedings()) {
			result.addAll(preceeding.visit());
		}
		result.add((GraphNode<T>)this);
		this.state=CycleDetectionState.FINISHED;
		return result;
	}
	
	public void finish() {
		if(this.state == CycleDetectionState.PENDING) 
			throw new IllegalStateException(String.format("Node '%s' has not been visited yet, but tried to finish", getName()));
		this.state = CycleDetectionState.FINISHED;
	}
	
	public void reset() {
		this.state = CycleDetectionState.PENDING;
	}
}