package alpha.android.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import alpha.android.common.CommonUtilities;
import android.os.AsyncTask;
import android.util.Log;


public class WebserviceManager extends AsyncTask<String, Void, String>
{
	private static HttpClient httpclient;
	private static HttpResponse response;
	private static String[] paramKeys;
	private static String restService;
    
	
	// Constructor taking the column names of db that will be used as argument
	public WebserviceManager(final String restServiceToUse, final String[] columnNames)
	{
		restService = restServiceToUse;
		paramKeys = columnNames;
	}
	
	
	// Main task: manage all the actions needed to send a request to the Web Service and process the corresponding response
    @Override
    protected String doInBackground(String... params)
    {
    	Log.i(CommonUtilities.TAG, "Beginning Async WebserviceManager backgroundtask");
    	
		// Create a default HttpClient Object
        httpclient = new DefaultHttpClient();
        
        return putWebservice(createHttpPutObject(), params);
    }


	// Displays the results of the AsyncTask
    @Override
    protected void onPostExecute(String result)
    {
    	Log.i(CommonUtilities.TAG, "Entered onPostExecute of the WebserviceManager \n" +
    							   "Printing result:   " + result);
    }
    
    
    // Creates a valid HttpPost object set for use with JSON with path set to the given restService
    private HttpPut createHttpPutObject()
    {
    	HttpPut httpPutRequest = null;
    	
        // HttpPost object to the rest_loginservice on the webservice with header JSON
        httpPutRequest = new HttpPut(CommonUtilities.HOST + CommonUtilities.PORT +
        							   CommonUtilities.PATH_WEBSERVICE + restService);

        httpPutRequest.setHeader("Content-type", "application/json");
        httpPutRequest.setHeader("Accept", "application/json");
        
        Log.i(CommonUtilities.TAG, "Succesfully addressed the httpPutRequest to " + httpPutRequest.getURI());
        
        return httpPutRequest;
	}
    
    
    // Interacts with the web server through a POST-request
    private static String putWebservice(HttpPut put, final String[] paramValues)
	{
		try
		{
            // Create JSON Object and add the parameters
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < paramKeys.length; i++)
            	jsonObject.put(paramKeys[i], paramValues[i]);
            
			
            // Create StringEntity from our JSONObject and add it to the post request object
            StringEntity jsonStringEntity = new StringEntity(jsonObject.toString());
            jsonStringEntity.setContentType("application/json");
            jsonStringEntity.setContentEncoding("UTF-8");
            put.setEntity(jsonStringEntity);
            Log.i(CommonUtilities.TAG, "Succesfully added JSON StringEntity: " + jsonStringEntity);

            
            // Execute the request and set response
            response = httpclient.execute(put);
            
            if (response != null)
            {
            	Log.i(CommonUtilities.TAG, "Succesfully executed the request");
            	
                // Return String read from the parsed response
                return readResponse();
            }
            else
            {
            	Log.i(CommonUtilities.TAG, "Received null from the Web Service while putting request.");
            	
            	return null;
            }
        }
		catch(ClientProtocolException e)
		{
			e.printStackTrace();
			Log.i(CommonUtilities.TAG, "ClientProtocolException thrown while executing POST request");
		}
		catch(IOException e)
		{
			e.printStackTrace();
			Log.i(CommonUtilities.TAG, "IOException thrown while executing POST request");
		}
		catch(Exception e)
		{
			e.printStackTrace();
            Log.i(CommonUtilities.TAG, "General error in WebserviceManager while putting the request");
        }
		
		Log.i(CommonUtilities.TAG, "Printing Request Objects: \n" +
						           " * Request-object:   " + put.toString() + " at URI " +
		   				           put.getURI().toString());
		
		return null;
	}

    
    // Reads the response received from the Web Service
    private static String readResponse()
    {
        String result = null;
        InputStream is = null;
        StringBuilder sb = null;
        
    	try
    	{
        	// Pass the response HttpEntity to our InputStream object
        	is = response.getEntity().getContent();
			Log.i(CommonUtilities.TAG, "Succesfully opened response stream object");
			
            // Try to read response into String
        	BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"), 8);
            
        	sb = new StringBuilder();
        	sb.append(reader.readLine() + "\n");
        
        	String line = "";
        	while ((line = reader.readLine()) != null)
        	{
        		sb.append(line + "\n");
        	}
       
        	is.close();
        	
        	result = sb.toString();

        	Log.i(CommonUtilities.TAG, "Response object was successfully read and converted to a String object");
        	Log.i(CommonUtilities.TAG, "RESULT:   " + result);
        	
        	return result;
		}
    	catch (ParseException e)
    	{
			e.printStackTrace();
		}
    	catch (IOException e)
		{
			e.printStackTrace();
		}
    	
    	return null;
    }
    
}
