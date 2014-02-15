package alpha.controller;

import java.sql.ResultSet;

import alpha.database.DbConnection;
import alpha.database.DbFunctionality;

public class RegistrationHandler
{
	private DbFunctionality dbFunc;
	
	// Constructor of  RegistrationHandler, creates a connection with the DB and uses it
	// to create a class variable of type DbFunctionality, which we'll later use in the DB accessing methods
	public RegistrationHandler()
	{
		try
		{
			this.dbFunc = new DbFunctionality(new DbConnection().getConnection());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println( "RegistrationHandler: ERR Instantiation of DbFunctionality object failed");
		}
	}
	
	
	/**
	 * Main "managing-method" for the registration handling
	 * Uses DbFunctionality class variable to call the selectObjectStatement() method that will check whether
	 * the user is already registered or not, based on the username parameter received from the client
	 * 
	 * @param username = entered username of client
	 * @param password = entered password of client
	 * @return String object representing whether the used registered successfully or not
	 */
	public String verifyRegistration(String username, String password, String gcm_registration_id)
	{
		String returnString = "";
		
		// Check if username still free
		if (!checkForDuplicateUsernames(username))
		{
			String [] colNames = {"username", "password", "gcm_registration_id"};
			String [] values = {username, password, gcm_registration_id};
	
			try
			{
				// Use DbFunctionality insert new user from the DB
				boolean isSucces = dbFunc.insertStatement("userinfo", colNames, values);
				
				// Check if insert-statement was successful
				if (isSucces)
					returnString = "Congratulations! You registered successfully.";
				else
					returnString = "Unfortunately, the registration failed.";
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("Exception thrown from verifyRegistration() in RegistrationHandler \n" +
								   "Probably an error while inserting an object into the DB using insertStatement(). \n" + 
								   "Printing Params received from RegistrationService:  \n" +
								   "==================================================\n\n" +
								   "Username:   " + username + "\n" + 
								   "Password:   " + password + "\n");
				return "Error. Please contact the system admin.";
			}
		}
		else
		{
			returnString = "Unfortunately, the username " + username + " has already been chosen by someone else. " +
				       	   "Please try again with a different name.";
		}
		
		return returnString;
	}

	
	// Queries the DB to check whether the entered username already exists
	private boolean checkForDuplicateUsernames(String user)
	{
		String [] keys = {"username"};
		String [] values = {user};

		try
		{
			// Use DbFunctionality to find entered username in the DB
			ResultSet rs = dbFunc.selectObjectStatement("userinfo", keys, values);
			
			// Check if duplicate user found and return boolean
			if (rs.next())
				return true;
			else
				return false;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception thrown from checkForDuplicateUsernames() in RegistrationHandler \n" +
							   "Probably an error with getting an object from the DB using selectObjectStatement(). \n" + 
							   "Printing Param received from RegistrationService:  \n" +
							   "==================================================\n\n" +
							   "Username:   " + user + "\n");
			return true;
		}
	}
}
