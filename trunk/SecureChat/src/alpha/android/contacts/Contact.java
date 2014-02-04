package alpha.android.contacts;

import java.util.concurrent.ExecutionException;

import alpha.android.common.CommonUtilities;
import alpha.android.webservice.WebserviceManager;

public class Contact
{
	private static final String UNINITVALUE = "ERROR";

	private String name, username;
	private String gcm_id;

	public Contact(String name, String description)
	{
		this(name, description, UNINITVALUE);
	}

	public Contact(String name, String username, String gcm_id)
	{
		this.name = name;
		this.username = username;
		this.gcm_id = gcm_id;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return username;
	}

	public String getGcm_id()
	{
		return gcm_id;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setDescription(String description)
	{
		this.username = description;
	}

	public void setGcm_id(String gcm_id)
	{
		this.gcm_id = gcm_id;
	}

	public boolean isGcmImplemented()
	{
		return gcm_id.equals(UNINITVALUE);
	}

	public boolean obtainGcmId()
	{
		String[] getKeyParamKeys = { "username" };
		String[] getKeyParamValues = { name };

		try
		{
			WebserviceManager webServiceManager = new WebserviceManager(
					CommonUtilities.REST_LOGIN, getKeyParamKeys);
			final String result = webServiceManager.execute(getKeyParamValues)
					.get();

			if (result == null)
				return false;

			gcm_id = result;

			return true;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		finally
		{
			return false;
		}
	}
}
