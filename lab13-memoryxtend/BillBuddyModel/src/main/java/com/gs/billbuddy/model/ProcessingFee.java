package com.gs.billbuddy.model;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceIndex;
import com.gigaspaces.annotation.pojo.SpaceRouting;
import com.gigaspaces.metadata.index.SpaceIndexType;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


/** 
* Payment class is a POJO which has transaction information between merchant and BillBuddy 
* 
* @author GigaSpaces
*/
@Entity
@SuppressWarnings("serial")
@SpaceClass
public class ProcessingFee implements Serializable{
	@Id
	private String processingFeeId;
    private Integer payingAccountId;
    private String dependentPaymentId;
    private String description;
    private Double amount;
    private TransactionStatus status;
    private Date createdDate;
    
    
    public ProcessingFee(String processingFeeId) {
        this.processingFeeId = processingFeeId;
    }

    public ProcessingFee() {
    }

    @SpaceId(autoGenerate = true)
    public String getProcessingFeeId() {
        return processingFeeId;
    }

    public void setProcessingFeeId(String processingFeeId) {
        this.processingFeeId = processingFeeId;
    }

    @SpaceRouting
	@SpaceIndex(type=SpaceIndexType.EQUAL)
	public Integer getPayingAccountId() {
		return payingAccountId;
	}

	public void setPayingAccountId(Integer payingAccountId) {
		this.payingAccountId = payingAccountId;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	
	@SpaceIndex(type=SpaceIndexType.EQUAL)
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	
	public void setDependentPaymentId(String dependentPaymentId) {
		this.dependentPaymentId = dependentPaymentId;
	}

	@SpaceIndex(type=SpaceIndexType.EQUAL)
	public String getDependentPaymentId() {
		return dependentPaymentId;
	}
	
}
