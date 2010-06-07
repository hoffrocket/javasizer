/*
 * Copyright 2010 Jon Hoffman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jh;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.LinkedList;

import org.junit.Test;


public class GraphTraverserTest {
	static class SimpleVisitor implements ObjectVisitor{
		int count = 0;

		public boolean visit(ClassInfo info, Object obj) {
			count += 1;
			return true;
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
	
	static class Parent {
		Object o = new Object();
	}
	static class Child extends Parent {
		Object o = new Object();
	}
	
	@Test 
	public void findsAllFieldsInHierarchy() {
		assertEquals(3, visit(new Child()));
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
