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
        "Prefs",
        "Options",
        "Logout"
    };
    
    public static final int MENU_POS_HOME = 0;
    public static final int MENU_POS_CONTACTS = 1;
    public static final int MENU_POS_CHAT = 2;
    public static final int MENU_POS_CAMERA = 3;
    public static final int MENU_POS_LOCATION = 4;
    public static final int MENU_POS_PREFS = 5;
    public static final int MENU_POS_OPTIONS = 6;
    public static final int MENU_POS_LOGOUT = 7;
    
    
    // Web Service + DB data
    public static final String HOST ="http://10.0.2.2";
    public static final String PORT =":8080";
    public static final String PATH_WEBSERVICE = "/Restful_Webservice";
    public static final String REST_LOGIN = "/REST/WebService/login";
    public static final String REST_REGISTER = "/REST/WebService/register";
    public static final String REST_CHECK_GCM = "/REST/WebService/checkgcm";
    public static final String REST_CHANGE_PASS = "/REST/WebService/changepass";
    
    
    // GOOGLE API PROJECT KEY
	public static final String SENDER_ID = "556777754488";

	
	// GOOGLE PLAY SERVICES - GOOGLE CLOUD MESSAGING
    public static final String CACHED_REG_ID = "registration_id";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    
    
    // CAMERA
	public static final int REQUEST_IMAGE_CAPTURE = 1;
	
	// MAPS
	public static final String GPSMARKER = "GPS";
	public static final double SPACESINGLE = 0.5d;
	public static final int MAPPADDING = 50;
	public static final boolean ANIMATION = true;
	public static final boolean ZOOMTOSELECTION = false;
	public static final String SIZEKEY = "marker_size";
	public static final String MARKERPREFIX = "marker_";
	
	// PREFERENCES
	public static final String SHARED_PREFS_NAME = "settings";
}
