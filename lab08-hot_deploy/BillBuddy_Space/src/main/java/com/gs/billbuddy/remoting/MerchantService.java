package com.gs.billbuddy.remoting;

import jakarta.annotation.Resource;

import com.gs.billbuddy.model.Merchant;
import com.gs.billbuddy.model.Payment;
import com.gs.billbuddy.model.ProcessingFee;
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
public class MerchantService implements IMerchantService {
	private final Log log = LogFactory.getLog(MerchantService.class);
	@Resource
	@Qualifier(value = "gigaSpace")
	private GigaSpace gigaSpace;
	
	
	public Double getMerchantProfit(Merchant merchant) {
		log.info("Staring getMerchantProfit to merchantId: " + merchant.getMerchantAccountId());
		
		Payment payment = new Payment();
		payment.setReceivingMerchantId(merchant.getMerchantAccountId());
		Payment[] payments = gigaSpace.readMultiple(payment);
		
		Double paymentAmount = 0d;
		for (int i = 0; i < payments.length; i++) {
			paymentAmount+= payments[i].getPaymentAmount();
		}
		
		
		ProcessingFee processingFee = new ProcessingFee();
		processingFee.setPayingAccountId(merchant.getMerchantAccountId());
		ProcessingFee[] processingFees = gigaSpace.readMultiple(processingFee);
		
		Double processingFeeAmount = 0d;
		for (int i = 0; i < processingFees.length; i++) {
			processingFeeAmount+= processingFees[i].getAmount();
		}
		
		log.info("merchantId profit is: " + (paymentAmount - processingFeeAmount));
		
		return paymentAmount - processingFeeAmount;
	}
}
