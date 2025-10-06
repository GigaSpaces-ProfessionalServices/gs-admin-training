package com.gs.billbuddy.client;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/** 
 * Payment Feeder class read user and merchant randomly  
 * by Spring framework. 
 *  
 * This class been injected by Spring framework and it 
 * performs loop on userNameList creates User class and writes it into space.
 * 
 * @author GigaSpaces
 */

@Component
public class PaymentFeederGenerator {
    private final Log log = LogFactory.getLog(PaymentFeederGenerator.class);
    
    @Resource
    private PaymentFeeder paymentFeeder;

 
    @PostConstruct
    public void construct() throws Exception {
        log.info("Starting PaymentFeeder");
        
        // Create a new Thread to handle writing a new payments
        
        Thread t = new Thread(new PaymentCreatorExecuter());
        t.start();
    }


	private PaymentFeeder getPaymentFeeder() {
		return paymentFeeder;
	}

	
	// Thread class in charge of creating a payment every second
	private class PaymentCreatorExecuter implements Runnable {
		private long defaultDelay = 1000;
	    public void run() {
	        try {
	        	log.info("PaymentFeeder.PaymentCreatorExecuter thread has start");
	        	while (true){
	        		
	        		// Create a payment 
	        		
	        		getPaymentFeeder().createPayment();
	        		
	        		// Create a delay between payments 
	        		
	        		Thread.sleep(defaultDelay);
	        	}
	        } catch (InterruptedException e) {
	        	log.error("PaymentFeeder.PaymentCreatorExecuter has failed");
	        }
	    }
    }
    
}

