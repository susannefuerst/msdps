package de.kempalab.msdps;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import de.kempalab.msdps.constants.ErrorMessage;
import de.kempalab.msdps.constants.FrequencyType;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.data.DataTable;
import de.kempalab.msdps.exception.FrequencyTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import de.kempalab.msdps.util.MathUtils;
import de.kempalab.msdps.util.ParserUtils;
/**
 * A map, used to identify a mass (the map key) to its frequency (the map value)
 * 
 * @author sfuerst
 *
 */
@SuppressWarnings("serial")
public class MassSpectrum extends LinkedHashMap<Double,Double> {
	public static final MyLogger LOGGER = MyLogger.getLogger(MassSpectrum.class);
	private FrequencyType frequencyType = FrequencyType.ABSOLUTE;
	public static final boolean NEW_VERSION = true;

	public MassSpectrum(FrequencyType frequencyType) {
		this.frequencyType = frequencyType;
	}
	
	/**
	 * Merges this map with an other map by adding frequencies of masses that are contained in both maps and adding
	 * new entries from the other map that are not contained in this map. This only makes sense if both have FrequencyType.ABSOLUTE.
	 * 
	 * @param otherSpectrum
	 * @return a new merged MassSpectrum
	 * @throws FrequencyTypeMismatchException 
	 */
	public MassSpectrum merge(MassSpectrum otherSpectrum) throws FrequencyTypeMismatchException {
		if (this.frequencyType.equals(FrequencyType.RELATIVE) || otherSpectrum.frequencyType.equals(FrequencyType.RELATIVE)) {
			throw new FrequencyTypeMismatchException(ErrorMessage.FREQUENCY_TYPE_MISMATCH.getMessage());
		}
		if (this.isEmpty()) {
			return otherSpectrum;
		}
		if (otherSpectrum.isEmpty()) {
			return this;
		}
		MassSpectrum newSpectrum = new MassSpectrum(FrequencyType.ABSOLUTE);
		for (Entry<Double, Double> entry : this.entrySet()) {
			Double thisMass = entry.getKey();
			Double thisFrequency = entry.getValue();
			double thisOldFrequency = newSpectrum.get(thisMass) != null ? newSpectrum.get(thisMass) : 0;
			newSpectrum.put(thisMass, thisOldFrequency + thisFrequency);
		}
		for (Entry<Double,Double> otherEntry : otherSpectrum.entrySet()) {
			Double otherMass = otherEntry.getKey();
			Double otherFrequency = otherEntry.getValue();
			double otherOldFrequency = newSpectrum.get(otherMass) != null ? newSpectrum.get(otherMass) : 0;
			newSpectrum.put(otherMass, otherOldFrequency + otherFrequency);
		}
		return newSpectrum;
	}
	
	/**
	 * This method converts the frequencies of this map to relative frequencies if this map contains not yet relative ones.
	 * Otherwise this map will be returned.
	 * @return A new map if this map contains not yet relative intensities (else this map will be returned)
	 */
	public MassSpectrum toRelativeFrequency() {
		if (this.frequencyType.equals(FrequencyType.RELATIVE)) {
			return this;
		} else {
			MassSpectrum newSpectrum = new MassSpectrum(FrequencyType.RELATIVE);
			Double absoluteNumberOfFragments = 0.0;
			for (Entry<Double,Double> entry : this.entrySet()) {
				absoluteNumberOfFragments = absoluteNumberOfFragments + entry.getValue();
			}
			for (Entry<Double,Double> entry : this.entrySet()) {
				Double relativeFrequency = entry.getValue() / absoluteNumberOfFragments;
				newSpectrum.put(entry.getKey(), relativeFrequency);
			}
			return newSpectrum;
		}
	}
	
	/**
	 * This returns a new map without the low frequency masses of this map.
	 * If this map does not yet contain relative frequencies they will be converted for the new map.
	 * @param minimalRelativeFrequency
	 * @return a new map with relative frequencies greater or equal the minimalRelativeFrequency
	 */
	public MassSpectrum skipLowFrequencies(Double minimalRelativeFrequency) {
		MassSpectrum newSpectrum = new MassSpectrum(FrequencyType.RELATIVE);
		if (this.frequencyType.equals(FrequencyType.RELATIVE)) {
			for (Entry<Double,Double> entry : this.entrySet()) {
				if (entry.getValue() >= minimalRelativeFrequency) {
					newSpectrum.put(entry.getKey(), entry.getValue());
				}
			}
		} else {
			LOGGER.info("Frequencies will be converted to relative ones to skip lowest");
			MassSpectrum relativeSpectrum = this.toRelativeFrequency();
			for (Entry<Double,Double> entry : relativeSpectrum.entrySet()) {
				if (entry.getValue() >= minimalRelativeFrequency) {
					newSpectrum.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return newSpectrum;
	}
	
	/**
	 * This returns a new map without the low frequency masses of this map.
	 * If this map does not yet contain relative frequencies they will be converted for the new map.
	 * @param minimalRelativeFrequency
	 * @return a new map with relative frequencies greater than the minimalRelativeFrequency
	 */
	public MassSpectrum skipFrequenciesUpTo(Double minimalRelativeFrequency) {
		MassSpectrum newSpectrum = new MassSpectrum(FrequencyType.RELATIVE);
		if (this.frequencyType.equals(FrequencyType.RELATIVE)) {
			for (Entry<Double,Double> entry : this.entrySet()) {
				if (entry.getValue() > minimalRelativeFrequency) {
					newSpectrum.put(entry.getKey(), entry.getValue());
				}
			}
		} else {
			LOGGER.info("Frequencies will be converted to relative ones to skip lowest");
			MassSpectrum relativeSpectrum = this.toRelativeFrequency();
			for (Entry<Double,Double> entry : relativeSpectrum.entrySet()) {
				if (entry.getValue() > minimalRelativeFrequency) {
					newSpectrum.put(entry.getKey(), entry.getValue());
				}
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
		MassSpectrum newSpectrum = new MassSpectrum(this.frequencyType);
		for (Entry<Double,Double> entry : this.entrySet()) {
			if (entry.getKey() <= highestMass) {
				newSpectrum.put(entry.getKey(), entry.getValue());
			}
		}
		return newSpectrum;
	}
	
	/**
	 * This method rounds all masses to the given precision. If this causes doubled masses they will be saved as one mass
	 * with their frequencies added.
	 * @param precision
	 * @return a new map with masses rounded to the given precision.
	 */
	public MassSpectrum roundMasses(int precision) {
		MassSpectrum newSpectrum = new MassSpectrum(this.frequencyType);
		for (Entry<Double,Double> currentEntry : this.entrySet()) {
			Double roundedMass = MathUtils.round(currentEntry.getKey(), precision);
			if (newSpectrum.containsKey(roundedMass)) {
				Double oldFrequency = newSpectrum.get(roundedMass);
				newSpectrum.put(roundedMass, currentEntry.getValue() + oldFrequency);
				
			} else {
				newSpectrum.put(roundedMass, currentEntry.getValue());
			}
		}
		return newSpectrum;
	}
	
	/**
	 * This method rounds the frequencies of this map to the given precision.
	 * @param precision
	 * @return a new map with frequencies rounded to the given precision.
	 */
	public MassSpectrum roundFrequencies(int precision) {
		MassSpectrum newSpectrum = new MassSpectrum(this.frequencyType);
		for (Entry<Double,Double> currentEntry : this.entrySet()) {
			Double roundedFrequency = MathUtils.round(currentEntry.getValue(), precision);
			newSpectrum.put(currentEntry.getKey(), roundedFrequency);
		}
		return newSpectrum;
		
	}
	
	/**
	 * 
	 * @return a new sorted spectrum
	 */
	public MassSpectrum sortDescendingByMass() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByKey());
		MassSpectrum sortedList = new MassSpectrum(this.frequencyType);
		for (int index = entryList.size() - 1; index >= 0; index--) {
			Entry<Double, Double> entry = entryList.get(index);
	            sortedList.put(entry.getKey(), entry.getValue());
		}
        return sortedList;
	}
	
	/**
	 * 
	 * @return a new sorted spectrum
	 */
	public MassSpectrum sortDescendingByFrequency() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByValue());
		MassSpectrum sortedList = new MassSpectrum(this.frequencyType);
		for (int index = entryList.size() - 1; index >= 0; index--) {
			Entry<Double, Double> entry = entryList.get(index);
	            sortedList.put(entry.getKey(), entry.getValue());
		}
        return sortedList;
	}
	
	/**
	 * 
	 * @return a new sorted spectrum
	 */
	public MassSpectrum sortAscendingByFrequency() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		entryList.sort(Entry.comparingByValue());
		MassSpectrum sortedList = new MassSpectrum(this.frequencyType);
		for (Entry<Double, Double> entry : entryList) {
			sortedList.put(entry.getKey(), entry.getValue());
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
		MassSpectrum sortedList = new MassSpectrum(this.frequencyType);
		for (Entry<Double, Double> entry : entryList) {
			sortedList.put(entry.getKey(), entry.getValue());
        }
        return sortedList;
	}
	
	public FrequencyType getFrequencyType() {
		return frequencyType;
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
		DataTable dataTable = new DataTable("Mass", "Frequency");
		dataTable.addColumn(this);
		return dataTable.toString("NA", true);
	}
	
	public List<Entry<Double, Double>> toEntryList() {
		List<Entry<Double, Double>> entryList = new ArrayList<>(this.entrySet());
		return entryList;
	}
	
	public Double sumOfFrequencies() {
		Double sum = 0.0;
		for (Entry<Double, Double> entry : this.entrySet()) {
			sum = sum + entry.getValue();
		}
		return sum;
	}
	
	public static MassSpectrum fromRawFileExportCsv(String absoluteFilePath) {
		File csvData = new File(absoluteFilePath);
		MassSpectrum spectrum = null;
		CSVParser parser;
		try {
			parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.RFC4180);
			List<CSVRecord> records = parser.getRecords();
			spectrum = ParserUtils.parseSpectrum(records, 0, 1, FrequencyType.ABSOLUTE, 7);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			e.printStackTrace();
		}
		return spectrum;
	}

	/**
	 * Creates a MassSpectrum with frequencies absolute to the number of elements in
	 * the fragmentMasses list.
	 * 
	 * @param fragmentMasses
	 * @return A MassSpectrum with ABSOLUTE frequencies
	 */
	public static MassSpectrum createSpectrumFromMasses(ArrayList<Double> fragmentMasses) {
		MassSpectrum spectrum = new MassSpectrum(FrequencyType.ABSOLUTE);
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
}
