package de.kempalab.msdps.fileconversion;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import de.kempalab.msdps.correction.NACorrector;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.util.FileWriterUtils;

public class NACorrectorFileConverter {

	/**
	 * Converts a csv file with entries according to {@link MZminePeakOutputHeader} to a csv file with entries according to {@link NACorrectionInputHeader}.
	 * This file can subsequently be used for natural abundance correction using {@link NACorrector}.
	 * @param mzMineOutpuFilePath, the file to convert
	 * @param naCorrectionInputFilePath, path of the converted file
	 */
	public static void convert(String mzMineOutpuFilePath, String naCorrectionInputFilePath) {
		File mzMinePeakData = new File(mzMineOutpuFilePath);
		DataTable naCorrectionInputTable = new DataTable(
				NACorrectionInputHeader.GROPUP_KEY.getHeaderValue(), 
				NACorrectionInputHeader.INTENSITY.getHeaderValue(),
				NACorrectionInputHeader.C_13_COUNT.getHeaderValue(),
				NACorrectionInputHeader.N_15_COUNT.getHeaderValue(),
				NACorrectionInputHeader.FORMULA.getHeaderValue());
		CSVParser parser;
		try {
			parser = CSVParser.parse(mzMinePeakData, Charset.defaultCharset(), CSVFormat.RFC4180);
			List<CSVRecord> records = parser.getRecords();
			String filename = "";
			String metaboliteFragment = "";
			String formula = "";
			int c13Count;
			int n15Count;
			String groupKey = "";
			LinkedHashMap<String, double[][]> groupKeyIntensitiesMap = new LinkedHashMap<>();
			ArrayList<String> formulas = new ArrayList<>();
//			double[][] singleIntensities = new double[10][10];
			for (CSVRecord csvRecord : records) {
				if (csvRecord.getRecordNumber() == 1) {
					String peakHeightHeader = csvRecord.get(MZminePeakOutputHeader.PEAK_HEIGHT.getColumnNumber());
					filename = StringUtils.remove(peakHeightHeader, MZminePeakOutputHeader.PEAK_HEIGHT.getHeaderValue());
					continue;
				}
				String detailStr = csvRecord.get(MZminePeakOutputHeader.SEPARATED_DETAILS.getColumnNumber());
				String[] details = StringUtils.split(detailStr, ";");
				formula = StringUtils.remove(details[MZmineDetailsIdentfier.FORMULA.getIndex()], MZmineDetailsIdentfier.FORMULA.getIdentifier());
				metaboliteFragment = StringUtils.remove(details[MZmineDetailsIdentfier.ID.getIndex()], MZmineDetailsIdentfier.ID.getIdentifier());
				metaboliteFragment = StringUtils.substringBefore(metaboliteFragment, "_");
				// FIXME: this overwrites the group key each time
				groupKey = filename + ";" + metaboliteFragment;
				if (groupKeyIntensitiesMap.get(groupKey) == null) {
					// TODO: choose a more elegant way for the size of the array
//					singleIntensities = new double[10][10];
					groupKeyIntensitiesMap.put(groupKey, new double[10][10]);
					formulas.add(formula);
				}
				Double intensity = Double
						.parseDouble(csvRecord.get(MZminePeakOutputHeader.PEAK_HEIGHT.getColumnNumber()));
				String name = StringUtils.remove(details[MZmineDetailsIdentfier.NAME.getIndex()], MZmineDetailsIdentfier.NAME.getIdentifier());
				String[] nameTokens = StringUtils.split(name, "_");
				int lastTokenIndex = nameTokens.length - 1;
				int secondLastTokenIndex = nameTokens.length - 2;
				c13Count = Integer.parseInt(nameTokens[secondLastTokenIndex]);
				n15Count = Integer.parseInt(nameTokens[lastTokenIndex]);
				groupKeyIntensitiesMap.get(groupKey)[c13Count][n15Count] = groupKeyIntensitiesMap.get(groupKey)[c13Count][n15Count] + intensity;
			}
			int entryCount = 0;
			for (Entry<String, double[][]> entry : groupKeyIntensitiesMap.entrySet()) {
				String currentGroupKey = entry.getKey();
				double[][] currentSingleIntensities = entry.getValue();
				for (int c = 0; c < 10; c++) {
					for (int n = 0; n < 10; n++) {
						if (currentSingleIntensities[c][n] > 0) {
							naCorrectionInputTable.addRow(currentGroupKey,
									String.valueOf(currentSingleIntensities[c][n]), String.valueOf(c),
									String.valueOf(n), formulas.get(entryCount));
						}
					}
				}
				entryCount++;
			}
			naCorrectionInputFilePath = FileWriterUtils.checkFilePath(naCorrectionInputFilePath, FileWriterUtils.CSV_EXTENSION);
			naCorrectionInputTable.writeToCsv("N/A", true, naCorrectionInputFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts an array of csv files with entries according to {@link MZminePeakOutputHeader} to a csv file with entries according to {@link NACorrectionInputHeader}.
	 * This file can subsequently be used for natural abundance correction using {@link NACorrector}.
	 * @param mzMineOutpuFilePaths, the files to convert
	 * @param naCorrectionInputFilePath, path of the converted file
	 */
	public static void convert(String[] mzMineOutpuFilePaths, String naCorrectionInputFilePath) {
		DataTable naCorrectionInputTable = new DataTable(
				NACorrectionInputHeader.GROPUP_KEY.getHeaderValue(), 
				NACorrectionInputHeader.INTENSITY.getHeaderValue(),
				NACorrectionInputHeader.C_13_COUNT.getHeaderValue(),
				NACorrectionInputHeader.N_15_COUNT.getHeaderValue(),
				NACorrectionInputHeader.FORMULA.getHeaderValue());
		try {
			for (String mzMineOutpuFilePath : mzMineOutpuFilePaths) {
				File mzMinePeakData = new File(mzMineOutpuFilePath);
				CSVParser parser;
				parser = CSVParser.parse(mzMinePeakData, Charset.defaultCharset(), CSVFormat.RFC4180);
				List<CSVRecord> records = parser.getRecords();
				String filename = "";
				String metaboliteFragment = "";
				String formula = "";
				int c13Count;
				int n15Count;
				String groupKey = "";
				LinkedHashMap<String, double[][]> groupKeyIntensitiesMap = new LinkedHashMap<>();
				ArrayList<String> formulas = new ArrayList<>();
//				double[][] singleIntensities = new double[10][10];
				for (CSVRecord csvRecord : records) {
					if (csvRecord.getRecordNumber() == 1) {
						String peakHeightHeader = csvRecord.get(MZminePeakOutputHeader.PEAK_HEIGHT.getColumnNumber());
						filename = StringUtils.remove(peakHeightHeader, MZminePeakOutputHeader.PEAK_HEIGHT.getHeaderValue());
						continue;
					}
					String detailStr = csvRecord.get(MZminePeakOutputHeader.SEPARATED_DETAILS.getColumnNumber());
					String[] details = StringUtils.split(detailStr, ";");
					formula = StringUtils.remove(details[MZmineDetailsIdentfier.FORMULA.getIndex()], MZmineDetailsIdentfier.FORMULA.getIdentifier());
					metaboliteFragment = StringUtils.remove(details[MZmineDetailsIdentfier.ID.getIndex()], MZmineDetailsIdentfier.ID.getIdentifier());
					metaboliteFragment = StringUtils.substringBefore(metaboliteFragment, "_");
					// FIXME: this overwrites the group key each time
					groupKey = filename + ";" + metaboliteFragment;
					if (groupKeyIntensitiesMap.get(groupKey) == null) {
						// TODO: choose a more elegant way for the size of the array
//						singleIntensities = new double[10][10];
						groupKeyIntensitiesMap.put(groupKey, new double[10][10]);
						formulas.add(formula);
					}
					Double intensity = Double
							.parseDouble(csvRecord.get(MZminePeakOutputHeader.PEAK_HEIGHT.getColumnNumber()));
					String name = StringUtils.remove(details[MZmineDetailsIdentfier.NAME.getIndex()], MZmineDetailsIdentfier.NAME.getIdentifier());
					String[] nameTokens = StringUtils.split(name, "_");
					int lastTokenIndex = nameTokens.length - 1;
					int secondLastTokenIndex = nameTokens.length - 2;
					c13Count = Integer.parseInt(nameTokens[secondLastTokenIndex]);
					n15Count = Integer.parseInt(nameTokens[lastTokenIndex]);
					groupKeyIntensitiesMap.get(groupKey)[c13Count][n15Count] = groupKeyIntensitiesMap.get(groupKey)[c13Count][n15Count] + intensity;
				}
				int entryCount = 0;
				for (Entry<String, double[][]> entry : groupKeyIntensitiesMap.entrySet()) {
					String currentGroupKey = entry.getKey();
					double[][] currentSingleIntensities = entry.getValue();
					for (int c = 0; c < 10; c++) {
						for (int n = 0; n < 10; n++) {
							if (currentSingleIntensities[c][n] > 0) {
								naCorrectionInputTable.addRow(currentGroupKey,
										String.valueOf(currentSingleIntensities[c][n]), String.valueOf(c),
										String.valueOf(n), formulas.get(entryCount));
							}
						}
					}
					entryCount++;
				}
			}
			naCorrectionInputFilePath = FileWriterUtils.checkFilePath(naCorrectionInputFilePath, FileWriterUtils.CSV_EXTENSION);
			naCorrectionInputTable.writeToCsv("N/A", true, naCorrectionInputFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void convert(ArrayList<String> mzMineOutpuFilePaths, String naCorrectionInputFilePath) {
		String[] array = new String[mzMineOutpuFilePaths.size()];
		for (int i = 0; i < mzMineOutpuFilePaths.size(); i++) {
			array[i] = mzMineOutpuFilePaths.get(i);
		}
		convert(array, naCorrectionInputFilePath);
	}



	public static void main(String[] args) {
		String mzMineOutpuFilePath = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaks.csv";
		String naCorrectionInputFilePath = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaksNAinput.csv";
		NACorrectorFileConverter.convert(mzMineOutpuFilePath, naCorrectionInputFilePath);
	}

}
