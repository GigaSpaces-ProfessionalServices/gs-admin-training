package com.gs.billbuddy.remoting;

import jakarta.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.core.GigaSpace;
import org.openspaces.remoting.RemotingService;

import com.gs.billbuddy.model.CategoryType;
import com.gs.billbuddy.model.Merchant;
import com.gs.billbuddy.model.Payment;
import com.j_spaces.core.client.SQLQuery;

/** 
* ServiceFinder class. 
*  
* Implements IServiceFinder interface remoting capabilities on top of the space
* 
* @author GigaSpaces
*/

@RemotingService
public class CategoryTransactionVolumeService implements ICategoryTransactionVolumeService {
	private final Log log = LogFactory.getLog(CategoryTransactionVolumeService.class);
	@Resource
	private GigaSpace gigaSpace;
	

	@Override
	public int findCategoryTransactionVolume(CategoryType categoryType) {
		log.info("Start TransactionsVolumeByCategoryTask service");
	    int transactionCount=0;
	    
	    // Search the space for all Merchant per selected category - prepare the query
	    
	    SQLQuery<Merchant> merchantQuery = new SQLQuery<Merchant>(Merchant.class, "category = ?");
	    merchantQuery.setParameter(1, categoryType);
	
	    // Execute Merchant query to the space
	    
	    Merchant[] merchantList = gigaSpace.readMultiple(merchantQuery, Integer.MAX_VALUE);
	    
	    // In case Merchant query result set has more then one Merchant
	    
	    if (merchantList.length > 0){
	    	
	    	SQLQuery<Payment> paymentQuery;
	    	log.info("Number of merchantList found: " + merchantList.length + " for category: " + categoryType);
	    	
	    	// Loop thru the list of Merchants
	    	
	    	for (Merchant merch : merchantList) {
			
	    		// Prepare Payment query to count number of Payments per each merchant
		    	
	    		paymentQuery = new SQLQuery<Payment>(Payment.class, "receivingMerchantId=?");
			    paymentQuery.setParameter(1, merch.getMerchantAccountId());
			    
			    // Execute query to count number of Payments per Merchant
			    
			    transactionCount += gigaSpace.count(paymentQuery);
	    	}

	    	log.info("Number of Transactions found: " + transactionCount + " for category: " + categoryType);
	    } else {
	    	log.info("No Merchants found for category: " + categoryType + " !!");
	    }
	    
	    log.info("TransactionsVolumeByCategoryTask- End Execute.");
	    return transactionCount;
	}
}
