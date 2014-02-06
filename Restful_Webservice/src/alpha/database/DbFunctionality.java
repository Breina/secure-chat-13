package alpha.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbFunctionality
{
	private Connection connection;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;

	
	public DbFunctionality(Connection conn)
	{
		this.connection = conn;
	}
	
	
	/*
	 * Creates a select-query and shows all the data of a given table
	 * 	@param tableName = name of the table to insert into
	 */
	public ResultSet selectAllStatement(String tableName)
	{
		resultSet = null;
		
		try
		{
			preparedStatement = connection.prepareStatement("SELECT * FROM " + tableName);
			
			resultSet = preparedStatement.executeQuery();
			
			System.out.println(" * SelectAll-statement was succesfully processed");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		preparedStatement = null;
		return resultSet;
	}
	
	
	/*
	 * Creates a select-query but shows only the data matching the keys and values, representing conditions
	 * 	@param tableName = name of the table to insert into
	 *  @param keys = name of the columns needed for the conditioning
	 *  @param values = actual values to do the conditioning
	 */
	public ResultSet selectObjectStatement(String tableName, String[] keys, String[] values)
	{
		String strStatement = "";
		resultSet = null;
		
		try
		{
			// Build the query string
			strStatement = "SELECT * FROM " + tableName + " WHERE ";
			
			for (String key : keys)
			{
				strStatement += key + " = ? AND ";
			}
			
			strStatement = strStatement.substring(0, strStatement.lastIndexOf("AND"));
			
			// Build the prepared statement
			preparedStatement = connection.prepareStatement(strStatement);
			
			// Add the parameters to the prepared statement
			for (int i = 1; i <= values.length; i++)
			{
				preparedStatement.setString(i, values[i - 1]);
			}
			
			// Execute the query
			resultSet = preparedStatement.executeQuery();
			
			System.out.println(" * SelectObject-statement was succesfully processed without errors");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		preparedStatement = null;
		return resultSet;
	}
	
	
	/*
	 * Creates an insert-query and inserts it into a given table
	 * 	@param tableName = name of the table to insert into
	 *  @param values = String array of the values (make sure to insert blanks for unwanted col values)
	 */
	public boolean insertStatement(String tableName, String[] colNames, String[] values)
	{
		boolean success = false;
		
		try
		{
			// Build the query string
			String strStatement = "INSERT INTO " + tableName + "(";
			
			for (@SuppressWarnings("unused") String col : colNames)
			{
				strStatement += col + ", ";
			}
			
			// Cut off last comma and close brackets
			strStatement = strStatement.substring(0, strStatement.lastIndexOf(','));
			strStatement += ") VALUES (";
			
			
			for (@SuppressWarnings("unused") String value : values)
			{
				strStatement += "?, ";
			}
			
			// Cut off last comma and finish query string
			strStatement = strStatement.substring(0, strStatement.lastIndexOf(','));
			strStatement += ")";
			
			// Build the prepared statement
			preparedStatement = connection.prepareStatement(strStatement);
			
			// Set parameters
			for (int i = 1; i <= values.length; i++)
			{
				preparedStatement.setString(i, values[i - 1]);
			}
			
			// Execute prepared statement
			preparedStatement.executeUpdate();
			
			success = true;
			System.out.println(" * Insert-statement was succesfully processed");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println(" * Insert-statement failed with error: " + e.getMessage().toString() + "\n" +
								   "Printing query string: " + preparedStatement.toString());
			
			return false;
		}

		preparedStatement = null;
		return success;
	}


	/*
	 * Creates a delete-query and deletes data from a given table based on a where-clause
	 * 	@param tableName = name of the table to insert into
	 *  @param whereClauseCol = String value representing the exact column name of the table to use in the where clause
	 *  @param whereClauseVal = String value representing the value corresponding to the col in the where clause
	 */
	public void deleteStatement(String tableName, String whereClauseCol, String whereClauseVal)
	{
	    try
	    {
			// Note to self: als het niet werkt, probeer es met ; achteraan
			preparedStatement = connection.prepareStatement("DELETE FROM " + tableName +
															"WHERE " + whereClauseCol + " = ?");
			
		    preparedStatement.setString(1, whereClauseVal);
		    preparedStatement.executeUpdate();
		    
		    System.out.println(" * Delete-statement was succesfully processed");
		}
	    catch (SQLException e)
	    {
			e.printStackTrace();
		}

	    preparedStatement = null;
	}

}
