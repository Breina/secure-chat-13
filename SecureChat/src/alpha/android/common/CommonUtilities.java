package alpha.android.common;

public final class CommonUtilities
{
	// Tag used in LOGCAT
	public static final String TAG = "ALPHA-DEBUG";
    
    
	// Titles of menu items that will be used in the MenuFragment when in HOME
    public static final String[] MENU_ITEMS_HOME =
    {
    	"Home",
        "Contacts",
        "Chat",
        "Camera",
        "Location",
        "Settings",
        "Logout"
    };
    
    public static final int MENU_POS_HOME = 0;
    public static final int MENU_POS_CONTACTS = 1;
    public static final int MENU_POS_CHAT = 2;
    public static final int MENU_POS_CAMERA = 3;
    public static final int MENU_POS_LOCATION = 4;
    public static final int MENU_POS_SETTINGS = 5;
    public static final int MENU_POS_LOGOUT = 6;
    
    
    // Web Service + DB data
    public static final String HOST ="http://10.0.2.2";
    public static final String PORT =":8080";
    public static final String PATH_WEBSERVICE = "/Restful_Webservice";
    public static final String REST_LOGIN = "/REST/WebService/login";
    public static final String REST_REGISTER = "/REST/WebService/register";
    public static final String REST_CHECK_GCM = "/REST/WebService/checkgcm";
    
    
    // GOOGLE API PROJECT KEY
	public static final String SENDER_ID = "556777754488";

	
	// GOOGLE PLAY SERVICES - GOOGLE CLOUD MESSAGING
    public static final String CACHED_REG_ID = "registration_id";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    
    
    // CAMERA
	public static final int REQUEST_IMAGE_CAPTURE = 1;
}
