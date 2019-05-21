package viva.oneplatinum.com.viva.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.app.VivaApplication;
import viva.oneplatinum.com.viva.dynamicgrid.BaseDynamicGridAdapter;
import viva.oneplatinum.com.viva.fragments.GridFragment3;
import viva.oneplatinum.com.viva.models.ImageItem;
import viva.oneplatinum.com.viva.utils.DataHolder;

public class DynamicAskerovAdapter extends BaseDynamicGridAdapter {
	Picasso mPicasso;
	DataHolder.Dataholder dataholder;
	Context context;
	int width;
	int l;
	VivaApplication app;

	public DynamicAskerovAdapter(Context context, ArrayList<String> items,
								 int columnCount, int width) {
		super(context, items, columnCount);
		// TODO Auto-generated constructor stub
		app = (VivaApplication) context.getApplicationContext();
		dataholder = DataHolder.Dataholder.getInstance();
		this.context = context;
		this.width = width;
		mPicasso = Picasso.with(context);
		l = dpToPx(2);
		removeNonExistentImages();
	}

	public void removeNonExistentImages() {
		File f;
		Boolean changed = false;
		for (int i = 0; i < dataholder.ids.size(); i++) {
			f = new File(
					dataholder.tempImageItemList.get(dataholder.ids.get(i)).imagePath);
			// if
			// (!checkIfImageExists(dataholder.tempImageItemList.get(dataholder.ids.get(i)).imagePath,
			// dataholder.ids.get(i))) {
			// dataholder.noItemsList.add(dataholder.ids.get(i));
			// }
			if (!f.exists()) {
				dataholder.noItemsList.add(dataholder.ids.get(i));
				Log.e("noImageAtAdapter",
						dataholder.tempImageItemList.get(dataholder.ids.get(i)).imagePath);
			}
		}

		for (int i = 0; i < dataholder.noItemsList.size(); i++) {
			Log.e("RemovingImageAtAdapter", dataholder.noItemsList.get(i) + "");
			dataholder.tempImageItemList.remove(dataholder.noItemsList.get(i));
			dataholder.ids.remove(dataholder.noItemsList.get(i));
			dataholder.images.remove(dataholder.noItemsList.get(i));
			changed = true;
		}
		dataholder.noItemsList.clear();
		Log.e("templist", dataholder.tempImageItemList.toString());
		Log.e("ids", dataholder.ids.toString());
		if (changed) {
			app.saveAllToShp();
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.d("atGridAdapter", dataholder.ids.size() + "");
		if (dataholder.ids.size() == 0) {
			GridFragment3.noImage.setVisibility(View.VISIBLE);
		} else {
			GridFragment3.noImage.setVisibility(View.GONE);
		}
		return dataholder.ids.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;

		FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(
				width - 3, width - 1);
		holder = new ViewHolder();
		convertView = LayoutInflater.from(context).inflate(
				R.layout.custom_gallery_item3, null);
		holder.image = (ImageView) convertView.findViewById(R.id.image);
		holder.overlay = (ImageView) convertView
				.findViewById(R.id.overlay_shade);
		holder.image.setLayoutParams(lParams);
		holder.overlay.setLayoutParams(lParams);
		holder.overlay.setAlpha((float) 0.4);
		holder.unschedulee = (ImageView) convertView
				.findViewById(R.id.image_unschedulee);
		holder.posted = (ImageView) convertView.findViewById(R.id.notposted);

		mPicasso.load(
				"file:"
						+ dataholder.tempImageItemList.get(dataholder.ids
								.get(position)).imagePath).resize(width, width)
				.centerCrop().into(holder.image);
		holder.image.setBackgroundColor(Color.parseColor("white"));
		ImageItem item = dataholder.tempImageItemList.get(dataholder.ids
				.get(position));

		if (!item.postDate.equals("")) {
			holder.unschedulee.setVisibility(View.GONE);
			long timeNow = System.currentTimeMillis();
			long setAlarmTime = item.timeMil;
			Log.d("postTimeForImage", setAlarmTime + " timeNow" + timeNow);
			Log.d("postTimeForImage1",
					setAlarmTime
							+ " timeNow"
							+ timeNow
							+ "  "
							+ dataholder.tempImageItemList.get(dataholder.ids
									.get(position)).posted);
			Log.d("POSTTIMEcomparison", (timeNow > setAlarmTime) + "");
			if (timeNow > setAlarmTime) {
				if (dataholder.tempImageItemList.get(dataholder.ids
						.get(position)).posted.equalsIgnoreCase("false")) {
					holder.posted.setVisibility(View.VISIBLE);
					holder.unschedulee.setVisibility(View.GONE);
					Log.d("postTimeForImage2",
							setAlarmTime
									+ " timeNow"
									+ timeNow
									+ "  "
									+ dataholder.tempImageItemList
											.get(dataholder.ids.get(position)).posted);

				}
			} else {
//				holder.unschedulee.setVisibility(View.VISIBLE);
//				holder.unschedulee.setImageResource(R.drawable.ic_scheduled);
			}
		} else {
//			holder.unschedulee.setVisibility(View.VISIBLE);
//			holder.unschedulee.setImageResource(R.drawable.ic_unscheduled);
		}
		if (item.posted.equals("true")) {
			// hide the unscheduled/unposted icon
			holder.unschedulee.setVisibility(View.GONE);
		}
		Log.d("path", dataholder.images.get(dataholder.ids.get(position)));
		Log.d("path_id", dataholder.ids.get(position) + "");
		Log.v("POSTEDORNOT",
				dataholder.tempImageItemList.get(dataholder.ids.get(position)).posted
						+ "");
		// setBitmap(holder.image,
		// Integer.parseInt(dataholder.ids.get(position)));
		// if
		// (dataholder.tempImageItemList.get(dataholder.ids.get(position)).posted
		// .equalsIgnoreCase("false")) {
		// holder.posted.setVisibility(View.VISIBLE);
		// }
		if (GridFragment3.deletedItemList.containsKey(dataholder.ids
				.get(position))) {
			holder.overlay.setVisibility(View.VISIBLE);
		} else {
			holder.overlay.setVisibility(View.INVISIBLE);
		}
		convertView.setTag(holder);
		convertView.setTag(dataholder.ids.get(position));
		// if (!checkIfImageExists(
		// dataholder.tempImageItemList.get(dataholder.ids.get(position)).imagePath,
		// dataholder.ids.get(position))) {
		// dataholder.noItemsList.add(dataholder.ids.get(position));
		// }
		return convertView;
	}

	public class ViewHolder {
		ImageView image, overlay, posted, unschedulee;

	}

	Boolean s = true;

	private Boolean checkIfImageExists(final String path, final String id) {
		new AsyncTask<Void, Void, Boolean>() {
			Boolean status = true;

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					// TODO Auto-generated method stub
					Bitmap bitmap = BitmapFactory.decodeFile(path);
					int h = bitmap.getHeight();
				} catch (NullPointerException e) {
					// TODO: handle exception
					Log.e("NPEonImageinGallery", "Image does not exist at :"
							+ path);

					status = false;
				}
				return status;
			}

			protected void onPostExecute(Boolean result) {
				s = result;
			}

		}.execute();
		return s;
	}

	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics = getContext().getResources()
				.getDisplayMetrics();
		int px = Math.round(dp
				* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}
}
