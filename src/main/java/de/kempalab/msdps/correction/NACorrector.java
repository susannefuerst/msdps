package de.kempalab.msdps.correction;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

import de.kempalab.msdps.IncorporationMap;
import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.fileconversion.NACorrectionInputHeader;
import de.kempalab.msdps.util.FileWriterUtils;

public class NACorrector {

	/**
	 * Corrects the intensities in a csv file with entries according to {@link NACorrectionInputHeader} for their natural abundances.
	 * The correction will be saved in a new csv file with entries according to {@link NACorrectionInputHeader}.
	 * @param naCorrectionInputFilePath, path of the uncorrected input file
	 * @param naCorrectionOutputFilePath, path of the corrected output file
	 */
	public static void correct(String naCorrectionInputFilePath, String naCorrectionOutputFilePath) {
		File naCorrectionInputFile = new File(naCorrectionInputFilePath);
		CSVParser parser;
		ArrayList<List<CSVRecord>> recordLists = new ArrayList<List<CSVRecord>>();
		try {
			parser = CSVParser.parse(naCorrectionInputFile, Charset.defaultCharset(), CSVFormat.RFC4180);
			List<CSVRecord> records = parser.getRecords();
			String currentGroupKey = records.get(1).get(NACorrectionInputHeader.GROPUP_KEY.getColumnValue());
			List<CSVRecord> currentRecords = new ArrayList<CSVRecord>();
			for (CSVRecord record : records) {
				if(record.getRecordNumber() == 1) {
					continue;
				}
				if(record.get(NACorrectionInputHeader.GROPUP_KEY.getColumnValue()).equals(currentGroupKey)) {
					currentRecords.add(record);
					if (record.getRecordNumber() == records.size()) {
						List<CSVRecord> copy = new ArrayList<CSVRecord>();
						for (CSVRecord rec : currentRecords) {
							copy.add(rec);
						}
						recordLists.add(copy);
					}
				} else {
					currentGroupKey = record.get(NACorrectionInputHeader.GROPUP_KEY.getColumnValue());
					List<CSVRecord> copy = new ArrayList<CSVRecord>();
					for (CSVRecord rec : currentRecords) {
						copy.add(rec);
					}
					recordLists.add(copy);
					currentRecords = new ArrayList<CSVRecord>();
					currentRecords.add(record);
				}

			}
			ArrayList<IncorporationMap> correctedMaps = new ArrayList<IncorporationMap>();
			for (List<CSVRecord> groupedRecords : recordLists) {
				IncorporationMap uncorrected = new IncorporationMap(groupedRecords, Element.C, Element.N);
				IncorporationMap corrected = uncorrected.correctForNaturalAbundance(uncorrected.getMaxElementFormula());
				correctedMaps.add(corrected.normalize(4));
			}
			StringBuilder mapsStringBuilder = new StringBuilder();
			mapsStringBuilder.append(correctedMaps.get(0).toCsvString(true));
			for (int index = 1; index < correctedMaps.size(); index++) {
				mapsStringBuilder.append(correctedMaps.get(index).toCsvString(false));
			}
			naCorrectionOutputFilePath = FileWriterUtils.checkFilePath(naCorrectionOutputFilePath, FileWriterUtils.CSV_EXTENSION);
			FileUtils.writeStringToFile(new File(naCorrectionOutputFilePath), mapsStringBuilder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String naCorrectionInputFilePath = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaksNAinput2.csv";
		String naCorrectionOutputFilePath = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaksNAcorrected2.csv";
		NACorrector.correct(naCorrectionInputFilePath, naCorrectionOutputFilePath);
	}

}
