package za.co.zebrav.smartdoor.main;

import za.co.zebrav.smartdoor.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class allows one to add a slider menu to an activity
 */
public class CustomMenu
{
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private String title = "";
	private Activity activity;
	private String[] options;
	private SharedPreferences settings = null;
	
	private ArrayAdapter<String> adapter;
	
	//menu content related variable declarations:
	private AlertDialog.Builder alert;
	public static final String PREFS_NAME = "MyPrefsFile";
	
	/**
	 * @param activity - the activity using the sliderMenu
	 * @param drawerList - specified drawerList to be used
	 * @param drawerLayout - specified drawerLayout to be used
	 */
	public CustomMenu(Activity activity, ListView drawerList, DrawerLayout drawerLayout, String[] options)
	{
		settings = activity.getSharedPreferences(PREFS_NAME, 0);
		this.options = options;
		this.alert  = new AlertDialog.Builder(activity);
		
		setActivity(activity);
		setTitle((String)activity.getTitle());
		setDrawerList(drawerList);
		setDrawerLayout(drawerLayout);
		setDrawerToggle();
		
		setupSettings();
		setDrawerListOnclickListener();
	}
	
	/**
	 * Setup settings is used to:
	 * 	- set the toggle to the drawerLayout
	 *  - initialize a new adapter and set it to the drawerList
	 * @param baseContext - base context of activity using this custom slider menu
	 * @param stringArray - content to be displayed on the menu as menu options
	 */
	public void setupSettings()
	{
		drawerLayout.setDrawerListener(drawerToggle);
		adapter = new ArrayAdapter<String>(activity.getBaseContext(), R.layout.drawer_list_item, options);
		drawerList.setAdapter(adapter);
	}
	
	/**
	 * this is used to change the options available in menu
	 * @param stringArray - menu options displayed in slide menu
	 */
	public void editMenuOptions(String[] stringArray, String title)
	{
		adapter = new ArrayAdapter<String>(activity.getBaseContext(), R.layout.drawer_list_item, stringArray);
		activity.getActionBar().setTitle(title);
		drawerList.setAdapter(adapter);
	}
	
	/**
	 * @param fragmentManager - fragmentManager of the activity using this custom slider menu
	 */
	public void setDrawerListOnclickListener()
	{
		// Setting item click listener for the listview mDrawerList
		drawerList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			/**
			 * Get selected item data and safe in fragment
			 * Then commit fragment transaction using fragment manager
			 */
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{	
				String selectedName = adapter.getItem(position);
				
				//------------------------------------------------------------------Main Menu Options:
				if(selectedName.equals("About"))
				{
					Toast.makeText(getActivity().getApplicationContext(),selectedName, Toast.LENGTH_LONG).show();
				}
				//----------------------------------------------------------------All
				/**
				 * Exit leaves closing this app up to the operating system (when device is switched off)
				 * When one uses "Exit" the device's home button is issued. 
				 */
				else if(selectedName.equals("Exit"))
				{
					System.exit(0);
				}
			}
		});
	}
	
	/**
	 * @return the drawerLayout used by menu
	 */
	public DrawerLayout getDrawerlayout()
	{
		return drawerLayout;
	}
	
	/**
	 * @param the drawerLayout to be used by menu
	 */
	public void setDrawerLayout(DrawerLayout drawerLayout)
	{
		this.drawerLayout = drawerLayout;
	}
	
	/**
	 * @return the drawerList used by menu
	 */
	public ListView getDrawerList()
	{
		return drawerList;
	}
	
	/**
	 * Setting reference to the ActionBarDrawerToggle
	 * @param drawerList to be by menu
	 */
	public void setDrawerList(ListView drawerList)
	{
		this.drawerList = drawerList;
	}
	
	/**
	 * @return drawerToggle used by menu
	 */
	public ActionBarDrawerToggle getDrawerToggle()
	{
		return drawerToggle;
	}
	
	/**
	 * Also handles title change as menu opens and closes
	 * @param drawerToggle to be used by menu
	 */
	public void setDrawerToggle()
	{
		this.drawerToggle = new ActionBarDrawerToggle(activity,getDrawerlayout(), R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
		{
			/** 
			 * Called when a drawer is opened 
			 * Changes title to menu
			 */
			public void onDrawerOpened(View drawerView)
			{
				activity.getActionBar().setTitle("Menu");
				activity.invalidateOptionsMenu();
			}
			/** 
			 * Called when drawer is closed 
			 * The title is set back to original title: Smart Door
			 */
			public void onDrawerClosed(View view)
			{
				activity.getActionBar().setTitle(getTitle());
				activity.invalidateOptionsMenu();
			}
		};
		
		activateTopLeftButton();
	}
	
	/**
	 * Needed for menu button top left to respond
	 */
	public void activateTopLeftButton()
	{
		activity.getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/**
	 * @return title in action bar
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * @param title in action bar
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * @param activity using this menu
	 */
	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}
	
	/**
	 * @return activity using this custom slider menu
	 */
	public Activity getActivity()
	{
		return activity;
	}	
	
}
