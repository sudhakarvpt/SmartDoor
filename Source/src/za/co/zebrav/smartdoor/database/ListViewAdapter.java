package za.co.zebrav.smartdoor.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import za.co.zebrav.smartdoor.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter
{
	private Context mContext;
	private List<User> useList = null;
	private ArrayList<User> arraylist;
	private LayoutInflater inflater;
	private UserProvider provider;
	private AlertDialog.Builder alert;
	
	/**
	 * Constructor
	 * @param context
	 * @param userList
	 */
	public ListViewAdapter(Context context, List<User> userList)
	{
		mContext = context;
		alert  = new AlertDialog.Builder(mContext);
		this.useList = userList;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = new ArrayList<User>();
		this.arraylist.addAll(userList);
		
		provider = new UserProvider(mContext);
	}
	
	/**
	 * Every GUI component you want to show  in a single list view item must be contained by this class
	 * then found in the search_user_listview_item layout
	 */
	public class ViewHolder
	{
		TextView firstName;
		TextView surname;
		TextView username;
	}
	
	/**
	 * Must be present, because of extends BaseAdapter
	 */
	@Override
	public int getCount()
	{
		return useList.size();
	}

	/**
	 * Must be present, because of extends BaseAdapter
	 */
	@Override
	public User getItem(int position)
	{
		return useList.get(position);
	}
	
	/**
	 * Must be present, because of extends BaseAdapter
	 */
	@Override
	public long getItemId(int pos)
	{
		return pos;
	}

	/**
	 * Here each list view item's view is set.
	 */
	@Override
	public View getView(final int position, View view, ViewGroup parent)
	{
		final ViewHolder holder;
		if (view == null)
		{
			holder= new ViewHolder();
			view = inflater.inflate(R.layout.search_user_listview_item, null);
			holder.firstName = (TextView) view.findViewById(R.id.firstnamesTV);
			holder.surname = (TextView) view.findViewById(R.id.surnamesTV);
			holder.username = (TextView) view.findViewById(R.id.usernameTV);
			view.setTag(holder);
		} else
		{
			holder = (ViewHolder) view.getTag();
		}
		
		// Set the results into TextViews
		holder.firstName.setText(useList.get(position).getFirstnames());
		holder.surname.setText(useList.get(position).getSurname());
		holder.username.setText(useList.get(position).getUsername());
	
		view.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				//alert if user wants to delete
				deleteAlert(useList.get(position), position);
			}
		});
			
		return view;
	}
	
	/**
	 * An dialogue box pops up, prompting the user if he is sure he wants to delete the selected user.
	 */
	private void deleteAlert(final User user, final int position)
	{
		alert.setTitle("Delete user");
		alert.setNegativeButton("Cancel",null);
		alert.setPositiveButton("Delete", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				provider.deleteUser(user);
				//update list
				useList.remove(position);
				notifyDataSetChanged();
			}
		});
		
		alert.show();
	}
	
	//--------------------------------------------------------------------------Search functionality
	/**
	 * Filter the listView via first names, surnames or even usernames
	 * @param charText
	 */
	public void filter(String charText)
	{
		charText = charText.toLowerCase(Locale.getDefault());
		useList.clear();
		if (charText.length() == 0)
		{
			useList.addAll(arraylist);
		} else
		{
			for (User us : arraylist)
			{
				if (us.getFirstnames().toLowerCase(Locale.getDefault()).contains(charText)
						|| us.getSurname().toLowerCase(Locale.getDefault()).contains(charText) 
						|| us.getUsername().toLowerCase(Locale.getDefault()).contains(charText))
				{
					useList.add(us);
				}
			}
		}
		notifyDataSetChanged();
	}
}