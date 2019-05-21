package viva.oneplatinum.com.viva.fragments;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.activities.SingleImageActivity;
import viva.oneplatinum.com.viva.adapters.DynamicAskerovAdapter;
import viva.oneplatinum.com.viva.app.VivaApplication;
import viva.oneplatinum.com.viva.dialogs.CustomGalleryFragment;
import viva.oneplatinum.com.viva.dynamicgrid.DynamicGridView;
import viva.oneplatinum.com.viva.utils.DataHolder;

public class GridFragment3 extends ParentFragment {
	int height, width, density, count = 0;
	String[] imagesPath, ids;
	ArrayList<String> selectedImages;
	public static DynamicGridView mDynView;
	public static ImageView delete;
	public static ImageView add;
	public static EditText feedTitle;
	public static DynamicAskerovAdapter mDynAdapter;
	LinearLayout deleteZoneContainer;
	ArrayList<String> imagePathlist, imageIdList;
	String album;
	DataHolder.Dataholder dh;
	VivaApplication app;
	EditText hashentry;
	ImageView hasButton;
	Runnable runnable;
	View draggedView;
	public static TimerTask tt;

	public static ArrayList<String> deleteList = new ArrayList<String>();
	public static Boolean deleteBoolean = false, reOrdered = false,
			backFromS1 = false, backFromS2 = false, backFromCGA = false,
			backFromCPGA = false;
	public static HashMap<String, String> deletedItemList = new HashMap<String, String>();

	public static GridFragment3 fragment;
	public static View noImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		fragment = this;

		dh = DataHolder.Dataholder.getInstance();
		app = (VivaApplication) getActivity().getApplication();

		album = bundle.getString("album");

		WindowManager wm = getActivity().getWindowManager();
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		height = dm.heightPixels;
		width = dm.widthPixels;
		density = dm.densityDpi;

		mDynAdapter = new DynamicAskerovAdapter(getActivity(), dh.ids, 3,
				((int) width / 3));

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		removeNonExistentImages();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.grid_layout3, null);
		noImage = v.findViewById(R.id.noImages);

		feedTitle = (EditText) v.findViewById(R.id.add_name);
		feedTitle.setText(app.getFeedtTitle());
		mDynView = (DynamicGridView) v.findViewById(R.id.dyn_grid);
		delete = (ImageView) v.findViewById(R.id.delete_images);
		add = (ImageView) v.findViewById(R.id.add_images);
		hashentry = (EditText) v.findViewById(R.id.hash_enter);
		hashentry.setVisibility(View.GONE);
		hashentry.setHint("#save #your #hashtags #here");
		hasButton = (ImageView) v.findViewById(R.id.hash_button);
		hasButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (hashentry.getVisibility() == View.GONE) {
					getActivity()
							.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
					hashentry.setText(app.getHashtags());
					hashentry.setVisibility(View.VISIBLE);
					hashentry.requestFocus();
					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(hashentry, 0);
				} else {
					getActivity()
							.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

					hashentry.setVisibility(View.GONE);
					app.putHashTags(hashentry.getText().toString());
					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(hashentry.getWindowToken(), 0);
				}
			}
		});
		hasButton.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (hashentry.getVisibility() == View.VISIBLE) {
					if (hashentry.getText().length() != 0) {
						ClipboardManager mClipBoradManager = (ClipboardManager) getActivity()
								.getSystemService(Service.CLIPBOARD_SERVICE);
						ClipData clip = ClipData.newPlainText(
								"PYGImageCaption", hashentry.getText()
										.toString());
						mClipBoradManager.setPrimaryClip(clip);
						Toast.makeText(getActivity(), "Got it!",
								Toast.LENGTH_LONG).show();
					}
				}
				return false;
			}
		});
		// hashentry.setOnEditorActionListener(new OnEditorActionListener() {
		//
		// @Override
		// public boolean onEditorAction(TextView v, int actionId,
		// KeyEvent event) {
		// // TODO Auto-generated method stub
		// InputMethodManager imm = (InputMethodManager) getActivity()
		// .getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(hashentry.getWindowToken(), 0);
		// hashentry.setVisibility(View.GONE);
		// app.putHashTags(hashentry.getText().toString());
		// hasButton.setImageResource(R.drawable.ic_hash);
		// return false;
		// }
		// });
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (count == 1) {
					// delete all items when count == 1
					dh.ids.clear();
					dh.imagelist.clear();
					dh.noItemsList.clear();
					dh.tempImageItemList.clear();
					mDynAdapter.notifyDataSetChanged();
					app.clearShp();
					return;
				}

				if (deleteBoolean) {
					deleteBoolean = false;
					add.setEnabled(true);
					add.setAlpha((float) 1);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					for (int i = 0; i < deleteList.size(); i++) {
						String deleteItem = deleteList.get(i);
						dh.ids.remove(deleteItem);
						dh.images.remove(deleteItem);
						dh.tempImageItemList.remove(deleteItem);
					}
					deleteList.clear();
					deletedItemList.clear();
					mDynAdapter.notifyDataSetChanged();
					if (dh.ids.size() == 0) {
						app.clearShp();
					} else {
						app.saveAllToShp();
						// app.setImageCount(dh.tempImageItemList.size());
					}
				} else {
					deleteBoolean = true;
					add.setEnabled(false);
					add.setAlpha((float) 0.3);
//					delete.setImageResource(R.drawable.ic_delete_red);
					Toast.makeText(getActivity(), "Select images to delete",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				add.setEnabled(true);
				add.setAlpha((float) 1);
			}
		};
		delete.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (!deleteBoolean) {
					Timer timer = new Timer();
					tt = new TimerTask() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							count = 0;
							getActivity().runOnUiThread(runnable);
						}
					};
					if (count == 0) {
						count = 1;
						Toast.makeText(getActivity(),
								"Press again to delete all images",
								Toast.LENGTH_SHORT).show();
//						delete.setImageResource(R.drawable.ic_delete_red);
						timer.schedule(tt, 3000);
						add.setEnabled(false);
						add.setAlpha((float) 0.3);
					} else {
						tt.cancel();
						add.setEnabled(true);
						add.setAlpha((float) 1);
					}
				}
				return true;
			}
		});
		delete.setOnDragListener(new OnDragListener() {

			@Override
			public boolean onDrag(View v, DragEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED) {
					// draggedView.setVisibility(View.GONE);

				}
				if (event.getAction() == DragEvent.ACTION_DRAG_EXITED) {
					// draggedView.setVisibility(View.VISIBLE);
				}
				if (event.getAction() == DragEvent.ACTION_DROP) {
					Toast.makeText(getActivity(), "Item Dropeed on",
							Toast.LENGTH_LONG).show();
					mDynView.stopEditMode();
				}
				return true;
			}
		});
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub

				Intent intent = new Intent(getActivity(),
						CustomGalleryFragment.class);
				intent.putExtra("GridActivity", true);
				backFromCGA = true;
				dh.feedTitle = feedTitle.getText().toString();
				startActivity(intent);
				getActivity().finish();
			}
		});
		feedTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
				feedTitle.setCursorVisible(true);
			}
		});
		feedTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
//				app.setFeedTitle(s.toString());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		feedTitle.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				feedTitle.setCursorVisible(false);
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(feedTitle.getWindowToken(), 0);
				feedTitle.clearFocus();
				return false;
			}
		});
		mDynView.setAdapter(mDynAdapter);
		mDynView.setVerticalSpacing(4);
		mDynView.setHorizontalSpacing(4);
		mDynView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (count == 0) {
					if (!deleteBoolean) {

						Intent intent = new Intent(getActivity(),
								SingleImageActivity.class);
						intent.putExtra("pos", position);
						startActivity(intent);

					} else {
						ImageView overlay = (ImageView) view
								.findViewById(R.id.overlay_shade);
						// check if the item already exists in the list
						if (!deletedItemList.containsKey(dh.ids.get(position))) {
							// put the item in the delete list
							deleteList.add(dh.ids.get(position));
							deletedItemList.put(dh.ids.get(position), "exists");
							overlay.setVisibility(View.VISIBLE);
						} else {
							deleteList.remove(dh.ids.get(position));
							deletedItemList.remove(dh.ids.get(position));
							overlay.setVisibility(View.INVISIBLE);
						}
					}
				}
			}
		});
		mDynView.setOnDropListener(new DynamicGridView.OnDropListener() {

			@Override
			public void onActionDrop(Rect r, int x, int y, int pos) {
				// TODO Auto-generated method stub
				mDynView.stopEditMode();
				// reset the delete icon to default if not set default
				if (r != null) {
					int l[] = new int[2];
					delete.getLocationInWindow(l);
					int height = delete.getHeight();
					int width = delete.getWidth();
					int dl[] = new int[2];
					mDynView.getLocationInWindow(dl);

					Rect posOfDelete = new Rect(l[0], l[1], l[0] + width, l[1]
							+ height);

					int hL = r.left;
					int hT = r.top;
					int hR = r.right;
					int hB = r.bottom;

					Rect posOfHover = new Rect(hL + dl[0], hT + dl[1], hR
							+ dl[0], hB + dl[1]);

					Log.d("itemDropped",
							"delete__CalculatedREct " + posOfDelete.toString()
									+ " Hover_passedRect " + r.toString()
									+ " Hover_cal_rect " + posOfHover
									+ " gridTop " + dl[1] + " gridLeft "
									+ dl[0] + " delTop " + l[1] + " delLeft "
									+ l[0]);

					if (Rect.intersects(posOfDelete, posOfHover)) {
						dh.tempImageItemList.remove(dh.ids.get(pos));
						dh.images.remove(dh.ids.get(pos));
						dh.ids.remove(pos);
						mDynAdapter.notifyDataSetChanged();
						Toast.makeText(getActivity(), "Image Deleted!",
								Toast.LENGTH_LONG).show();
					}
				}
			}

		});
		mDynView.setOnDragListener(new DynamicGridView.OnDragListener() {

			@Override
			public void onDragStarted(int position) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onDragPositionsChanged(int oldPosition, int newPosition) {
				// TODO Auto-generated method stub
				String temp = dh.ids.get(oldPosition);
				dh.ids.remove(oldPosition);
				dh.ids.add(newPosition, temp);
				reOrdered = true;
				mDynView.stopEditMode();
			}

			@Override
			public void onHoverStarted(Rect r, int x, int y, ImageView im) {
				// TODO Auto-generated method stub
				if (r != null) {
					int l[] = new int[2];
					delete.getLocationInWindow(l);
					int height = delete.getHeight();
					int width = delete.getWidth();
					int dl[] = new int[2];
					mDynView.getLocationInWindow(dl);

					Rect posOfDelete = new Rect(l[0], l[1], l[0] + width, l[1]
							+ height);

					int hL = r.left;
					int hT = r.top;
					int hR = r.right;
					int hB = r.bottom;

					Rect posOfHover = new Rect(hL + dl[0], hT + dl[1], hR
							+ dl[0], hB + dl[1]);

					// Tint the image if the image is hovering over the delete
					// button
					if (Rect.intersects(posOfDelete, posOfHover)) {
//						delete.setImageResource(R.drawable.ic_delete_red);
						im.getDrawable().setColorFilter(
								Color.parseColor("#eb6c6c"),
								PorterDuff.Mode.OVERLAY);
					} else {
						// remove all tints
						im.getDrawable().setColorFilter(null);
					}
				}
			}
		});
		mDynView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (!deleteBoolean) {
					Item item = new Item(dh.ids.get(position));
					String[] des = { ClipDescription.MIMETYPE_TEXT_PLAIN };
					ClipData data = new ClipData(dh.ids.get(position), des,
							item);
					DragShadowBuilder sBuilder = new DragShadowBuilder(
							view);
					view.setTag(data);
					draggedView = view;
					mDynView.startEditMode(position);
					view.setVisibility(View.GONE);
					// view.startDrag(data, sBuilder, null, 0);
				}
				return true;
			}
		});
		return v;
	}

	private void setBitmap(final ImageView iv, final int id) {

		new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... params) {
				Log.d("GETTHUMBNAIL", "id " + id);
				return MediaStore.Images.Thumbnails.getThumbnail(getActivity()
						.getApplicationContext().getContentResolver(), id,
						MediaStore.Images.Thumbnails.MINI_KIND, null);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				if (result == null) {
					Log.e("SETTHUMBNAIL", "nullImageThumbnail");
				}
				iv.setImageBitmap(result);
			}
		}.execute();
	}

	public static void cancelDeleteShade() {
		for (int i = 0; i < deleteList.size(); i++) {
			try {
				View v = mDynView.findViewWithTag(deleteList.get(i));
				ImageView o = (ImageView) v.findViewById(R.id.overlay_shade);
				o.setVisibility(View.GONE);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (!dh.noItemsList.isEmpty()) {
			for (int i = 0; i < dh.noItemsList.size(); i++) {
				dh.tempImageItemList.remove(dh.noItemsList.get(i));
				dh.imagelist.remove(dh.noItemsList.get(i));
				dh.ids.remove(dh.noItemsList.get(i));
			}
			Log.d("noitemlistAtGF3", dh.noItemsList + "");
			dh.noItemsList.clear();
			mDynAdapter.notifyDataSetChanged();
		}
	}

	public void removeNonExistentImages() {
		File f;
		Boolean changed = false;
		for (int i = 0; i < dh.ids.size(); i++) {
			f = new File(dh.tempImageItemList.get(dh.ids.get(i)).imagePath);
			// if
			// (!checkIfImageExists(dataholder.tempImageItemList.get(dataholder.ids.get(i)).imagePath,
			// dataholder.ids.get(i))) {
			// dataholder.noItemsList.add(dataholder.ids.get(i));
			// }
			if (!f.exists()) {
				dh.noItemsList.add(dh.ids.get(i));
				Log.e("noImageAtAdapter",
						dh.tempImageItemList.get(dh.ids.get(i)).imagePath);
			}
		}

		for (int i = 0; i < dh.noItemsList.size(); i++) {
			Log.e("RemovingImageAtAdapter", dh.noItemsList.get(i) + "");
			dh.tempImageItemList.remove(dh.noItemsList.get(i));
			dh.ids.remove(dh.noItemsList.get(i));
			dh.images.remove(dh.noItemsList.get(i));
			changed = true;
		}
		dh.noItemsList.clear();
		Log.e("templist", dh.tempImageItemList.toString());
		Log.e("ids", dh.ids.toString());
		if (changed) {
			app.saveAllToShp();
		}
		mDynAdapter.notifyDataSetChanged();
	}
}
