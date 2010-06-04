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
	
	private static List<FieldInfo> getFields(Class<?> clazz) {

		Field[] declaredFields = clazz.getDeclaredFields();
		List<FieldInfo> fields = new ArrayList<FieldInfo>(declaredFields.length);
		for (Field f : declaredFields) {
			if (!Modifier.isStatic(f.getModifiers()))
				fields.add(new FieldInfo(f));
		}

		return Collections.unmodifiableList(fields);
	}
}