package de.kempalab.msdps.visualisation;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.MassSpectrum;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.constants.SpectrumType;

@SuppressWarnings("serial")
public class MSLineChartApplicationWindow extends ApplicationFrame {
	
	public MSLineChartApplicationWindow(String title, MassSpectrum massSpectrum) {
		super(title);
		JFreeChart chart = MSLineChartCreator.createMsLineChart(massSpectrum);
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(1700, 850));
		setContentPane(chartPanel);
	}
	
	public static void main(String[] args) throws IOException {
		File file = new File(PathConstants.TEST_RESOURCES.toAbsolutePath("MSShiftDatabaseTest01.csv"));
		MSShiftDatabase msDatabase = new MSShiftDatabase(file.getAbsolutePath(), SpectrumType.CENTROIDED);
		MSLineChartApplicationWindow demo = new MSLineChartApplicationWindow("Bar Demo 1", msDatabase.getNaturalSpectrum());
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
}
