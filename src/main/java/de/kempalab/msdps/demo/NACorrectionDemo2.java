package de.kempalab.msdps.demo;

import de.kempalab.msdps.correction.NACorrector;
import de.kempalab.msdps.fileconversion.NACorrectorFileConverter;

public class NACorrectionDemo2 {
	public static void main(String[] args) {
		String mzMineOutpuFilePath1 = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\Gln12CPeaks.csv";
		String mzMineOutpuFilePath2 = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\Gln13CPeaks.csv";
		String mzMineOutpuFilePath3 = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\Gln15NPeaks.csv";
		String mzMineOutpuFilePath4 = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\Gln13C15NPeaks.csv";

		String mzMineOutpuFilePatha = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixAPeaks.csv";
		String mzMineOutpuFilePathb = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixBPeaks.csv";
		String mzMineOutpuFilePathc = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixCPeaks.csv";
		String mzMineOutpuFilePathd = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixDPeaks.csv";
		String mzMineOutpuFilePathe = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixEPeaks.csv";
		String mzMineOutpuFilePathf = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixFPeaks.csv";

		String[] mzMineOutpuFilePaths = { mzMineOutpuFilePath1, mzMineOutpuFilePath2, mzMineOutpuFilePath3,
				mzMineOutpuFilePath4, mzMineOutpuFilePatha, mzMineOutpuFilePathb, mzMineOutpuFilePathc,
				mzMineOutpuFilePathd, mzMineOutpuFilePathe, mzMineOutpuFilePathf };
		String naCorrectionInputFilePath = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\GlnCorrectionInput.csv";
		String naCorrectionOutputFilePath = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\GlnCorrectionOutput.csv";
		NACorrectorFileConverter.convert(mzMineOutpuFilePaths, naCorrectionInputFilePath);
		NACorrector.correct(naCorrectionInputFilePath, naCorrectionOutputFilePath);
	}
}
