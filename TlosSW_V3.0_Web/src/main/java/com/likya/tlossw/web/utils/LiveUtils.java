package com.likya.tlossw.web.utils;

import com.likya.tlossw.model.client.spc.JobInfoTypeClient;

public class LiveUtils {
	
	public static String jobPath(JobInfoTypeClient job) {
		String jobPath = new String();
		jobPath = job.getTreePath() + "." + job.getJobKey();
		return jobPath;
	}
	
	public static String getConcatenatedPathAndFileName(String path, String fileName) {
		if (path.indexOf('\\') >= 0) {
			if (path.lastIndexOf('\\') == path.length() - 1) {
				path = path.substring(0, path.length() - 1);
			}
			path = path + "\\";
		} else if (path.indexOf('/') >= 0) {
			if (path.lastIndexOf('/') == path.length() - 1) {
				path = path.substring(0, path.length() - 1);
			}
			path = path + "/";
		}

		return path + fileName;
	}
}
