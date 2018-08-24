package jscience;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class SortTest{
	
	@Test
	public void run() {
		int length=100000;
		int nr = 100;
		List<List<Integer>> lists = new LinkedList<>();
		for(int i = 0; i < nr; i++) {
			lists.add(generate(length, length*100));
		}
		long start = System.currentTimeMillis();
		for(List<Integer> list : lists) {
			list.stream().sorted().findFirst().get();
		}
		System.out.println("Time: " + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		generate(length*nr, length*nr*100).stream().sorted().findFirst().get();
		System.out.println("Time: " + (System.currentTimeMillis() - start));
	}
	
	public List<Integer> generate(int n, int max) {
		List<Integer> result = new LinkedList<>();
		for(int i = 0; i < n; i++) {
			result.add((int)(Math.random()*max));
		}
		return result;
	}
	
}