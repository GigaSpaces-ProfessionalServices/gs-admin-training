package com.gs.billbuddy.dal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.openspaces.core.GigaSpace;
import org.openspaces.remoting.ExecutorProxy;
import org.springframework.stereotype.Component;

import com.gs.billbuddy.model.Contract;
import com.gs.billbuddy.model.Merchant;
import com.gs.billbuddy.model.Payment;
import com.gs.billbuddy.model.ProcessingFee;
import com.gs.billbuddy.model.User;
import com.gs.billbuddy.remoting.IServiceFinder;
import com.gs.billbuddy.report.ProcessServiceAmountTask;
import com.gigaspaces.async.AsyncFuture;
import com.j_spaces.core.client.SQLQuery;

/** 
 *	Data Access Layer design pattern.
 *	
 *	Enables access to High level business statistics(total revenue, top users, etc.)
 *
 *	Dal object is injected by Spring framework as a singleton, thus, BillBuddy space 
 *	also injected to Dal instance.
 * 
 * @author GigaSpaces
 */
@Component
public class BillBuddyDal {

	private static BillBuddyDal instance;

	@Resource
	private GigaSpace gigaSpace;

	@ExecutorProxy(gigaSpace="gigaSpace",
	    	    broadcast=true,
	    	    remoteResultReducerType=Top5MerchantFeeAmountReducer.class)
	private IServiceFinder merchantServiceFinder;
	@ExecutorProxy(gigaSpace="gigaSpace",
    	    broadcast=true,
    	    remoteResultReducerType=Top10ProcessingFeeReducer.class)
	private IServiceFinder processingFeeServiceFinder;
	@ExecutorProxy(gigaSpace="gigaSpace",
    	    broadcast=true,
    	    remoteResultReducerType=Top10PaymentReducer.class)
	private IServiceFinder paymentServiceFinder;
	
	
	public static BillBuddyDal getInstance() {
		return instance;
	}

	public BillBuddyDal() {
	}
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void init()
	{
		instance=this;
	}
	
	public Double getBillBuddyRevenue() throws Exception, ExecutionException {
		AsyncFuture<Double> future = gigaSpace
				.execute(new ProcessServiceAmountTask());
		Double processingFeeAmount = future.get();

		return processingFeeAmount;
	}

	public List<Merchant> getTop5Merchants() throws Exception {
		List<Merchant> top5MerchantList = new ArrayList<Merchant>();
		top5MerchantList = Arrays.asList(merchantServiceFinder.findTop5MerchantFeeAmount());
		return top5MerchantList;
	}

	public List<Payment> getTop10Payments() {
		List<Payment> top10PaymentList = new ArrayList<Payment>();
		top10PaymentList = Arrays.asList(paymentServiceFinder.findTop10Payments());
		return top10PaymentList;
	}

	public List<ProcessingFee> getTop10ProcessingFees() {
		List<ProcessingFee> top10ProcessingFeeList = new ArrayList<ProcessingFee>();
		top10ProcessingFeeList = Arrays.asList(processingFeeServiceFinder.findTop10ProcessingFees());
		return top10ProcessingFeeList;
	}

	public Payment getPaymentById(String paymentId) {
		SQLQuery<Payment> query = new SQLQuery<Payment>(Payment.class,
				"paymentId = ?");
		query.setParameter(1, paymentId);
		return gigaSpace.read(query);
	}
	public Contract getContract(Integer merchantID){
		Contract contract = new Contract();
		contract.setMerchantAccountId(merchantID);
		System.out.println(contract);
		return gigaSpace.read(contract);
	}
	public HashMap<String,Integer> getTotalObjectCount()
	{
		HashMap<String,Integer> totals = new HashMap<String, Integer>();
		totals.put("users", gigaSpace.count(new User()));
		totals.put("merchants", gigaSpace.count(new Merchant()));
		totals.put("payments", gigaSpace.count(new Payment()));
		return totals;
	}
}


