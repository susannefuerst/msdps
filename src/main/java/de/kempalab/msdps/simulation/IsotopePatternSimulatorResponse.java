package de.kempalab.msdps.simulation;

import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MSDatabaseList;

/**
 * Includes a list of {@link MSDatabase}s corresponding to the requested fragments and options from a
 * {@link IsotopePatternSimulatorRequest}.
 * @author sfuerst
 *
 */
public class IsotopePatternSimulatorResponse {
	
	private MSDatabaseList msDatabaseList;

	/**
	 * @return the msDatabaseList
	 */
	public MSDatabaseList getMsDatabaseList() {
		return msDatabaseList;
	}

	/**
	 * @param msDatabaseList the msDatabaseList to set
	 */
	public void setMsDatabaseList(MSDatabaseList msDatabaseList) {
		this.msDatabaseList = msDatabaseList;
	}

}
