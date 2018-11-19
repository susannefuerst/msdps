package de.kempalab.msdps;

import java.util.Comparator;

import de.kempalab.msdps.constants.Isotope;
/**
 * Compares two isotopes by their mass.
 * @author sfuerst
 *
 */
public class IsotopeMassComparator implements Comparator<Isotope> {
	
	/**
	 * @return 
	 *  0 if both have the same mass,
	 * -1 if mass of firstIsotope is less than mass of otherIsotope,
	 *  1, otherwise.
	 */
	@Override
	public int compare(Isotope firstIsotope, Isotope secondIsotope) {
		return firstIsotope.getAtomicMass().compareTo(secondIsotope.getAtomicMass());
	}

}
