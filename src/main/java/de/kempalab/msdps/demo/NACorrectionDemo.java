package de.kempalab.msdps.demo;

import de.kempalab.msdps.correction.NACorrector;
import de.kempalab.msdps.fileconversion.NACorrectorFileConverter;

public class NACorrectionDemo {
	
	public static void main(String[] args) {
		String mzMineOutpuFilePath = "D:\\data\\raw\\asn\\analysis\\mixAexport.csv";
		String naCorrectionInputFilePath = "D:\\\\data\\\\raw\\\\asn\\\\analysis\\\\mixAexportConverted.csv";
		String naCorrectionOutputFilePath = "D:\\\\data\\\\raw\\\\asn\\\\analysis\\\\mixAexportCorrected.csv";
		NACorrectorFileConverter.convert(mzMineOutpuFilePath, naCorrectionInputFilePath);
		NACorrector.correct(naCorrectionInputFilePath, naCorrectionOutputFilePath);
	}

}
