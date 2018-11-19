package de.kempalab.msdps;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class FragmentList extends ArrayList<Fragment> {
	
	public FragmentList() {
		
	}
	
	public FragmentList(Fragment...fragments ) {
		for (Fragment fragment : fragments) {
			this.add(fragment);
		}
	}

}
