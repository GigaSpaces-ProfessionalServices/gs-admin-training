package com.gs.billbuddy.remoting;

import com.gs.billbuddy.model.CategoryType;


/** 
* ICategoryTransactionVolumeService Interface. 
*  
* Define method which will be executed by remoting on top of the space
* 
* @author GigaSpaces
*/

public interface ICategoryTransactionVolumeService{
  
    int findCategoryTransactionVolume(CategoryType categoryType);
}
