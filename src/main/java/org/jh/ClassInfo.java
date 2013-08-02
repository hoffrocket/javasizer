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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

class ClassInfo {
  final boolean isArray;
  final boolean isPrimitive;
  final FieldInfo[] fields;
  final Class<?> clazz;

  public ClassInfo(Class<?> clazz) {
    this.clazz = clazz;
    isArray = clazz.isArray();
    isPrimitive = clazz.isPrimitive() || (isArray && clazz.getComponentType().isPrimitive());
    fields = getFields(clazz);
  }

  public static FieldInfo[] getFields(Class<?> clazz) {
    List<FieldInfo> fields = new ArrayList<FieldInfo>();
    getFields(clazz, fields);
    return fields.toArray(new FieldInfo[fields.size()]);
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
  
  @Override
  public String toString() {
    return "ClassInfo(" + clazz.getName() + ", isArray: " + isArray 
        + ", isPrimitive: " + isPrimitive + ", fieldCount: " + fields.length + ")";
  }
}