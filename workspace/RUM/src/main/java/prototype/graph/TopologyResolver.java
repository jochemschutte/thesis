package prototype.graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class TopologyResolver<T extends GraphNode<T>> implements Serializable{
	
	private static final long serialVersionUID = -8132766395454717369L;
	Map<GraphNode<T>, List<GraphNode<T>>> followers = new HashMap<>();
	List<GraphNode<T>> order = new LinkedList<>();
	
	public TopologyResolver(Collection<T> components) {
		Stack<GraphNode<T>> stack = new Stack<GraphNode<T>>();
		for(T gn :components) {
			stack.push(gn);
		}
		while(!stack.isEmpty()) {
			GraphNode<T> head = stack.pop();
			order.addAll(head.visit());
		}
		
		stack = new Stack<GraphNode<T>>();
		for(T gn :components) {
			stack.push(gn);
		}
		while(!stack.isEmpty()) {
			resolveFollowing(stack.pop());
		} 
	}
	
	private List<GraphNode<T>> resolveFollowing(GraphNode<T> t){
		List<GraphNode<T>> result = followers.get(t);
		if(result == null) {
			result = new LinkedList<>();
			for(GraphNode<T> t2: t.followings()) {
				result.add(t2);
				result.addAll(resolveFollowing(t2));
				Collections.sort(result);
			}
			followers.put(t, result);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getFollowers(GraphNode<T> t){
		return (List<T>)followers.get(t);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getFullOrder(){
		return (List<T>)this.order;
	}
	
	
	
}