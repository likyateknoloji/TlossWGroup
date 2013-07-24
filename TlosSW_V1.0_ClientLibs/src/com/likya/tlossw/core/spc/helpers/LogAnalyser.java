package com.likya.tlossw.core.spc.helpers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import com.likya.tlos.model.xmlbeans.data.DirectionType;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ModeType;
import com.likya.tlos.model.xmlbeans.data.LogAnalysisDocument.LogAnalysis;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlossw.utils.LiveStateInfoUtils;

public class LogAnalyser {

	public static void evaluate(JobProperties jobProperties) {

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
				// Event is not implemented yet
				// Do event thing

				LiveStateInfo liveStateInfo = logAnalysis.getAction().getThen().getForcedResult().getLiveStateInfo();
				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, liveStateInfo);

			} else if (!result && logAnalysis.getAction().getElse() != null) {
				// Event is not implemented yet
				// Do event thing

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
			throw new UnsupportedOperationException();
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
			throw new UnsupportedOperationException();
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
					if (isCaseSensitive) {
						result = in.nextLine().indexOf(searchString) >= 0;
					} else {
						result = in.nextLine().toUpperCase().indexOf(searchString.toUpperCase()) >= 0;
					}

					break;
				case ModeType.INT_REG_EX:
					result = in.nextLine().matches(searchString);
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

	public static boolean reverseFind(InputStream inputStream, String searchString, boolean isCaseSensitive) {

		boolean result = false;

		Scanner in = null;

		try {
			in = new Scanner(inputStream);
			while (in.hasNextLine() && !result) {
				if (isCaseSensitive) {
					result = in.nextLine().indexOf(searchString) >= 0;
				} else {
					result = in.nextLine().toUpperCase().indexOf(searchString.toUpperCase()) >= 0;
				}
			}
		} finally {
			try {
				in.close();
			} catch (Exception e) { /* ignore */
			}
		}

		return result;

	}

}
