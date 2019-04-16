package de.kempalab.msdps.demo;

import de.kempalab.msdps.correction.NACorrector;
import de.kempalab.msdps.fileconversion.NACorrectorFileConverter;

public class NACorrectionDemo2 {
	public static void main(String[] args) {
		String pre = "D:\\data\\raw\\asn\\analysis\\";
		String[] mzMineOutpuFilePaths = {pre + "mixAexport.csv", pre + "mixBexport.csv", pre + "mixCexport.csv", pre + "mixDexport.csv"};
		String naCorrectionInputFilePath = pre + "Converted.csv";
		String naCorrectionOutputFilePath = pre + "Corrected.csv";
		NACorrectorFileConverter.convert(mzMineOutpuFilePaths, naCorrectionInputFilePath);
		NACorrector.correct(naCorrectionInputFilePath, naCorrectionOutputFilePath);
	}
}
