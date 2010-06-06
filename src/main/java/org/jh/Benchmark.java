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
