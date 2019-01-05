package de.kempalab.msdps.tools;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.constants.NaturalConstants;
import de.kempalab.msdps.exception.FragmentNotFoundException;

public class FragmentMassCalculator {
	final static int CHARGE = 1;

	public static void main(String[] args) throws FragmentNotFoundException {
		FragmentList fragments = FragmentsDatabase.getAllFregments();
//		FragmentList fragments = new FragmentList(
//				FragmentsDatabase.getFragment(FragmentKey.ALA_116), FragmentsDatabase.getFragment(FragmentKey.ALA_190));
		for (Fragment fragment : fragments) {
			System.out.println(fragment.getFragmentKey() + ": "
					+ (fragment.lowestMass() - CHARGE * NaturalConstants.ELECTRON_MASS.getValue()));
		}
	}

}
