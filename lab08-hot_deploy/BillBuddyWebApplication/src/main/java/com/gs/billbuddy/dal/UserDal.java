package com.gs.billbuddy.dal;

import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.gs.billbuddy.model.Payment;
import com.gs.billbuddy.model.User;
import java.util.Arrays;
import com.j_spaces.core.client.SQLQuery;
/** 
 *	Data Access Layer design pattern.
 *	
 *	Enables access to Administrative User operations(getUserPayments,getAllUsers etc.)
 *
 *	Dal object is injected by Spring framework as a singleton, thus, BillBuddy space 
 *	also injected to Dal instance.
 * 
 * @author GigaSpaces
 */
@Component
public class UserDal {
	
	private static UserDal instance;
	
	@Resource
	@Qualifier(value = "gigaSpace")
	private GigaSpace gigaSpace;
	
	public static UserDal getInstance(){
		return instance;
	}
	public UserDal(){}
	
	@PostConstruct
	@SuppressWarnings("unused")
	private void init(){
		instance = this;
	}
	
	public List<User> getAllUsers(){
		List<User> allUsers = null;
		SQLQuery<User> query = new SQLQuery<User>(User.class,"order by userAccountId");
		User[] users = (User[])gigaSpace.readMultiple(query, Integer.MAX_VALUE);

		if(users != null){
			allUsers = Arrays.asList(users);
		}
		
		return allUsers;
	}
	public List<Payment> getUserPayments(User user){
		List<Payment> userPaymentList = null;
		SQLQuery<Payment> paymentQuery = new SQLQuery<Payment>(Payment.class,"payingAccountId = ? order by paymentId ");
		paymentQuery.setParameter(1, user.getUserAccountId());
		Payment[] userPayments = (Payment[])gigaSpace.readMultiple(paymentQuery, Integer.MAX_VALUE);
		if(userPayments != null){
			userPaymentList = Arrays.asList(userPayments);
		}
		
		return userPaymentList;
	}
	
	
}
