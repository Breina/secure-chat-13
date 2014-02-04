package alpha.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import alpha.controller.LoginHandler;
import alpha.controller.RegistrationHandler;
import alpha.webservices.jaxbeans.CheckGcmBean;
import alpha.webservices.jaxbeans.LoginBean;
import alpha.webservices.jaxbeans.RegistrationBean;


/**
 * JAVA WS RS - RESTFUL WEB SERVICE WITH JERSEY 
 * ============================================
 */
@Path("/WebService")
public class WebServices
{
	private static final String SENDER_ID = "556777754488";


	/**
	 * This method handles the PUT-event from the /login path and accepts a LoginBean (jaxb) parameter
	 * loginRequest, which is set in the client's application by using a JSONObject (JERSEY)
	 * 
	 * @param loginRequest = LoginBean representation of client's request object's JSON parameter
	 * @return String representing the message the user will receive on his browser,
	 * 		   being whether he logged in successfully or not
	 * 				
	 */
	@PUT
	@Path("/login")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String login(final LoginBean loginRequest)
	{
	    try
	    {
	    	// Check if JAXB BEAN loginRequest (POST parameter) is correctly set and received
	    	if (loginRequest != null)
		       return new LoginHandler().verifyLogin(loginRequest.username, loginRequest.password);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(" * LoginService failed with error:   " + e.getMessage().toString());
		}
	    
	    return null;
	}
	
	
	/**
	 * This method handles the PUT-event from the /register path and accepts a RegistrationBeanf (jaxb) parameter
	 * loginRequest, which is set in the client's application by using a JSONObject (JERSEY)
	 * 
	 * @param loginRequest = RegistrationBean representation of client's request object's JSON parameter
	 * @return String representing the message the user will receive on his browser,
	 * 		   being whether he registrated successfully or not
	 * 				
	 */
	@PUT
	@Path("/register")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String register(final RegistrationBean registrationRequest)
	{
	    try
	    {
	    	// Check if JAXB BEAN loginRequest (POST parameter) is correctly set and received
	    	if (registrationRequest != null)
		       return new RegistrationHandler().verifyRegistration(registrationRequest.username, registrationRequest.password,
		    		   											   registrationRequest.email, registrationRequest.gcm_registration_id);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(" * LoginService failed with error:   " + e.getMessage().toString());
		}
	    
	    return null;
	}
	
	
	@PUT
	@Path("/gcm")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String sendMessage(String msg, String deviceKey)
	{
		Result result = null;
		
		// We'll collect the "CollapseKey" and "Message" values from our JSP page
		String collapseKey = "";
		
		// Instance of com.android.gcm.server.Sender, that does the
		// transmission of a Message to the Google Cloud Messaging service.
		Sender sender = new Sender(SENDER_ID);
		
		// TODO: MAKE JSON
		Message message = new Message.Builder()
			.collapseKey(collapseKey)
			.timeToLive(30)
			.delayWhileIdle(true)
			.addData("message", msg)
			.build();
		
		try
		{
			// use this for multicast messages.  The second parameter
			// of sender.send() will need to be an array of register ids.
			result = sender.send(message, deviceKey, 1);
			
			if (result != null)
			{
				System.out.println("*  Result of message sending: " + result.toString());
			}
			else
			{
				System.out.println("*  Messaging failure: " + result.toString());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
		
		return result.toString();
	}
	
	
		
		/**
		 * CHECKS WHETHER THE ENTERED USER HAS ALREADY AN ENTRY FOR A GCM REGISTRATION ID IN THE DB
		 * 
		 * @param username entered user
		 * @return result
		 */
		@PUT
		@Path("/checkgcm")
		@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
		@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
		public String checkgcm(final CheckGcmBean gcmBean)
		{
		    try
		    {
		        return new LoginHandler().checkForGcmRegistration(gcmBean.username);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.out.println(" * LoginService failed with error:   " + e.getMessage().toString());
			}
		    
		    return null;
		}
}