package alpha.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConnection
{
	private static final String CONNURL = "jdbc:mysql://localhost:3306/alpha";
	private static final String JDBC = "com.mysql.jdbc.Driver";
	private static final String LOGIN = "root";
	private static final String PASS = "41B7579d-";
	
	/**
	 * Main method for creating a connection to the alpha MYSQL database using JDBC
	 * 
	 * @return Connection object representing the connection with our DB - alpha
	 * 
	 */
	public Connection getConnection() throws Exception
	{
		Connection connection = null;
		
		try
		{
			// Create instance of the jdbc Driver (com.mysql.jdbc)
			Class.forName(JDBC).newInstance();
			System.out.println(" * Initiating JDBC instance succeeded");
			
			// Attempt to get connection to our DB
			connection = DriverManager.getConnection(CONNURL, LOGIN, PASS);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(" * SQLException caught in DbConnection");
			
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(" * Exception of type: " + e.getClass().toString() + " was caught in DbConnection");
			
			return null;
		}
		
		return connection;
	}

}
