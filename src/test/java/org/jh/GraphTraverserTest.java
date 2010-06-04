package org.jh;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.LinkedList;

import org.junit.Test;


public class GraphTraverserTest {
	static class SimpleVisitor implements ObjectVisitor{
		int count = 0;

		public void visit(Object obj) {
			count += 1;
		}
	}
	
	long visit(Object o) {
		GraphTraverser traverser = new GraphTraverser();
		SimpleVisitor visitor = new SimpleVisitor();
		traverser.traverse(visitor, o);
		return visitor.count;
	}
	
	@Test
	public void canHandleDeepTree(){
		LinkedList<Object> toMeasure = new LinkedList<Object>(Collections.nCopies(5000, new Object()));
		visit(toMeasure);
	}
	
	@Test
	public void traverseArray(){
		Integer [] ints = {1,2,3,4,5};
		assertEquals(6, visit(ints));
	}
	
	static class MyClass {
		MyClass next;
	}
	
	@Test
	public void noInfiniteLoops(){
		MyClass o = new MyClass();
		o.next = o;
		assertEquals(1, visit(o));
	}
	
	@Test
	public void handleNull() {
		assertEquals(1, visit(new MyClass()));
	}
}
