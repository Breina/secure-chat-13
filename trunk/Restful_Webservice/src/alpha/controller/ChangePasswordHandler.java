package alpha.controller;

import alpha.database.DbConnection;
import alpha.database.DbFunctionality;

public class ChangePasswordHandler
{
	private DbFunctionality dbFunc;
	
	// Constructor of the ChangePasswordHandler, creates a connection with the DB and uses it
	// to create a class variable of type DbFunctionality, which we'll later use in the DB accessing methods
	public ChangePasswordHandler()
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
	 * Main "managing-method" for the handling of setting a new password
	 * Uses DbFunctionality class variable to call the insertStatement() method that will insert
	 * the new password into the DB
	 * 
	 * @param username = entered username of client
	 * @param password = entered password of client
	 * @return String object representing whether the used logged in successfully or not
	 * @throws Exception
	 */
	public boolean changePassword(String username, String newPassword) throws Exception
	{
		try
		{
			// Use DbFunctionality insert new user from the DB
			return dbFunc.alterObject("userinfo", "password", newPassword, "username", username);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception thrown from changePassword() in ChangePasswordHandler \n" +
							   "Probably an error while altering an object into the DB using alterObject() statement. \n" + 
							   "Printing Params received from Service:  \n" +
							   "==================================================\n\n" +
							   "Username:   " + username + "\n" + 
							   "New password:   " + newPassword + "\n");
			return false;
		}
	}
}
