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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;

import org.junit.Before;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;

public class SizerTest {

  @Test
  public void instIsNotNull() {
    assertNotNull(Sizer.inst);
  }

  @Test
  public void shallowSizeWorks() {
    assertTrue("should not be 0", Sizer.shallowSize(1) != 0);
  }

  @Test
  public void shallowSizeConsistentForDifferentSizeStrings() {
    assertEquals(Sizer.shallowSize("123"), Sizer.shallowSize("123456"));
  }

  @Test
  public void traverseArrayWithInstrumentation() {
    Integer[] ints = { 1, 2, 3, 4, 5 };
    long expectedSize = Sizer.shallowSize(new Integer[5]) + Sizer.shallowSize(1) * ints.length;
    assertEquals(expectedSize, Sizer.sizeof(new Sizer.InstrumentationSizeVisitor(), ints));
  }


  static abstract class AbstractFoo {
    int fooVal = 0;
  }

  static class ConcreteFoo extends AbstractFoo {
    int concreteVal = 0;

  }

  static class SimpleObjectWithField {
    Object o = new Object();
    short l = 1;
    short l1 = 2;
    short l3 = 2;
  }

  static class SimpleObjectWithSharedField {
    private Object _shared;
    short l = 1;
    public SimpleObjectWithSharedField(Object shared) {
      _shared = shared;
    }
  }

  @Test
  public void nullObjectHasZeroSize() throws ClassNotFoundException {
    assertEquals(0,
        new Sizer.ReflectionSizeVisitor().calculateSize(null, new ClassInfo(Class.forName(("java.lang.Integer")))));
  }

  void assertNoInstSizeIsSane(Object o) {
    assertEquals(Sizer.sizeof(new Sizer.InstrumentationSizeVisitor(), o),
        Sizer.sizeof(new Sizer.ReflectionSizeVisitor(), o));
  }

  @Test
  public void sizeOfObject() {
    assertNoInstSizeIsSane(new Object());
  }


  @Test
  public void sizeOfObjectWithField() {
    assertNoInstSizeIsSane(new SimpleObjectWithField());
  }

  @Test
  public void sizeOfArray() {
    char[] hello = { 'h', 'e', 'l', 'l', 'o' };
    assertNoInstSizeIsSane(hello);
  }

  @Test
  public void sizeOfEmptyArray() {
    char[] hello = {};
    assertNoInstSizeIsSane(hello);
  }

  @Test
  public void sizeOfOneIntArray() {
    int[] hello = {1};
    assertNoInstSizeIsSane(hello);
  }

  @Test
  public void sizeOfObjectArray() {
    Object[] hello = { new Object() };
    assertNoInstSizeIsSane(hello);
  }

  @Test
  public void sizeOfNullObjectArray() {
    Object[] hello = new Object[10];
    assertNoInstSizeIsSane(hello);
  }

  @Test
  public void sizeOfArrayList() {
    assertNoInstSizeIsSane(new ArrayList<Object>());
  }

  @Test
  public void sizeOfString() {
    assertNoInstSizeIsSane("hello");
  }

  @Test
  public void inheritence() {
    assertNoInstSizeIsSane(new ConcreteFoo());
  }

  @Test
  public void exclude() {
    int[] shared = new int[1024];
    SimpleObjectWithSharedField o = new SimpleObjectWithSharedField(shared);
    long sharedSize = Sizer.sizeof(shared);
    long wholeSize = Sizer.sizeof(o);
    long excludedSize = Sizer.sizeof(o, Collections.singleton(shared));
    assertTrue(
      "obj w/shared size: " + wholeSize + " shared: " + sharedSize + " obj shared excluded: " + excludedSize,
      excludedSize < wholeSize
    );
  }
}
