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

import com.gs.billbuddy.model.Payment;

/** 
 *	{@link RemoteResultReducer} object used to reduce the results
 *	returned from the space.
 * 
 * @author GigaSpaces
 */


public class Top10PaymentReducer implements RemoteResultReducer<Payment[], Payment[]> {

	private final Log log = LogFactory.getLog(Top10PaymentReducer.class);



	@Override
	public Payment[] reduce(SpaceRemotingResult<Payment[]>[] results,
			SpaceRemotingInvocation remotingInvocation) throws Exception {
		log.info("Starting Top10MerchantFeeAmountReducer");
		
		List<Payment> payments = new ArrayList<Payment>();

        // Each result is an array of events. Each result is from a single partition.        
        for (SpaceRemotingResult<Payment[]> result : results) {
            if (result.getException() != null) {
                // just log the fact that there was an exception
                log.error("Executor Remoting Exception [" + result.getException() + "]");

                continue;
            }
           	Collections.addAll(payments, result.getResult());
        }
        
        Collections.sort(payments,
                new Comparator<Payment>() {
                    public int compare(Payment p1, Payment p2) {
                        return p2.getPaymentAmount().compareTo(p1.getPaymentAmount());
                    }
        });

        // If the number of results needed is less than the number of events that were reduced, then
        // return a sublist. Otherwise, return the entire list of events.
        Payment[] top10Payment;
        if (payments.size() < 10){
        	top10Payment = new Payment[payments.size()];
        	payments.toArray(top10Payment);
        	
        }
        else {
        	top10Payment = new Payment[10];
        	payments.subList(0, 10).toArray(top10Payment);
        }
        return top10Payment;
	}
    
}
