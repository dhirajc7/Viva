package viva.oneplatinum.com.viva.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.app.VivaApplication;
import viva.oneplatinum.com.viva.fragments.GridFragment3;
import viva.oneplatinum.com.viva.fragments.ImagePagerFragment;
import viva.oneplatinum.com.viva.interfaces.ImagePostListener;
import viva.oneplatinum.com.viva.models.ImageItem;
import viva.oneplatinum.com.viva.utils.DataHolder;

public class SingleImageActivity extends FragmentActivity implements
		ImagePostListener {
	LinearLayout back, time, clipboard, instagram;
	ViewPager preview;
	Typeface proxima;
	LinearLayout done;
	TextView time_text, schedule_text;
//	CustomEditText caption;
	LinearLayout timeLayout, imagePanel;
	ScrollView scroll;

	ClipboardManager mClipBoradManager;
	Boolean datePickerBoolean = true, resetDatepicker = false,
			roughlist = false;
	Dialog datePickerDialog = null;
	ArrayList<String> itemslist, itemsidlist;
	int startPos;
	VivaApplication app;
	ImageItem currentItem;
	ImageView postedIcon, captionBtn;
	int currentPos;
	DataHolder.Dataholder dataholder;
	HashMap<String, ImageItem> roughList = new HashMap<String, ImageItem>();
//	HashMap<String, ImageAlarm> alarmslist = new HashMap<String, ImageAlarm>();
	ArrayList<String> alarmidList = new ArrayList<String>();
	Boolean[] alarms;

	private int eventEndYear, eventEndMonth, eventEndDay, eventEndHour,
			eventEndMin;
	String album;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (dataholder == null) {
			if (app == null) {
				app = (VivaApplication) getApplication();
			}
			app.getAllFromShp();
			createRough();
		}
		super.onResume();
	}

	public void createRough() {
		for (int p = 0; p < dataholder.ids.size(); p++) {
			if (!roughList.containsKey(dataholder.ids.get(p))) {
				ImageItem itemdata1 = new ImageItem(), itemdata2 = new ImageItem();
				itemdata1 = dataholder.tempImageItemList.get(dataholder.ids
						.get(p));
				itemdata2.caption = String.valueOf(itemdata1.caption);
				itemdata2.imageBucket = String.valueOf(itemdata1.imageBucket);
				itemdata2.imageId = String.valueOf(itemdata1.imageId);
				itemdata2.imagePath = String.valueOf(itemdata1.imagePath);
				itemdata2.postDate = String.valueOf(itemdata1.postDate);
				itemdata2.posted = String.valueOf(itemdata1.posted);
				itemdata2.timeMil = Long.valueOf(itemdata1.timeMil);
				itemdata2.reminder = String.valueOf(itemdata1.reminder);
				roughList.put(itemdata2.imageId, itemdata2);
				alarms[p] = false;
				Log.d("postAddingtoR", itemdata2.toString());
				roughlist = true;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_image_layout);
		dataholder = DataHolder.Dataholder.getInstance();

		proxima = Typeface.createFromAsset(getAssets(),
				"proxima-nova-regular.ttf");

		// create a roughlist clone to the imageitemlist
		dataholder.reEntry = false;
		alarms = new Boolean[dataholder.ids.size()];

		createRough();

		// application
		app = (VivaApplication) getApplication();

		this.album = getIntent().getStringExtra("album");
		startPos = getIntent().getIntExtra("pos", 0);

		// keyboard adjust
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		// get the clipboard manager service
		mClipBoradManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

		back = (LinearLayout) findViewById(R.id.back);
		time = (LinearLayout) findViewById(R.id.set_time_image);
		clipboard = (LinearLayout) findViewById(R.id.copy_clipboard);
		instagram = (LinearLayout) findViewById(R.id.post_instagram);
		postedIcon = (ImageView) findViewById(R.id.postedIcon);

		// imagePanel = (LinearLayout) findViewById(R.id.image_panel);
		// scroll = (ScrollView) findViewById(R.id.scroll_view);
		preview = (ViewPager) findViewById(R.id.image_preview);

		schedule_text = (TextView) findViewById(R.id.schedule_text);
		schedule_text.setTypeface(proxima);

		timeLayout = (LinearLayout) findViewById(R.id.schedule_color);
		done = (LinearLayout) findViewById(R.id.done_preview);
//		caption = (CustomEditText) findViewById(R.id.image_caption);
//		caption.setImageView(captionBtn);
		time_text = (TextView) findViewById(R.id.time_text);
		captionBtn.setVisibility(View.GONE);

		preview.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
		preview.setCurrentItem(startPos);
		preview.setOffscreenPageLimit(2);
		// Toast.makeText(
		// this,
		// "scrollHeight " + scroll.getMeasuredHeight() + " panelheight "
		// + timeLayout.getHeight() + imagePanel.getHeight(), 1)
		// .show();
		// preview.setrequiredHeight(scroll.getHeight() - timeLayout.getHeight()
		// - imagePanel.getHeight());
		// preview.invalidate();

		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Done();
			}
		});
		preview.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				currentPos();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		currentPos = preview.getCurrentItem();

		captionBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.hideSoftInputFromWindow(caption.getWindowToken(), 0);
//				caption.clearFocus();
			}
		});
//		caption.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				// TODO Auto-generated method stub
//				currentPos = preview.getCurrentItem();
//				try {
//					roughList.get(dataholder.ids.get(currentPos)).caption = s
//							.toString();
//				} catch (IndexOutOfBoundsException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//			}
//		});

//		caption.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				if (hasFocus && caption.getText().length() > 0) {
//					captionBtn.setVisibility(View.VISIBLE);
//				} else {
//					captionBtn.setVisibility(View.GONE);
//				}
//			}
//		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		clipboard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				if (caption.getText().length() != 0) {
//					ClipData clip = ClipData.newPlainText("PYGImageCaption",
//							caption.getText().toString());
//					mClipBoradManager.setPrimaryClip(clip);
//					Toast.makeText(SingleImageActivity.this, "Got it!",
//							Toast.LENGTH_LONG).show();
//				}
			}
		});
		time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDateTimePicker();
			}
		});

		time.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				final ImageItem i = dataholder.tempImageItemList
						.get(dataholder.ids.get(preview.getCurrentItem()));
				if (i.timeMil > System.currentTimeMillis()) {
					// if the reminder set on the image is in the future
					TextView menuItem = new TextView(SingleImageActivity.this);
					menuItem.setText("Remove");
//					menuItem.setTextColor(R.color.standard_blue);
					menuItem.setGravity(Gravity.CENTER);
					PopupMenu pm = new PopupMenu(SingleImageActivity.this, v,
							Gravity.TOP);
					Menu menu = pm.getMenu();
					menu.addSubMenu("Remove");
					menu.getItem(0).setActionView(menuItem);
					pm.setOnMenuItemClickListener(new OnMenuItemClickListener() {

						@Override
						public boolean onMenuItemClick(MenuItem arg0) {
							// TODO Auto-generated method stub
//							AlarmService as = new AlarmService(
//									SingleImageActivity.this);
//							as.removeAlarm(i.imageId);
							dataholder.tempImageItemList.get(i.imageId).postDate = "";
							dataholder.tempImageItemList.get(i.imageId).posted = "false";
							dataholder.tempImageItemList.get(i.imageId).timeMil = 0;
							dataholder.tempImageItemList.get(i.imageId).reminder = "false";
							roughList.get(i.imageId).postDate = "";
							roughList.get(i.imageId).posted = "false";
							roughList.get(i.imageId).timeMil = 0;
							roughList.get(i.imageId).reminder = "false";
							Log.d("removeAlarm", "item " + i.toString());
							Log.d("removeAlarm",
									"dataholder "
											+ dataholder.tempImageItemList.get(dataholder.ids
											.get(preview
													.getCurrentItem())));
							Toast.makeText(SingleImageActivity.this,
									"Reminder removed", Toast.LENGTH_SHORT)
									.show();
//							alarmslist.remove(i.imageId);
							alarmidList.remove(i.imageId);
							currentPos();
							app.saveAllToShp();
							return true;
						}
					});
					pm.show();
				} else {
					if (!alarms[preview.getCurrentItem()]) {

						if (i.timeMil != 0) {
							TextView menuItem = new TextView(
									SingleImageActivity.this);
							menuItem.setText("Remove");
//							menuItem.setTextColor(R.color.standard_blue);
							menuItem.setGravity(Gravity.CENTER);
							PopupMenu pm = new PopupMenu(
									SingleImageActivity.this, v, Gravity.TOP);
							Menu menu = pm.getMenu();
							menu.addSubMenu("Remove");
							menu.getItem(0).setActionView(menuItem);
							pm.setOnMenuItemClickListener(new OnMenuItemClickListener() {

								@Override
								public boolean onMenuItemClick(MenuItem arg0) {
									// TODO Auto-generated method stub
									dataholder.tempImageItemList.get(i.imageId).postDate = "";
									dataholder.tempImageItemList.get(i.imageId).posted = "false";
									dataholder.tempImageItemList.get(i.imageId).timeMil = 0;
									dataholder.tempImageItemList.get(i.imageId).reminder = "false";
									roughList.get(i.imageId).postDate = "";
									roughList.get(i.imageId).posted = "false";
									roughList.get(i.imageId).timeMil = 0;
									roughList.get(i.imageId).reminder = "false";
									Log.d("removeAlarm", "item " + i.toString());
									Log.d("removeAlarm",
											"dataholder "
													+ dataholder.tempImageItemList.get(dataholder.ids.get(preview
															.getCurrentItem())));
									Toast.makeText(SingleImageActivity.this,
											"Reminder removed",
											Toast.LENGTH_SHORT).show();
									currentPos();
									app.saveAllToShp();
									return true;
								}
							});
							pm.show();

						} else {
							Toast.makeText(SingleImageActivity.this,
									"No Reminder set on this image",
									Toast.LENGTH_SHORT).show();
						}
					}
					// check for alarms if set on the temporary list
					// else {
					// PopupMenu pm = new PopupMenu(SingleImageActivity.this,
					// v, Gravity.RIGHT);
					// TextView menuItem = new TextView(
					// SingleImageActivity.this);
					// menuItem.setText("Remove");
					// menuItem.setTextColor(R.color.standard_blue);
					// menuItem.setGravity(Gravity.CENTER);
					// Menu menu = pm.getMenu();
					// menu.addSubMenu("Remove");
					// pm.setOnMenuItemClickListener(new
					// OnMenuItemClickListener() {
					//
					// @Override
					// public boolean onMenuItemClick(MenuItem arg0) {
					// // TODO Auto-generated method stub
					// roughList.get(i.imageId).postDate = "";
					// roughList.get(i.imageId).posted = "false";
					// roughList.get(i.imageId).timeMil = 0;
					// roughList.get(i.imageId).reminder = "false";
					// Log.d("removeAlarm", "item " + i.toString());
					// Log.d("removeAlarm",
					// "dataholder "
					// +
					// dataholder.tempImageItemList.get(dataholder.ids.get(preview
					// .getCurrentItem())));
					// Toast.makeText(SingleImageActivity.this,
					// "Reminder removed", Toast.LENGTH_SHORT)
					// .show();
					// alarms[preview.getCurrentItem()] = false;
					// alarmslist.remove(i.imageId);
					// alarmidList.remove(i.imageId);
					// currentPos();
					// return true;
					// }
					// });
					// pm.show();
					// }
				}
				return true;
			}
		});
		instagram.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = getPackageManager().getLaunchIntentForPackage(
						"com.instagram.android");
				if (intent != null) {
					Intent shareIntent = new Intent();
					shareIntent.setAction(Intent.ACTION_SEND);
					shareIntent.setPackage("com.instagram.android");
					try {
						shareIntent.putExtra(Intent.EXTRA_STREAM, Uri
								.parse("content://media/external/images/media/"
										+ dataholder.ids.get(preview
												.getCurrentItem())));

						// revert the posted status from not posted
						dataholder.tempImageItemList.get(dataholder.ids
								.get(preview.getCurrentItem())).posted = "true";
						roughList.get(dataholder.ids.get(preview
								.getCurrentItem())).posted = "true";
						Log.d("ImagePostedToInstagram",
								dataholder.tempImageItemList.get(dataholder.ids
										.get(preview.getCurrentItem())).posted);
					} catch (NullPointerException e2) {
						e2.printStackTrace();
					}
					shareIntent.setType("image/jpeg");

					startActivity(shareIntent);
				} else {
					// bring user to the market to download the app.
					// or let them choose an app?
					intent = new Intent(Intent.ACTION_VIEW);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setData(Uri.parse("market://details?id="
							+ "com.instagram.android"));
					startActivity(intent);
				}
			}
		});
//		caption.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				// TODO Auto-generated method stub
////				caption.setError(null);
//				if (s.length() > 0) {
//					captionBtn.setVisibility(View.VISIBLE);
//				} else {
//					captionBtn.setVisibility(View.GONE);
//				}
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//
//			}
//		});
	}

	public void showDateTimePicker() {
		// TODO Auto-generated method stub
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				SingleImageActivity.this);

		final View view = getLayoutInflater().inflate(
				R.layout.date_and_time_picker_layout, null);
		builder.setView(view);

		final DatePicker datePicker = (DatePicker) view
				.findViewById(R.id.date_picker);
		final TimePicker timePicker = (TimePicker) view
				.findViewById(R.id.time_picker);
		final Button ok = (Button) view.findViewById(R.id.datePickerButton);
		final Button remove = (Button) view.findViewById(R.id.datePickerRemove);
		ImageItem item = new ImageItem();
		item = roughList.get(dataholder.ids.get(preview.getCurrentItem()));

		final Calendar mCalendar = Calendar.getInstance();
		final Calendar referenceCalendar = Calendar.getInstance();
		final int year = mCalendar.get(Calendar.YEAR);
		final int month = mCalendar.get(Calendar.MONTH);
		final int day = mCalendar.get(Calendar.DAY_OF_MONTH);
		datePicker.setMinDate(System.currentTimeMillis() - 1000);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				Calendar calendar = Calendar.getInstance();
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int min = calendar.get(Calendar.MINUTE);
				int dayToday = calendar.get(Calendar.DAY_OF_MONTH);
				int yearToday = calendar.get(Calendar.YEAR);
				int monthToday = calendar.get(Calendar.MONTH);
				int month, year, day;
				month = datePicker.getMonth();
				day = datePicker.getDayOfMonth();
				year = datePicker.getYear();
				if (resetDatepicker) {
					if (month == monthToday && year == yearToday
							&& day == dayToday) {
						if (hourOfDay > hour) {
							ok.setEnabled(true);
						} else if (hourOfDay == hour) {
							if (minute > min) {
								ok.setEnabled(true);
							} else {
								ok.setEnabled(false);
							}
						} else {
							ok.setEnabled(false);
						}
					}
				}
			}
		});
		// SEt the datepicker and timepicker to the alarm time if alarm is set
		// for the item
		if (item.timeMil != 0 && !item.posted.equals("true")) {
			mCalendar.setTimeInMillis(item.timeMil);
			datePicker.init(mCalendar.get(Calendar.YEAR),
					mCalendar.get(Calendar.MONTH),
					mCalendar.get(Calendar.DAY_OF_MONTH), null);
			timePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
		} else {
			datePicker.init(referenceCalendar.get(Calendar.YEAR),
					referenceCalendar.get(Calendar.MONTH),
					referenceCalendar.get(Calendar.DAY_OF_MONTH), null);
			timePicker.setCurrentHour(referenceCalendar
					.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(referenceCalendar.get(Calendar.MINUTE));
		}
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String formattedDate = "";
				if (datePickerBoolean) {
					datePicker.setVisibility(View.GONE);
					timePicker.setVisibility(View.VISIBLE);
					datePickerBoolean = false;
					resetDatepicker = true;
				} else {
					datePickerBoolean = true;
					eventEndHour = timePicker.getCurrentHour();
					eventEndMin = timePicker.getCurrentMinute();
					eventEndYear = datePicker.getYear();
					eventEndMonth = datePicker.getMonth();
					eventEndDay = datePicker.getDayOfMonth();
					Calendar calendar = Calendar.getInstance();
					calendar.set(eventEndYear, eventEndMonth, eventEndDay,
							eventEndHour, eventEndMin, 0);
//					formattedDate = GeneralUtil.getFormattedDateTime(calendar
//							.getTime());
					roughList.get(dataholder.ids.get(preview.getCurrentItem())).postDate = formattedDate;
					roughList.get(dataholder.ids.get(preview.getCurrentItem())).timeMil = calendar
							.getTimeInMillis();
					roughList.get(dataholder.ids.get(preview.getCurrentItem())).posted = "false";
					roughList.get(dataholder.ids.get(preview.getCurrentItem())).reminder = "true";

					dataholder.tempImageItemList.get(dataholder.ids.get(preview
							.getCurrentItem())).postDate = formattedDate;
					dataholder.tempImageItemList.get(dataholder.ids.get(preview
							.getCurrentItem())).timeMil = calendar
							.getTimeInMillis();
					dataholder.tempImageItemList.get(dataholder.ids.get(preview
							.getCurrentItem())).posted = "false";
					dataholder.tempImageItemList.get(dataholder.ids.get(preview
							.getCurrentItem())).reminder = "true";

					// set alarms in temp list
					// alarms[preview.getCurrentItem()] = true;

//					ImageAlarm ia = new ImageAlarm();
//					ia.firstTime = calendar.getTimeInMillis();
//					ia.image = dataholder.images.get(dataholder.ids.get(preview
//							.getCurrentItem()));
//					ia.id = dataholder.ids.get(preview.getCurrentItem());
//					ia.time = formattedDate;
//					ia.caption = roughList.get(dataholder.ids.get(preview
//							.getCurrentItem())).caption;
//
//					AlarmService as = new AlarmService(SingleImageActivity.this);
//					as.startAlarm(ia.firstTime, ia.image, ia.id, ia.caption,
//							ia.time);
//
//					// alarmslist.put(ia.id, ia);
//					// alarmidList.add(ia.id);
//					time_text.setText(GeneralUtil.getCustomDate(ia.firstTime));
					app.saveAllToShp();
					currentPos();
					datePickerDialog.dismiss();
				}
			}
		});

		builder.setTitle("Set post date and time");

		datePickerDialog = builder.create();
		datePickerDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				datePickerBoolean = true;
				resetDatepicker = false;
			}
		});
		datePickerDialog.show();

	}

	public class ImagePagerAdapter extends FragmentStatePagerAdapter {
		ArrayList<String> itemList, itemsId;

		public ImagePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			// TODO Auto-generated method stub
			Fragment fragment = new ImagePagerFragment();
			Bundle bundle = new Bundle();
			bundle.putString("id", dataholder.ids.get(pos));
			bundle.putString("image",
					dataholder.images.get(dataholder.ids.get(pos)));

			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dataholder.ids.size();
		}

	}

	@Override
	public void currentPos() {
		// TODO Auto-generated method stub
		if (preview != null) {
			try {
				currentPos = preview.getCurrentItem();
				ImageItem i = new ImageItem();

				i = roughList.get(dataholder.ids.get(currentPos));

				ImageItem i2 = dataholder.tempImageItemList.get(dataholder.ids
						.get(currentPos));
				Log.d("IMageItemATCurrentPos " + currentPos, i.toString());
				if (i.postDate.length() != 0) {
//					time_text.setText(GeneralUtil.getCustomDate(i.timeMil));
//					schedule_text.setText(GeneralUtil.getCustomDate(i.timeMil));
					Log.d("POSTEDtimemil", i.timeMil + "");

				} else {
					time_text.setText("Set reminder");
					schedule_text.setText("UNSCHEDULED");
//					postedIcon.setImageResource(R.drawable.ic_bigclock);
				}
				if (i2.timeMil != 0) {// the alarm HAS BEEN SET for this image
										// at
										// least once
					if (i2.timeMil < System.currentTimeMillis()
							&& i.posted.equals("false")) {
//						postedIcon.setImageResource(R.drawable.ic_schedule);
//						timeLayout.setBackgroundColor(getResources().getColor(
//								R.color.standard_red));

					} else {
//						postedIcon.setImageResource(R.drawable.ic_bigclock);
//						timeLayout.setBackgroundColor(getResources().getColor(
//								R.color.standard_blue));
					}
				} else {
					// alarm has never been set on this image
//					postedIcon.setImageResource(R.drawable.ic_bigclock);
//					timeLayout.setBackgroundColor(getResources().getColor(
//							R.color.standard_grey));
				}
				if (i.caption.length() != 0) {
//					caption.setText(i.caption);
				} else {
//					caption.setText("");
				}
			} catch (IndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				finish();
			}

		}
	}

	public void Done() {
		// AlarmService as = new AlarmService(SingleImageActivity.this);
		// for (int i = 0; i < alarmslist.size(); i++) {
		// ImageAlarm ia = alarmslist.get(alarmidList.get(i));
		//
		// as.startAlarm(ia.firstTime, ia.image, ia.id, ia.caption, ia.time);
		// Log.d("alarmsSetForImages", ia.toString());
		// Log.d("alarmsetOnImage", roughList.get(ia.id).toString());
		// }
		dataholder.tempImageItemList = roughList;
		app.saveAllToShp();
		GridFragment3.backFromS1 = true;
		dataholder.reEntry = true;
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		GridFragment3.backFromS1 = true;
		dataholder.reEntry = true;
		if (!dataholder.noItemsList.isEmpty()) {
			for (int i = 0; i < dataholder.noItemsList.size(); i++) {
				dataholder.tempImageItemList.remove(dataholder.noItemsList
						.get(i));
				dataholder.ids.remove(dataholder.noItemsList.get(i));
				dataholder.images.remove(dataholder.noItemsList.get(i));
			}
		}
		super.onBackPressed();
	}
}
