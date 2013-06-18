package com.likya.tlossw.core.spc.helpers;

import java.io.File;
import java.io.Writer;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileListener;
import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.listener.FileNameTypeDocument.FileNameType;
import com.likya.tlos.model.xmlbeans.listener.PollingTypeDocument.PollingType;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;

public class CustomFileListener implements FileListener {

	private com.likya.tlos.model.xmlbeans.listener.FileListenerDocument.FileListener fileListener;

	private int repetationNumber = 0;

	private Writer outputFile;

	private Logger myLogger = Logger.getLogger(CustomFileListener.class);

	@Override
	public void fileChanged(FileChangeEvent arg0) throws Exception {
		if (fileListener.getPollingProperties().getPollingType() == PollingType.UPDATE) {

			if (fileListener.getFileNameType() == FileNameType.FULL_TEXT) {

				String triggerFileWithPath = ParsingUtils.getConcatenatedPathAndFileName(fileListener.getPollingProperties().getTriggerFileDirectory(), fileListener.getPollingProperties().getTriggerFile());

				File file = new File(arg0.getFile().getURL().getPath());

				if (triggerFileWithPath.equals(file.getPath())) {
					myLogger.info("\"" + file.getPath() + "\" guncellendi");
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " \"" + file.getPath() + "\" guncellendi" + System.getProperty("line.separator"));

					setRepetationNumber(getRepetationNumber() + 1);
				}
			} else {
				String includeRegex = fileListener.getReadAndListFileProperties().getIncludeFiles();
				String excludeRegex = fileListener.getReadAndListFileProperties().getExcludeFiles();
				String fileName = arg0.getFile().getURL().toString().substring(arg0.getFile().getParent().toString().length() + 1);

				if (fileName.matches(includeRegex) && !fileName.matches(excludeRegex)) {
					myLogger.info("\"" + fileName + "\" guncellendi");
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " \"" + fileName + "\" guncellendi" + System.getProperty("line.separator"));

					setRepetationNumber(getRepetationNumber() + 1);
				}
			}
		}
	}

	@Override
	public void fileCreated(FileChangeEvent arg0) throws Exception {
		if (fileListener.getPollingProperties().getPollingType() == PollingType.CREATE) {

			if (fileListener.getFileNameType() == FileNameType.FULL_TEXT) {

				String triggerFileWithPath = ParsingUtils.getConcatenatedPathAndFileName(fileListener.getPollingProperties().getTriggerFileDirectory(), fileListener.getPollingProperties().getTriggerFile());

				File file = new File(arg0.getFile().getURL().getPath());

				if (triggerFileWithPath.equals(file.getPath())) {
					myLogger.info("\"" + file.getPath() + "\" olusturuldu");
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " \"" + file.getPath() + "\" olusturuldu" + System.getProperty("line.separator"));

					setRepetationNumber(getRepetationNumber() + 1);
				}
			} else {
				String includeRegex = fileListener.getReadAndListFileProperties().getIncludeFiles();
				String excludeRegex = fileListener.getReadAndListFileProperties().getExcludeFiles();
				String fileName = arg0.getFile().getURL().toString().substring(arg0.getFile().getParent().toString().length() + 1);

				if (fileName.matches(includeRegex) && !fileName.matches(excludeRegex)) {
					myLogger.info("\"" + fileName + "\" olusturuldu");
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " \"" + fileName + "\" olusturuldu" + System.getProperty("line.separator"));

					setRepetationNumber(getRepetationNumber() + 1);
				}
			}
		}
	}

	@Override
	public void fileDeleted(FileChangeEvent arg0) throws Exception {

		// System.out.println("\"" + arg0.getFile().getURL().getFile().substring(3) + "\" deleted");
	}

	public boolean repetationNumberExceeded() {
		if (getRepetationNumber() >= fileListener.getRepetationNumber()) {
			return true;
		}
		return false;
	}

	public void setRepetationNumber(int repetationNumber) {
		this.repetationNumber = repetationNumber;
	}

	public int getRepetationNumber() {
		return repetationNumber;
	}

	public void setOutputFile(Writer outputFile) {
		this.outputFile = outputFile;
	}

	public Writer getOutputFile() {
		return outputFile;
	}

	public void setFileListener(com.likya.tlos.model.xmlbeans.listener.FileListenerDocument.FileListener fileListener) {
		this.fileListener = fileListener;
	}

	public com.likya.tlos.model.xmlbeans.listener.FileListenerDocument.FileListener getFileListener() {
		return fileListener;
	}

}
