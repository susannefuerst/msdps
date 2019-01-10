package de.kempalab.msdps;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import de.kempalab.msdps.constants.FragmentKey;
import de.kempalab.msdps.constants.FrequencyType;
import de.kempalab.msdps.constants.IncorporationType;
import de.kempalab.msdps.constants.MSShiftDatabaseColKey;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.util.FileWriterUtils;
import de.kempalab.msdps.util.ParserUtils;

/**
 * An extension of a MSDatabase where mass shifts and the isotopes that induced the shifts are also included.
 * See also: MassShiftDataSet.
 * @author sfuerst
 *
 */
public class MSShiftDatabase extends MSDatabase {
	
	public static final MyLogger LOGGER = MyLogger.getLogger(MSShiftDatabase.class);
	public static final String FORMULA_REG_EX = "([A-Z][a-z]{0,1})([0-9]{0,3})";
	
	private MassShiftDataSet naturalMassShifts = new MassShiftDataSet();
	private MassShiftDataSet markedMassShifts = new MassShiftDataSet();
	private MassShiftDataSet mixedMassShifts = new MassShiftDataSet();
	
	public MSShiftDatabase() {
		
	}
	
	/**
	 * creates a MSShiftDatabase from a csv file given by the absolute path.
	 * The file should represent a table as follows:
	 * 
	 * IncRate | NaturalMass | NaturalFrequency | NaturalShiftValues | NaturalShiftIsotopes | MarkedMass | MarkedFrequency | MarkedShiftValues | MarkedShiftIsotopes | MixedMass | MixedFrequency | MixedShiftValues | MixedShiftIsotopes | FragmentKey | IncorporatedTracers | FragmentFormula | 
	 * ________|_____________|__________________|____________________|______________________|____________|_________________|___________________|_____________________|___________|________________|__________________|____________________|_______________|_____________________|_________________|_
     *     0.8 |     43.9898 |           0.9876 |           0.0[0-0] |                 NONE |    44.9932 |          0.9966 |          0.0[0-0] |                NONE |   43.9898 |         0.1975 |         0.0[0-0] |               NONE |       UNKNOWN |                   C |             CO2 | 
     *     0.8 |     44.9932 |           0.0089 |        1.0034[0-1] |                 C_13 |         NA |              NA |                NA |                  NA |   44.9932 |          0.799 |      1.0034[0-1] |               C_13 |       UNKNOWN |                   C |             CO2 | 
	 * 
	 * @param absoluteFilePath
	 */
	public MSShiftDatabase(String absoluteFilePath) {
		this.parseCsv(absoluteFilePath);
	}
	
	/**
	 * parse this object from a csv file with information according to the MSShiftDatabaseColKey enum.
	 * The file should represent a table as follows:
	 * <br>
	 * |IncRate | NaturalMass | NaturalFrequency | NaturalShiftValues | NaturalShiftIsotopes | MarkedMass | MarkedFrequency | MarkedShiftValues | MarkedShiftIsotopes | MixedMass | MixedFrequency | MixedShiftValues | MixedShiftIsotopes | FragmentKey | IncorporatedTracers | FragmentFormula | 
	 * |________|_____________|__________________|____________________|______________________|____________|_________________|___________________|_____________________|___________|________________|__________________|____________________|_______________|_____________________|_________________|
     * |    0.8 |     43.9898 |           0.9876 |           0.0[0-0] |                 NONE |    44.9932 |          0.9966 |          0.0[0-0] |                NONE |   43.9898 |         0.1975 |         0.0[0-0] |               NONE |       UNKNOWN |                   C |             CO2 | 
     * |    0.8 |     44.9932 |           0.0089 |        1.0034[0-1] |                 C_13 |         NA |              NA |                NA |                  NA |   44.9932 |          0.799 |      1.0034[0-1] |               C_13 |       UNKNOWN |                   C |             CO2 | 
	 * 
	 * @param absoluteFilePath
	 */
	@Override
	public void parseCsv(String absoluteFilePath) {
		File csvData = new File(absoluteFilePath);
		CSVParser parser;
		try {
			parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.RFC4180);
			List<CSVRecord> records = parser.getRecords();
			setNaturalSpectrum(ParserUtils.parseSpectrum(records,
					MSShiftDatabaseColKey.NATURAL_MASS.getColumnIndex(),
					MSShiftDatabaseColKey.NATURAL_FREQUENCY.getColumnIndex(), FrequencyType.MID, 1));
			setMarkedSpectrum(ParserUtils.parseSpectrum(records,
					MSShiftDatabaseColKey.MARKED_MASS.getColumnIndex(),
					MSShiftDatabaseColKey.MARKED_FREQUENCY.getColumnIndex(), FrequencyType.MID, 1));
			setMixedSpectrum(ParserUtils.parseSpectrum(records,
					MSShiftDatabaseColKey.MIXED_MASS.getColumnIndex(),
					MSShiftDatabaseColKey.MIXED_FREQUENCY.getColumnIndex(), FrequencyType.MID, 1));
			setIncorporationRate(Double.parseDouble(records.get(1).get(
					MSShiftDatabaseColKey.INC_RATE.getColumnIndex())));
			setFragmentKey(FragmentKey.byKeyName(records.get(1).get(
					MSShiftDatabaseColKey.FRAGMENT_KEY.getColumnIndex())));
			setIncorporatedTracers(records.get(1).get(
					MSShiftDatabaseColKey.INCORPORATED_TRACERS.getColumnIndex()));
			setFragmentFormula(records.get(1).get(
					MSShiftDatabaseColKey.FRAGMENT_FORMULA.getColumnIndex()));
			naturalMassShifts = ParserUtils.parseMassShiftDataSet(records,
					MSShiftDatabaseColKey.NATURAL_SHIFT_VALUES.getColumnIndex(),
					MSShiftDatabaseColKey.NATURAL_SHIFT_ISOTOPES.getColumnIndex());
			markedMassShifts = ParserUtils.parseMassShiftDataSet(records,
					MSShiftDatabaseColKey.MARKED_SHIFT_VALUES.getColumnIndex(),
					MSShiftDatabaseColKey.MARKED_SHIFT_ISOTOPES.getColumnIndex());
			mixedMassShifts = ParserUtils.parseMassShiftDataSet(records,
					MSShiftDatabaseColKey.MIXED_SHIFT_VALUES.getColumnIndex(),
					MSShiftDatabaseColKey.MIXED_SHIFT_ISOTOPES.getColumnIndex());
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			e.printStackTrace();
		}
	}
	
	/**
	 * write this object to csv using the headers defined in MSShiftDatabaseColKey enum.
	 * The csv will represent a table as follows:
	 * 
	 * IncRate | NaturalMass | NaturalFrequency | NaturalShiftValues | NaturalShiftIsotopes | MarkedMass | MarkedFrequency | MarkedShiftValues | MarkedShiftIsotopes | MixedMass | MixedFrequency | MixedShiftValues | MixedShiftIsotopes | FragmentKey | IncorporatedTracers | FragmentFormula | 
	 * ________|_____________|__________________|____________________|______________________|____________|_________________|___________________|_____________________|___________|________________|__________________|____________________|_______________|_____________________|_________________|_
     *     0.8 |     43.9898 |           0.9876 |           0.0[0-0] |                 NONE |    44.9932 |          0.9966 |          0.0[0-0] |                NONE |   43.9898 |         0.1975 |         0.0[0-0] |               NONE |       UNKNOWN |                   C |             CO2 | 
     *     0.8 |     44.9932 |           0.0089 |        1.0034[0-1] |                 C_13 |         NA |              NA |                NA |                  NA |   44.9932 |          0.799 |      1.0034[0-1] |               C_13 |       UNKNOWN |                   C |             CO2 | 
	 * 
	 * 
	 * @param outputFolderPath
	 * @throws IOException
	 */
	public void writeCsv(String outputFolderPath) throws IOException {
		File folder = new File(outputFolderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
		String filename = createFilename() + ".csv";
		filename = FileWriterUtils.checkFilePath(filename, ".csv");
		DataTable dataTable = new DataTable(MSShiftDatabaseColKey.toHeaderList());
		dataTable.addColumn(getNaturalSpectrum());
		dataTable.addColumn(getNaturalMassShifts());
		dataTable.addColumn(getMarkedSpectrum());
		dataTable.addColumn(getMarkedMassShifts());
		dataTable.addColumn(getMixedSpectrum());
		dataTable.addColumn(getMixedMassShifts());
		dataTable.addConstantValueColumn(0,getIncorporationRate());
		dataTable.addConstantValueColumn(getFragmentKey().name());
		dataTable.addConstantValueColumn(getIncorporatedTracers());
		dataTable.addConstantValueColumn(getFragmentFormula());
		dataTable.writeToCsv(NA_VALUE, true, outputFolderPath + filename);
	}
	
	/**
	 * Returns a table string representation of this object as follows:
	 *
	 * IncRate | NaturalMass | NaturalFrequency | NaturalShiftValues | NaturalShiftIsotopes | MarkedMass | MarkedFrequency | MarkedShiftValues | MarkedShiftIsotopes | MixedMass | MixedFrequency | MixedShiftValues | MixedShiftIsotopes | FragmentKey | IncorporatedTracers | FragmentFormula | 
	 * ________|_____________|__________________|____________________|______________________|____________|_________________|___________________|_____________________|___________|________________|__________________|____________________|_______________|_____________________|_________________|_
     *     0.8 |     43.9898 |           0.9876 |           0.0[0-0] |                 NONE |    44.9932 |          0.9966 |          0.0[0-0] |                NONE |   43.9898 |         0.1975 |         0.0[0-0] |               NONE |       UNKNOWN |                   C |             CO2 | 
     *     0.8 |     44.9932 |           0.0089 |        1.0034[0-1] |                 C_13 |         NA |              NA |                NA |                  NA |   44.9932 |          0.799 |      1.0034[0-1] |               C_13 |       UNKNOWN |                   C |             CO2 | 
	 * 
	 */
	@Override
	public String toString() {
		DataTable dataTable = new DataTable(MSShiftDatabaseColKey.toHeaderList());
		dataTable.addColumn(getNaturalSpectrum());
		dataTable.addColumn(getNaturalMassShifts());
		dataTable.addColumn(getMarkedSpectrum());
		dataTable.addColumn(getMarkedMassShifts());
		dataTable.addColumn(getMixedSpectrum());
		dataTable.addColumn(getMixedMassShifts());
		dataTable.addConstantValueColumn(0,getIncorporationRate());
		dataTable.addConstantValueColumn(getFragmentKey().name());
		dataTable.addConstantValueColumn(getIncorporatedTracers());
		dataTable.addConstantValueColumn(getFragmentFormula());
		return dataTable.toString(NA_VALUE, true);
	}
	
	
	public MassShiftDataSet getNaturalMassShifts() {
		return naturalMassShifts;
	}
	public void setNaturalMassShifts(MassShiftDataSet naturalMassShifts) {
		this.naturalMassShifts = naturalMassShifts;
	}
	public MassShiftDataSet getMarkedMassShifts() {
		return markedMassShifts;
	}
	public void setMarkedMassShifts(MassShiftDataSet markedMassShifts) {
		this.markedMassShifts = markedMassShifts;
	}
	public MassShiftDataSet getMixedMassShifts() {
		return mixedMassShifts;
	}
	public void setMixedMassShifts(MassShiftDataSet mixedMassShifts) {
		this.mixedMassShifts = mixedMassShifts;
	}
	
	/**
	 * creates a string using {@link FragmentKey}, incorporated tracers and incorporation rate
	 */
	@Override
	public String createFilename() {
		int incorporationPerCent = (int) (getIncorporationRate() * 100);
		return getFragmentKey() + "_" + getIncorporatedTracers() + "_" + incorporationPerCent + "_shift";
	}
	
	/**
	 * creates the MassShiftDataset members (natural, mixed and marked) from the MassSpectra members
	 * (natural, mixed and marked).
	 */
	public void analyseAllShifts() {
		ElementList elements = ElementList.fromFormula(getFragmentFormula());
		if (getNaturalSpectrum() != null) {
			naturalMassShifts = getNaturalSpectrum().analyseMassShifts(elements);
			LOGGER.debug("Analysed naturalMassShifts");
		} else {
			LOGGER.warn("Missing naturalSpectrum to determine naturalMassShifts");
		}
		if (getMarkedSpectrum() != null) {
			markedMassShifts = getMarkedSpectrum().analyseMassShifts(elements);
			LOGGER.debug("Analysed markedMassShifts");
		} else {
			LOGGER.warn("Missing markedSpectrum to determine markedMassShifts");
		}
		if (getMixedSpectrum() != null) {
			mixedMassShifts = getMixedSpectrum().analyseMassShifts(elements);
			LOGGER.debug("Analysed mixedMassShifts");
		} else {
			LOGGER.warn("Missing mixedSpectrum to determine mixedMassShifts");
		}
	}
	
	/**
	 * Creates a nice formula representation of the isotopes that induced the shift from the p_0 peak to the peak
	 * with the parameter mass. i.e. (¹²C)₂(¹³C)₃(¹H)₂(²H)₅(¹⁵N)₂
	 * @param incType a hint to the MassSpectrum we refer to 
	 * IncorporationType.NATURAL -> natural spectrum
	 * IncorporationType.MARKED -> marked spectrum
	 * IncorporationType.MIXED -> mixed spectrum
	 * @param mass
	 * @return A nice formula representation of the isotopes that induce this mass, i.e. (¹²C)₂(¹³C)₃(¹H)₂(²H)₅(¹⁵N)₂.
	 */
	public String shiftInducingIsotopesNiceFormatted(IncorporationType incType, Double mass) {
		return shiftInducingIsotopes(incType, mass).toNiceFormattedFormula();
	}
	
	/**
	 * Creates a nice formula representation of the isotopes that induced the shift
	 * from the p_0 peak to the peak with the parameter mass. i.e.
	 * (¹²C)₂(¹³C)₃(¹H)₂(²H)₅(¹⁵N)₂
	 * 
	 * @param incType a hint to the MassSpectrum we refer to
	 *                IncorporationType.NATURAL -> natural spectrum
	 *                IncorporationType.MARKED -> marked spectrum
	 *                IncorporationType.MIXED -> mixed spectrum
	 * @param mass
	 * @return A nice formula representation of the isotopes that induce this mass,
	 *         i.e. (12C)2(13C)3(1H)2(2H)5(15N)2.
	 */
	public String shiftInducingIsotopesSimpleString(IncorporationType incType, Double mass) {
		return shiftInducingIsotopes(incType, mass).toSimpleString();
	}

	public IsotopeFormula shiftInducingIsotopes(IncorporationType incType, Double mass) {
		MassSpectrum spectrum = spectrumByIncorporationType(incType);
		List<Entry<Double, Double>> spectrumEntryList = new ArrayList<>(spectrum.entrySet());
		MassShiftDataSet shiftDataset = shiftDatasetByIncorporationType(incType);
		List<Entry<MassShiftList, IsotopeListList>> shiftEntryList = new ArrayList<>(shiftDataset.entrySet());
		int massIndex = 0;
		for (Entry<Double, Double> massEntry : spectrumEntryList) {
			if (massEntry.getKey().equals(mass)) {
				massIndex = spectrumEntryList.indexOf(massEntry);
				break;
			}
		}
		IsotopeListList shiftInducingIsotopes = shiftEntryList.get(massIndex).getValue();
		return shiftInducingIsotopes.toIsotopeFormula();
	}

	/**
	 * @param incType a hint to the MassSpectrum or MassShiftDataset we refer to:
	 *                IncorporationType.NATURAL -> natural spectrum
	 *                IncorporationType.MARKED -> marked spectrum
	 *                IncorporationType.MIXED -> mixed spectrum
	 * @return The MassShiftDataset related to this IncorporationType
	 */
	public MassShiftDataSet shiftDatasetByIncorporationType(IncorporationType incType) {
		if (incType.equals(IncorporationType.NATURAL)) {
			return getNaturalMassShifts();
		}
		if (incType.equals(IncorporationType.MIXED)) {
			return getMixedMassShifts();
		}
		return getMarkedMassShifts();
	}
	
	/**
	 * @param incType a hint to the MassSpectrum or MassShiftDataset we refer to:
	 * IncorporationType.NATURAL -> natural spectrum
	 * IncorporationType.MARKED -> marked spectrum
	 * IncorporationType.MIXED -> mixed spectrum
	 * @return The MassSpectrum related to this IncorporationType
	 */
	public MassSpectrum spectrumByIncorporationType(IncorporationType incType) {
		if (incType.equals(IncorporationType.NATURAL)) {
			return getNaturalSpectrum();
		}
		if (incType.equals(IncorporationType.MIXED)) {
			return getMixedSpectrum();
		}
		return getMarkedSpectrum();
	}
	
	public boolean includesMarkedSpectrum() {
		return !getMarkedSpectrum().entrySet().isEmpty();
	}
	
	public boolean includesMixedSpectrum() {
		return !getMixedSpectrum().entrySet().isEmpty();
	}
}
