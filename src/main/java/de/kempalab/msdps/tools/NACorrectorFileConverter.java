package de.kempalab.msdps.tools;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import de.kempalab.msdps.data.DataTable;

public class NACorrectorFileConverter {

	private enum MZminePeakOutputHeader {
		EXACT_MASS("row m/z", 0),//
		RETENTION_TIME("row retention time", 1),//
		SEPARATED_DETAILS("row identity (main ID + details)", 2),//
		PEAK_HEIGHT(" Peak height", 3),// the header has actually a filename prefix
		PEAK_AREA(" Peak area", 4);// the header has actually a filename prefix

		private String headerValue;
		private int columnNumber;

		private MZminePeakOutputHeader(String headerValue, int columnNumber) {
			this.headerValue = headerValue;
			this.columnNumber = columnNumber;
		}

		public String getHeaderValue() {
			return headerValue;
		}

		public int getColumnNumber() {
			return columnNumber;
		}   
	}

	private enum MZmineDetailsIdentfier {
		ID("ID: ", 0),//
		NAME("Name ", 1),//
		FORMULA("Molecular formula: ", 2);//

		private String identifier;
		private int index;
		private MZmineDetailsIdentfier(String identifier, int index) {
			this.identifier = identifier;
			this.index = index;
		}
		public String getIdentifier() {
			return identifier;
		}
		public int getIndex() {
			return index;
		}

	}

	private enum NACorrectionInputHeader {
		GROPUP_KEY("GroupKey", 0),//
		INTENSITY("Intensity", 1),//
		C_13_COUNT("13C-Count", 2),//
		N_15_COUNT("15N-Count", 3),//
		FORMULA("Formula",4);//

		private String headerValue;
		private int columnValue;

		private NACorrectionInputHeader(String headerValue, int columnValue) {
			this.headerValue = headerValue;
			this.columnValue = columnValue;
		}

		public String getHeaderValue() {
			return headerValue;
		}

		public int getColumnValue() {
			return columnValue;
		}
	}

	public static void main(String[] args) {
		String mzMineOutpuFilePath = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaks.csv";
		String naCorrectionInputFilePath = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaksNAinput.csv";
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
			String metabolitePeakID = "";
			String formula = "";
			String c13Count;
			String n15Count;
			String groupKey;
			for (CSVRecord csvRecord : records) {
				if (csvRecord.getRecordNumber() == 1) {
					String peakHeightHeader = csvRecord.get(MZminePeakOutputHeader.PEAK_HEIGHT.getColumnNumber());
					filename = StringUtils.remove(peakHeightHeader, MZminePeakOutputHeader.PEAK_HEIGHT.getHeaderValue());
					continue;
				}
				String detailStr = csvRecord.get(MZminePeakOutputHeader.SEPARATED_DETAILS.getColumnNumber());
				String[] details = StringUtils.split(detailStr, ";");
				if (csvRecord.getRecordNumber() == 2) {
					formula = StringUtils.remove(details[MZmineDetailsIdentfier.FORMULA.getIndex()], MZmineDetailsIdentfier.FORMULA.getIdentifier());
				}
				metabolitePeakID = StringUtils.remove(details[MZmineDetailsIdentfier.ID.getIndex()], MZmineDetailsIdentfier.ID.getIdentifier());
				String intensity = csvRecord.get(MZminePeakOutputHeader.PEAK_HEIGHT.getColumnNumber());
				String name = StringUtils.remove(details[MZmineDetailsIdentfier.NAME.getIndex()], MZmineDetailsIdentfier.NAME.getIdentifier());
				String[] nameTokens = StringUtils.split(name, "_");
				int lastTokenIndex = nameTokens.length - 1;
				int secondLastTokenIndex = nameTokens.length - 2;
				c13Count = nameTokens[secondLastTokenIndex];
				n15Count = nameTokens[lastTokenIndex];
				groupKey = filename + ";" + metabolitePeakID;
				naCorrectionInputTable.addRow(groupKey, intensity, c13Count, n15Count, formula);
			}
			naCorrectionInputTable.writeToCsv("N/A", true, naCorrectionInputFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
