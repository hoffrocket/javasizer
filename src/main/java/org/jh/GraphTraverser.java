package org.jh;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;


public class GraphTraverser {
	private static final Object MARKER = new Object();
	
	private final Map<Class<?>, ClassInfo> _classMap = new HashMap<Class<?>, ClassInfo>();
	private final IdentityHashMap<Object, Object> _visited = new IdentityHashMap<Object, Object>();
	
	private boolean shouldFollow(Object o) {
		return o != null && _visited.put(o, MARKER) == null;
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
			visitor.visit(obj);
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
	
}
