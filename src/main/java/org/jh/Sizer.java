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

import java.util.logging.Level;

import java.util.logging.Logger;

import java.lang.reflect.Field;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import sun.misc.Unsafe;

public class Sizer {
  
  final static Logger LOG = Logger.getLogger(Sizer.class.getName());

  static Instrumentation inst;
  private static Unsafe unsafe;

  public static void premain(String options, Instrumentation inst) {
    Sizer.inst = inst;
    LOG.info("Sizer Agent Configured.");
  }

  public static long shallowSize(Object object) {
    if (inst == null)
      throw new IllegalStateException("Instrumentation is null");
    if (object == null)
      return 0;

    return inst.getObjectSize(object);
  }

  interface SizeVisitor extends ObjectVisitor {
    long getSize();
  }

  private static int refSize() {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      unsafe = (Unsafe)field.get(null);

      int arrayIndexScale = unsafe.arrayIndexScale( Object[].class );
      
      LOG.info("Sizer detected reference size of: " + arrayIndexScale);
      
      return arrayIndexScale;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  static abstract class CachingSizeVisitor implements SizeVisitor {
    private final Map<Class<?>, Long> sizeCache = new HashMap<Class<?>, Long>();

    abstract long calculateSize(Object obj, ClassInfo info);

    long shallowSize(Object obj, ClassInfo info) {
      Class<?> clazz = obj.getClass();
      Long size = sizeCache.get(clazz);
      if (size == null) {
        size = calculateSize(clazz, info);
        if (LOG.isLoggable(Level.FINE)){
          LOG.fine("Got size " + size + " for " + info);
        }
        sizeCache.put(clazz, size);
      }
      return size;
    }
  }

  public static class ReflectionSizeVisitor extends CachingSizeVisitor {
    static int REFERENCE = refSize();
    static int OBJECT_OVERHEAD = 8 + REFERENCE; // 8 bytes of header plus a reference to the Class object
    static int ARRAY_OVERHEAD = unsafe.arrayBaseOffset(Object[].class);
    static int INT = 4;
    static int LONG = 8;
    static int BYTE = 1;
    static int BOOLEAN = 1;
    static int SHORT = 2;
    static int CHAR = 2;
    static int FLOAT = 4;
    static int DOUBLE = 8;

    private long size = 0;

    public long getSize() {
      return size;
    }

    private long sizeOfType(Class<?> type) {
      if (type == int.class) {
        return INT;
      } else if (type == long.class) {
        return LONG;
      } else if (type == byte.class) {
        return BYTE;
      } else if (type == boolean.class) {
        return BOOLEAN;
      } else if (type == char.class) {
        return CHAR;
      } else if (type == float.class) {
        return FLOAT;
      } else if (type == double.class) {
        return DOUBLE;
      } else if (type == short.class) {
        return SHORT;
      } else
        return REFERENCE;
    }

    /**
     * align to 8 byte boundaries. Not sure if this is correct behavior on all
     * JVMs
     * 
     * @param size
     * @return
     */
    private long align(long size) {
      long rem = size % 8;
      return size + ((rem == 0) ? 0 : 8 - rem);
    }

    public boolean visit(ClassInfo info, Object obj) {
      if (LOG.isLoggable(Level.FINER)){
        LOG.finer("Looking at " + obj + " info " + info);
      }
      if (info.isArray) {
        long arraySize = align(ARRAY_OVERHEAD + Array.getLength(obj) * sizeOfType(obj.getClass().getComponentType()));
        if (LOG.isLoggable(Level.FINER)){
          LOG.finer("\tcalculated array size: " + arraySize);
        }
        size += align(arraySize);
      } else {
        size += shallowSize(obj, info);
      }
      return !info.isPrimitive;
    }

    @Override
    long calculateSize(Object obj, ClassInfo info) {
      if (obj == null) {
        return 0;
      }

      long maxOffset = OBJECT_OVERHEAD;
      long maxOffsetTypeSize = 0;
      
      // it turns out that fields in from inherited classes will have oddly aligned offsets
      // this finds the Field with the maximum offset and assumes that the total size of the instance
      // is that offset plus the size of that field.
      for (FieldInfo f : info.fields) {
        long sizeOfType = sizeOfType(f.field.getType());
        long offset = unsafe.objectFieldOffset(f.field);
        
        if (LOG.isLoggable(Level.FINER)){
          LOG.finer("\tfield got size " + sizeOfType + " for type " + f.field.getType() + " offset " + offset);
        }
        
        if (offset > maxOffset) {
          maxOffset = offset;
          maxOffsetTypeSize = sizeOfType;
        }
      }
      return align(maxOffset + maxOffsetTypeSize);
    }

  }

  public static class InstrumentationSizeVisitor implements SizeVisitor {
    long size = 0;

    public long getSize() {
      return size;
    }

    public boolean visit(ClassInfo info, Object obj) {
      long shallowSize = Sizer.shallowSize(obj);
      if (LOG.isLoggable(Level.FINER)){
        LOG.finer("Instrumentation got shallowSize " + shallowSize + " for " + info);
      }
      size += shallowSize;
      return !info.isPrimitive;
    }
  }

  static class NoopVisitor implements ObjectVisitor {
    public boolean visit(ClassInfo info, Object obj) {
      return true;
    }
  }

  private static SizeVisitor createSizeVisitor() {
    if (inst == null)
      return new ReflectionSizeVisitor();
    else
      return new InstrumentationSizeVisitor();
  }

  public static long sizeof(Object o) {
    return sizeof(createSizeVisitor(), o);
  }

  public static long sizeof(SizeVisitor visitor, Object o) {
    GraphTraverser traverser = new GraphTraverser();
    traverser.traverse(visitor, o);
    return visitor.getSize();
  }

  public static long sizeof(Object base, Object measured) {
    GraphTraverser traverser = new GraphTraverser();
    NoopVisitor noop = new NoopVisitor();
    traverser.traverse(noop, base);
    SizeVisitor visitor = createSizeVisitor();
    traverser.traverse(visitor, measured);
    return visitor.getSize();
  }
}
