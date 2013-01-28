package com.likya.tlosswagent.core.model;

import com.likya.tlos.model.xmlbeans.agentconfig.AgentConfigInfoDocument.AgentConfigInfo;
import com.likya.tlossw.utils.GlobalRegistry;

public interface AgentGlobalRegistry extends GlobalRegistry {
	public AgentConfigInfo getAgentConfigInfo();
}
