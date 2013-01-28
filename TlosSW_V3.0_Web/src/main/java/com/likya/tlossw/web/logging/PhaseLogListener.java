/*
 * Copyright 2010 Blue Lotus Software, LLC.
 * Copyright 2007-2010 John Yeary <jyeary@bluelotussoftware.com>.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
/*
 * PhasePrinterListener.java
 *
 * Created on February 3, 2007, 11:18 PM
 *
 * $Id: PhasePrinterListener.java 174 2010-01-23 03:52:31Z jyeary $
 */
package com.likya.tlossw.web.logging;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.log4j.Logger;

/**
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.1
 */
public class PhaseLogListener implements PhaseListener {

    private static final Logger logger = Logger.getLogger("global");
    private static final long serialVersionUID = 3131268230269004403L;

    private long startTime;
    
    @Override
    public void beforePhase(PhaseEvent event) {
    	startTime = System.currentTimeMillis();
//    	System.out.println(String.format("[Start time : %1$tm.%1$td.%1$tY %1$tH:%1$tM:%1$tS]", startTime));
    	
        if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
            logger.info("Processing new request!" + " Start : " + startTime + " ms"); 
        }
        logger.info("Before - " + event.getPhaseId().toString() + " Start : " + startTime + " ms");
//        System.out.println("Before - " + event.getPhaseId().toString() + " Start : " + startTime + " ms");
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void afterPhase(PhaseEvent event) {
    	
    	long endTime = System.currentTimeMillis();
    	long duration = endTime - startTime;
//    	System.out.println(String.format("[End Time : %1$tm.%1$td.%1$tY %1$tH:%1$tM:%1$tS]", endTime));
    	String durationLabel = String.format("%d ms", duration);
    	
        logger.info("After - " + event.getPhaseId().toString() + " Duration : " + durationLabel + " ms");
//        System.out.println("After - " + event.getPhaseId().toString() + " Duration : " + durationLabel + " ms");
//        System.out.println("Done with request!" + " Duration : " + durationLabel + " ms");
        
        if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
            logger.info("Done with request!" + " Duration : " + durationLabel + " ms");
        }
       
    }

}
