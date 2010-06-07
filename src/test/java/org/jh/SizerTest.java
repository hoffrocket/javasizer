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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

public class SizerTest {
	
	@Test
	public void instIsNotNull(){
		assertNotNull(Sizer.inst);
	}
	
	@Test
	public void shallowSizeWorks(){
		assertTrue("should not be 0", Sizer.shallowSize(1) != 0);
	}
	
	@Test
	public void shallowSizeConsistentForDifferentSizeStrings(){
		assertEquals(Sizer.shallowSize("123"), Sizer.shallowSize("123456"));
	}
	
	@Test
	public void traverseArray(){
		Integer [] ints = {1,2,3,4,5};
		long expectedSize = Sizer.shallowSize(new Integer[5]) + Sizer.shallowSize(1)*ints.length;
		assertEquals(expectedSize, Sizer.sizeof(ints));
	}
	
	static class SimpleObjectWithField {
		Object o = new Object();
	}
	
	void assertNoInstSizeIsSane(Object o) {
		assertEquals(Sizer.sizeof(new Sizer.InstrumentationSizeVisitor(), o),Sizer.sizeof(new Sizer.ReflectionSizeVisitor(), o));
	}
	@Test 
	public void sizeOfObject() {
		assertNoInstSizeIsSane(new Object());
	}
	
	@Test 
	public void sizeOfObjectWithField() {
		assertNoInstSizeIsSane(new SimpleObjectWithField());
	}
	
	@Test 
	public void sizeOfArray() {
		char [] hello = {'h','e','l','l','o'};
		assertNoInstSizeIsSane(hello);
	}
	
	@Test 
	public void sizeOfEmptyArray() {
		char [] hello = {};
		assertNoInstSizeIsSane(hello);
	}
	
	@Test 
	public void sizeOfObjectArray() {
		Object [] hello = {new Object()};
		assertNoInstSizeIsSane(hello);
	}
	
	@Test
	@Ignore("I think this fails because byte boundaries are determined at each level in a class hierarchy")
	public void sizeOfArrayList() {
		assertNoInstSizeIsSane(new ArrayList<Object>());
	}
	
	@Test 
	public void sizeOfString() {
		assertNoInstSizeIsSane("hello");
	}
}
