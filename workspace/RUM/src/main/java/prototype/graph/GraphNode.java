package prototype.graph;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class GraphNode<T extends GraphNode<T>> implements Comparable<GraphNode<T>>{
	
	enum CycleDetectionState {PENDING, IN_PROGRESS, FINISHED}
	
	private CycleDetectionState state = CycleDetectionState.PENDING;
	private Integer index = null;
		
	public abstract String getName();
	public abstract Set<GraphNode<T>> preceedings();
	public abstract Set<GraphNode<T>> followings();
	
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
		if(preceedings().isEmpty()) {
			this.index = 0;
		}else {
			this.index = preceedings().stream().mapToInt(n -> n.getIndex()).max().getAsInt()+1;
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
	
	@Override
	public int compareTo(GraphNode<T> n) {
		int result =  Integer.compare(index, n.getIndex());
		if(result == 0)
			result = this.getName().compareTo(n.getName());
		return result;
	}
	
	protected int getIndex() {
		return this.index;
	}
}