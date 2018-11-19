package de.kempalab.msdps.util;

import java.util.List;

import org.apache.commons.csv.CSVRecord;
import de.kempalab.msdps.IsotopeListList;
import de.kempalab.msdps.MSDatabase;
import de.kempalab.msdps.MassShiftDataSet;
import de.kempalab.msdps.MassShiftList;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.FrequencyType;

public class ParserUtils {
	
	/**
	 * Parses a MassSpectrum from a list of csv records where masses will be read from the massColumnIndex and the corresponding
	 * frequencies from the frequencyColumnIndex.
	 * @param records
	 * @param massColumnIndex
	 * @param frequencyColumnIndex
	 * @return A MassSpectrum from the csv records with 0.0 entries removed.
	 */
	public static MassSpectrum parseSpectrum(List<CSVRecord> records, int massColumnIndex, int frequencyColumnIndex, FrequencyType frequencyType, int headerRow) {
		MassSpectrum massSpectrum = new MassSpectrum(frequencyType);
		for (CSVRecord csvRecord : records) {
			try {
				if (csvRecord.getRecordNumber() <= headerRow) {
					continue;
				}
				if (csvRecord.get(massColumnIndex).equals(MSDatabase.NA_VALUE) || csvRecord.get(massColumnIndex).equals("0.0")) {
					continue;
				}
				if (csvRecord.get(massColumnIndex) == null || csvRecord.get(frequencyColumnIndex) == null) {
					continue;
				}
				Double mass = Double.parseDouble(csvRecord.get(massColumnIndex));
				Double frequency = Double.parseDouble(csvRecord.get(frequencyColumnIndex));
				massSpectrum.put(mass, frequency);
			} catch (ArrayIndexOutOfBoundsException e) {
				// there may be an empty row
				continue;
			}
		}
		massSpectrum.remove(0.0);
		return massSpectrum;
	}

	public static MassShiftDataSet parseMassShiftDataSet(List<CSVRecord> records,
			int shiftValuesColumnIndex, int shifIsotopesColumnIndex) {
		MassShiftDataSet shiftDataSet = new MassShiftDataSet();
		for (CSVRecord csvRecord : records) {
			try {
				if (csvRecord.getRecordNumber() == 1) {
					continue;
				}
				if (csvRecord.get(shiftValuesColumnIndex).equals(MSDatabase.NA_VALUE)) {
					continue;
				}
				String massShiftListString = csvRecord.get(shiftValuesColumnIndex);
				String isotopesListString = csvRecord.get(shifIsotopesColumnIndex);
				MassShiftList massShiftList = MassShiftList.fromString(massShiftListString);
				IsotopeListList isotopeListList = IsotopeListList.fromString(isotopesListString);
				shiftDataSet.put(massShiftList, isotopeListList);
			} catch (ArrayIndexOutOfBoundsException e) {
				// there may be an empty row
				continue;
			}
		}
		return shiftDataSet;
	}

}
