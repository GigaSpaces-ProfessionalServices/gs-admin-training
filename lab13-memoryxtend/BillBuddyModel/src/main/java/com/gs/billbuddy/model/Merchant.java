package com.gs.billbuddy.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceIndex;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import com.gigaspaces.metadata.index.SpaceIndexType;


/** 
* Merchant class is a POJO which has merchant account information 
* 
* @author GigaSpaces
*/

@Entity
@SuppressWarnings("serial")
@SpaceClass
public class Merchant implements Serializable{
	@Id
	private Integer merchantAccountId;
    private String name;
    private Double receipts;
    private Double feeAmount;
    private CategoryType category;
    private AccountStatus status;
    
    
    public Merchant(Integer merchantAccountId) {
        this.merchantAccountId = merchantAccountId;
    }

    public Merchant() {
    }

    @SpaceId(autoGenerate = false)
    @SpaceRouting
    public Integer getMerchantAccountId() {
        return merchantAccountId;
    }

    public void setMerchantAccountId(Integer merchantAccountId) {
        this.merchantAccountId = merchantAccountId;
    }

	public void setName(String name) {
		this.name = name;
	}
	
	@SpaceIndex(type=SpaceIndexType.EQUAL)
	public String getName() {
		return name;
	}

	public CategoryType getCategory() {
		
		return category;
	}

	public void setCategory(CategoryType category) {
		this.category = category;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}


	public void setReceipts(Double receipts) {
		this.receipts = receipts;
	}

	public Double getReceipts() {
		return receipts;
	}

	public void setFeeAmount(Double feeAmount) {
		this.feeAmount = feeAmount;
	}

	public Double getFeeAmount() {
		return feeAmount;
	}

}
