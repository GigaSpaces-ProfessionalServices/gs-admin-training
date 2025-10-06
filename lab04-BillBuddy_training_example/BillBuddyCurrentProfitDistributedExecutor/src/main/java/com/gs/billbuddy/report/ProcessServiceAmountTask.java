package com.gs.billbuddy.report;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.executor.DistributedTask;
import org.openspaces.core.executor.TaskGigaSpace;

import com.gs.billbuddy.model.Merchant;
import com.gs.billbuddy.model.ProcessingFee;
import com.gigaspaces.async.AsyncResult;

/**
 * ProcessServiceAmountTask class.
 * 
 * Task executing more than once (concurrently) and returns a result that is a
 * reduced operation of all the different executions and gets all Processing Fee
 * amounts
 * 
 * 
 * @author GigaSpaces
 */

@SuppressWarnings("serial")
public class ProcessServiceAmountTask implements
		DistributedTask<Double, Double> {
	private final static Log log = LogFactory.getLog(ProcessServiceAmountTask.class);

	@TaskGigaSpace
	private transient GigaSpace gigaSpace;

	public Double execute() throws Exception {
    	log.info("ProcessServiceAmountTask- Start Execute.");
	
        Double processingFeesTotal=new Double(0);
   	
    	Merchant merchantTemplate = new Merchant();
    	Merchant[] merchants = gigaSpace.readMultiple(merchantTemplate, Integer.MAX_VALUE);
    	if(merchants!=null){
    	for(int i=0;i<merchants.length;i++){
    		processingFeesTotal+=merchants[i].getFeeAmount();
    	}}
    	
	    log.info("Number of Merchants found: " + merchants.length);
	    log.info("Total Fee Amount for current partition: " + processingFeesTotal);
	    log.info("ProcessServiceAmountTask- End Execute.");
	    
	    return processingFeesTotal;
    }

	public Double reduce(List<AsyncResult<Double>> results)
			throws Exception {
		log.info("MerchantByCategoryTask- Start reduce.");
		
		double processingFeeAmount = 0;

		for (AsyncResult<Double> result : results) {
			if (result.getException() != null) {
				throw result.getException();
			}

//			Collections.addAll(processingFees, result.getResult());
			processingFeeAmount+=result.getResult();
		}
		
		log.info("Processing Fee Amount is " + processingFeeAmount);
		log.info("ProcessServiceAmountTask- End reduce.");

		return processingFeeAmount;
	}

}
