package de.kempalab.msdps.demo;

import de.kempalab.msdps.correction.NACorrector;
import de.kempalab.msdps.fileconversion.NACorrectorFileConverter;

public class NACorrectionDemo2 {
	public static void main(String[] args) {
		String mzMineOutpuFilePath1 = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaks.csv";
		String mzMineOutpuFilePath2 = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaks2.csv";
		String mzMineOutpuFilePath3 = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaks3.csv";
		String[] mzMineOutpuFilePaths = {mzMineOutpuFilePath1, mzMineOutpuFilePath2, mzMineOutpuFilePath3};
		String naCorrectionInputFilePath = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaksNAinput.csv";
		String naCorrectionOutputFilePath = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaksNAcorrected.csv";
		NACorrectorFileConverter.convert(mzMineOutpuFilePaths, naCorrectionInputFilePath);
		NACorrector.correct(naCorrectionInputFilePath, naCorrectionOutputFilePath);
	}
}
