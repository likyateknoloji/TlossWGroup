package com.likya.tlossw.core.spc.helpers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.likya.tlos.model.xmlbeans.data.DirectionType;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.LogAnalysisDocument.LogAnalysis;
import com.likya.tlos.model.xmlbeans.data.ModeType;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.utils.LiveStateInfoUtils;

public class LogAnalyser {

	public void evaluate(Job job) {

		
		JobProperties jobProperties = job.getJobRuntimeProperties().getJobProperties();
		
		LogAnalysis logAnalysis = jobProperties.getLogAnalysis();

		if (logAnalysis != null && logAnalysis.getActive()) {

			boolean result = false;

			try {
				// Evaluate log analyzing procedures.

				String filePath = jobProperties.getBaseJobInfos().getJobLogPath();
				String fileName = jobProperties.getBaseJobInfos().getJobLogFile();

				File sourceFiile = new File(filePath + File.separator + fileName);

				int direction = logAnalysis.getFindWhat().getDirection().intValue();

				boolean matcWholeWordOnly = logAnalysis.getFindWhat().getMatchWholeWordOnly();
				boolean isCaseSensitive = logAnalysis.getFindWhat().getMatchCase();

				String searchString = logAnalysis.getFindWhat().getStringValue();

				int modeType = logAnalysis.getFindWhat().getMode().intValue();

				if (matcWholeWordOnly) {
					result = matcWholeWordOnly(sourceFiile, searchString, isCaseSensitive, direction, modeType);
				} else {
					result = matcWord(sourceFiile, searchString, isCaseSensitive, direction, modeType);
				}

			} catch (UnsupportedOperationException uoe) {
				uoe.printStackTrace();
			}

			if (result && logAnalysis.getAction().getThen() != null) {
				
				job.setChanged();
				job.notifyObservers();
				
				LiveStateInfo liveStateInfo = logAnalysis.getAction().getThen().getForcedResult().getLiveStateInfo();
				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, liveStateInfo);

			} else if (!result && logAnalysis.getAction().getElse() != null) {
				
				job.setChanged();
				job.notifyObservers(this);
				
				LiveStateInfo liveStateInfo = logAnalysis.getAction().getElse().getForcedResult().getLiveStateInfo();
				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, liveStateInfo);
			}

		}

	}

	private static boolean matcWord(File sourceFile, String searchString, boolean isCaseSensitive, int direction, int modeType) {

		boolean retValue = false;

		switch (direction) {

		case DirectionType.INT_DOWN:
			retValue = find(sourceFile, searchString, isCaseSensitive, modeType);
			break;

		case DirectionType.INT_UP:
			retValue = reverseFind(sourceFile, " " + searchString + " ", isCaseSensitive, modeType);
			break;
			
		default:
			throw new UnsupportedOperationException();
		}

		return retValue;

	}

	private static boolean matcWholeWordOnly(File sourceFile, String searchString, boolean isCaseSensitive, int direction, int modeType) {

		boolean retValue = false;

		switch (direction) {

		case DirectionType.INT_DOWN:
			retValue = find(sourceFile, " " + searchString + " ", isCaseSensitive, modeType);
			break;

		case DirectionType.INT_UP:
			retValue = reverseFind(sourceFile, " " + searchString + " ", isCaseSensitive, modeType);
			break;
			
		default:
			throw new UnsupportedOperationException();
		}

		return retValue;
	}

	public static boolean find(File f, String searchString, boolean isCaseSensitive, int modeType) {

		boolean result = false;

		Scanner in = null;

		try {
			
			in = new Scanner(new FileReader(f));
			
			while (in.hasNextLine() && !result) {

				switch (modeType) {

				case ModeType.INT_NORMAL:
					result = searchNormal(in.nextLine(), searchString, isCaseSensitive);
					break;
				case ModeType.INT_REG_EX:
					result = searchRegEx(in.nextLine(), searchString, isCaseSensitive);
					break;
				default:
					throw new UnsupportedOperationException();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) { /* ignore */
			}
		}

		return result;

	}

	public static boolean reverseFind(File f, String searchString, boolean isCaseSensitive, int modeType) {

		boolean result = false;

		Scanner in = null;

		try {
			
			ReverseLineInputStream reverseLineInputStream = new ReverseLineInputStream(f);
			
			in = new Scanner(reverseLineInputStream);
			
			while (in.hasNextLine() && !result) {
				
				switch (modeType) {

				case ModeType.INT_NORMAL:
					result = searchNormal(in.nextLine(), searchString, isCaseSensitive);
					break;
				case ModeType.INT_REG_EX:
					result = searchRegEx(in.nextLine(), searchString, isCaseSensitive);
					break;
				default:
					throw new UnsupportedOperationException();
				}				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) { /* ignore */
			}
		}

		return result;

	}
	
	private static boolean searchNormal(String source, String key, boolean isCaseSensitive) {
		
		boolean result = false;
		
		if (isCaseSensitive) {
			result = source.indexOf(key) >= 0;
		} else {
			result = source.indexOf(key.toUpperCase()) >= 0;
		}
		
		return result;
	}

	private static boolean searchRegEx(String source, String key, boolean isCaseSensitive) {
		
		boolean result = false;
		Pattern pattern = null;
		
		if(isCaseSensitive) {
			pattern = Pattern.compile(key);
		} else {
			pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
		}
		
		Matcher matcher = pattern.matcher(source);
		
	
		result = matcher.find();
				
		return result;
	}
}
