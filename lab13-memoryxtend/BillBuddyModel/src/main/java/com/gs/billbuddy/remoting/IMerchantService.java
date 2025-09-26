package com.gs.billbuddy.remoting;

import org.openspaces.remoting.Routing;

import com.gs.billbuddy.model.Merchant;


/** 
* IServiceFinder Interface. 
*  
* Define method which will be executed by remoting on top of the space
* 
* @author GigaSpaces
*/

public interface IMerchantService {
  
	Double getMerchantProfit(@Routing("getMerchantAccountId") Merchant merchant);

	// Other option for Routing on remoting call
	//	Double getMerchantProfit(@Routing Integer merchantAccountId);
    
}
