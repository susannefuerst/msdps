package de.kempalab.msdps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import de.kempalab.msdps.constants.Element;
import de.kempalab.msdps.constants.ErrorMessage;
import de.kempalab.msdps.constants.IntensityType;
import de.kempalab.msdps.constants.Isotope;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.exception.IntensityTypeMismatchException;
import de.kempalab.msdps.log.MyLogger;
import junit.framework.TestCase;

public class MassSpectrumTest extends TestCase {
	
	public static final MyLogger LOGGER = MyLogger.getLogger(MassSpectrumTest.class);
	
	public void testMerge() throws Exception {
		MassSpectrum firstSpectrum = new MassSpectrum(IntensityType.ABSOLUTE);
		firstSpectrum.put(123.000, 10.0);
		firstSpectrum.put(123.010, 11.0);
		firstSpectrum.put(124.000, 5.0);
		firstSpectrum.put(125.000, 5.0);
		MassSpectrum otherSpectrum = new MassSpectrum(IntensityType.ABSOLUTE);
		otherSpectrum.put(123.010, 4.0);
		otherSpectrum.put(124.010, 8.0);
		otherSpectrum.put(125.000, 10.0);
		MassSpectrum expectedMergedSpectrum = new MassSpectrum(IntensityType.ABSOLUTE);
		expectedMergedSpectrum.put(123.000, 10.0);
		expectedMergedSpectrum.put(123.010, 15.0);
		expectedMergedSpectrum.put(124.000, 5.0);
		expectedMergedSpectrum.put(124.010, 8.0);
		expectedMergedSpectrum.put(125.000, 15.0);
		MassSpectrum mergedSpectrum = firstSpectrum.merge(otherSpectrum);
		LOGGER.info(expectedMergedSpectrum);
		LOGGER.info(mergedSpectrum);
		assertEquals(expectedMergedSpectrum, mergedSpectrum);
	}
	
	public void testMergeFail() {
		MassSpectrum firstSpectrum = new MassSpectrum(IntensityType.MID);
		firstSpectrum.put(123.000, 10.0);
		MassSpectrum otherSpectrum = new MassSpectrum(IntensityType.ABSOLUTE);
		otherSpectrum.put(123.010, 4.0);
		try {
			firstSpectrum.merge(otherSpectrum);
			fail("This should throw an exception becaus of frequency type mismatch");
		} catch (IntensityTypeMismatchException e) {
			assertEquals(e.getMessage(), ErrorMessage.INTENSITY_TYPE_MISMATCH.getMessage());
		}
	}
	
	public void testToMIDFrequency() {
		MassSpectrum map = new MassSpectrum(IntensityType.ABSOLUTE);
		map.put(123.000, 10.0);
		map.put(123.010, 11.0);
		map.put(124.000, 5.0);
		map.put(125.000, 5.0);
		MassSpectrum convertedSpectrum = map.toMID();
		MassSpectrum expectedConvertedSpectrum = new MassSpectrum(IntensityType.MID);
		expectedConvertedSpectrum.put(123.000, 10.0 / 31.0);
		expectedConvertedSpectrum.put(123.010, 11.0 / 31.0);
		expectedConvertedSpectrum.put(124.000, 5.0 / 31.0);
		expectedConvertedSpectrum.put(125.000, 5.0 / 31.0);
		LOGGER.info(expectedConvertedSpectrum);
		LOGGER.info(convertedSpectrum);
		assertEquals(expectedConvertedSpectrum, convertedSpectrum);
		assertEquals(expectedConvertedSpectrum.getIntensityType(), convertedSpectrum.getIntensityType());
	}
	
	public void testSkipLowMIDs() {
		MassSpectrum map = new MassSpectrum(IntensityType.MID);
		map.put(123.000, 0.001);
		map.put(123.010, 0.009);
		map.put(124.000, 0.09);
		map.put(125.000, 0.9);
		MassSpectrum expectedSpectrum = new MassSpectrum(IntensityType.MID);
		expectedSpectrum.put(124.000, 0.09);
		expectedSpectrum.put(125.000, 0.9);
		MassSpectrum newSpectrum = map.skipLowIntensity(0.01);
		LOGGER.info(expectedSpectrum);
		LOGGER.info(newSpectrum);
		assertEquals(expectedSpectrum, newSpectrum);
	}
	
	public void testSkipHighMassses() {
		MassSpectrum map = new MassSpectrum(IntensityType.ABSOLUTE);
		map.put(123.000, 1.0);
		map.put(123.010, 9.0);
		map.put(124.000, 90.0);
		map.put(124.010, 90.0);
		map.put(125.000, 900.0);
		MassSpectrum newSpectrum = map.skipHighMasses(124.010);
		MassSpectrum expectedSpectrum = new MassSpectrum(IntensityType.ABSOLUTE);
		expectedSpectrum.put(123.000, 1.0);
		expectedSpectrum.put(123.010, 9.0);
		expectedSpectrum.put(124.000, 90.0);
		expectedSpectrum.put(124.010, 90.0);
		LOGGER.info(expectedSpectrum);
		LOGGER.info(newSpectrum);
		assertEquals(expectedSpectrum, newSpectrum);
	}
	
	public void testRoundMassses() {
		MassSpectrum map = new MassSpectrum(IntensityType.ABSOLUTE);
		map.put(123.00019, 1.0);
		map.put(123.01015, 9.0);
		map.put(124.00014, 90.0);
		map.put(124.01010, 90.0);
		map.put(125.00000, 900.0);
		map.put(123.00018, 1.0);
		map.put(123.01016, 9.0);
		map.put(124.00013, 90.0);
		map.put(124.01011, 90.0);
		map.put(125.00001, 900.0);
		MassSpectrum newSpectrum = map.roundMasses(4);
		MassSpectrum expectedSpectrum = new MassSpectrum(IntensityType.ABSOLUTE);
		expectedSpectrum.put(123.0002, 2.0);
		expectedSpectrum.put(123.0102, 18.0);
		expectedSpectrum.put(124.0001, 180.0);
		expectedSpectrum.put(124.0101, 180.0);
		expectedSpectrum.put(125.0000, 1800.0);
		LOGGER.info(expectedSpectrum);
		LOGGER.info(newSpectrum);
		assertEquals(expectedSpectrum, newSpectrum);
	}
	
	public void roundFrequenciesTest() {
		MassSpectrum map = new MassSpectrum(IntensityType.MID);
		map.put(123.00019, 0.01234567);
		map.put(123.01015, 0.62837565);
		map.put(124.00014, 0.00013245);
		map.put(124.01010, 0.00045368);
		map.put(125.00000, 0.01293847);
		map.put(123.00018, 0.17283745);
		map.put(123.01016, 0.14253647);
		MassSpectrum newSpectrum = map.roundIntensities(4);
		MassSpectrum expectedSpectrum = new MassSpectrum(IntensityType.MID);
		expectedSpectrum.put(123.00019, 0.0123);
		expectedSpectrum.put(123.01015, 0.6284);
		expectedSpectrum.put(124.00014, 0.0001);
		expectedSpectrum.put(124.01010, 0.0005);
		expectedSpectrum.put(125.00000, 0.0129);
		expectedSpectrum.put(123.00018, 0.1728);
		expectedSpectrum.put(123.01016, 0.1425);
		LOGGER.info(expectedSpectrum);
		LOGGER.info(newSpectrum);
		assertEquals(expectedSpectrum, newSpectrum);
	}
	
	public void testSortDescendingByMass() {
		MassSpectrum map = new MassSpectrum(IntensityType.MID);
		map.put(123.01015, 0.6284);
		map.put(123.00019, 0.0123);
		map.put(125.00000, 0.0129);
		map.put(124.00014, 0.0001);
		map.put(124.01010, 0.0005);
		map.put(123.00018, 0.1728);
		map.put(123.01016, 0.1425);
		MassSpectrum newSpectrum = map.sortAscendingByMass();
		MassSpectrum expectedSpectrum = new MassSpectrum(IntensityType.MID);
		expectedSpectrum.put(123.00018, 0.1728);
		expectedSpectrum.put(123.00019, 0.0123);
		expectedSpectrum.put(123.01015, 0.6284);
		expectedSpectrum.put(123.01016, 0.1425);
		expectedSpectrum.put(124.00014, 0.0001);
		expectedSpectrum.put(124.01010, 0.0005);
		expectedSpectrum.put(125.00000, 0.0129);
		LOGGER.info(expectedSpectrum);
		LOGGER.info(newSpectrum);
		assertEquals(expectedSpectrum, newSpectrum);
	}
	
	public void testSortAscendingByMass() {
		MassSpectrum map = new MassSpectrum(IntensityType.MID);
		map.put(123.01015, 0.6284);
		map.put(123.00019, 0.0123);
		map.put(125.00000, 0.0129);
		map.put(124.00014, 0.0001);
		map.put(124.01010, 0.0005);
		map.put(123.00018, 0.1728);
		map.put(123.01016, 0.1425);
		MassSpectrum newSpectrum = map.sortAscendingByMass();
		MassSpectrum expectedSpectrum = new MassSpectrum(IntensityType.MID);
		expectedSpectrum.put(125.00000, 0.0129);
		expectedSpectrum.put(124.01010, 0.0005);
		expectedSpectrum.put(124.00014, 0.0001);
		expectedSpectrum.put(123.01016, 0.1425);
		expectedSpectrum.put(123.01015, 0.6284);
		expectedSpectrum.put(123.00019, 0.0123);
		expectedSpectrum.put(123.00018, 0.1728);
		LOGGER.info(expectedSpectrum);
		LOGGER.info(newSpectrum);
		assertEquals(expectedSpectrum, newSpectrum);
	}
	
	public void testSortAscendingByFrequency() {
		MassSpectrum map = new MassSpectrum(IntensityType.MID);
		map.put(123.01015, 0.6284);
		map.put(123.00019, 0.0123);
		map.put(125.00000, 0.0129);
		map.put(124.00014, 0.0001);
		map.put(124.01010, 0.0005);
		map.put(123.00018, 0.1728);
		map.put(123.01016, 0.1425);
		MassSpectrum newSpectrum = map.sortAscendingByIntensity();
		MassSpectrum expectedSpectrum = new MassSpectrum(IntensityType.MID);
		expectedSpectrum.put(123.01015, 0.6284);
		expectedSpectrum.put(123.00018, 0.1728);
		expectedSpectrum.put(123.01016, 0.1425);
		expectedSpectrum.put(125.00000, 0.0129);
		expectedSpectrum.put(123.00019, 0.0123);
		expectedSpectrum.put(124.01010, 0.0005);
		expectedSpectrum.put(124.00014, 0.0001);
		LOGGER.info(expectedSpectrum);
		LOGGER.info(newSpectrum);
		assertEquals(expectedSpectrum, newSpectrum);
	}
	
	public void testSortDescendingByFrequency() {
		MassSpectrum map = new MassSpectrum(IntensityType.MID);
		map.put(123.01015, 0.6284);
		map.put(123.00019, 0.0123);
		map.put(125.00000, 0.0129);
		map.put(124.00014, 0.0001);
		map.put(124.01010, 0.0005);
		map.put(123.00018, 0.1728);
		map.put(123.01016, 0.1425);
		MassSpectrum newSpectrum = map.sortDescendingByIntensity();
		MassSpectrum expectedSpectrum = new MassSpectrum(IntensityType.MID);
		expectedSpectrum.put(124.00014, 0.0001);
		expectedSpectrum.put(124.01010, 0.0005);
		expectedSpectrum.put(123.00019, 0.0123);
		expectedSpectrum.put(125.00000, 0.0129);
		expectedSpectrum.put(123.01016, 0.1425);
		expectedSpectrum.put(123.00018, 0.1728);
		expectedSpectrum.put(123.01015, 0.6284);
		LOGGER.info(expectedSpectrum);
		LOGGER.info(newSpectrum);
		assertEquals(expectedSpectrum, newSpectrum);
	}
	
	public void testAnalyseMassShifts() {
		MassSpectrum spectrum = new MassSpectrum(IntensityType.ABSOLUTE);
		spectrum.put(130.0324, 12.0);
		spectrum.put(131.032, 12.0);
		spectrum.put(132.0324, 12.0);
		ArrayList<MassShiftList> massShiftLists = new ArrayList<>();
		massShiftLists.add(new MassShiftList(new MassShift(0, 0, 0.0)));
		massShiftLists.add(new MassShiftList(new MassShift(0, 1, 0.9996)));
		massShiftLists.add(new MassShiftList(new MassShift(0, 2, 2.0)));
		MassShiftDataSet expectedMassShiftData = new MassShiftDataSet();
		ArrayList<IsotopeListList> isotopeLists = new ArrayList<>();
		isotopeLists.add(new IsotopeListList(new IsotopeList(Isotope.NONE)));
		isotopeLists.add(new IsotopeListList(new IsotopeList(Isotope.Si_29)));
		isotopeLists.add(new IsotopeListList(new IsotopeList(Isotope.Si_30)));
		for (MassShiftList massShiftList : massShiftLists) {
			int index = massShiftLists.indexOf(massShiftList);
			expectedMassShiftData.put(massShiftList, isotopeLists.get(index));
		}
		MassShiftDataSet actualMassShifts = spectrum.analyseMassShifts(new ElementList(Element.Si));
		assertSameData(expectedMassShiftData, actualMassShifts);
	}
	
	public void testAnalyseMassShifts2() {
		MassSpectrum spectrum = new MassSpectrum(IntensityType.ABSOLUTE);
		spectrum.put(133.0362, 12.0);
		spectrum.put(134.0395, 12.0);
		spectrum.put(135.0391, 12.0);
		spectrum.put(135.0429, 12.0);
		spectrum.put(136.0395, 12.0);
		spectrum.put(136.0425, 12.0);
		ArrayList<MassShiftList> massShiftLists = new ArrayList<>();
		massShiftLists.add(new MassShiftList(new MassShift(0, 0, 0.0)));
		massShiftLists.add(new MassShiftList(new MassShift(0, 1, 1.0033)));
		massShiftLists.add(new MassShiftList(new MassShift(0, 1, 1.0033), new MassShift(1, 2, 0.9996)));
		massShiftLists.add(new MassShiftList(new MassShift(0, 1, 1.0033), new MassShift(1, 3, 1.0034)));
		massShiftLists.add(new MassShiftList(new MassShift(0, 1, 1.0033), new MassShift(1, 4, 2.0)));
		massShiftLists.add(new MassShiftList(new MassShift(0, 1, 1.0033), new MassShift(1, 3, 1.0034), new MassShift(3, 5, 0.9996)));
		ArrayList<IsotopeListList> isotopeLists = new ArrayList<>();
		isotopeLists.add(new IsotopeListList(new IsotopeList(Isotope.NONE)));
		isotopeLists.add(new IsotopeListList(new IsotopeList(Isotope.C_13)));
		isotopeLists.add(new IsotopeListList(new IsotopeList(Isotope.C_13), new IsotopeList(Isotope.Si_29)));
		isotopeLists.add(new IsotopeListList(new IsotopeList(Isotope.C_13), new IsotopeList(Isotope.C_13)));
		isotopeLists.add(new IsotopeListList(new IsotopeList(Isotope.C_13), new IsotopeList(Isotope.Si_30)));
		isotopeLists.add(new IsotopeListList(new IsotopeList(Isotope.C_13), new IsotopeList(Isotope.C_13), new IsotopeList(Isotope.Si_29)));
		MassShiftDataSet expectedMassShiftData = new MassShiftDataSet();
		for (MassShiftList massShiftList : massShiftLists) {
			int index = massShiftLists.indexOf(massShiftList);
			expectedMassShiftData.put(massShiftList, isotopeLists.get(index));
		}
		MassShiftDataSet actualMassShifts = spectrum.analyseMassShifts(new ElementList(Element.Si, Element.C));
		LOGGER.info(expectedMassShiftData);
		LOGGER.info(actualMassShifts);
		assertSameData(expectedMassShiftData, actualMassShifts);
	}

	private void assertSameData(MassShiftDataSet expectedMassShiftData, MassShiftDataSet actualMassShiftsData) {
		assertEquals(expectedMassShiftData.size(), actualMassShiftsData.size());
		List<Entry<MassShiftList, IsotopeListList>> expectedEntryList = new ArrayList<>(expectedMassShiftData.entrySet());
		List<Entry<MassShiftList, IsotopeListList>> actualEntryList = new ArrayList<>(actualMassShiftsData.entrySet());
		for (int index = 0; index < expectedEntryList.size(); index++) {
			MassShiftList expectedMassShiftList = expectedEntryList.get(index).getKey();
			IsotopeListList expectedIsotopeList = expectedEntryList.get(index).getValue();
			MassShiftList actualMassShiftList = actualEntryList.get(index).getKey();
			IsotopeListList actualIsotopeList = actualEntryList.get(index).getValue();
			assertTrue(expectedMassShiftList.equals(actualMassShiftList));
			assertTrue(expectedIsotopeList.equals(actualIsotopeList));
		}
		
	}
	
	public void testFromRawFileExportCsv() {
		MassSpectrum spectrum = MassSpectrum.fromRawFileExportCsv(PathConstants.TEST_RESOURCES.toAbsolutePath("fromRawFileExport.csv"));
		LOGGER.info(spectrum.toString());
	}

	
}
