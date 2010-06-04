package org.jh;

import static org.junit.Assert.*;

import org.junit.Test;

public class SizerTest {
	
	@Test
	public void instIsNotNull(){
		assertNotNull(Sizer.inst);
	}
	
	@Test
	public void shallowSizeWorks(){
		assertTrue("should not be 0", Sizer.shallowSize(1) != 0);
	}
	
	@Test
	public void traverseArray(){
		Integer [] ints = {1,2,3,4,5};
		long expectedSize = Sizer.shallowSize(new Integer[5]) + Sizer.shallowSize(1)*ints.length;
		assertEquals(expectedSize, Sizer.sizeof(ints));
	}
}
