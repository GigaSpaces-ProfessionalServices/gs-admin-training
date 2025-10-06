package com.gs.billbuddy.dal;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.remoting.RemoteResultReducer;
import org.openspaces.remoting.SpaceRemotingInvocation;
import org.openspaces.remoting.SpaceRemotingResult;

import com.gs.billbuddy.model.Merchant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/** 
 *	{@link RemoteResultReducer} object used to reduce the results
 *	returned from the space.
 * 
 * @author GigaSpaces
 */

public class Top5MerchantFeeAmountReducer implements RemoteResultReducer<Merchant[], Merchant[]> {

	private final Log log = LogFactory.getLog(Top5MerchantFeeAmountReducer.class);

    public Merchant[] reduce(SpaceRemotingResult<Merchant[]>[] results,
        SpaceRemotingInvocation remotingInvocation) throws Exception {
    	
    	log.info("Starting Top5MerchantFeeAmountReducer");
    	
        List<Merchant> merchants = new ArrayList<Merchant>();

        // Each result is an array of events. Each result is from a single partition.        
        for (SpaceRemotingResult<Merchant[]> result : results) {
            if (result.getException() != null) {
                // just log the fact that there was an exception
                log.error("Executor Remoting Exception [" + result.getException() + "]");

                continue;
            }
           	Collections.addAll(merchants, result.getResult());
        }
        
        Collections.sort(merchants,
                new Comparator<Merchant>() {
                    public int compare(Merchant p1, Merchant p2) {
                        return p2.getFeeAmount().compareTo(p1.getFeeAmount());
                    }
        });

        // If the number of results needed is less than the number of events that were reduced, then
        // return a sublist. Otherwise, return the entire list of events.
        Merchant[] top5Merchant;
        if (merchants.size() < 5){
        	top5Merchant = new Merchant[merchants.size()];
        	merchants.toArray(top5Merchant);
        }
        else {
        	top5Merchant = new Merchant[5];
        	merchants.subList(0, 5).toArray(top5Merchant);
        }
        return top5Merchant;
    }
    
}
