package topsort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import prototype.graph.GraphNode;
import prototype.graph.TopologyResolver;

public class TopSortTest{
	
	@Test
	public void run() {
		Node a = new Node("a");
		Node b = new Node("b");
		Node c = new Node("c");
		Node d = new Node("d");
		Node e = new Node("e");
		Node f = new Node("f");
		
		
		
		connect(c,d);
		connect(d,e);
		connect(b,f);
		connect(e,f);

		connect(a,b);
		connect(a,c);
		

		List<Node> elems = ImmutableList.of(a,b,c,d,e,f);
		TopologyResolver<Node> resolver = new TopologyResolver<>(elems);
		for(Node n : elems) {
			System.out.println(n + "=" +resolver.getFollowers(n));
		}
		System.out.println(resolver.getFullOrder());
	}
	
	public static void connect(Node a, Node b) {
		a.followings().add(b);
		b.preceedings().add(a);
	}
	
	public static class Node extends GraphNode<Node>{

		String name;
		HashSet<GraphNode<Node>> followers = new HashSet<>();
		HashSet<GraphNode<Node>> preceeding = new HashSet<>();
		
		public Node(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public Set<GraphNode<Node>> preceedings() {
			return this.preceeding;
		}

		@Override
		public Set<GraphNode<Node>> followings() {
			return this.followers;
		}
		
		@Override
		public String toString() {
			return this.name + " " + this.getIndex();
		}
		
		
		
	}
}