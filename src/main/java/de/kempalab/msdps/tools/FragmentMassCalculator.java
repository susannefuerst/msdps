package de.kempalab.msdps.tools;

import de.kempalab.msdps.Fragment;
import de.kempalab.msdps.FragmentList;
import de.kempalab.msdps.FragmentsDatabase;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.exception.FragmentNotFoundException;

public class FragmentMassCalculator {

	public static void main(String[] args) throws FragmentNotFoundException {
		FragmentList fragments = new FragmentList(
				FragmentsDatabase.getFragment(FragmentKey.ASP_130),
				FragmentsDatabase.getFragment(FragmentKey.ASP_160)
				);
		for (Fragment fragment : fragments) {
			System.out.println(fragment.getFragmentKey() + ": " + fragment.lowestMass());
		}
	}

}
