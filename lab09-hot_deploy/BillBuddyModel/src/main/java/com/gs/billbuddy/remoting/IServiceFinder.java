package com.gs.billbuddy.remoting;

import com.gs.billbuddy.model.Merchant;
import com.gs.billbuddy.model.Payment;
import com.gs.billbuddy.model.ProcessingFee;


/** 
* IServiceFinder Interface. 
*  
* Define method which will be executed by remoting on top of the space
* 
* @author GigaSpaces
*/

public interface IServiceFinder {
  
	Payment[] findTop10Payments();
    
    Merchant[] findTop5MerchantFeeAmount();
    
    ProcessingFee[] findTop10ProcessingFees();
}
