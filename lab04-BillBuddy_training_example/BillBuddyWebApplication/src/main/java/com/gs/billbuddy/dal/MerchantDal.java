package com.gs.billbuddy.dal;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.openspaces.core.GigaSpace;
import org.springframework.stereotype.Component;

import com.gs.billbuddy.model.Contract;
import com.gs.billbuddy.model.Merchant;
import com.gs.billbuddy.model.Payment;
import com.gs.billbuddy.model.ProcessingFee;
import com.j_spaces.core.client.SQLQuery;
/** 
 *	Data Access Layer design pattern.
 *	
 *	Enables access to Administrative Merchant operations(getMerchant, totalProcessingFee, etc.)
 *
 *	Dal object is injected by Spring framework as a singleton, thus, BillBuddy space 
 *	also injected to Dal instance.
 * 
 * @author GigaSpaces
 */
@Component
public class MerchantDal {
	
	private static MerchantDal instance;
	
	@Resource
	private GigaSpace gigaSpace;
	
	
	public static MerchantDal getInstance(){
		return instance;
	}
	public MerchantDal() {
	}

	@PostConstruct
	@SuppressWarnings("unused")
	private void init(){
		instance = this;
	}
	public List<Merchant> getAllMerchants() {
		List<Merchant> merchantList = null;
		SQLQuery<Merchant> merchantQuery = new SQLQuery<Merchant>(Merchant.class,"order by merchantAccountId");
		Merchant[] merchants = gigaSpace.readMultiple(merchantQuery,
				Integer.MAX_VALUE);
		if (merchants != null) {
			merchantList = Arrays.asList(merchants);
		}

		return merchantList;
	}

	public List<Payment> getMerchantPayments(Merchant merchant) {
		List<Payment> merchantPaymentList = null;
		SQLQuery<Payment> paymentQuery = new SQLQuery<Payment>(Payment.class,"receivingMerchantId = ? order by paymentId");
		paymentQuery.setParameter(1, merchant.getMerchantAccountId());
		
		Payment[] merchantPayments = gigaSpace.readMultiple(paymentQuery,
				Integer.MAX_VALUE);
		
		if (merchantPayments != null) {
			merchantPaymentList = Arrays.asList(merchantPayments);
		}
		return merchantPaymentList;
	}

	public List<ProcessingFee> getMerchantProcessingFees(Merchant merchant) {
		List<ProcessingFee> merchantProcessingFeesList = null;
		SQLQuery<ProcessingFee> processingFeeQuery = new SQLQuery<ProcessingFee>(ProcessingFee.class,"payingAccountId = ? order by processingFeeId");
		processingFeeQuery.setParameter(1, merchant.getMerchantAccountId());
		ProcessingFee[] merchantProcessingFees = gigaSpace.readMultiple(processingFeeQuery,
				Integer.MAX_VALUE);

		if (merchantProcessingFees != null) {
			merchantProcessingFeesList = Arrays.asList(merchantProcessingFees);
		}

		return merchantProcessingFeesList;
	}
	public List<Contract> getMerchantContractDocument(Merchant merchant) {
		List<Contract> merchantContractDocumentsList = null;
		SQLQuery<Contract> contractDocumentQuery = new SQLQuery<Contract>(Contract.class,"merchantId = ?");
		contractDocumentQuery.setParameter(1, merchant.getMerchantAccountId());
		Contract[] merchantContractDocuments = gigaSpace.readMultiple(contractDocumentQuery,
				Integer.MAX_VALUE);

		if (merchantContractDocuments != null) {
			merchantContractDocumentsList = Arrays.asList(merchantContractDocuments);
		}

		return merchantContractDocumentsList;
	}
	
	public Double totalProcessingFee(Merchant merchant){
		Double totalFeeAmount = 0D;
		
		List<ProcessingFee> processingFeeList = getMerchantProcessingFees(merchant);
		
		for(ProcessingFee pf:processingFeeList){
			totalFeeAmount += pf.getAmount();
		}
		
		return totalFeeAmount;
	}
}
