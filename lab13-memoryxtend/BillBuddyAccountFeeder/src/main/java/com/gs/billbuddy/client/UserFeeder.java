package com.gs.billbuddy.client;
import java.util.ArrayList;
import java.util.Random;

import org.openspaces.core.GigaSpace;
import com.gs.billbuddy.model.AccountStatus;
import com.gs.billbuddy.model.Address;
import com.gs.billbuddy.model.CountryNames;
import com.gs.billbuddy.model.User;

/** 
 * User Feeder class reads userNameList which is stored in users data member. 
 * The class performing loop on the list and create user class and write it into space
 * 
 * The Class also enables creating one static user & write into the space
 * 
 * @author GigaSpaces
 */


public class UserFeeder {
	
	@SuppressWarnings("serial")
	private static final ArrayList<String> users=new ArrayList<String>(){{
	    add("James Johnson"); add("Peter Gardener"); add("Andrei Saizovsky"); add("Petr Kirul"); 
	    add("Gerard Bourtagne"); add("Hans Freihof"); add("Sami Filppula"); add("Niklas Nilsson"); 	
	    add("Marian Varga"); add("Sigur Briem"); add("Bill Klien"); add("David King"); add("Magic Jordan"); 
	    add("Hana Brill"); add("Mustafa Cohen"); add("Michel Peet"); add("Samnta Gold");
	    add("Snoop Cat"); add("Marian Vog"); add("Suger Baby");
	}};
	
	public UserFeeder(){
	}

	// Method loads a list of users from users data member.
	// It then writes them into the space using a GigaSpace space proxy.
	// It reads users from the space & displays them into the console.
	
	public static void loadData(GigaSpace gigaSpace) throws Exception {
		System.out.println("Starting User Feeder");
		System.out.println("Method: loadData - loads all users into the space");
        
        Integer userAccountId = 1;
        
        // Read a list of user & iterate over them one by one.
        
        for (String userName : UserFeeder.users) {
          
        	// Check if user by the selected id already exists in the space.
     		
        	User foundUser = gigaSpace.readById(User.class,userAccountId);
            
            // Write a user to the space only if it does not exist already in the space
            
     		if (foundUser == null) {
            	
            	// create user
            	
            	User user = new User();
            	user.setName(userName);
                user.setBalance(0.0);
                user.setCreditLimit(-10000.0);
                
                user.setStatus(AccountStatus.ACTIVE);
                user.setUserAccountId(userAccountId);
                
                // Create User Address
                
                Address tempAddress = new Address();
            	tempAddress.setCountry(CountryNames.values()[new Random().nextInt(CountryNames.values().length)]);
            	tempAddress.setCity("GigaSpaces.com");
            	tempAddress.setState("GIGASPACES");
            	tempAddress.setStreet("Here and There");
            	tempAddress.setZipCode(new Random().nextInt());
            	
            	user.setAddress(tempAddress);
                
                // Write user to the space
                
                gigaSpace.write(user);
                
                System.out.println("Added User object with name: " + user.getName());
            }
            userAccountId++;
        }

        System.out.println("Stopping User Feeder writing process");
        
    }

}
