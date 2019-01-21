package de.kempalab.msdps;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import de.kempalab.msdps.constants.FragmentDatabaseColKey;
import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.MetaboliteKey;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.exception.FragmentNotFoundException;

/**
 * The FragmentsDatabase is a map of each the formula of a fragment concatenated by the short name of the molecule
 * from where the fragment was derived from to the Fragment. To add Fragments to the database add them in fragmentsDatabase.csv.
 * 
 * @author sfuerst
 *
 */
public class FragmentsDatabase {
	
	private static FragmentList data = new FragmentList();
	static {
		File csvData = new File(PathConstants.FRAGMENTS_DATABASE_FILE.toAbsolutePath());
		CSVParser parser;
		try {
			parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.RFC4180);
			for (CSVRecord csvRecord : parser) {
				if (csvRecord.getRecordNumber() == 1) {
					continue;
				}
				String fragmentFormula = csvRecord.get(FragmentDatabaseColKey.FRAGMENT_FORMULA.getColumnIndex());
				String shortMoleculeName = csvRecord.get(FragmentDatabaseColKey.SHORT_MOLECULE_NAME.getColumnIndex());
				String fragmentCapacity = csvRecord.get(FragmentDatabaseColKey.FRAGMENT_CAPACITY.getColumnIndex());
				String derivate = csvRecord.get(FragmentDatabaseColKey.DERIVATE.getColumnIndex());
				String retentionTimeStr = csvRecord.get(FragmentDatabaseColKey.RETENTION_TIME.getColumnIndex());
				Double rt = retentionTimeStr != null && retentionTimeStr != "NA" ? Double.parseDouble(retentionTimeStr) : null; 
				int baseMass = Integer.parseInt(csvRecord.get(FragmentDatabaseColKey.FRAGMENT_BASE_INT_MASS.getColumnIndex()));
				FragmentKey key = FragmentKey.byMassAndAbbreviation(baseMass, shortMoleculeName);
				Fragment fragment = new Fragment(key, fragmentFormula, fragmentCapacity);
				fragment.setDerivate(derivate);
				data.add(fragment);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param key
	 * @return the fragment associated with the given key
	 * @throws FragmentNotFoundException 
	 */
	public static Fragment getFragment(FragmentKey key) throws FragmentNotFoundException {
		for (Fragment fragment : data) {
			if (fragment.getFragmentKey().equals(key)) {
				return fragment.copy();
			}
		}
		throw new FragmentNotFoundException("Cannot find fragment " + key);
	}
	
	public static FragmentList getAllFregments() {
		return data.copy();
	}

	public static FragmentList getFragments(MetaboliteKey metabolite) {
		FragmentList fragments = new FragmentList();
		for (Fragment fragment : data) {
			if ((fragment.getFragmentKey().getMetaboliteKey().equals(metabolite))) {
				fragments.add(fragment.copy());
			}
		}
		return fragments;
	}

	public static FragmentList getFragments(MetaboliteKey[] metabolites) {
		FragmentList fragments = new FragmentList();
		for (MetaboliteKey metabolite : metabolites) {
			for (Fragment fragment : data) {
				if ((fragment.getFragmentKey().getMetaboliteKey().equals(metabolite))) {
					fragments.add(fragment.copy());
				}
			}
		}
		return fragments;
	}

}
