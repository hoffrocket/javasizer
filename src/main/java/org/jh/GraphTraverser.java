package org.jh;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;


public class GraphTraverser {
	
	private final Map<Class<?>, ClassInfo> _classMap = new HashMap<Class<?>, ClassInfo>();
	private final IdentitySet _visited = new LiteIdentitySet();
	
	private boolean shouldFollow(Object o) {
		return o != null && _visited.add(o);
	}
	
	private ClassInfo getClassInfo(Object obj) {
		Class<?> clazz = obj.getClass();
		ClassInfo info = _classMap.get(clazz);
		if (info == null){
			info = new ClassInfo(clazz);
			_classMap.put(clazz, info);
		}
		return info;
	}
	
	public void traverse(ObjectVisitor visitor, Object root) {
		Stack stack = new Stack();
		stack.put(root);
		shouldFollow(root);
		while (!stack.empty()) {
			Object obj = stack.pop();
			ClassInfo info = getClassInfo(obj);
			if (visitor.visit(info, obj)) {
				if (info.isArray){
					if (!info.isPrimitive) {
						int length = Array.getLength(obj);
						for (int i = 0; i < length; i++){
							Object child = Array.get(obj, i);
							if (shouldFollow(child)) {
								stack.put(child);
							}
						}
					}
				} else {
					for (FieldInfo f : info.fields) {
						if (!f.isPrimitive){
							Object child = f.get(obj);
							if (shouldFollow(child)) {
								stack.put(child);
							}
						}
					}
				}
			}
		}
	}
	
	private static class Stack {
		List<Object> _inner = new ArrayList<Object>();
		
		void put(Object o) {
			_inner.add(o);
		}
		
		Object pop() {
			return _inner.remove(_inner.size() - 1);
		}
		
		boolean empty() {
			return _inner.isEmpty();
		}
		
	}
	
	private interface IdentitySet {
		boolean add(Object o);
	}
	
	private static class IdentityHashSet implements IdentitySet {
		private static final Object MARKER = new Object();
		private final IdentityHashMap<Object, Object> _set = new IdentityHashMap<Object, Object>();
		public boolean add(Object o) {
			return _set.put(o, MARKER) == null;
		}
	}
	
	
	/*
	 * Some of this code was borrowed from http://code.google.com/p/ontopia/source/browse/trunk/ontopia/src/java/net/ontopia/utils/CompactHashSet.java
	 * (it's apache 2.0 licensed)
	 */
	private static class LiteIdentitySet implements IdentitySet {
		private static final float LOAD_FACTOR = 0.8f;
		private static final int SIZE = 10;

		protected int elements = 0;
		protected Object[] objects = new Object[SIZE];

		public boolean add(Object o) {
			int index = hash(o) % objects.length;
			int offset = 1;
		
			while (objects[index] != null && !(objects[index] == o)) {
				index = ((index + offset) & 0x7FFFFFFF) % objects.length;
				offset = offset * 2 + 1;
		
				if (offset == -1)
					offset = 2;
			}
		
			if (objects[index] == null) {

				// rehash and grow
				if ((elements / (double) objects.length) > LOAD_FACTOR) {
					rehash((objects.length << 1) + 1);
				}
				elements++;
				objects[index] = o;
				
				return true;
			} else return false;
		}
		
		private void rehash(int newSize) {
			Object[] newObjects = new Object[newSize];

			for (int i = 0; i < objects.length; i++) {
				Object o = objects[i];

				int index = hash(o) % newSize;
				int offset = 1;

				while (newObjects[index] != null) {
					index = ((index + offset) & 0x7FFFFFFF) % newSize;
					offset = offset * 2 + 1;

					if (offset == -1)
						offset = 2;
				}

				newObjects[index] = o;
			}

			objects = newObjects;
		}
		
		private int hash(Object obj) {
		    return System.identityHashCode(obj) & 0x7FFFFFFF;
		}
    }
}
