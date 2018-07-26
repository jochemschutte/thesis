package prototype.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class TopologyResolver<T extends GraphNode<T>>{
	
	@SuppressWarnings("unchecked")
	public List<T> resolve(Collection<T> components) {
		List<GraphNode<T>> result = new LinkedList<>();
		Stack<GraphNode<T>> stack = new Stack<GraphNode<T>>();
		components.forEach(c -> stack.push(c));
		while(!stack.isEmpty()) {
			GraphNode<T> head = stack.pop();
			result.addAll(head.visit());
		}
		return (List<T>)result;
	}
	
}