package de.kempalab.msdps.demo;

import de.kempalab.msdps.correction.NACorrector;
import de.kempalab.msdps.fileconversion.NACorrectorFileConverter;

public class NACorrectionDemo2 {
	public static void main(String[] args) {
		String pre = "D:\\data\\raw\\asn\\120k\\analysis\\";
		String[] mzMineOutpuFilePaths = {pre + "mixAexport.csv", pre + "mixBexport.csv", pre + "mixCexport.csv", pre + "mixDexport.csv"};
		String naCorrectionInputFilePath = pre + "Converted.csv";
		String naCorrectionOutputFilePath = pre + "Corrected.csv";
		NACorrectorFileConverter.convert(mzMineOutpuFilePaths, naCorrectionInputFilePath);
		NACorrector.correct(naCorrectionInputFilePath, naCorrectionOutputFilePath);
		
		String[] mzMineOutpuFilePaths2 = {pre + "15Nexport.csv", pre + "15N2_13C4export.csv", pre + "unlabeledexport.csv"};
		String naCorrectionInputFilePath2 = pre + "ConvertedStd.csv";
		String naCorrectionOutputFilePath2 = pre + "CorrectedStd.csv";
		NACorrectorFileConverter.convert(mzMineOutpuFilePaths2, naCorrectionInputFilePath2);
		NACorrector.correct(naCorrectionInputFilePath2, naCorrectionOutputFilePath2);
	}
}
