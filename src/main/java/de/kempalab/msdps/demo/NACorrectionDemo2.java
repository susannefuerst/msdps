package de.kempalab.msdps.demo;

import de.kempalab.msdps.correction.NACorrector;
import de.kempalab.msdps.fileconversion.NACorrectorFileConverter;

public class NACorrectionDemo2 {
	public static void main(String[] args) {
		String mzMineOutpuFilePath1 = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\12c.csv";
		String mzMineOutpuFilePath2 = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\13c.csv";
		String mzMineOutpuFilePath3 = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\15n.csv";
		String mzMineOutpuFilePath4 = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\13c15n.csv";

		String mzMineOutpuFilePatha = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\a.csv";
		String mzMineOutpuFilePathb = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\b.csv";
		String mzMineOutpuFilePathc = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\c.csv";
		String mzMineOutpuFilePathd = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\d.csv";
		String mzMineOutpuFilePathe = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\e.csv";
		String mzMineOutpuFilePathf = "K:\\MZmine-2.37\\MZmine-2.37\\gln_peaks_new\\f.csv";

		String[] mzMineOutpuFilePaths = { mzMineOutpuFilePath1, mzMineOutpuFilePath2, mzMineOutpuFilePath3,
				mzMineOutpuFilePath4, mzMineOutpuFilePatha, mzMineOutpuFilePathb, mzMineOutpuFilePathc,
				mzMineOutpuFilePathd, mzMineOutpuFilePathe, mzMineOutpuFilePathf };
		String naCorrectionInputFilePath = "K:\\\\MZmine-2.37\\\\MZmine-2.37\\\\gln_peaks_new\\GlnCorrectionInputNew.csv";
		String naCorrectionOutputFilePath = "K:\\\\MZmine-2.37\\\\MZmine-2.37\\\\gln_peaks_new\\GlnCorrectionOutputNew.csv";
		NACorrectorFileConverter.convert(mzMineOutpuFilePaths, naCorrectionInputFilePath);
		NACorrector.correct(naCorrectionInputFilePath, naCorrectionOutputFilePath);
	}
}
