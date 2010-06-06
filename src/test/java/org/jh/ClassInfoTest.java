package org.jh;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;


public class ClassInfoTest {

	@Test
	public void getsAllFieldsInHierarchyForArrayList(){
		assertEquals(3, ClassInfo.getFields(ArrayList.class).size());
	}
}
