package de.kempalab.msdps.util;

public class StringUtils {
	
	private static final String ON = "1";
	private static final String OFF = "0";
	private static final String ON_OFF = "10";
	
	/**
	 * 
	 * @param binary, a string representation of sequence of bits, e.g "10011"
	 * @return a string representation of the input binary, where the last off bit is switched witch the following bit.
	 */
	public static String switchLastOffBit(String binary) {
		int lastOffIndex = binary.lastIndexOf(OFF);
//		String firstPart = binary.substring(0, lastOffIndex);
//		String lastPart = binary.substring(lastOffIndex + 2);
//		String switched = firstPart + ON_OFF + lastPart;
//		return switched;
		return binary.substring(0, lastOffIndex) + ON_OFF + binary.substring(lastOffIndex + 2);
	}
	
	/**
	 * 
	 * @param binary, a string representation of sequence of bits, e.g "10011"
	 * @return a string representation of the input binary, where the off bit next to the last off bit is switched witch the following bit
	 * and all following ons are shifted to the right.
	 */
	public static String switchNextToLastOffBitAndShiftRemainingOnsToTheRight(String binary) {
		int lastOnIndex = binary.lastIndexOf(ON);
		String substringBeforeLastOn = binary.substring(0, lastOnIndex);
		int lastOffBeforeLastOn = substringBeforeLastOn.lastIndexOf(OFF);
//		String firstPart = binary.substring(0, lastOffBeforeLastOn);
//		String lastPart = binary.substring(lastOffBeforeLastOn + 2);
//		String switchedAnShifted = firstPart + ON_OFF + shiftAllOnesToRight(lastPart);
//		return switchedAnShifted;
		return binary.substring(0, lastOffBeforeLastOn) + ON_OFF + shiftAllOnesToRight(binary.substring(lastOffBeforeLastOn + 2));
	}
	
	/**
	 * 
	 * @param binary, a sequence of leading ons, followed by only offs
	 * @return The input binary where all leading ons are shifted to the right end.
	 */
	private static String shiftAllOnesToRight(String binary) {
		int lastOnIndex = binary.lastIndexOf(ON);
//		String offs = binary.substring(lastOnIndex + 1);
//		String ons = binary.substring(0, lastOnIndex + 1);
//		return offs + ons;
		return binary.substring(lastOnIndex + 1) + binary.substring(0, lastOnIndex + 1);
	}
	
}
