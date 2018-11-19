package de.kempalab.msdps;

import java.util.Comparator;

import de.kempalab.msdps.constants.Isotope;

/**
 * Compares two isotopes by their abundance.
 * @author sfuerst
 *
 */
public class IsotopeAbundancyComparator implements Comparator<Isotope> {
	
	/**
	 * @return 
	 *  0 if both have the same abundance,
	 * -1 if abundance of firstIsotope is less than abundance of otherIsotope,
	 *  1, otherwise.
	 */
	@Override
	public int compare(Isotope firstIsotope, Isotope secondIsotope) {
		return firstIsotope.getAbundance().compareTo(secondIsotope.getAbundance());
	}

}
