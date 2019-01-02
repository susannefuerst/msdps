package de.kempalab.msdps;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.FrequencyType;
import de.kempalab.msdps.constants.MSDatasetOption;
import de.kempalab.msdps.constants.MSDatabaseColKey;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.util.FileWriterUtils;
import de.kempalab.msdps.util.MathUtils;
import de.kempalab.msdps.util.ParserUtils;
import de.kempalab.msdps.visualisation.MSCategoryBarChartCreator;

/**
 * A mass spectra database, containing natural, marked and mixed spectra of a fragment.
 * Information like incorporation rate, metabolite key and incorporated tracers is also included.
 * @author sfuerst
 *
 */
public class MSDatabase {
	
	public static final String NA_VALUE = "NA";
	public static final int JPEG_WIDTH_PER_CATEGORY = 200;
	public static final int JPEG_HEIGHT_PER_CATEGORY = 100;
	public static final int JPEG_MIN_WIDTH = 800;
	public static final int JPEG_MIN_HEIGHT = 400;
	
	private Double incorporationRate;
	private MassSpectrum naturalSpectrum = new MassSpectrum(FrequencyType.MID);
	private MassSpectrum markedSpectrum = new MassSpectrum(FrequencyType.MID);
	private MassSpectrum mixedSpectrum = new MassSpectrum(FrequencyType.MID);
	private FragmentKey fragmentKey;
	private String incorporatedTracers;
	private String fragmentFormula;
	
	public MSDatabase() {

	}
	
	public MSDatabase(String absoluteFilePath) {
		this.parseCsv(absoluteFilePath);
	}
	
	/**
	 * parse this object from a csv file with information according to the MSDatabaseColKey enum.
	 * @param absoluteFilePath
	 */
	public void parseCsv(String absoluteFilePath) {
		File csvData = new File(absoluteFilePath);
		CSVParser parser;
		try {
			parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.RFC4180);
			List<CSVRecord> records = parser.getRecords();
			naturalSpectrum = ParserUtils.parseSpectrum(records,
					MSDatabaseColKey.NATURAL_MASS.getColumnIndex(),
					MSDatabaseColKey.NATURAL_FREQUENCY.getColumnIndex(), FrequencyType.MID, 1);
			markedSpectrum = ParserUtils.parseSpectrum(records,
					MSDatabaseColKey.MARKED_MASS.getColumnIndex(),
					MSDatabaseColKey.MARKED_FREQUENCY.getColumnIndex(), FrequencyType.MID, 1);
			mixedSpectrum = ParserUtils.parseSpectrum(records,
					MSDatabaseColKey.MIXED_MASS.getColumnIndex(),
					MSDatabaseColKey.MIXED_FREQUENCY.getColumnIndex(), FrequencyType.MID, 1);
			incorporationRate = Double.parseDouble(records.get(1).get(
					MSDatabaseColKey.INC_RATE.getColumnIndex()));
			fragmentKey = FragmentKey.byKeyName(records.get(1).get(
					MSDatabaseColKey.FRAGMENT_KEY.getColumnIndex()));
			incorporatedTracers = records.get(1).get(
					MSDatabaseColKey.INCORPORATED_TRACERS.getColumnIndex());
			fragmentFormula = records.get(1).get(
					MSDatabaseColKey.FRAGMENT_FORMULA.getColumnIndex());
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			e.printStackTrace();
		}
	}
	
	/**
	 * write this object to csv using the headers defined in MSDatabaseColKey enum
	 * @param outputFolderPath
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void writeCsv(String outputFolderPath) throws IOException {
		File folder = new File(outputFolderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		String filePath = outputFolderPath + createFilename() + FileWriterUtils.CSV_EXTENSION;
		filePath = FileWriterUtils.checkFilePath(filePath, FileWriterUtils.CSV_EXTENSION);
		DataTable dataTable = new DataTable(MSDatabaseColKey.toHeaderList());
		dataTable.addColumns(naturalSpectrum, markedSpectrum, mixedSpectrum);
		dataTable.addConstantValueColumn(0,incorporationRate);
		dataTable.addConstantValueColumn(fragmentKey.name());
		dataTable.addConstantValueColumn(incorporatedTracers);
		dataTable.addConstantValueColumn(fragmentFormula);
		dataTable.writeToCsv(NA_VALUE, true, filePath);
	}
	
	/**
	 * Creates a MSCategoryBarChart and saves it as jpeg to the specified outputFolderPath.
	 * The filename will be automatically created.
	 * @param outputFolderPath
	 * @throws IOException
	 */
	public void saveMSCategoryBarChartAsJPEG(String outputFolderPath, MSDatasetOption datasetOption) throws IOException {
		File folder = new File(outputFolderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		String filePath = outputFolderPath + createFilename() + FileWriterUtils.JPEG_EXTENSION;
		filePath = FileWriterUtils.checkFilePath(filePath, FileWriterUtils.JPEG_EXTENSION);
		File jpegFile = new File(filePath);
		JFreeChart barChart = MSCategoryBarChartCreator.createMSBarChart(this, datasetOption);
		int jpegSizeScaleFactor = MathUtils.maxSize(getNaturalSpectrum(), getMarkedSpectrum(), getMixedSpectrum());
		int width = Math.max(JPEG_WIDTH_PER_CATEGORY * jpegSizeScaleFactor, JPEG_MIN_WIDTH);
		int height = Math.max(JPEG_HEIGHT_PER_CATEGORY * jpegSizeScaleFactor, JPEG_MIN_HEIGHT);
		ChartUtilities.saveChartAsJPEG(jpegFile, barChart, width, height);
	}

	public Double getIncorporationRate() {
		return incorporationRate;
	}

	public void setIncorporationRate(Double incorporationRate) {
		this.incorporationRate = incorporationRate;
	}

	public MassSpectrum getNaturalSpectrum() {
		return naturalSpectrum;
	}

	public void setNaturalSpectrum(MassSpectrum naturalSpectrum) {
		this.naturalSpectrum = naturalSpectrum;
	}

	public MassSpectrum getMarkedSpectrum() {
		return markedSpectrum;
	}

	public void setMarkedSpectrum(MassSpectrum markedSpectrum) {
		this.markedSpectrum = markedSpectrum;
	}

	public MassSpectrum getMixedSpectrum() {
		return mixedSpectrum;
	}

	public void setMixedSpectrum(MassSpectrum mixedSpectrum) {
		this.mixedSpectrum = mixedSpectrum;
	}

	public FragmentKey getFragmentKey() {
		return fragmentKey;
	}

	public void setFragmentKey(FragmentKey fragmentKey) {
		this.fragmentKey = fragmentKey;
	}

	public String getIncorporatedTracers() {
		return incorporatedTracers;
	}

	public void setIncorporatedTracers(String incorporatedTracers) {
		this.incorporatedTracers = incorporatedTracers;
	}

	public String getFragmentFormula() {
		return fragmentFormula;
	}

	public void setFragmentFormula(String fragmentFormula) {
		this.fragmentFormula = fragmentFormula;
	}
	
	/**
	 * 
	 * @return string containing FragmentKey, incorporated tracer and incorporation percent
	 */
	public String createFilename() {
		int incorporationPerCent = (int) (incorporationRate * 100);
		return fragmentKey + "_" + incorporatedTracers + "_" + incorporationPerCent;
	}
	
	/**
	 * 
	 * @return the incorporation rate as percent value
	 */
	public int incorporationPerCent() {
		return (int) (incorporationRate * 100);
	}
	
	/**
	 * creates a table string representation of this {@link MSDatabase}
	 */
	@Override
	public String toString() {
		DataTable dataTable = new DataTable(MSDatabaseColKey.toHeaderList());
		dataTable.addColumn(getNaturalSpectrum());
		dataTable.addColumn(getMarkedSpectrum());
		dataTable.addColumn(getMixedSpectrum());
		dataTable.addConstantValueColumn(0,getIncorporationRate());
		dataTable.addConstantValueColumn(getFragmentKey().name());
		dataTable.addConstantValueColumn(getIncorporatedTracers());
		dataTable.addConstantValueColumn(getFragmentFormula());
		return dataTable.toString(NA_VALUE, true);
	}

}
