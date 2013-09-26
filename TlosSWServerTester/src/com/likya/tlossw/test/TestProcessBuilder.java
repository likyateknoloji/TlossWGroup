package com.likya.tlossw.test;

import java.io.File;
import java.io.IOException;

import com.likya.tlossw.utils.ValidPlatforms;

public class TestProcessBuilder {

	public static void main(String[] args) {
		
		ProcessBuilder processBuilder = null;
		
		String[] cmd = ValidPlatforms.getCommand("/Users/serkan/Desktop/tlosTest/" + "job1.sh");
		processBuilder = new ProcessBuilder(cmd);
		
		processBuilder.directory(new File("/Applications"));
		
		Process process = null;
		try {
			process = processBuilder.start();
			
			process.waitFor();

			int processExitValue = process.exitValue();
			
			System.out.println(processExitValue);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
