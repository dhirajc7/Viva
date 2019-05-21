package viva.oneplatinum.com.viva.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.models.Notification_Image;

public class Hori_ListAdapter extends ArrayAdapter<Notification_Image> {
	ArrayList<Notification_Image> event;
	private Context context;

	public Hori_ListAdapter(Context context, ArrayList<Notification_Image> event) {
		super(context, R.layout.list_imageview);
		// TODO Auto-generated constructor stub
		event = new ArrayList<Notification_Image>();
		this.event = event;
		this.context = context;
	}



	private class ViewHolder {

		public ImageView Image;

	}

	@Override
	public int getCount() {
		return 10;
	}

	@Override
	public Notification_Image getItem(int position) {
		return event.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		// TODO Auto-generated method stub
		if (position == getCount() - 2) {
			notifyDataSetChanged();
		}
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(this.context);
			convertView = inflater.inflate(R.layout.list_imageview, null);
			viewHolder = new ViewHolder();

			viewHolder.Image = (ImageView) convertView.findViewById(R.id.image);

			convertView.setTag(viewHolder);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}

		return convertView;

	}
}
