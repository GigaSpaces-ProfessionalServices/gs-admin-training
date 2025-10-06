package com.gs.billbuddy.report;

/** 
* ProcessServiceAmountFinder class. 
* 
* Executing a task which gets all Processing Fee amounts
*  
* @author GigaSpaces
*/

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.core.GigaSpace;
import org.springframework.stereotype.Component;

import com.gigaspaces.async.AsyncFuture;
@Component
public class ProcessServiceAmountFinder {

	private final static Log log = LogFactory.getLog(ProcessServiceAmountFinder.class);
    @Resource
   
    private GigaSpace gigaSpace;
    
    @PostConstruct
    public void construct() throws Exception {
       
    	log.info("Calculate Process service amount from all processing fee");
    	
    	AsyncFuture<Double> future = gigaSpace.execute(new ProcessServiceAmountTask());
    	Double processingFeeAmount = future.get();
        if(processingFeeAmount > 0.0){
    	log.info("Processing Fee Amount is: " + processingFeeAmount);
        }else if(processingFeeAmount == 0.0){
        	log.info("BillBuddy profit is 0 (ZERO), Please run the PaymentFeeder project.");
        }
        
    }
}
