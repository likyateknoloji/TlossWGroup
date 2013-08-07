package com.likya.tlossw.web.utils;

import java.util.ArrayList;

import com.likya.tlos.model.xmlbeans.nrperesults.CpuKullanimType.Timein;
import com.likya.tlos.model.xmlbeans.nrperesults.MessageDocument.Message;
import com.likya.tlos.model.xmlbeans.nrperesults.NrpeDataDocument.NrpeData;
import com.likya.tlos.model.xmlbeans.nrperesults.ResponseDocument.Response;
import com.likya.tlos.model.xmlbeans.nrperesults.ResponseDocument.Response.Command;
import com.likya.tlos.model.xmlbeans.sla.ForWhatAttribute.ForWhat;
import com.likya.tlossw.model.client.resource.CpuInfoTypeClient;
import com.likya.tlossw.model.client.resource.DiskInfoTypeClient;
import com.likya.tlossw.model.client.resource.MemoryInfoTypeClient;
import com.likya.tlossw.model.client.resource.NrpeDataInfoTypeClient;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;

public class LiveUtils {

	public static String jobPath(JobInfoTypeClient job) {
		String jobPath = new String();
		jobPath = job.getTreePath() + "." + job.getJobId();
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

	public static NrpeDataInfoTypeClient convertNrpeData(NrpeData nrpeData) {

		NrpeDataInfoTypeClient nrpeDataInfoTypeClient = new NrpeDataInfoTypeClient();

		ArrayList<CpuInfoTypeClient> cpuInfoTypeClientList = new ArrayList<CpuInfoTypeClient>();
		ArrayList<DiskInfoTypeClient> diskInfoTypeClientList = new ArrayList<DiskInfoTypeClient>();
		ArrayList<MemoryInfoTypeClient> memoryInfoTypeClientList = new ArrayList<MemoryInfoTypeClient>();

		// kac ayri mesaj geldiyse o kadar tariyor, iceriklerini gerekli yerlere set ediyor
		for (int i = 0; i < nrpeData.getNrpeCallArray(0).getMessageArray().length; i++) {

			Message message = nrpeData.getNrpeCallArray(0).getMessageArray(i);

			// her komut icin ayri response degeri geldigi icin tum response degerleri taraniyor
			for (int j = 0; j < message.getResponseArray().length; j++) {

				Response response = message.getResponseArray(j);

				// cpu response kismi icin buraya geliyor
				if (response.getCommand().equals(Command.ALIAS_CPU)) {

					CpuInfoTypeClient cpuInfoTypeClient = new CpuInfoTypeClient();
					cpuInfoTypeClient.setCpuUnit(response.getCpuArray(0).getBirim().toString());

					for (int cpuCnt = 0; cpuCnt < response.getCpuArray().length; cpuCnt++) {
						if (response.getCpuArray(cpuCnt).getTimein().equals(Timein.X_1)) {
							cpuInfoTypeClient.setUsedCpuOneMin(response.getCpuArray(cpuCnt).getStringValue());

						} else if (response.getCpuArray(cpuCnt).getTimein().equals(Timein.X_5)) {
							cpuInfoTypeClient.setUsedCpuFiveMin(response.getCpuArray(cpuCnt).getStringValue());

						} else if (response.getCpuArray(cpuCnt).getTimein().equals(Timein.X_15)) {
							cpuInfoTypeClient.setUsedCpuFifteenMin(response.getCpuArray(cpuCnt).getStringValue());
						}
					}

					cpuInfoTypeClientList.add(cpuInfoTypeClient);

					// disk response kismi icin buraya geliyor
				} else if (response.getCommand().equals(Command.ALIAS_DISK)) {

					DiskInfoTypeClient diskInfoTypeClient = new DiskInfoTypeClient();
					diskInfoTypeClient.setDiskUnit(response.getDiskArray(0).getBirim().toString());

					for (int diskCnt = 0; diskCnt < response.getDiskArray().length; diskCnt++) {
						if (response.getDiskArray(diskCnt).getForWhat().toString().equals(ForWhat.USED.toString())) {
							diskInfoTypeClient.setUsedDisk(response.getDiskArray(diskCnt).getStringValue());

						} else if (response.getDiskArray(diskCnt).getForWhat().toString().equals(ForWhat.FREE.toString())) {
							diskInfoTypeClient.setFreeDisk(response.getDiskArray(diskCnt).getStringValue());
						}
					}

					diskInfoTypeClientList.add(diskInfoTypeClient);

					// memory response kismi icin buraya geliyor
				} else if (response.getCommand().equals(Command.ALIAS_MEM)) {

					MemoryInfoTypeClient memoryInfoTypeClient = new MemoryInfoTypeClient();
					memoryInfoTypeClient.setMemoryUnit(response.getMemArray(0).getBirim().toString());

					for (int memCnt = 0; memCnt < response.getMemArray().length; memCnt++) {
						if (response.getMemArray(memCnt).getForWhat().toString().equals(ForWhat.USED.toString())) {
							memoryInfoTypeClient.setUsedMemory(response.getMemArray(memCnt).getStringValue());

						} else if (response.getMemArray(memCnt).getForWhat().toString().equals(ForWhat.FREE.toString())) {
							memoryInfoTypeClient.setFreeMemory(response.getMemArray(memCnt).getStringValue());
						}
					}

					memoryInfoTypeClientList.add(memoryInfoTypeClient);
				}
			}
		}

		nrpeDataInfoTypeClient.setCpuInfoTypeClientList(cpuInfoTypeClientList);
		nrpeDataInfoTypeClient.setDiskInfoTypeClientList(diskInfoTypeClientList);
		nrpeDataInfoTypeClient.setMemoryInfoTypeClientList(memoryInfoTypeClientList);

		return nrpeDataInfoTypeClient;
	}
}
