package alpha.android.fragments;

import alpha.android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuFragment extends ListFragment
{
    private OnMenuSelectedListener parentCallback;


    @SuppressLint("InlinedApi")
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

        
        // Get bundle data representing the menu items and put in String array
        Bundle bundleData = getArguments();
        String [] menuItems = (String[]) bundleData.get("menu_items");

        // Create an array adapter for the list view, using CommonUtilities
        setListAdapter(new ArrayAdapter<String>(getActivity(), layout, menuItems));
    }

    
    @Override
    public void onStart()
    {
        super.onStart();

        // When in Large layout, set the listview choice mode
        if (getFragmentManager().findFragmentById(R.id.content_fragment) != null)
        	getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // Make sure the container activity has implemented the callback interface. If not, throw an exception.
        try
        {
            parentCallback = (OnMenuSelectedListener) activity;
        } 
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    
    // Interface for handling the MenuFragment-items click-events
    public interface OnMenuSelectedListener
    {
        public void onMenuItemSelected(int position);
    }
    
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        // Notify the parent activity of selected item
        parentCallback.onMenuItemSelected(position);
        
        // Set the item as checked to be highlighted
        getListView().setItemChecked(position, true);
    }
    
}