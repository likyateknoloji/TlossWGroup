package com.likya.tlossw.core.spc.helpers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.likya.tlos.model.xmlbeans.data.ActionDocument.Action;
import com.likya.tlos.model.xmlbeans.data.DirectionType;
import com.likya.tlos.model.xmlbeans.data.EventDocument.Event;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.LogAnalysisDocument.LogAnalysis;
import com.likya.tlos.model.xmlbeans.data.ModeType;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlossw.core.spc.jobs.Job;

public class LogAnalyser {
	
	private JobProperties jobProperties;
	private LogAnalysis logAnalysis;
	
	private String defaultLogContent = "";
	
	private LimitedArrayList<Object> limitedArrayList = new LimitedArrayList<Object>();
	private int logLineNumBack = 0;
	private int logLineNumForward = 0;
	
	public void evaluate(Job job) {

		jobProperties = job.getJobRuntimeProperties().getJobProperties();
		logAnalysis = jobProperties.getLogAnalysis();
		
		Action logAction = logAnalysis.getAction();
				
		if (logAction.getThen() != null) {
			parseEvent(logAction.getThen().getEvent());
		} else if (logAction.getElse() != null) {
			parseEvent(logAction.getElse().getEvent());
		}

		boolean result = false;

		try {
			// Evaluate log analyzing procedures.

			String filePath = jobProperties.getBaseJobInfos().getJobLogPath();
			String fileName = jobProperties.getBaseJobInfos().getJobLogFile();

			File sourceFile = new File(filePath + File.separator + fileName);

			int direction = logAnalysis.getFindWhat().getDirection().intValue();

			boolean matcWholeWordOnly = logAnalysis.getFindWhat().getMatchWholeWordOnly();
			boolean isCaseSensitive = logAnalysis.getFindWhat().getMatchCase();

			String searchString = logAnalysis.getFindWhat().getStringValue();

			int modeType = logAnalysis.getFindWhat().getMode().intValue();

			if (matcWholeWordOnly) {
				result = matcWholeWordOnly(sourceFile, limitedArrayList, logLineNumForward, searchString, isCaseSensitive, direction, modeType);
			} else {
				result = matcWord(sourceFile, limitedArrayList, logLineNumForward, searchString, isCaseSensitive, direction, modeType);
			}

		} catch (UnsupportedOperationException uoe) {
			uoe.printStackTrace();
		}
		
		
		if(logAction.getThen() != null || logAction.getElse() != null) {
			
			for(Object text : limitedArrayList.toArray()) {
				defaultLogContent += text.toString() + "\n";
			}
			
			job.setChanged();
			job.notifyObservers(defaultLogContent);
			
			LiveStateInfo liveStateInfo = null;
			
			if(result) {
				liveStateInfo = logAction.getThen().getForcedResult().getLiveStateInfo();
			} else {
				liveStateInfo = logAction.getElse().getForcedResult().getLiveStateInfo();
			}
			
			job.insertNewLiveStateInfo(liveStateInfo);
		}

	}
	
	private void parseEvent(Event myEvent) {

		if (myEvent != null && myEvent.getContent() != null) {
			defaultLogContent = myEvent.getContent().getStringValue() + "\n";
			if (myEvent.getContent().getLogLineNumBack() != null) {
				logLineNumBack = myEvent.getContent().getLogLineNumBack().intValue();
			}
			if (myEvent.getContent().getLogLineNumForward() != null) {
				logLineNumForward = myEvent.getContent().getLogLineNumForward().intValue();
			}
		}

		limitedArrayList.setMaxLength(logLineNumBack + logLineNumForward + 1);
	}

	private static boolean matcWord(File sourceFile, AbstractCollection<Object> collection, int logLineNumForward, String searchString, boolean isCaseSensitive, int direction, int modeType) {

		boolean retValue = false;

		switch (direction) {

		case DirectionType.INT_DOWN:
			retValue = find(sourceFile, collection, logLineNumForward, searchString, isCaseSensitive, modeType);
			break;

		case DirectionType.INT_UP:
			retValue = reverseFind(sourceFile, collection, logLineNumForward, " " + searchString + " ", isCaseSensitive, modeType);
			break;

		default:
			throw new UnsupportedOperationException();
		}

		return retValue;

	}

	private static boolean matcWholeWordOnly(File sourceFile, AbstractCollection<Object> collection, int logLineNumForward, String searchString, boolean isCaseSensitive, int direction, int modeType) {

		boolean retValue = false;

		switch (direction) {

		case DirectionType.INT_DOWN:
			retValue = find(sourceFile, collection, logLineNumForward, " " + searchString + " ", isCaseSensitive, modeType);
			break;

		case DirectionType.INT_UP:
			retValue = reverseFind(sourceFile, collection, logLineNumForward, " " + searchString + " ", isCaseSensitive, modeType);
			break;

		default:
			throw new UnsupportedOperationException();
		}

		return retValue;
	}

	public static boolean find(File f, AbstractCollection<Object> collection, int logLineNumForward, String searchString, boolean isCaseSensitive, int modeType) {

		boolean result = false;

		Scanner in = null;

		try {

			in = new Scanner(new FileReader(f));

			int fwCounter = 0;
			while (in.hasNextLine() && (fwCounter < logLineNumForward)) {

				String myLine = in.nextLine();

				if (!result) {
					switch (modeType) {

					case ModeType.INT_NORMAL:
						result = searchNormal(myLine, searchString, isCaseSensitive);
						break;
					case ModeType.INT_REG_EX:
						result = searchRegEx(myLine, searchString, isCaseSensitive);
						break;
					default:
						throw new UnsupportedOperationException();
					}
				} else {
					fwCounter++;
				}

				collection.add(myLine);

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

	public static boolean reverseFind(File f, AbstractCollection<Object> collection, int logLineNumForward, String searchString, boolean isCaseSensitive, int modeType) {

		boolean result = false;

		Scanner in = null;

		try {

			ReverseLineInputStream reverseLineInputStream = new ReverseLineInputStream(f);

			in = new Scanner(reverseLineInputStream);

			int fwCounter = 0;
			while (in.hasNextLine() && (fwCounter < logLineNumForward)) {

				String myLine = in.nextLine();

				if (!result) {
					switch (modeType) {

					case ModeType.INT_NORMAL:
						result = searchNormal(myLine, searchString, isCaseSensitive);
						break;
					case ModeType.INT_REG_EX:
						result = searchRegEx(myLine, searchString, isCaseSensitive);
						break;
					default:
						throw new UnsupportedOperationException();
					}

				} else {
					fwCounter++;
				}

				collection.add(myLine);

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

		if (isCaseSensitive) {
			pattern = Pattern.compile(key);
		} else {
			pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
		}

		Matcher matcher = pattern.matcher(source);

		result = matcher.find();

		return result;
	}
}
