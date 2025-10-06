package com.gs.billbuddy.remoting;

import jakarta.annotation.Resource;

import com.gs.billbuddy.model.Merchant;
import com.gs.billbuddy.model.Payment;
import com.gs.billbuddy.model.ProcessingFee;
import com.gs.billbuddy.remoting.IServiceFinder;
import com.j_spaces.core.client.SQLQuery;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.core.GigaSpace;
import org.openspaces.remoting.RemotingService;
import org.springframework.beans.factory.annotation.Qualifier;

/** 
* ServiceFinder class. 
*  
* Implements IServiceFinder interface remoting capabilities on top of the space
* 
* @author GigaSpaces
*/

@RemotingService
public class ServiceFinder implements IServiceFinder {
	private final Log log = LogFactory.getLog(ServiceFinder.class);
	@Resource
	@Qualifier(value = "gigaSpace")
	private GigaSpace gigaSpace;

	public Payment[] findTop10Payments() {

		log.info("Find Top 10 payment merchant over the space");

		SQLQuery<Payment> query = new SQLQuery<Payment>(Payment.class,
				" order by paymentAmount desc");
		Payment[] payments = gigaSpace.readMultiple(query, 10);

		log.info("Found: " + payments.length + " payments.");

		return payments;
	}

	public Merchant[] findTop5MerchantFeeAmount() {
		log.info("Find Top 5 merchant fee amount over the space");

		SQLQuery<Merchant> query = new SQLQuery<Merchant>(Merchant.class,
				" order by feeAmount desc");
		Merchant[] merchants = gigaSpace.readMultiple(query, 5);

		log.info("Found: " + merchants.length + " merchants.");

		return merchants;

	}
	public ProcessingFee[] findTop10ProcessingFees() {
		log.info("Find Top 10 Processing fees over the space");

		SQLQuery<ProcessingFee> query = new SQLQuery<ProcessingFee>(ProcessingFee.class,
				" order by amount desc");
		ProcessingFee[] processingFees = gigaSpace.readMultiple(query, 10);

		log.info("Found: " + processingFees.length + " merchants.");
		return processingFees;
	}
}
