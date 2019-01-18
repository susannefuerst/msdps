package de.kempalab.msdps.demo;

import de.kempalab.msdps.correction.NACorrector;
import de.kempalab.msdps.fileconversion.NACorrectorFileConverter;

public class NACorrectionDemo {
	
	public static void main(String[] args) {
		String mzMineOutpuFilePath = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaks.csv";
		String naCorrectionInputFilePath = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaksNAinput.csv";
		String naCorrectionOutputFilePath = "D:\\_Susi\\_mdc\\data\\mzmine_export\\gln\\13C15NglnPeaksNAcorrected.csv";
		NACorrectorFileConverter.convert(mzMineOutpuFilePath, naCorrectionInputFilePath);
		NACorrector.correct(naCorrectionInputFilePath, naCorrectionOutputFilePath);
	}

}
