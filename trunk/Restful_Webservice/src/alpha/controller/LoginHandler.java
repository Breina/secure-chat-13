package alpha.controller;

import java.sql.ResultSet;

import alpha.database.DbConnection;
import alpha.database.DbFunctionality;
 
public class LoginHandler
{
	private DbFunctionality dbFunc;
	
	// Constructor of the LoginHandler, creates a connection with the DB and uses it
	// to create a class variable of type DbFunctionality, which we'll later use in the DB accessing methods
	public LoginHandler()
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
	 * Main "managing-method" for the login handling
	 * Uses DbFunctionality class variable to call the selectObjectStatement() method that will check whether
	 * the user logged in successfully or not, based on the parameters received from the client
	 * 
	 * @param username = entered username of client
	 * @param password = entered password of client
	 * @return String object representing whether the used logged in successfully or not
	 * @throws Exception
	 */
	public String verifyLogin(String username, String password) throws Exception
	{
		String [] keys = {"username", "password"};
		String [] values = {username, password};

		try
		{
			// Use DbFunctionality to get all users from the db
			ResultSet rs = dbFunc.selectObjectStatement("userinfo", keys, values);
			
			// Check if successfull and return corresponding message to LoginService instance
			if (rs.next())
				return username;
			else
				return "fail";
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception thrown from LoginHandler \n" +
							   "Probably an error with getting an object from the DB using selectObjectStatement(). \n" + 
							   "Printing Params received from LoginService:  \n" +
							   "===========================================\n\n" +
							   "Username:   " + username + "\n" +
							   "Password:   " + password + "\n\n");
			return null;
		}
	}

}