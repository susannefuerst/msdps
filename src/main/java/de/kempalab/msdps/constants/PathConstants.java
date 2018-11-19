package de.kempalab.msdps.constants;

import java.io.File;
/**
 * Important folders to read and write for the isotopeincorporation project
 * @author sfuerst
 *
 */
public enum PathConstants {
	FRAGMENTS_DATABASE_FILE("\\src\\main\\resources\\fragmentsDatabase.csv"),
	FILE_OUTPUT_FOLDER("\\out\\"),
	TMP_FOLDER("\\tmp\\"),
	TEST_RESOURCES("\\src\\test\\resources\\");
	
	private String relativePath;

	PathConstants(String realativePath) {
		this.relativePath = realativePath;
	}
	
	public String getRelativePath() {
		return relativePath;
	}
	
	/**
	 * 
	 * @return the relative path of this PathConstant converted to the absolute path.
	 */
	public String toAbsolutePath() {
		return getProjectPath() + relativePath;
	}
	
	/**
	 * 
	 * @return the absolute path to the isotopeincorporation project
	 */
	public static String getProjectPath() {
		return new File("").getAbsolutePath();
	}
	
	/**
	 * 
	 * @param filename
	 * @return the filename concatenated with the absolute path of this PathConstant
	 */
	public String toAbsolutePath(String filename) {
		return toAbsolutePath() + filename;
	}

}
