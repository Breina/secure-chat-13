package alpha.android.contacts;

public class Contact
{
	private static final String UNINITVALUE = "ERROR";

	private String name, description;
	private String gcm_id;

	public Contact(String name, String description)
	{
		this(name, description, UNINITVALUE);
	}

	public Contact(String name, String description, String gcm_id)
	{
		this.name = name;
		this.description = description;
		this.gcm_id = gcm_id;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
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
		this.description = description;
	}

	public void setGcm_id(String gcm_id)
	{
		this.gcm_id = gcm_id;
	}

	public boolean isGcmImplemented() {
		return gcm_id.equals(UNINITVALUE);
	}
}
