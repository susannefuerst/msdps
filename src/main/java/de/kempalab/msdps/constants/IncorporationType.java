package de.kempalab.msdps.constants;
/**
 * If a set of fragments exhibits a natural isotope incorporation we refer to this incorporation as NATURAL.
 * If all fragments in a set contain the same amount of tracers we refer to this incorporation as an EXPERIMENTAL one.
 * @author sfuerst
 *
 */
public enum IncorporationType {
	
	NATURAL(),EXPERIMENTAL(), MARKED(), MIXED();

}
