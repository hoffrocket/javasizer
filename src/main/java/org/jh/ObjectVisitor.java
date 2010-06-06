/**
 * 
 */
package org.jh;

interface ObjectVisitor {
	boolean visit(ClassInfo info, Object obj);
}