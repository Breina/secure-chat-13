package alpha.android.contacts;

import java.util.ArrayList;

import alpha.android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactsAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<Contact> contacts;
	
	public ContactsAdapter(Context context) {
		this.context = context;
		contacts = new ArrayList<Contact>();
	}

	@Override
	public int getCount()
	{
		return contacts.size();
	}
	
	public void addContact(Contact contact) {
		contacts.add(contact);
	}
	
	public void removeContact(Contact contract) {
		contacts.remove(contract);
	}

	@Override
	public Object getItem(int position)
	{
		return contacts.get(position);
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		Contact contact = (Contact) getItem(position);

		ViewHolder holder;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_contacts_row, parent, false);
			
			holder.name = (TextView) convertView.findViewById(R.id.firstLine);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();

		holder.name.setText(contact.getName());
		
		return convertView;
		

		/*
		 * LayoutInflater inflater = getLayoutInflater(); View row; row =
		 * inflater.inflate(R.layout.custom, parent, false); TextView title,
		 * detail; ImageView i1; title = (TextView)
		 * row.findViewById(R.id.title); detail = (TextView)
		 * row.findViewById(R.id.detail);
		 * i1=(ImageView)row.findViewById(R.id.img);
		 * title.setText(Title[position]); detail.setText(Detail[position]);
		 * i1.setImageResource(imge[position]);
		 * 
		 * return (row);
		 */
	}

	private static class ViewHolder
	{
		TextView name;
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}
}
