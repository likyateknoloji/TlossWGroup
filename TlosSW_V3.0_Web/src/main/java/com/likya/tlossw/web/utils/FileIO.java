package com.likya.tlossw.web.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import com.likya.tlossw.utils.FileUtils;

public class FileIO {

	public static String readFile(File fileName) {
	
		FileReader csvFileReader = null;
		
		try {

			if ((fileName == null) || !FileUtils.checkFile(fileName.getName())) {
				throw new FileNotFoundException();
			}

			File csvFile = new File("Test.csv");
            
			csvFileReader = new FileReader(csvFile);

		} catch (FileNotFoundException e) {
			System.err.println("csv file not found ! fileName => " + fileName);
			System.exit(-1);
		} catch (RuntimeException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		} 
		try {
			csvFileReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String csvFileString = csvFileReader.toString();
		return csvFileString;
	}
//	private static final File LOCATION = new File("/path/to/all/uploads");
//	public void upload() throws IOException {
//	    if (file != null) {
//	        String prefix = FilenameUtils.getBaseName(file.getName()); 
//	        String suffix = FilenameUtils.getExtension(file.getName());
//	        File save = File.createTempFile(prefix + "-", "." + suffix, LOCATION);
//	        Files.write(save.toPath(), file.getContents());
//	        // Add success message here.
//	    }
//	}
	
	
}
