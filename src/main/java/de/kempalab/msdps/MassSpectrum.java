package de.kempalab.msdps;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.google.common.collect.Range;

import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.ErrorMessage;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.constants.NaturalConstants;
import de.kempalab.msdps.constants.SpectrumType;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.exception.TypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.util.MathUtils;
import de.kempalab.msdps.util.ParserUtils;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.MassSpectrumType;
import net.sf.mzmine.datamodel.PolarityType;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.impl.SimpleDataPoint;
import net.sf.mzmine.datamodel.impl.SimpleScan;
import net.sf.mzmine.project.impl.RawDataFileImpl;

/**
 * A map, used to identify a mass (the map key) to its intensity (the map value)
 * 
 * @author sfuerst
 *
 */
@SuppressWarnings("serial")
public class MassSpectrum extends LinkedHashMap<Double,Double> {
	public static final MyLogger LOGGER = MyLogger.getLogger(MassSpectrum.class);
	private IntensityType intensityType;
	private SpectrumType spectrumType;
	public static final boolean NEW_VERSION = true;
	IsotopeComposition compositions = new IsotopeComposition();

	public MassSpectrum(IntensityType intensityType, SpectrumType spectrumType) {
		this.intensityType = intensityType;
		this.spectrumType = spectrumType;
	}
	
	/**
	 * Merges this map with an other map by adding intensities of masses that are
	 * contained in both maps and adding new entries from the other map that are not
	 * contained in this map. This only makes sense if both have
	 * IntensityType.ABSOLUTE.
	 * 
	 * @param otherSpectrum
	 * @return a new merged MassSpectrum
	 * @throws IntensityTypeMismatchException
	 */
	public MassSpectrum merge(MassSpectrum otherSpectrum) throws TypeMismatchException {
		if (!this.intensityType.equals(otherSpectrum.getIntensityType())) {
			throw new TypeMismatchException(ErrorMessage.INTENSITY_TYPE_MISMATCH.getMessage());
		}
		if (!this.spectrumType.equals(otherSpectrum.getSpectrumType())) {
			throw new TypeMismatchException(ErrorMessage.SPECTRUM_TYPE_MISMATCH.getMessage());
		}
		if (this.isEmpty()) {
			return otherSpectrum;
		}
		if (otherSpectrum.isEmpty()) {
			return this;
		}
		MassSpectrum newSpectrum = new MassSpectrum(IntensityType.ABSOLUTE, this.getSpectrumType());
		for (Entry<Double, Double> entry : this.entrySet()) {
			Double thisMass = entry.getKey();
			Double thisIntensity = entry.getValue();
			IsotopeFormula thisComposition = this.getComposition(thisMass);
			double thisOldIntensity = newSpectrum.get(thisMass) != null ? newSpectrum.get(thisMass) : 0;
			newSpectrum.put(thisMass, thisOldIntensity + thisIntensity);
			newSpectrum.putComposition(thisMass, thisComposition);
		}
		for (Entry<Double,Double> otherEntry : otherSpectrum.entrySet()) {
			Double otherMass = otherEntry.getKey();
			Double otherIntensity = otherEntry.getValue();
			IsotopeFormula otherComposition = otherSpectrum.getComposition(otherMass);
			double otherOldIntensity = newSpectrum.get(otherMass) != null ? newSpectrum.get(otherMass) : 0;
			newSpectrum.put(otherMass, otherOldIntensity + otherIntensity);
			newSpectrum.putComposition(otherMass, otherComposition);
		}
		if (this.getIntensityType().equals(IntensityType.MID)) {
			newSpectrum = newSpectrum.toMID();
		}
		if (this.getIntensityType().equals(IntensityType.RELATIVE)) {
			newSpectrum = newSpectrum.toRelativeIntensity();
		}
		return newSpectrum;
	}
	
	public IsotopeFormula getComposition(Double mass) {
		if (getCompositions().get(mass) == null) {
//			LOGGER.info("No composition for mass " + mass);
		}
		return getCompositions().get(mass);
	}

	/**
	 * 
	 * @return A copy of this Spectrum with intensities converted to MIDs (mass
	 *         isotopomer distribution)
	 * @throws TypeMismatchException 
	 */
	public MassSpectrum toMID() throws TypeMismatchException {
		if (this.getSpectrumType().equals(SpectrumType.CONTINUOUS)) {
			throw new TypeMismatchException(ErrorMessage.NO_MID_DEFINITION.getMessage());
		}
		if (this.intensityType.equals(IntensityType.MID)) {
			return this;
		} else {
			MassSpectrum newSpectrum = new MassSpectrum(IntensityType.MID, this.getSpectrumType());
			Double absoluteNumberOfFragments = 0.0;
			for (Entry<Double,Double> entry : this.entrySet()) {
				absoluteNumberOfFragments = absoluteNumberOfFragments + entry.getValue();
			}
			for (Entry<Double,Double> entry : this.entrySet()) {
				Double relativeIntensity = entry.getValue() / absoluteNumberOfFragments;
				newSpectrum.put(entry.getKey(), relativeIntensity);
			}
			newSpectrum.setCompositions(this.getCompositions());
			return newSpectrum;
		}
	}
	
	/**
	 * 
	 * @return A copy of this Spectrum with intensities converted to relative ones
	 */
	public MassSpectrum toRelativeIntensity() {
		if (this.intensityType.equals(IntensityType.RELATIVE)) {
			return this;
		} else {
			MassSpectrum newSpectrum = new MassSpectrum(IntensityType.RELATIVE, this.getSpectrumType());
			Double highestIntensity = getHighestIntensity();
			for (Entry<Double, Double> entry : this.entrySet()) {
				Double relativeIntensity = (entry.getValue() / highestIntensity) * 100;
				newSpectrum.put(entry.getKey(), relativeIntensity);
			}
			newSpectrum.setCompositions(this.getCompositions());
			return newSpectrum;
		}
	}

	/**
	 * 
	 * @param minimalValue
	 * @return A copy of this map without masses that have an abundance under the
	 *         minimalValue
	 */
	public MassSpectrum skipLowIntensity(Double minimalValue) {
		MassSpectrum newSpectrum = new MassSpectrum(this.getIntensityType(), this.getSpectrumType());
		for (Entry<Double, Double> entry : this.entrySet()) {
			if (entry.getValue() >= minimalValue) {
				newSpectrum.put(entry.getKey(), entry.getValue());
				newSpectrum.putComposition(entry.getKey(), getComposition(entry.getKey()));
			}
		}
		return newSpectrum;
	}
	
	/**
	 * This returns a new map without masses of this map, that are greater than the highest mass parameter.
	 * @param highestMass
	 * @return a new map with masses lower than upper bound
	 */
	public MassSpectrum skipHighMasses(Double highestMass) {
		MassSpectrum newSpectrum = new MassSpectrum(this.intensityType, this.getSpectrumType());
		for (Entry<Double,Double> entry : this.entrySet()) {
			if (entry.getKey() <= highestMass) {
				newSpectrum.put(entry.getKey(), entry.getValue());
				newSpectrum.putComposition(entry.getKey(), getComposition(entry.getKey()));
			}
		}
		return newSpectrum;
	}
	
	/**
	 * This method rounds all masses to the given precision. If this causes doubled
	 * masses they will be saved as one mass with their intensities added.
	 * 
	 * @param precision
	 * @return a new map with masses rounded to the given precision.
	 */
	public MassSpectrum roundMasses(int precision) {
		MassSpectrum newSpectrum = new MassSpectrum(this.intensityType, this.getSpectrumType());
		for (Entry<Double,Double> currentEntry : this.entrySet()) {
			Double roundedMass = MathUtils.round(currentEntry.getKey(), precision);
			if (newSpectrum.containsKey(roundedMass)) {
				Double oldIntensity = newSpectrum.get(roundedMass);
				newSpectrum.put(roundedMass, currentEntry.getValue() + oldIntensity);
				
			} else {
				newSpectrum.put(roundedMass, currentEntry.getValue());
			}
			// TODO: this overwrites existing entries, check how to decide for the
			// composition with the highest intensity
			newSpectrum.putComposition(roundedMass, getComposition(currentEntry.getKey()));
		}
		return newSpectrum;
	}
	
	/**
	 * This method rounds the intensities of this map to the given precision.
	 * 
	 * @param precision
	 * @return a new map with intensities rounded to the given precision.
	 */
	public MassSpectrum roundIntensities(int precision) {
		MassSpectrum newSpectrum = new MassSpectrum(this.intensityType, this.getSpectrumType());
		for (Entry<Double,Double> currentEntry : this.entrySet()) {
			Double roundedIntensity = MathUtils.round(currentEntry.getValue(), precision);
			newSpectrum.put(currentEntry.getKey(), roundedIntensity);
//			newSpectrum.putComposition(currentEntry.getKey(), getComposition(currentEntry.getKey()));
		}
		newSpectrum.setCompositions(compositions);
		return newSpectrum;
		
	}
	
	/**
	 * 
	 * @return a new sorted spectrum
	 */
	public MassSpectrum sortDescendingByMass() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByKey());
		MassSpectrum sortedList = new MassSpectrum(this.intensityType, this.getSpectrumType());
		for (int index = entryList.size() - 1; index >= 0; index--) {
			Entry<Double, Double> entry = entryList.get(index);
	            sortedList.put(entry.getKey(), entry.getValue());
		}
		sortedList.setCompositions(getCompositions().sortDescendingByMass());
        return sortedList;
	}
	
	/**
	 * 
	 * @return a new sorted spectrum
	 */
	public MassSpectrum sortDescendingByIntensity() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByValue());
		MassSpectrum sortedList = new MassSpectrum(this.intensityType, this.getSpectrumType());
		for (int index = entryList.size() - 1; index >= 0; index--) {
			Entry<Double, Double> entry = entryList.get(index);
	            sortedList.put(entry.getKey(), entry.getValue());
	            sortedList.putComposition(entry.getKey(), getComposition(entry.getKey()));
		}
        return sortedList;
	}
	
	/**
	 * 
	 * @return a new sorted spectrum
	 */
	public MassSpectrum sortAscendingByIntensity() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByValue());
		MassSpectrum sortedList = new MassSpectrum(this.intensityType, this.getSpectrumType());
		for (Entry<Double, Double> entry : entryList) {
			sortedList.put(entry.getKey(), entry.getValue());
			sortedList.putComposition(entry.getKey(), getComposition(entry.getKey()));
        }
        return sortedList;
	}
	
	/**
	 * 
	 * @return a new sorted spectrum
	 */
	public MassSpectrum sortAscendingByMass() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByKey());
		MassSpectrum sortedList = new MassSpectrum(this.intensityType, this.getSpectrumType());
		for (Entry<Double, Double> entry : entryList) {
			sortedList.put(entry.getKey(), entry.getValue());
        }
		IsotopeComposition newComposition = this.getCompositions().sortAscendingByMass();
		sortedList.setCompositions(newComposition);
        return sortedList;
	}
	
	public IntensityType getIntensityType() {
		return intensityType;
	}
	
	public void setIntensityType(IntensityType intensityType) {
		this.intensityType = intensityType;
	}

	public SpectrumType getSpectrumType() {
		return spectrumType;
	}

	public void setSpectrumType(SpectrumType spectrumType) {
		this.spectrumType = spectrumType;
	}

	/**
	 * @param elements elements that may induce a shift
	 * @return the MassShiftDataSet corresponding to the isotope pattern represented by this MassSpectrum,
	 * under the assumption that each mass gap between two consecutive peaks is induced by only up to one isotope.
	 * 
	 */
	public MassShiftDataSet analyseMassShifts(ElementList elements) {
		MassShiftDataSet massShiftData = new MassShiftDataSet();
		ArrayList<MassShiftList> massShiftLists = new ArrayList<>();
		massShiftLists.add(new MassShiftList(new MassShift(0, 0, 0.0)));
		List<Entry<Double, Double>> massEntries = new ArrayList<>(this.entrySet());
		int numberOfMassEntries = massEntries.size();
		LOGGER.debugValue("Analyse total peaks", this.size());
		for (int leadingIndex = 1; leadingIndex < numberOfMassEntries; leadingIndex++) {
			MassShiftList massShiftList = new MassShiftList();
			if (NEW_VERSION) {
				if (leadingIndex > 1) {
					massShiftList.addAll(massShiftLists.subList(1, leadingIndex));
				}
				LOGGER.debugValue("Analyse peak number", leadingIndex);
				int peak1 = leadingIndex - 1;
				int peak2 = leadingIndex;
				Double shiftValue = massEntries.get(leadingIndex).getKey() - massEntries.get(leadingIndex - 1).getKey();
				Double roundedShiftValue = MathUtils.round(shiftValue, 6);
				/*
				 * as long as the roundedShiftValue cannot be connected with an isotope check if an peak overleaping mass shift
				 * should be considered. That means if there is no isotope that can induce a shift from p_2 to p_3 there may be
				 * an isotope that induces a shift from p_0 to p_3.
				 */
				int peakBackShift = 2;
				boolean backShiftPossible = leadingIndex - peakBackShift >= 0;
				boolean isotopeNotIdentified = Isotope.approximatelyByMassShiftValueAndAvailableElements(
						roundedShiftValue, elements).equals(new IsotopeList(Isotope.UNDEFINED));
				while (isotopeNotIdentified && backShiftPossible) {
					shiftValue = massEntries.get(leadingIndex).getKey() - massEntries.get(leadingIndex - peakBackShift).getKey();
					roundedShiftValue = MathUtils.round(shiftValue, 6);
					peak1 = leadingIndex - peakBackShift;
					isotopeNotIdentified = Isotope.approximatelyByMassShiftValueAndAvailableElements(
							roundedShiftValue, elements).equals(new IsotopeList(Isotope.UNDEFINED));
					peakBackShift++;
					backShiftPossible = leadingIndex - peakBackShift >= 0;
				}
				massShiftList.add(new MassShift(peak1, peak2, roundedShiftValue));
			} else {
				for (int index = 1; index <= leadingIndex; index++ ) {
					int peak1 = index - 1;
					int peak2 = index;
					Double shiftValue = massEntries.get(index).getKey() - massEntries.get(index - 1).getKey();
					Double roundedShiftValue = MathUtils.round(shiftValue, 6);
					/*
					 * as long as the roundedShiftValue cannot be connected with an isotope check if an peak overleaping mass shift
					 * should be considered. That means if there is no isotope that can induce a shift from p_2 to p_3 there may be
					 * an isotope that induces a shift from p_0 to p_3.
					 */
					int peakBackShift = 2;
					boolean backShiftPossible = index - peakBackShift >= 0;
					boolean isotopeNotIdentified = Isotope.approximatelyByMassShiftValueAndAvailableElements(
							roundedShiftValue, elements).equals(new IsotopeList(Isotope.UNDEFINED));
					while (isotopeNotIdentified && backShiftPossible) {
						shiftValue = massEntries.get(index).getKey() - massEntries.get(index - peakBackShift).getKey();
						roundedShiftValue = MathUtils.round(shiftValue, 6);
						peak1 = index - peakBackShift;
						isotopeNotIdentified = Isotope.approximatelyByMassShiftValueAndAvailableElements(
								roundedShiftValue, elements).equals(new IsotopeList(Isotope.UNDEFINED));
						peakBackShift++;
						backShiftPossible = index - peakBackShift >= 0;
					}
					massShiftList.add(new MassShift(peak1, peak2, roundedShiftValue));
				}
			}
			massShiftList = massShiftList.findConnectedSubSequence(0,leadingIndex);
			massShiftLists.add(massShiftList);
		}
		for (MassShiftList massShiftList : massShiftLists) {
			IsotopeListList correspondingIsotopes = massShiftList.correspondingIsotopes();
			massShiftData.put(massShiftList, correspondingIsotopes);
		}
		return massShiftData;
	}
	
	@Override
	public String toString() {
		DataTable dataTable = new DataTable("Mass", "Frequency", "Composition");
		dataTable.addColumn(this);
		ArrayList<String> compositionList = new ArrayList<>();
		for (Entry<Double,IsotopeFormula> entry : compositions.entrySet()) {
			compositionList.add(entry.getValue().toSimpleString());
		}
		dataTable.add(compositionList);
		return dataTable.toString("NA", true);
	}
	
	public List<Entry<Double, Double>> toEntryList() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		return entryList;
	}
	
	public Double sumOfIntensities() {
		Double sum = 0.0;
		for (Entry<Double, Double> entry : this.entrySet()) {
			sum = sum + entry.getValue();
		}
		return sum;
	}
	
	public static MassSpectrum fromRawFileExportCsv(String absoluteFilePath, SpectrumType spectrumType) {
		File csvData = new File(absoluteFilePath);
		MassSpectrum spectrum = null;
		CSVParser parser;
		try {
			parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.RFC4180);
			List<CSVRecord> records = parser.getRecords();
			spectrum = ParserUtils.parseSpectrum(records, 0, 1, IntensityType.ABSOLUTE, 7, spectrumType);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			e.printStackTrace();
		}
		return spectrum;
	}

	/**
	 * Creates a MassSpectrum with intensities absolute to the number of elements in
	 * the fragmentMasses list.
	 * 
	 * @param fragmentMasses
	 * @return A MassSpectrum with ABSOLUTE intensities
	 */
	public static MassSpectrum createSpectrumFromMasses(ArrayList<Double> fragmentMasses, SpectrumType spectrumType) {
		MassSpectrum spectrum = new MassSpectrum(IntensityType.ABSOLUTE, spectrumType);
		for (Double mass : fragmentMasses) {
			if (!(mass > 0.0)) {
				LOGGER.info("Detected mass 0.0");
			}
			if (spectrum.get(mass) == null /* count each mass only once */) {
				int index = fragmentMasses.indexOf(mass);
				int massCount = 1;
				for (Double otherMass : fragmentMasses.subList(index + 1, fragmentMasses.size())) {
					if (mass.equals(otherMass)) {
						massCount++;
					}
				}
				spectrum.put(mass, Double.valueOf(massCount));
			}
		}
		return spectrum;
	}

	/**
	 * 
	 * @param charge, charge of the molecule
	 * @return this spectrum where the masses are reduced by charge * electron mass
	 */
	public MassSpectrum adjustToCharge(int charge) {
		if (charge == 0) {
			return this;
		}
		MassSpectrum adjusted = new MassSpectrum(this.getIntensityType(), this.getSpectrumType());
		for (Entry<Double, Double> entry : this.entrySet()) {
			adjusted.put(entry.getKey() - charge * NaturalConstants.ELECTRON_MASS.getValue(), entry.getValue());
		}
		IsotopeComposition newComposition = new IsotopeComposition();
		for (Entry<Double, IsotopeFormula> entry : getCompositions().entrySet()) {
			newComposition.put(entry.getKey() - charge * NaturalConstants.ELECTRON_MASS.getValue(), entry.getValue());
		}
		adjusted.setCompositions(newComposition);
		return adjusted;
	}

	public Double getHighestMass() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByKey());
		return entryList.get(entryList.size() - 1).getKey();
	}

	public Double getLowestMass() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByKey());
		return entryList.get(0).getKey();
	}

	public Double getHighestIntensity() {
		if (this.size() == 0) {
			return 0.0;
		}
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByValue());
		return entryList.get(entryList.size() - 1).getValue();
	}

	public Double getLowestIntensity() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByValue());
		return entryList.get(0).getValue();
	}

	public Double getMostAbundandMass() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByValue());
		return entryList.get(entryList.size() - 1).getKey();
	}

	public RawDataFile toRawDataFile() throws IOException {
		RawDataFileImpl rawDataFile = new RawDataFileImpl("fileFromSimulatedSpectrum");
		int scanNumber = 1;
		int msLevel = 1;
		int fragmentScans[] = { 1 };
		DataPoint[] dataPoints = new SimpleDataPoint[this.size()];
		int entryCount = 0;
		for (Entry<Double, Double> entry : this.entrySet()) {
			DataPoint datapoint = new SimpleDataPoint(entry.getKey(), entry.getValue());
			dataPoints[entryCount] = datapoint;
			entryCount++;
		}
		double precursorMZ = 0.0;
		int precursorCharge = 0;
		double retentionTime = 0.0;
		Range<Double> mzRange = Range.closed(60.0, 600.0);
		Double mostAbundantMass = this.getMostAbundandMass();
		DataPoint basePeak = new SimpleDataPoint(mostAbundantMass, this.get(mostAbundantMass));
		double totalIonCurrent = 100.0;
		MassSpectrumType spectrumType = MassSpectrumType.CENTROIDED;
		PolarityType polarity = PolarityType.NEGATIVE;
		String scanDefinition = "SimulatedScan";
		Range<Double> scanMZRange = Range.closed(this.getLowestMass(), this.getHighestMass());
		SimpleScan scan = new SimpleScan(rawDataFile, scanNumber, msLevel, retentionTime, precursorMZ, precursorCharge,
				fragmentScans, dataPoints, spectrumType, polarity, scanDefinition, scanMZRange);
		rawDataFile.addScan(scan);
		return rawDataFile;
	}
	
	public IsotopePattern analyseCompositions(ElementFormula formula) { 
		if (!compositions.removeNullValues().isEmpty()) {
			return new IsotopePattern(this, true);
		}
		if (this.getSpectrumType().equals(SpectrumType.CONTINUOUS)) {
			LOGGER.warn("Cannot analyze mass shifts for continuous data");
			return new IsotopePattern(this, true);
		}
		MassShiftDataSet massShiftDataset = this.analyseMassShifts(formula.toElementList());
		IsotopeComposition isotopeComposition = new IsotopeComposition();
		IsotopeComposition peakInducingHeavyIsotopes = new IsotopeComposition();
		int massIndex = 0;
		for (Entry<MassShiftList, IsotopeListList> shiftEntry : massShiftDataset.entrySet()) {
			IsotopeFormula shiftInducingIsotopes = shiftEntry.getValue().toIsotopeFormula();
			peakInducingHeavyIsotopes.put(this.getMass(massIndex), shiftInducingIsotopes);
			IsotopeFormula completeIsotopeFormula = new IsotopeFormula();
			for (Entry<Element, Integer> compoundFormulaEntry : formula.entrySet()) {
				Element element = compoundFormulaEntry.getKey();
				for (Isotope isotope : element.getIsotopes()) {
					int totalElementNumber = formula.get(element);
					if (shiftInducingIsotopes.get(isotope) != null) {
						int numberOfHeavyIsotopes = shiftInducingIsotopes.get(isotope);
						completeIsotopeFormula.put(element.lightestIsotope(),
								totalElementNumber - numberOfHeavyIsotopes);
						completeIsotopeFormula.put(isotope, numberOfHeavyIsotopes);
					} else {
						completeIsotopeFormula.put(element.lightestIsotope(), totalElementNumber);
					}
				}
			}
			isotopeComposition.put(this.getMass(massIndex), completeIsotopeFormula);
			massIndex++;
		}
		IsotopePattern pattern = new IsotopePattern(this, true);
		pattern.setIsotopeComposition(isotopeComposition);
		pattern.setPeakInducingHeavyIsotopes(peakInducingHeavyIsotopes);
		return pattern;
	}

	public MassSpectrum addLabel(ElementFormula elementFormula) {
		MassSpectrum labeledSpectrum = new MassSpectrum(this.getIntensityType(), this.getSpectrumType());
		IsotopeFormula tracer = new IsotopeFormula();
		for (Entry<Element,Integer> elementEntry : elementFormula.entrySet()) {
			tracer.put(elementEntry.getKey().getTracer(), elementEntry.getValue());
		}
		Double tracerMass = tracer.calculateMass(0);
		for (Entry<Double,Double> entry : this.entrySet()) {
			labeledSpectrum.put(entry.getKey() + tracerMass, entry.getValue());
		}
		return labeledSpectrum;
	}
	
	public MassSpectrum scale(Double scaleFactor) {
		MassSpectrum scaledSpectrum = new MassSpectrum(this.getIntensityType(), this.getSpectrumType());
		for (Entry<Double,Double> entry : this.entrySet()) {
			scaledSpectrum.put(entry.getKey(), entry.getValue() * scaleFactor);
		}
		scaledSpectrum.setCompositions(getCompositions());
		return scaledSpectrum;
	}

	public void putComposition(double mass, IsotopeFormula composition) {
		compositions.put(mass, composition);
	}

	public IsotopeComposition getCompositions() {
		return compositions;
	}

	public void setCompositions(IsotopeComposition compositions) {
		this.compositions = compositions;
	}
	
	public Double getMass(int index) {
		List<Entry<Double, Double>> list = this.toEntryList();
		return list.get(index).getKey();
	}

}
