package org.jh;

import java.lang.instrument.Instrumentation;



public class Sizer {

	static Instrumentation inst;
	
	public static void premain(String options, Instrumentation inst) {
		Sizer.inst = inst;
		System.out.println("Sizer Agent Configured.");
	}
	
	public static long shallowSize(Object object)
	{
		if (inst == null)
			throw new IllegalStateException("Instrumentation is null");
		if (object == null)
			return 0;

		return inst.getObjectSize(object);
	}
	
	static class SizeVisitor implements ObjectVisitor{
		long size = 0;

		public void visit(Object obj) {
			size += Sizer.shallowSize(obj);
		}
	}
	
	static class NoopVisitor implements ObjectVisitor {
		public void visit(Object obj) {
			//
		}
	}
	
	public static long sizeof(Object o) {
		GraphTraverser traverser = new GraphTraverser();
		SizeVisitor visitor = new SizeVisitor();
		traverser.traverse(visitor, o);
		return visitor.size;
	}
	
	public static long sizeof(Object base, Object measured) {
		GraphTraverser traverser = new GraphTraverser();
		NoopVisitor noop = new NoopVisitor();
		traverser.traverse(noop, base);
		SizeVisitor visitor = new SizeVisitor();
		traverser.traverse(visitor, measured);
		return visitor.size;
	}
}
