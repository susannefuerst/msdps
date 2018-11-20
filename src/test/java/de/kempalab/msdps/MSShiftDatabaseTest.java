package de.kempalab.msdps;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import de.kempalab.msdps.MSShiftDatabase;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.log.MyLogger;

public class MSShiftDatabaseTest extends TestCase {
	
	private File[] testFiles = new File(PathConstants.TEST_RESOURCES.toAbsolutePath()).listFiles();
	private static final MyLogger LOGGER = MyLogger.getLogger(MSShiftDatabaseTest.class);
	
	public void testReadWriteCsv() throws IOException {
		String filePath = "";
		for (File file : testFiles) {
			if (file.getName().contains("MSShiftDatabaseTest")) {
				filePath = file.getAbsolutePath();
				LOGGER.infoValue("Test file", file.getName());
				MSShiftDatabase msDatabase = new MSShiftDatabase(filePath);
				LOGGER.infoValue("msDatabase\n", msDatabase);
				msDatabase.writeCsv(PathConstants.TMP_FOLDER.toAbsolutePath());
			}
		}
		File[] created = new File(PathConstants.TMP_FOLDER.toAbsolutePath()).listFiles();
		for (File file : created) {
			file.delete();
		}
	}
	
}
