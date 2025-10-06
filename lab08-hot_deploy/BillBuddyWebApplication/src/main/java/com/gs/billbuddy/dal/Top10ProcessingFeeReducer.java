package com.gs.billbuddy.dal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.remoting.RemoteResultReducer;
import org.openspaces.remoting.SpaceRemotingInvocation;
import org.openspaces.remoting.SpaceRemotingResult;

import com.gs.billbuddy.model.ProcessingFee;

/** 
 *	{@link RemoteResultReducer} object used to reduce the results
 *	returned from the space.
 * 
 * @author GigaSpaces
 */


public class Top10ProcessingFeeReducer implements RemoteResultReducer<ProcessingFee[], ProcessingFee[]> {

	private final Log log = LogFactory.getLog(Top10ProcessingFeeReducer.class);

    public ProcessingFee[] reduce(SpaceRemotingResult<ProcessingFee[]>[] results,
        SpaceRemotingInvocation remotingInvocation) throws Exception {
    	
    	log.info("Starting Top10MerchantFeeAmountReducer");
    	
        List<ProcessingFee> processingFees = new ArrayList<ProcessingFee>();

        // Each result is an array of events. Each result is from a single partition.        
        for (SpaceRemotingResult<ProcessingFee[]> result : results) {
            if (result.getException() != null) {
                // just log the fact that there was an exception
                log.error("Executor Remoting Exception [" + result.getException() + "]");

                continue;
            }
           	Collections.addAll(processingFees, result.getResult());
        }
        
        Collections.sort(processingFees,
                new Comparator<ProcessingFee>() {
                    public int compare(ProcessingFee p1, ProcessingFee p2) {
                        return p2.getAmount().compareTo(p1.getAmount());
                    }
        });

        // If the number of results needed is less than the number of events that were reduced, then
        // return a sublist. Otherwise, return the entire list of events.
        ProcessingFee[] top10ProcessingFee;
        if (processingFees.size() < 10){
        	top10ProcessingFee = new ProcessingFee[processingFees.size()];
        	processingFees.toArray(top10ProcessingFee);
        	
        }
        else {
        	top10ProcessingFee = new ProcessingFee[10];
        	processingFees.subList(0, 10).toArray(top10ProcessingFee);
        }
        return top10ProcessingFee;
    }
    
}
