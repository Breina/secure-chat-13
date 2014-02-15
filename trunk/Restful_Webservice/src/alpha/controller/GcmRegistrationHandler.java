package alpha.controller;

import java.sql.ResultSet;

import alpha.database.DbConnection;
import alpha.database.DbFunctionality;

public class GcmRegistrationHandler
{
	private DbFunctionality dbFunc;
	
	// Constructor of the GcmRegistrationHandler, creates a connection with the DB and uses it
	// to create a class variable of type DbFunctionality, which we'll later use in the DB accessing methods
	public GcmRegistrationHandler()
	{
		try
		{
			this.dbFunc = new DbFunctionality(new DbConnection().getConnection());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println( " ERR Instantiation of DbFunctionality object failed");
		}
	}
	
	
	/**
	 * Main "managing-method" for the Gcm Registration handling
	 * Uses DbFunctionality class variable to call the selectObjectStatement() method that will check whether
	 * the user is already registered for Gcm usage, based on the username parameter received from the client
	 * 
	 * @param username = entered username of client
	 * @return String registration ID if one, else "fail"
	 * @throws Exception
	 */
	public String checkForGcmRegistration(String username)
	{
		String [] keys = {"username"};
		String [] values = {username};

		try
		{
			// Use DbFunctionality to get user from the db
			ResultSet rs = dbFunc.selectObjectStatement("userinfo", keys, values);
			
			// Check if successfull and if one, return corresponding gcm registration ID
			if (rs.next())
			{
				return rs.getString(4);
			}
			else
				return "fail";
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception thrown from checking the Gcm Registration with cause: " + e.getCause());
			return null;
		}
	}
}
