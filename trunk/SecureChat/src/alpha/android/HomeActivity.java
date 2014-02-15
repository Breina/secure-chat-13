package alpha.android;

import alpha.android.common.CommonUtilities;
import alpha.android.fragments.CameraFragment;
import alpha.android.fragments.ContactsFragment;
import alpha.android.fragments.HomeFragment;
import alpha.android.fragments.MapFragment;
import alpha.android.fragments.MessageFragment;
import alpha.android.fragments.OptionsFragment;
import alpha.android.fragments.PrefsFragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity
{
	public static String username;
	
	// Menu Objects
    private DrawerLayout menuDrawerLayout;
    private ListView menuDrawerListView;
    private ActionBarDrawerToggle menuDrawerToggle;

    
    // On Create Home Activity
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container_home);

		// Get Client's user name and set it in the title textview
		username = getIntent().getStringExtra("username");
		username = username.trim();

		// Get Drawer's Layout and List
        menuDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuDrawerListView = (ListView) findViewById(R.id.left_drawer);

        // Set up the drawer's list view with items and click listener
        menuDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.list_navigation_drawer_row, CommonUtilities.MENU_ITEMS_HOME));
        menuDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
        
        // Set up the ActionBarDrawerToggle
        menuDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* HomeActivity */
                menuDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "Open drawer" description */
                R.string.drawer_close  /* "Close drawer" description */
                )
        {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view)
            {
                ImageView slider = (ImageView) findViewById(R.id.iv_left_drawer);
                slider.setVisibility(View.VISIBLE);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView)
            {
                ImageView slider = (ImageView) findViewById(R.id.iv_left_drawer);
                slider.setVisibility(View.INVISIBLE);
            }
        };

        // Set the Drawer Toggle as the DrawerListener
        menuDrawerLayout.setDrawerListener(menuDrawerToggle);

 		// Set the Home Icon as menu button
 	    getActionBar().setHomeButtonEnabled(true);

 	    // Inflates HomeFragment
 		menuItemClicked(CommonUtilities.MENU_POS_HOME);
    }
	
	
    // Syncs the Drawer Toggle state after onRestoreInstanceState has occurred.
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        
        menuDrawerToggle.syncState();
    }
    

    // Configuration changed
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        
        menuDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    
	// Click event of Drawer Action Bar
    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            menuItemClicked(position);
        }
    }


    // App Icon clicked (Menu drop-down)
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Pass the event to ActionBarDrawerToggle when the app icon was clicked
        if (menuDrawerToggle.onOptionsItemSelected(item))
        {
        	return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    
    // Occurs when a menu-item was clicked -> handles the fragments
    private void menuItemClicked(int position)
    {
		Fragment content = null;

		switch (position)
		{
			case CommonUtilities.MENU_POS_HOME:
				content = new HomeFragment();
				break;
			
			case CommonUtilities.MENU_POS_CONTACTS:
				content = new ContactsFragment();
				break;
				
			case CommonUtilities.MENU_POS_CHAT:
				content = new MessageFragment();
				break;
			
			case CommonUtilities.MENU_POS_CAMERA:
				content = new CameraFragment();
				break;
			
			case CommonUtilities.MENU_POS_LOCATION:
				content = new MapFragment();
				break;
				
			case CommonUtilities.MENU_POS_PREFS:
				content = new PrefsFragment();
				break;
	
			case CommonUtilities.MENU_POS_OPTIONS:
				content = new OptionsFragment();
				break;
	
			case CommonUtilities.MENU_POS_LOGOUT:
				Toast.makeText(getApplicationContext(), "Successfully logged out", Toast.LENGTH_LONG).show();
				finish();
				break;
	
			default:
				break;
		}

		// Check if there was previous content -> replace , else -> add
		if (content != null)
			if (getSupportFragmentManager().findFragmentById(
					R.id.contentFragment_container_main) != null)
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.contentFragment_container_main, content)
						.commit();
			else
				getSupportFragmentManager().beginTransaction()
						.add(R.id.contentFragment_container_main, content)
						.commit();
		
        // Highlight the selected item, update the title, and close the drawer
        menuDrawerListView.setItemChecked(position, true);
        getActionBar().setTitle((CommonUtilities.MENU_ITEMS_HOME[position]));
        menuDrawerLayout.closeDrawer(menuDrawerListView);
    }
    
}
