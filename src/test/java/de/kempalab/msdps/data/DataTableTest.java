package de.kempalab.msdps.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import junit.framework.TestCase;
import de.kempalab.msdps.constants.PathConstants;
import de.kempalab.msdps.data.DataTable;

public class DataTableTest extends TestCase {
	
	public void testWriteToCsv() throws IOException {
		ArrayList<String> headers = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			String header = "Test" + i;
			headers.add(header);
		}
		DataTable dataTable = new DataTable(headers);
		ArrayList<String> colWithTwoRows = new ArrayList<>();
		colWithTwoRows.add("2 rows");
		colWithTwoRows.add("2 rows");
		dataTable.addColumn(colWithTwoRows);
		LinkedHashMap<String, Double> colsWith7Rows = new LinkedHashMap<>();
		for (int i = 0; i < 7; i++) {
			String key = "Row " + i;
			colsWith7Rows.put(key, 1.2);
		}
		dataTable.addColumn(colsWith7Rows);
		ArrayList<String> insertedCol = new ArrayList<>();
		insertedCol.add("This was inserted at index 0");
		dataTable.addColumn(0, insertedCol);
		dataTable.addConstantValueColumn("This was created from a const");
		
		File tmpFolder = new File(PathConstants.TMP_FOLDER.toAbsolutePath());
		if (!tmpFolder.exists()) {
			tmpFolder.mkdir();
		}
		dataTable.writeToCsv("NA", true, PathConstants.TMP_FOLDER.toAbsolutePath("testfile.csv"));
		File[] created = new File(PathConstants.TMP_FOLDER.toAbsolutePath()).listFiles();
		for (File file : created) {
			file.delete();
		}
	}
}
