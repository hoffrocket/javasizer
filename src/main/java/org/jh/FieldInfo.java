/**
 * 
 */
package org.jh;

import java.lang.reflect.Field;

class FieldInfo {
	final Field field;
	final boolean isPrimitive;

	public FieldInfo(Field field) {
		field.setAccessible(true);
		this.field = field;
		isPrimitive = field.getType().isPrimitive();
	}

	public Object get(Object obj) {
		try {
			return field.get(obj);
		} catch (Exception e) {
			throw new RuntimeException("Error getting field value",e);
		}
	}
}