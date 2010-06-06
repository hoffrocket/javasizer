/**
 * 
 */
package org.jh;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ClassInfo {
	final boolean isArray;
	final boolean isPrimitive;
	final List<FieldInfo> fields;
	public ClassInfo(Class<?> clazz) {
		isArray = clazz.isArray();
		isPrimitive = clazz.isPrimitive() || (isArray && clazz.getComponentType().isPrimitive());
		fields = getFields(clazz);
	}
	
	public static List<FieldInfo> getFields(Class<?> clazz) {
		List<FieldInfo> fields = new ArrayList<FieldInfo>();
		getFields(clazz, fields);
		return Collections.unmodifiableList(fields);
	}
	
	private static void getFields(Class<?> clazz, List<FieldInfo> fieldAcc) {

		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field f : declaredFields) {
			if (!Modifier.isStatic(f.getModifiers()))
				fieldAcc.add(new FieldInfo(f));
		}
		if (clazz.getSuperclass() != null)
			getFields(clazz.getSuperclass(), fieldAcc);
	}
}