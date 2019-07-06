package de.kempalab.msdps.demo;

import java.util.ArrayList;

import de.kempalab.msdps.correction.NACorrector;
import de.kempalab.msdps.fileconversion.NACorrectorFileConverter;

public class NACorrectionDemo2 {
	public static void main(String[] args) {
//		String pre = "D:\\data\\raw\\asn\\120k\\analysis\\28052019\\";
		String pre = "C:\\Users\\sufuers\\data\\raw\\gln\\12062019\\60k\\analysis\\14062019\\";
//		String[] mzMineOutpuFilePaths = {pre + "2.csv", pre + "3.csv", pre + "a.csv", pre + "b.csv", pre + "d.csv"};
		ArrayList<String> mzMineOutpuFilePaths = new ArrayList<>();
		for (int i=1; i <= 5; i++) {
			mzMineOutpuFilePaths.add(pre + i + ".csv");
		}
		String naCorrectionInputFilePath = pre + "gln_60k_uncorrected.csv";
		String naCorrectionOutputFilePath = pre + "gln_60k_corrected.csv";
		NACorrectorFileConverter.convert(mzMineOutpuFilePaths, naCorrectionInputFilePath);
		NACorrector.correct(naCorrectionInputFilePath, naCorrectionOutputFilePath);
		
//		String[] mzMineOutpuFilePaths2 = {pre + "15Nexport.csv", pre + "15N2_13C4export.csv", pre + "unlabeledexport.csv"};
//		String naCorrectionInputFilePath2 = pre + "ConvertedStd.csv";
//		String naCorrectionOutputFilePath2 = pre + "CorrectedStd.csv";
//		NACorrectorFileConverter.convert(mzMineOutpuFilePaths2, naCorrectionInputFilePath2);
//		NACorrector.correct(naCorrectionInputFilePath2, naCorrectionOutputFilePath2);
	}
}
