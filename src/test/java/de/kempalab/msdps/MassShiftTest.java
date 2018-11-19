package de.kempalab.msdps;

import java.util.InputMismatchException;

import junit.framework.TestCase;
import de.kempalab.msdps.MassShift;

public class MassShiftTest extends TestCase {
	
	public void testToString() {
		MassShift massShift = new MassShift(1, 2, 1.0033);
		assertEquals("1.0033[1-2]", massShift.toString());
	}
	
	public void testFromString() {
		MassShift massShift = MassShift.fromString("1.0033[1-2]");
		MassShift expectedShift = new MassShift(1, 2, 1.0033);
		assertEquals(expectedShift, massShift);
	}
	
	public void testFailFromString() {
		try {
			MassShift.fromString("wrong pattern");
			fail("This should throw an InputMismatchException");
		} catch (InputMismatchException e) {
			// this exception was expected
		}
	}

}
