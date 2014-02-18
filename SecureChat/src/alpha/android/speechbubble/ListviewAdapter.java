package alpha.android.speechbubble;

import java.util.ArrayList;

import alpha.android.R;
import alpha.android.fragments.MapFragment;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class ListviewAdapter extends BaseAdapter {
	private Context appContext;
	private ArrayList<Message> mMessages;

	public ListviewAdapter(Context context, ArrayList<Message> messages) {
		super();
		this.appContext = context;
		this.mMessages = messages;
	}

	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessages.get(position);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Message message = (Message) this.getItem(position);

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(appContext).inflate(
					R.layout.list_chat_row, parent, false);
			holder.message = (TextView) convertView
					.findViewById(R.id.message_text);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		// Set the message as text in the Chat Bubble
		if (message.getMessage() instanceof SpannableString) {
			holder.message.setText((SpannableString) message.getMessage());
			holder.message.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SpannableString imageMessage = (SpannableString) message
							.getMessage();
					String fileName = imageMessage.toString();

					Toast.makeText(appContext, fileName, Toast.LENGTH_LONG)
							.show();

					final Dialog nagDialog = new Dialog(
							appContext,
							android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
					nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					nagDialog.setCancelable(false);
					nagDialog.setContentView(R.layout.preview_image);
					Button btnClose = (Button) nagDialog
							.findViewById(R.id.btnIvClose);
					ImageView ivPreview = (ImageView) nagDialog
							.findViewById(R.id.iv_preview_image);
					ivPreview.setImageBitmap(BitmapFactory
							.decodeFile(appContext.getFilesDir() + "/"
									+ "LAST_SAVED_IMAGE.png"));

					btnClose.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							nagDialog.dismiss();
						}
					});

					nagDialog.show();
				}
			});
		} else
			holder.message.setText(message.getMessage().toString());

		LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();

		if (message.isLocation()) {

			if (message.isMine()) {
				holder.message.setBackgroundResource(R.drawable.location_right);
				lp.gravity = Gravity.RIGHT;
			} else {
				holder.message.setBackgroundResource(R.drawable.location_left);
				lp.gravity = Gravity.LEFT;
			}
			
			holder.message.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					addMarker((String) message.getMessage(), message.getPos());
					
//					TextView tv = (TextView) v;
//					Log.d(CommonUtilities.TAG, tv.getText().toString());
					
				}
			});

			// Check whether message is mine to show green background and align
			// to right
		} else if (message.isMine()) {
			holder.message
					.setBackgroundResource(R.drawable.speech_bubble_green);
			lp.gravity = Gravity.RIGHT;
		}
		// If not mine then it is from sender to show orange background and
		// align to left
		else {
			holder.message
					.setBackgroundResource(R.drawable.speech_bubble_orange);
			lp.gravity = Gravity.LEFT;
		}
		holder.message.setLayoutParams(lp);
		holder.message.setTextColor(R.color.textColor);

		return convertView;
	}

	private static class ViewHolder {
		TextView message;
	}

	@Override
	public long getItemId(int position) {
		// Unimplemented, because we aren't using Sqlite.
		return 0;
	}
	
	private void addMarker(String title, LatLng pos) {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
		Editor editor = prefs.edit();
		
		int size = prefs.getInt(MapFragment.SIZEKEY, 0);
		
		editor.putLong(MapFragment.MARKERPREFIX + "lat_" + size,
				Double.doubleToLongBits(pos.latitude));
		editor.putLong(MapFragment.MARKERPREFIX + "lng_" + size,
				Double.doubleToLongBits(pos.longitude));
		editor.putString(MapFragment.MARKERPREFIX + "title_" + size, title);
		size++;
		
		editor.putInt(MapFragment.SIZEKEY, size);
		
		editor.commit();
		
		Toast.makeText(appContext, "Marker added to map", Toast.LENGTH_SHORT).show();		
		
	}

}
