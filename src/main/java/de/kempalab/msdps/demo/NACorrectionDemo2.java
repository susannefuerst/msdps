package de.kempalab.msdps.demo;

import de.kempalab.msdps.correction.NACorrector;
import de.kempalab.msdps.fileconversion.NACorrectorFileConverter;

public class NACorrectionDemo2 {
	public static void main(String[] args) {
//		String mzMineOutpuFilePath1 = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\12c.csv";
//		String mzMineOutpuFilePath2 = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\13c.csv";
//		String mzMineOutpuFilePath3 = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\15n.csv";
//		String mzMineOutpuFilePath4 = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\13c15n.csv";
//
//		String mzMineOutpuFilePatha = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\a.csv";
//		String mzMineOutpuFilePathb = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\b.csv";
//		String mzMineOutpuFilePathc = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\c.csv";
//		String mzMineOutpuFilePathd = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\d.csv";
//		String mzMineOutpuFilePathe = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\e.csv";
//		String mzMineOutpuFilePathf = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\f.csv";

		String mzMineOutpuFilePath1 = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\Gln12Cpeaks.csv";
		String mzMineOutpuFilePath2 = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\Gln13Cpeaks.csv";
		String mzMineOutpuFilePath3 = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\Gln15Npeaks.csv";
		String mzMineOutpuFilePath4 = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\Gln13C15Npeaks.csv";

		String mzMineOutpuFilePatha = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixApeaks.csv";
		String mzMineOutpuFilePathb = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixBpeaks.csv";
		String mzMineOutpuFilePathc = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixCpeaks.csv";
		String mzMineOutpuFilePathd = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixDpeaks.csv";
		String mzMineOutpuFilePathe = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixEpeaks.csv";
		String mzMineOutpuFilePathf = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\MixFpeaks.csv";

		String[] mzMineOutpuFilePaths = { mzMineOutpuFilePath1, mzMineOutpuFilePath2, mzMineOutpuFilePath3,
				mzMineOutpuFilePath4, mzMineOutpuFilePatha, mzMineOutpuFilePathb, mzMineOutpuFilePathc,
				mzMineOutpuFilePathd, mzMineOutpuFilePathe, mzMineOutpuFilePathf };
//		String naCorrectionInputFilePath = "K:\\\\MZmine-2.37\\\\MZmine-2.37\\\\gln_peaks_new\\GlnCorrectionInputNew.csv";
//		String naCorrectionOutputFilePath = "K:\\\\MZmine-2.37\\\\MZmine-2.37\\\\gln_peaks_new\\GlnCorrectionOutputNew.csv";

		String naCorrectionInputFilePath = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\GlnCorrectionInput2.csv";
		String naCorrectionOutputFilePath = "Z:\\MZmine-2.37\\projects\\gln\\gln\\peaks\\GlnCorrectionOutput2.csv";
		NACorrectorFileConverter.convert(mzMineOutpuFilePaths, naCorrectionInputFilePath);
		NACorrector.correct(naCorrectionInputFilePath, naCorrectionOutputFilePath);
	}
}
