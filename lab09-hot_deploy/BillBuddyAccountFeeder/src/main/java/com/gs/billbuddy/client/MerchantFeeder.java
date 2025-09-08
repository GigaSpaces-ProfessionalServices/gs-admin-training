package com.gs.billbuddy.client;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.openspaces.core.GigaSpace;
import org.springframework.transaction.annotation.Transactional;

import com.gs.billbuddy.model.AccountStatus;
import com.gs.billbuddy.model.CategoryType;
import com.gs.billbuddy.model.Contract;
import com.gs.billbuddy.model.Merchant;

/** 
 * Merchant Feeder class gets merchant name list which is stored in merchants data member. 
 * The class performing loop on the list and create user class and write it into space
 * 
 * The Class also enables creating one static user & write into the space
 * 
 * @author GigaSpaces
 */


public class MerchantFeeder {
	
	
	@SuppressWarnings("serial")
	private static final ArrayList<String> merchants=new ArrayList<String>(){{
	    add("Like Pace"); add("Konegsad"); add("SomeDisk"); add("Swakowsky"); 
	    add("Green Head band"); add("Shiruckou"); add("Eagle"); add("Lohitech"); 
	    add("The musicals"); add("SoccerMaster"); add("Fort"); add("2-Times"); 
	    add("Mazalaty"); add("jewelry 4 U"); add("Gems"); add("Hautika"); 

	}};
	
	public MerchantFeeder(){
		
	}
	
	// Method loads a list of merchants from the DataUtil class that serves as a user repository.
	// It then writes them into the space using a GigaSpace space proxy.
	// It reads users from the space & displays them into the console.
    
	public static void loadData(GigaSpace gigaSpace) throws Exception {
		System.out.println("Starting Merchant Feeder");
		System.out.println("Method: loadData - loads all merchants into the space");
		
        // merchantAccountId will serve as the Unique Identifier value
        
		Integer merchantAccountId = 1;

        // for each merchant in the merchantList do:
        
        for (String merchantName : MerchantFeeder.merchants) {
        	
        	createMerchant(merchantAccountId, merchantName,gigaSpace);
            merchantAccountId++;               
        }
        	
        System.out.println("Stopping Merchant Feeder");
        
    }

	   @Transactional
	    private static void createMerchant(Integer merchantAccountId, String merchantName,GigaSpace gigaSpace) {
	    	Merchant templateMerchant = new Merchant();
	        templateMerchant.setMerchantAccountId(merchantAccountId);
	        
	        Merchant foundMerchant = gigaSpace.read(templateMerchant);

	        
	        if (foundMerchant == null) {
	         	
	        	Merchant merchant = new Merchant();
	        	
	        	merchant.setName(merchantName);
	            merchant.setReceipts(0d);
	            merchant.setFeeAmount(0d);
	            
	            // Select Random Category
	            
	            CategoryType[] categoryTypes = CategoryType.values();
	            merchant.setCategory(categoryTypes[(int) ((categoryTypes.length - 1) * Math.random())]);
	            merchant.setStatus(AccountStatus.ACTIVE);
	            merchant.setMerchantAccountId(merchantAccountId);
	            
	            // Merchant is not found, let's add it.
	            
	            gigaSpace.write(merchant);
	            
	            System.out.println(String.format("Added Merchant object with name '%s'", merchant.getName()));
	            
	            createMerchantContract(merchantAccountId,gigaSpace);
	        }
			
		}

	
	
	
    /** 
     * Creates Contract with the terms between Merchant and BillBuddy 
     */ 
    private static void createMerchantContract(Integer merchantId, GigaSpace gigaSpace) {
    	
    	Calendar calendar = Calendar.getInstance();

		Contract contract = new Contract();
		contract.setId(merchantId);
		contract.setMerchantAccountId(merchantId);
		double randomTransactionFee = Double.parseDouble(new DecimalFormat("#.##").format(Math.random()/10 + 0.01));  
		contract.setTransactionPrecentFee(randomTransactionFee);
		contract.setContractDate(calendar.getTime());
   		
		       
        // 3. Write the document to the space:
        
		gigaSpace.write(contract);
        
        System.out.println(String.format("Added MerchantContract object with id '%s' with transaction fee of %.2f%n", contract.getId(), contract.getTransactionPrecentFee()));
		
	}
    
}
