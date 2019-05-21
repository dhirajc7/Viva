package viva.oneplatinum.com.viva.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.models.CategoryInfo;
import viva.oneplatinum.com.viva.models.SubCategory;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;
import viva.oneplatinum.com.viva.widgets.RobotoLightEditText;
import viva.oneplatinum.com.viva.widgets.RobotoLightTextView;
import viva.oneplatinum.com.viva.widgets.RobotoRegularEditText;

/**
 * Created by D-MAX on 3/23/2016.
 */
public class CreateEvent3 extends ParentActivity {

    FontIcon backBtnToolbar, nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    Toolbar mToolBar;
    AutoCompleteTextView location;
    RobotoLightTextView start_Date_Time, end_Date_Time;
    RobotoLightEditText eventDescription,  eventPhone, eventPrice;
    AutoCompleteTextView au_eventLocation;
    RobotoRegularEditText event_Au_email;
    ArrayAdapter<String> placeslistAdapter;
    String PlaceArray[] = {};
    ArrayList<String> places;
    String eventStartYear;
    String eventStartMonth;
    String eventStartDay;
    String eventStartHour;
    String eventStartMin;
    String eventEndYear;
    String eventEndMonth;
    String eventEndDay;
    String eventEndHour;
    String eventEndMin;
    Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance(), c = Calendar.getInstance();
    Boolean datePickerBoolean = true;
    AlertDialog datePickerDialog;
    SharedPreferences eventInfo;
    JSONArray image = new JSONArray();
    JSONArray categories = new JSONArray();
    StartFromScratch mStartFromScratch;
    ArrayList<CategoryInfo> categoryInfoArrayList = new ArrayList<CategoryInfo>();
    String eventName, circularImage, squareImage1, squareImage2, squareImage3, squareImage4, eventType, type1, type2, type3, type4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event3);
        initToolBar();
        initViews();
        mStartFromScratch = (StartFromScratch) StartFromScratch.mActivity;
        eventName = getIntent().getStringExtra("eventName");
        String categoriesData = getIntent().getStringExtra("categoriesData");
        circularImage = getIntent().getStringExtra("circularImage");
        squareImage1 = getIntent().getStringExtra("squareImage1");
        squareImage2 = getIntent().getStringExtra("squareImage2");
        squareImage3 = getIntent().getStringExtra("squareImage3");
        squareImage4 = getIntent().getStringExtra("squareImage4");
        eventType = getIntent().getStringExtra("profileType");
        type1 = getIntent().getStringExtra("type1");
//        Log.d("type",type1.toString());
        type2 = getIntent().getStringExtra("type2");
//        Log.d("type1",type2.toString());
        type3 = getIntent().getStringExtra("type3");
//        Log.d("type2",type3.toString());
        type4 = getIntent().getStringExtra("type4");
//        Log.d("type3",type4.toString());
        try {
            JSONObject categoriesObject = new JSONObject(categoriesData);
            categoryInfoArrayList = ParseUtility.parseCategoryDetail(categoriesObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        places = new ArrayList<String>();
        placeslistAdapter = new ArrayAdapter<String>(CreateEvent3.this, android.R.layout.simple_list_item_1, PlaceArray);
        au_eventLocation.setAdapter(placeslistAdapter);
        au_eventLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                // SearchUserByAddress();
                // call the google api
                // Toast.makeText(getActivity(), "supported encoding" + arg0,
                // Toast.LENGTH_SHORT).show();
                String url = DataHolder.Google_Places;
                try {
                    String searchCode = url + URLEncoder.encode(arg0.toString(), "utf8");
                    Log.d("places", "" + searchCode);
                    AQuery aq = new AQuery(CreateEvent3.this);
                    aq.ajax(searchCode, JSONObject.class, getplaceslistcallback());
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    // Toast.makeText(getActivity(), "unsupported encoding",
                    // Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
        start_Date_Time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                hideSoftKeyboard();
                c1 = showDateTimePickerStart(start_Date_Time);

            }
        });
        end_Date_Time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                hideSoftKeyboard();
                c2 = showDateTimePickerEnd(end_Date_Time, c1);

            }
        });
    }

    // callback for the places list
    private AjaxCallback<JSONObject> getplaceslistcallback() {
        return new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                super.callback(url, object, status);
                Log.d("PlaceCallback", object + "");
                if (object != null) {
                    try {
                        JSONArray predictions = object.getJSONArray("predictions");
                        // get the place objects
                        // Toast.makeText(getActivity(), predictions.toString(),
                        // 1)
                        // .show();
                        places.clear();
                        for (int i = 0; i < predictions.length(); i++) {
                            JSONObject place = predictions.getJSONObject(i);
                            String description = place.getString("description");
                            places.add(description);
                            Log.d("place from google", description);
                        }

                        String[] stringArray = places.toArray(new String[places.size()]);
                        PlaceArray = new String[places.size()];
                        for (int j = 0; j < places.size(); j++) {
                            PlaceArray[j] = places.get(j);
                            Log.d("placearray items",
                                    places.get(j) + "  " + PlaceArray[j] + " ," + stringArray.toString());
                        }
                        PlaceArray = stringArray.clone();
//                        Log.d("list of places by address", PlaceArray.length + "");
                        placeslistAdapter = new ArrayAdapter<String>(CreateEvent3.this, android.R.layout.simple_list_item_1,
                                PlaceArray);
                        au_eventLocation.setAdapter(placeslistAdapter);
                        placeslistAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // showDataView();
                }
            }
        };
    }

    ;

    private void initViews() {

        start_Date_Time = (RobotoLightTextView) findViewById(R.id.start_Date_Time);
        end_Date_Time = (RobotoLightTextView) findViewById(R.id.end_Date_Time);
        eventPhone = (RobotoLightEditText) findViewById(R.id.eventPhone);
        eventPrice = (RobotoLightEditText) findViewById(R.id.eventPrice);
        eventDescription = (RobotoLightEditText) findViewById(R.id.eventDescription);
        event_Au_email = (RobotoRegularEditText) findViewById(R.id.event_Au_email);
        au_eventLocation = (AutoCompleteTextView) findViewById(R.id.au_eventLocation);


    }

    private void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        mToolBar.addView(getCustomView());
    }

    private View getCustomView() {

        LayoutInflater linflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customView = linflater.inflate(
                R.layout.toolbar_item, null);
        Toolbar.LayoutParams layoutParam = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        customView.setLayoutParams(layoutParam);
        backBtnToolbar = (FontIcon) customView.findViewById(R.id.backBtnToolbar);
        titleToolbar = (RobotoBoldTextView) customView.findViewById(R.id.titleToolbar);
        nextBtnToolbar = (FontIcon) customView.findViewById(R.id.nextBtnToolbar);

        titleToolbar.setText("CREATE EVENT");
        nextBtnToolbar.setText("s");
        nextBtnToolbar.setTextColor(getResources().getColor(R.color.Blue));

        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        backBtnToolbar.setText("o");
        backBtnToolbar.setTextColor(getResources().getColor(R.color.Blue));
        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {               finish();
            }
        });
        nextBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
//                    try {
//                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                    } catch (Exception e) {
//
//                    }
                    postEvent();
                } else {
                    // do nothing
                }

            }
        });


        return customView;
    }


    public Calendar showDateTimePickerStart(final TextView h) {
        // TODO Auto-generated method stub
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateEvent3.this);

        final View view = getLayoutInflater().inflate(R.layout.date_and_time_picker_layout, null);
        builder.setView(view);
        final Calendar startDate = Calendar.getInstance();
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        datePicker.setMinDate(c.getTimeInMillis());

        datePicker.updateDate(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
        timePicker.setCurrentHour(c1.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c1.get(Calendar.MINUTE));
        final Button ok = (Button) view.findViewById(R.id.datePickerButton);
        final Button remove = (Button) view.findViewById(R.id.datePickerRemove);
        timePicker.setIs24HourView(true);
        // datePicker.setMinDate(System.currentTimeMillis());

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (datePickerBoolean) {
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    datePickerBoolean = false;
                } else {
                    datePickerBoolean = true;
                    int eventStartHour = timePicker.getCurrentHour();
                    int eventStartMin = timePicker.getCurrentMinute();
                    int eventStartYear = datePicker.getYear();
                    int eventStartMonth = datePicker.getMonth();
                    int eventStartDay = datePicker.getDayOfMonth();

                    c1.set(eventStartYear, eventStartMonth, eventStartDay, eventStartHour, eventStartMin, 0);
                    String convertedDate = GeneralUtil.convertDate(eventStartYear + "-" + (eventStartMonth + 1) + "-" + eventStartDay + "   " + eventStartHour
                            + ":" + eventStartMin + ":00");
//					h.setText(eventStartYear + "-" + eventStartMonth + "-" + eventStartDay + "   " + eventStartHour
//							+ ":" + eventStartMin);
                    h.setText(convertedDate);
                    datePickerDialog.dismiss();

                }
            }
        });

        builder.setTitle("Set Event date and time");

        datePickerDialog = builder.create();
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {


            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                datePickerBoolean = true;
            }
        });
        datePickerDialog.show();
        return startDate;
    }

    public Calendar showDateTimePickerEnd(final TextView h, final Calendar cL) {
        // TODO Auto-generated method stub
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreateEvent3.this);

        final View view = getLayoutInflater().inflate(R.layout.date_and_time_picker_layout, null);
        builder.setView(view);
        final Calendar startDate = Calendar.getInstance();
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        datePicker.setMinDate(c.getTimeInMillis());
        datePicker.updateDate(c2.get(Calendar.YEAR), (c2.get(Calendar.MONTH)), c2.get(Calendar.DAY_OF_MONTH));
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
        timePicker.setCurrentHour(c2.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c2.get(Calendar.MINUTE));
        final Button ok = (Button) view.findViewById(R.id.datePickerButton);
        final Button remove = (Button) view.findViewById(R.id.datePickerRemove);
        timePicker.setIs24HourView(true);
        // datePicker.setMinDate(System.currentTimeMillis());

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (datePickerBoolean) {
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    datePickerBoolean = false;
                } else {
                    datePickerBoolean = true;
                    int eventStartHour = timePicker.getCurrentHour();
                    int eventStartMin = timePicker.getCurrentMinute();
                    int eventStartYear = datePicker.getYear();
                    int eventStartMonth = datePicker.getMonth();
                    int eventStartDay = datePicker.getDayOfMonth();

                    c2.set(eventStartYear, eventStartMonth, eventStartDay, eventStartHour, eventStartMin, 0);

                    if (cL.after(c2)) {
                        new AlertDialog.Builder(CreateEvent3.this).setTitle("Confirmation")
                                .setMessage("Please Enter Valid End Date..").setNeutralButton("Back", null).show();

                    } else {
                        String converDate = GeneralUtil.convertDate(eventStartYear + "-" + (eventStartMonth + 1) + "-" + eventStartDay + "   " + eventStartHour
                                + ":" + eventStartMin + ":00");
//						h.setText(eventStartYear + "-" + eventStartMonth + "-" + eventStartDay + "   " + eventStartHour
//								+ ":" + eventStartMin);
                        h.setText(converDate);
                    }

                    datePickerDialog.dismiss();

                }
            }
        });

        builder.setTitle("Set Event date and time");

        datePickerDialog = builder.create();
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                datePickerBoolean = true;
            }
        });
        datePickerDialog.show();
        return startDate;
    }

    public Boolean validation() {
        Boolean val = true;
        if (eventDescription.getText().length() == 0) {
            val = false;
            eventDescription.setError("This field is required!");
            eventDescription.requestFocus();
        }
        if (au_eventLocation.getText().length() == 0) {
            val = false;
            au_eventLocation.setError("This field is required!");
            au_eventLocation.requestFocus();
        }
        if (eventPhone.getText().length() == 0) {
            val = false;
            eventPhone.setError("This field is required!");
            eventPhone.requestFocus();
        }
        if (eventPrice.getText().length() == 0) {
            val = false;
            eventPrice.setError("This field is required!");
            eventPrice.requestFocus();
        }
        if (start_Date_Time.getText().length() == 0) {
            val = false;
            start_Date_Time.setError("This field is required!");
            start_Date_Time.requestFocus();
        }
        if (end_Date_Time.getText().length() == 0) {
            val = false;
            end_Date_Time.setError("This field is required!");
            end_Date_Time.requestFocus();
        }
        if (event_Au_email.getText().length() == 0) {
            val = false;
            event_Au_email.setError("This field is required!");
            event_Au_email.requestFocus();
        }
        else if (!GeneralUtil.isValidUrl(event_Au_email.getText().toString().trim())) {
            val = false;
            event_Au_email.setError("Please enter valid website.");
            event_Au_email.requestFocus();
        }

        return val;
    }

    private void postEvent() {
        AQuery aquery = new AQuery(this);
        if (GeneralUtil.isNetworkAvailable(this)) {
            showLoadingDialog();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                if (eventType.equals("public")) {
                    mainJson.put("type_id", 1);
                } else {
                    mainJson.put("type_id", 2);
                }
                mainJson.put("token",  mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("name", eventName);
                mainJson.put("description", eventDescription.getText().toString());
                Log.d("eventDescrition", eventDescription.getText().toString());
                mainJson.put("location", au_eventLocation.getText().toString());
                mainJson.put("website", event_Au_email.getText().toString());
                mainJson.put("phone", eventPhone.getText().toString());
                mainJson.put("price", eventPrice.getText().toString());
                if (circularImage != null) {
                    image = image.put(circularImage);

                }
                if (squareImage1 != null) {
                    image = image.put(squareImage1);

                }
                if (squareImage2 != null) {
                    image = image.put(squareImage2);

                }
                if (squareImage3 != null) {
                    image = image.put(squareImage3);

                }
                if (squareImage4 != null) {
                    image = image.put(squareImage4);

                }

                if(type1!= null) {
                    for (int i = 0; i < categoryInfoArrayList.size(); i++) {
                        ArrayList<SubCategory> subCategories = new ArrayList<SubCategory>();
                        subCategories = categoryInfoArrayList.get(i).subCategoriesList;
                        for (int j = 0; j < subCategories.size(); j++) {
                            if (type1.equals(subCategories.get(j).sub_category_name)) {
                                categories.put(subCategories.get(j).sub_category_id);
                            }
                        }
                    }
                }
                if(type2!= null) {
                    for (int i = 0; i < categoryInfoArrayList.size(); i++) {
                        ArrayList<SubCategory> subCategories = new ArrayList<SubCategory>();
                        subCategories = categoryInfoArrayList.get(i).subCategoriesList;
                        for (int j = 0; j < subCategories.size(); j++) {
                            if (type2.equals(subCategories.get(j).sub_category_name)) {
                                categories.put(subCategories.get(j).sub_category_id);
                            }
                        }
                    }
                }
                if(type3!= null) {
                    for (int i = 0; i < categoryInfoArrayList.size(); i++) {
                        ArrayList<SubCategory> subCategories = new ArrayList<SubCategory>();
                        subCategories = categoryInfoArrayList.get(i).subCategoriesList;
                        for (int j = 0; j < subCategories.size(); j++) {
                            if (type3.equals(subCategories.get(j).sub_category_name)) {
                                categories.put(subCategories.get(j).sub_category_id);
                            }
                        }
                    }
                }
                if(type4!= null) {
                    for (int i = 0; i < categoryInfoArrayList.size(); i++) {
                        ArrayList<SubCategory> subCategories = new ArrayList<SubCategory>();
                        subCategories = categoryInfoArrayList.get(i).subCategoriesList;
                        for (int j = 0; j < subCategories.size(); j++) {
                            if (type4.equals(subCategories.get(j).sub_category_name)) {
                                categories.put(subCategories.get(j).sub_category_id);
                            }
                        }
                    }
                }
                mainJson.put("image", image);
                mainJson.put("categories", categories);
                Log.d("categories", categories + "");
                mainJson.put("start_date",
                        c1.get(Calendar.YEAR) + "-" + getTime(c1.get(Calendar.MONTH) + 1) + "-"
                                + getTime(c1.get(Calendar.DAY_OF_MONTH)) + " " + getTime(c1.get(Calendar.HOUR_OF_DAY))
                                + ":" + getTime(c1.get(Calendar.MINUTE)) + ":00");


                mainJson.put("end_date",
                        c2.get(Calendar.YEAR) + "-" + getTime(c2.get(Calendar.MONTH) + 1) + "-"
                                + getTime(c2.get(Calendar.DAY_OF_MONTH)) + " " + getTime(c2.get(Calendar.HOUR_OF_DAY))
                                + ":" + getTime(c2.get(Calendar.MINUTE)) + ":00");
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);

                Log.d("params", params.toString());
                System.out.println(DataHolder.CREATE_EVENT + mainJson.toString());
                aquery.ajax(DataHolder.CREATE_EVENT, params, JSONObject.class, fetchDataCallback());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println(DataHolder.CREATE_EVENT + "no internet");
            showToast("No internet connection available.");
        }
    }

    private AjaxCallback<JSONObject> fetchDataCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.e("result", "" + result);
                hideLoadingDialog();
                if (result != null) {
                    try {
                        JSONObject object = result.getJSONObject("output");
                        int finalstatus = object.optInt("status");
                        String message = object.optString("message");
                        showToast(message);
                        if (finalstatus == 1) {
                            JSONObject response = object.getJSONObject("response");
                            String eventId = response.optString("event_id");
                            Intent i = new Intent(CreateEvent3.this, EventDetailActivity.class);
                            i.putExtra("eventId", eventId);
                            i.putExtra("isFromCreate",true);
                            startActivity(i);
                            finish();
                            mStartFromScratch.finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    au_eventLocation.requestFocus();
                    showToast("Location not valid");
                    hideLoadingDialog();

                }
            }
        }.header("Content-Type", "application/json");
    }

    Dialog loadingDialog;

    public void showLoadingDialog() {
        loadingDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_loading, null);
        loadingDialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

    }

    public void hideLoadingDialog() {
        loadingDialog.dismiss();
    }

    private String getTime(int time) {
        String str = "";
        if (time > 9) {
            str = time + "";
        } else {
            str = "0" + time;
        }
        return str;
    }

}
