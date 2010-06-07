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

import java.util.ArrayList;
import java.util.List;

public class Benchmark {

	static class Parent {
		int i1 = 1;
	}
	static class TestObject extends Parent {
		int i2 = 2;
		byte b1 = 0x01;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int count = 1000;
		List<Object> toMeasure = new ArrayList<Object>();
		for (int i = 0; i < 1000; i++) {
			toMeasure.add(new TestObject());
		}
		for (int i = 0; i < 5; i++) {
			time(new Sizer.InstrumentationSizeVisitor(), toMeasure, count);
			time(new Sizer.ReflectionSizeVisitor(), toMeasure, count);
		}
	}
	
	static void time(Sizer.SizeVisitor visitor, Object o, int count) {
		long start = System.currentTimeMillis();
		long size = 0;
		for (int i = 0; i < count; i++) {
			size = Sizer.sizeof(visitor, o);
		}
		long time = System.currentTimeMillis() - start;
		System.out.println(visitor.getClass().getSimpleName() + " took " + time + " ms. " + time/count + " ms per. " + size);
	}

}
