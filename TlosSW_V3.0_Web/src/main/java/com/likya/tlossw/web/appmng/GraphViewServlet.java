package com.likya.tlossw.web.appmng;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.model.auth.WebAppUser;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.webclient.TEJmxMpClient;
import com.likya.tlossw.webclient.TEJmxMpDBClient;

public class GraphViewServlet extends HttpServlet {

	private static final long serialVersionUID = 2212000510316898588L;

	@SuppressWarnings("unused")
	private ServletConfig config;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.config = config;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String panel = request.getParameter("panel");
		String scenarioId = request.getParameter("scenarioId");
		
		WebAppUser webAppUser = (WebAppUser) request.getSession().getAttribute("webAppUser");
		if(webAppUser == null) {
	    	System.err.println(" Grafik çizimi için webAppUser bilgisi alınamadı. Error Code : 1985");
        }
		String gml = null;
		byte[] graphML = null;
		
		if (panel.equals(ConstantDefinitions.LIVE_TREE)) {
			gml = constructGMLforLiveTree(webAppUser, scenarioId);
			
			response.setContentType("text/xml");

		} else if (panel.equals(ConstantDefinitions.DEFINITION_TREE)) {
			
			gml = constructGML(webAppUser, scenarioId);

			response.setContentType("text/plain");

			int contentLength = gml.getBytes().length;
			response.setHeader("Content-Length", "" + contentLength);

			response.setStatus(200);

		} else {
			gml = "panel => " + panel;
			
			response.setContentType("text/xml");
		}

		graphML = gml.getBytes();
		
		OutputStream outputStream = null;
		outputStream = response.getOutputStream();

		if (graphML != null) {
			outputStream.write(graphML);
		}
		outputStream.flush();
		outputStream.close();
	}
	
	private String constructGMLforLiveTree(WebAppUser webAppUser, String scenarioId) {
		String gml = null;
		
		//ilgili senaryonun islerinin anlik bilgilerini sunucudan aliyor
		ArrayList<JobInfoTypeClient> jobInfoList = null;
		
		try {
			jobInfoList = (ArrayList<JobInfoTypeClient>) TEJmxMpClient.getJobInfoTypeClientList(webAppUser, scenarioId);	
	    } catch (NullPointerException npe) {
	    	System.err.println(" Grafik çizimi için Senaryo detayı alınamadı. Error Code : 1986");
        }
		
		gml = "<Graph>\n";
		
		String rootKey = "Job_List";
		
		long colorIndicator = 0xFFFFFF; // 0x8F8FFF;
		String nodeIcon = "gri";
		
		gml += "<Node id=\"" + rootKey + "\" name=\"" + rootKey + "\" nodeColor=\"" + colorIndicator + "\" nodeSize=\"" + "200" + "\" nodeClass=\"" + "" + "\" nodeIcon=\"" + "gri" + "\" />\n";

		String edgesML = "";
		
		if(jobInfoList != null) {
			for(JobInfoTypeClient job: jobInfoList) {
				
				LiveStateInfo liveStateInfo = job.getLiveStateInfo();
				
				if(liveStateInfo.getStateName().equals(StateName.PENDING)) {
					nodeIcon = "gri";
					colorIndicator = 0xD8D8D8;
				} else if(liveStateInfo.getStateName().equals(StateName.RUNNING)) {
					nodeIcon = "mavi";
					colorIndicator = 0x0101DF;
				} else if(liveStateInfo.getStateName().equals(StateName.CANCELLED)) {
					nodeIcon = "gri";
					colorIndicator = 0xb2b9c0;
				} else if(liveStateInfo.getStateName().equals(StateName.FINISHED)) {
					if(liveStateInfo.getSubstateName().equals(SubstateName.STOPPED) || liveStateInfo.getSubstateName().equals(SubstateName.SKIPPED)) {
						nodeIcon = "gri";
						colorIndicator = 0xb2b9c0;
					} else {
						if(liveStateInfo.getStatusName().equals(StatusName.FAILED)) {
							nodeIcon = "kirmizi";
							colorIndicator = 0xFF0040;
						} else {
							nodeIcon = "yesil";
							colorIndicator = 0x088A08;
						}
					}
				} else if(liveStateInfo.getStateName().equals(StateName.FAILED)) {
					nodeIcon = "kirmizi";
					colorIndicator = 0xFF0040;
				} 
				
				String jobId = job.getJobId();
				String jobName = job.getJobName();
				
				if(job.getJobDependencyList() != null && job.getJobDependencyList().size() > 0) {
					for(String dependencyItem: job.getJobDependencyList()) {
						edgesML += "<Edge fromID=\"" + dependencyItem + "\" toID=\"" + jobId + "\" color=\"" + colorIndicator + "\" flow=\"50\"  />\n";
					}
				} else {
					edgesML += "<Edge fromID=\"" + rootKey + "\" toID=\"" + jobId + "\" color=\"" + colorIndicator + "\" flow=\"50\"  />\n";
				} 
				
				gml += "<Node id=\"" + jobId + "\" name=\"" + jobName + "\" desc=\"" + jobName + "\" nodeColor=\"" + colorIndicator + "\" nodeSize=\""
				+ "32" + "\" nodeClass=\"" + "earth" + "\" nodeIcon=\"" + nodeIcon + "\" />\n";
			}
		}
		
		gml += edgesML;
		gml += "</Graph>";
		
		return gml;
	}
	
	private String constructGML(WebAppUser webAppUser, String scenarioId) {
		String gml = null;
		
		Scenario scenario = null; 
				
		try {
		   scenario = TEJmxMpDBClient.getScenarioFromId(webAppUser, CommonConstantDefinitions.JOB_DEFINITION_DATA, Integer.parseInt(scenarioId));
        } catch (NullPointerException npe) {
    	    System.out.println(" Grafik çizimi için getScenarioFromId bilgisi alınamadı. Error Code : 1987");
    	    return "";
        }
		gml = "<Graph>\n";
		
		String rootKey = "Job_List";
		
		long colorIndicator = 0xFFFFFF; // 0x8F8FFF;
		String nodeIcon = "yesil2";
		
		gml += "<Node id=\"" + rootKey + "\" name=\"" + rootKey + "\" nodeColor=\"" + colorIndicator + "\" nodeSize=\"" + "200" + "\" nodeClass=\"" + "" + "\" nodeIcon=\"" + "gri" + "\" />\n";

		String edgesML = "";

		for(JobProperties job: scenario.getJobList().getJobPropertiesArray()) {
			
			//isin bagimlilik tanimina gore edge'leri ayarliyor
			if(job.getDependencyList() != null && job.getDependencyList().getItemArray() != null && job.getDependencyList().getItemArray().length > 0) {
				
				for(Item dependencyItem: job.getDependencyList().getItemArray()) {
					edgesML += "<Edge fromID=\"" + dependencyItem.getJsId() + "\" toID=\"" + job.getID() + "\" color=\"0x8F8FFF\" flow=\"50\"  />\n";
				}
			} else {
				edgesML += "<Edge fromID=\"" + rootKey + "\" toID=\"" + job.getID() + "\" color=\"0x8F8FFF\" flow=\"50\" />\n";
			} 
			
			gml += "<Node id=\"" + job.getID() + "\" name=\"" + job.getBaseJobInfos().getJsName() + "\" desc=\"" + job.getBaseJobInfos().getJsName() + "-" + job.getBaseJobInfos().getComment() + "\" nodeColor=\"" + colorIndicator + "\" nodeSize=\""
				+ "32" + "\" nodeClass=\"" + "earth" + "\" nodeIcon=\"" + nodeIcon + "\" />\n";
		}
		
		gml += edgesML;
		gml += "</Graph>";
		
		return gml;
	}
}
