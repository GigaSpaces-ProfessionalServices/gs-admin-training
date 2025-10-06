package com.gs.billbuddy.events;





import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.events.EventDriven;
import org.openspaces.events.EventTemplate;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.polling.Polling;

import com.gs.billbuddy.model.Payment;
import com.gs.billbuddy.model.TransactionStatus;

/** 
 * AuditPaymentEventContainer class. 
 *  
 * Pooling Event starts when new Payment with Transaction status NEW written into space
 * It takes the Payment object and change it's status to AUDIT and write it into space
 * 
 * @author GigaSpaces
 */

@EventDriven
@Polling(gigaSpace="gigaSpace")
@TransactionalEvent
public class AuditPaymentPollingEventContainer {
    private final Log log = LogFactory.getLog(AuditPaymentPollingEventContainer.class);
   

    @EventTemplate
    public Payment findNewPayments() {
    	log.info("Starting AuditPaymentEventContainer EventTemplate for Payment with NEW status.");
    	
    	Payment paymentTemplate = new Payment();
    	paymentTemplate.setStatus(TransactionStatus.NEW);
   
        return paymentTemplate;
    }

    @SpaceDataEvent
    public Payment processPayment(Payment payment) {
    	
    	log.info("Starting AuditPaymentEventContainer SpaceDataEvent.");
    	log.info("Payment ID: "+payment.getPaymentId() +" Merchant ID: "+ payment.getReceivingMerchantId() +
    			" User ID: "	+ payment.getPayingAccountId() + " Payment Amount: "	+ payment.getPaymentAmount());
    	// Set payment status
    	payment.setStatus(TransactionStatus.AUDITED);
    	
        return payment;
    }
    
   
}
