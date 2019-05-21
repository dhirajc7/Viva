package viva.oneplatinum.com.viva.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.ListAdapter;
import viva.oneplatinum.com.viva.maps.GPSTracker;
import viva.oneplatinum.com.viva.maps.MapActivity;
import viva.oneplatinum.com.viva.maps.ShowEventLocationActivity;
import viva.oneplatinum.com.viva.models.Event;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by D-MAX on 4/10/2016.
 */
public class FilterSearchActivity extends ParentActivity implements AdapterView.OnItemClickListener {
    FontIcon Arty, Sporty, Party, Whatever, Morning, Afternoon, Evening, Any_time, Next_day, Next_week, Next_month, Whenever, two_km, ten_km, fifty_km, Wherever;
    TextView resetAllSelected;
    Context context;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar, searchIcon;
    RobotoBoldTextView titleToolbar, randomize;
    ListView lv;
    ListAdapter listAdapter;
    ArrayList<Event> eventArrayList;
    private int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
    int totalpageCount, CurrentPageCount = 1;
    Boolean isFetching = false, isFirstFetch = true;
    EditText searchEvent;
    LinearLayout containerLayout;
    Boolean isEventList = false;
    LinearLayout customSearch, eventSearch;
    AQuery aq;
    Boolean isFromHomeActivity=true, isFromMapActivity;
    String Feel, Time, Period, Distance, Lat, Lng, currentLat, currentLng,reset="0";
    boolean isRandomSelected = false;
    JSONObject result=new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_categories);
        eventArrayList = new ArrayList<Event>();
        initViews();
        initData();
        context = this;
        initToolBar();
//        getVivaFreinds(DataHolder.MY_EVENTS);
        isFromHomeActivity = getIntent().getBooleanExtra("isFromHomeActivity", false);
        Lat = getIntent().getStringExtra("lat");
        Lng = getIntent().getStringExtra("lng");
        aq = new AQuery(this);
        feelLikeDoind();
        when();
        howSoon();
        howFarAway();
        // reset();
        resetAllSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    private void reset() {
        Arty.setSelected(false);
        Party.setSelected(false);
        Sporty.setSelected(false);
        Whatever.setSelected(true);
        Feel = "Whatever";
        Morning.setSelected(false);
        Evening.setSelected(false);
        Afternoon.setSelected(false);
        Any_time.setSelected(true);
        Period = "Any_time";
        Next_month.setSelected(false);
        Next_day.setSelected(false);
        Next_week.setSelected(true);
        Time = "Next_week";
        Whenever.setSelected(false);
        two_km.setSelected(false);
        ten_km.setSelected(true);
        Distance = "10_km";
        fifty_km.setSelected(false);
        Wherever.setSelected(false);
        reset="1";
    }

    private void howFarAway() {
        two_km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                two_km.setSelected(true);
                fifty_km.setSelected(false);
                ten_km.setSelected(false);
                Wherever.setSelected(false);
                Distance = "2_km";
            }
        });
        ten_km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ten_km.setSelected(true);
                two_km.setSelected(false);
                fifty_km.setSelected(false);
                Wherever.setSelected(false);
                Distance = "10_km";
            }
        });
        fifty_km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fifty_km.setSelected(true);
                two_km.setSelected(false);
                ten_km.setSelected(false);
                Wherever.setSelected(false);
                Distance = "50_km";
            }
        });
        Wherever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wherever.setSelected(true);
                two_km.setSelected(false);
                ten_km.setSelected(false);
                fifty_km.setSelected(false);
                Distance = "Wherever";
            }
        });
    }

    private void howSoon() {
        Next_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Next_day.setSelected(true);
                Next_week.setSelected(false);
                Next_month.setSelected(false);
                Whenever.setSelected(false);
                Time = "Next_day";

            }
        });
        Next_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Next_week.setSelected(true);
                Next_day.setSelected(false);
                Next_month.setSelected(false);
                Whenever.setSelected(false);
                Time = "Next_week";
            }
        });
        Next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Next_month.setSelected(true);
                Next_day.setSelected(false);
                Next_week.setSelected(false);
                Whenever.setSelected(false);
                Time = "Next_month";
            }
        });
        Whenever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Whenever.setSelected(true);
                Next_week.setSelected(false);
                Next_day.setSelected(false);
                Next_month.setSelected(false);
                Time = "Whenever";
            }
        });
    }

    private void when() {
        Morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Morning.setSelected(true);
                Evening.setSelected(false);
                Afternoon.setSelected(false);
                Any_time.setSelected(false);
                Period = "Morning";
            }
        });
        Afternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Afternoon.setSelected(true);
                Morning.setSelected(false);
                Evening.setSelected(false);
                Any_time.setSelected(false);
                Period = "Afternoon";
            }
        });
        Evening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Evening.setSelected(true);
                Morning.setSelected(false);
                Afternoon.setSelected(false);
                Any_time.setSelected(false);
                Period = "Evening";
            }
        });
        Any_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Any_time.setSelected(true);
                Morning.setSelected(false);
                Evening.setSelected(false);
                Afternoon.setSelected(false);
                Period = "Any_time";
            }
        });
    }

    private void feelLikeDoind() {
        Arty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Arty.setSelected(true);
                Sporty.setSelected(false);
                Party.setSelected(false);
                Wherever.setSelected(false);
                Feel = "Arty";
            }
        });
        Sporty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Arty.setSelected(false);
                Sporty.setSelected(true);
                Party.setSelected(false);
                Wherever.setSelected(false);
                Feel = "Sporty";
            }
        });
        Party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Arty.setSelected(false);
                Sporty.setSelected(false);
                Party.setSelected(true);
                Wherever.setSelected(false);
                Feel = "Party";
            }
        });
        Whatever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Arty.setSelected(false);
                Sporty.setSelected(false);
                Party.setSelected(false);
                Whatever.setSelected(true);
                Feel = "Whatever";
            }
        });

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

        titleToolbar.setText("FILTER");
        nextBtnToolbar.setVisibility(View.VISIBLE);
        nextBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isFromHomeActivity) {
//                    Intent i = new Intent(FilterSearchActivity.this, HomeActivity.class);
//                    startActivity(i);
//                   postEventforWheel();
//
//                }
//                if (isFromMapActivity) {
//                    Intent i = new Intent(FilterSearchActivity.this, MapActivity.class);
//                    startActivity(i);
//                    postEventForMap();
//                }
                if(isEventList){
                    mVivaApplicatiion.setUserData(DataHolder.EVENTS, result.toString());
                    if (isFromHomeActivity) {
                        Intent i = new Intent(FilterSearchActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(FilterSearchActivity.this, MapActivity.class);
                        i.putExtra("eventData", mVivaApplicatiion.getUserData(DataHolder.EVENTS));
                        startActivity(i);
                        finish();
                    }
                }else {
                    postEventForMap();
                }
            }
        });

        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        backBtnToolbar.setText("j");
        backBtnToolbar.setTextColor(getResources().getColor(R.color.red));
        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        return customView;
    }


    private void initViews() {

        Arty = (FontIcon) findViewById(R.id.arty);
        Sporty = (FontIcon) findViewById(R.id.sporty);
        Party = (FontIcon) findViewById(R.id.party);
        Whatever = (FontIcon) findViewById(R.id.whatever);
        Morning = (FontIcon) findViewById(R.id.morning);
        Afternoon = (FontIcon) findViewById(R.id.afternoon);
        Evening = (FontIcon) findViewById(R.id.evening);
        Any_time = (FontIcon) findViewById(R.id.anytime);
        Next_day = (FontIcon) findViewById(R.id.nextDay);
        Next_week = (FontIcon) findViewById(R.id.nextWeek);
        Next_month = (FontIcon) findViewById(R.id.nextMonth);
        Whenever = (FontIcon) findViewById(R.id.dayWhatEver);
        two_km = (FontIcon) findViewById(R.id.two_km);
        ten_km = (FontIcon) findViewById(R.id.ten_km);
        fifty_km = (FontIcon) findViewById(R.id.fifty_km);
        Wherever = (FontIcon) findViewById(R.id.howFarWhatEver);
        resetAllSelected = (TextView) findViewById(R.id.reset);
        customSearch = (LinearLayout) findViewById(R.id.customSearch);
        eventSearch = (LinearLayout) findViewById(R.id.eventSearch);
        lv = (ListView) findViewById(R.id.event_list);

        containerLayout = (LinearLayout) findViewById(R.id.container);
        searchIcon = (FontIcon) findViewById(R.id.searchIcon);
        randomize = (RobotoBoldTextView) findViewById(R.id.randomize);
        randomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRandomSelected) {
                    isRandomSelected = true;
                    randomize.setBackgroundResource(R.color.random_selected_color);
                } else {
                    isRandomSelected = false;
                    randomize.setBackgroundResource(R.color.random_unselected_color);
                }
            }
        });
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventSearch.setVisibility(View.VISIBLE);
                customSearch.setVisibility(View.GONE);
                searchFriendList();
            }
        });
        searchEvent = (EditText) findViewById(R.id.searchEvents);

        searchEvent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent key) {
                // TODO Auto-generated method stub
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
                    eventSearch.setVisibility(View.VISIBLE);
                    customSearch.setVisibility(View.GONE);
                    searchFriendList();
                    addDoneinKeyboard();
                }

                return false;
            }
        });
        searchEvent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (s.length() == 0) {
                        eventSearch.setVisibility(View.GONE);
                        customSearch.setVisibility(View.VISIBLE);
                        containerLayout.setVisibility(View.GONE);
                        lv.setVisibility(View.GONE);
                        hideSoftKeyboard();

                } else if (s.length() > 0) {
                    //do your searches here
                    searchFriendList();
                }

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
    }

    private void initData() {
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentScrollState = scrollState;
                if (currentVisibleItemCount > 0 && currentScrollState == SCROLL_STATE_IDLE) {
//                    isScrollCompleted();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
            }

        });
        listAdapter = new ListAdapter(getApplicationContext(), 0, eventArrayList, mVivaApplicatiion.getUserData(DataHolder.TOKEN), true);
    }

//    public void isScrollCompleted() {
//        if (!isFetching) {
//            if (CurrentPageCount < totalpageCount) {
//                int pageCount = (CurrentPageCount + 1);
//                getVivaFreinds(DataHolder.MY_EVENTS);
//                isFetching = true;
//                Log.d("getCategoriesresponse", "getCategoriesresponse");
//            }
//        }
//    }

//    public void getVivaFreinds(String url) {
//        if (isNetworkAvailable()) {
//            if (isFirstFetch) {
//                showLoading();
//            }
//            Log.e("getCategoriesresponse", "getCategoriesresponse");
//            HashMap<String, String> params = new HashMap<String, String>();
//            params.put("token", "cdojghf6cchb2g06ot2hnfrcgaqfe5vxbl612orq");
//            mAqueryTask.sendRequest(url, params, JSONObject.class, getMyEventsCallback());
//
//        }
//    }
//
//    private AjaxCallback<JSONObject> getMyEventsCallback() {
//        return new AjaxCallback<JSONObject>() {
//            public void callback(String url, JSONObject result,
//                                 AjaxStatus status) {
//
//                System.out.println(result);
//                Log.e("getCategoriesresponse", result.toString());
////                mProgressDialog.dismiss();
//
//                if (result != null) {
//                    try {
//                        JSONObject outpuJsonObject = result.getJSONObject("output");
//                        int userStatus = outpuJsonObject.optInt("status");
//                        if (userStatus == 1) {
//                            eventArrayList = ParseUtility.parseEventsList(result);
////                            setData();
//                            showDataView();
//                        } else {
//                            showErrorLoading(outpuJsonObject.optString("message"));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            ;
//        };
//    }

    public void setData() {
        listAdapter = new ListAdapter(getApplicationContext(), 0, eventArrayList, mVivaApplicatiion.getUserData(DataHolder.TOKEN), true);
        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(FilterSearchActivity.this, EventDetailActivity.class);
                intent.putExtra("eventId", eventArrayList.get(position).eventId);
                startActivity(intent);
            }
        });
        listAdapter.notifyDataSetChanged();
        eventSearch.setVisibility(View.VISIBLE);
        customSearch.setVisibility(View.GONE);
        containerLayout.setVisibility(View.GONE);
        lv.setVisibility(View.VISIBLE);
    }

    private void searchFriendList() {
        if (GeneralUtil.isNetworkAvailable(this)) {
            // view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            // HashMap<String, String>params = new HashMap<String, String>();
            // params.put("token", "1234");
            // progressLayout.setVisibility(View.VISIBLE);
//            showLoadingDialog();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("keyword", searchEvent.getText().toString());
                mainJson.put("lat", mVivaApplicatiion.getUserData(DataHolder.CURRENT_LOCATION_LATITUDE));
                mainJson.put("lng", mVivaApplicatiion.getUserData(DataHolder.CURRENT_LOCATION_LONGITUDE));
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);
                System.out.println(DataHolder.MY_EVENTS + mainJson);
                aq.ajax(DataHolder.MY_EVENTS, params, JSONObject.class,
                        fetchDataCallbackForSearch());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            System.out.println(DataHolder.MY_EVENTS + "no internet");

        }
    }


    private AjaxCallback<JSONObject> fetchDataCallbackForSearch() {
        return new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object,
                                 AjaxStatus status) {

                System.out.println("allFriends" + object);
//                hideLoadingDialog();
                if (object == null) {

                } else {

                    try {
                        eventArrayList = new ArrayList<Event>();
                        JSONObject outpuJsonObject = object.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            result=object;
                            eventArrayList = ParseUtility.parseEventsList(object);
                            setData();
                            isEventList=true;
                            showDataView();
//                            filterFriends(eventArrayList);
                        } else {
                            showToast(outpuJsonObject.optString("message"));
                            // getVivaFreinds(DataHolder.MY_EVENTS);
                            View empty = getLayoutInflater().inflate(
                                    R.layout.layout_error_loading, null);
                            TextView text = (TextView) empty
                                    .findViewById(R.id.txt_error_loading_message);
                            text.setText("No Events found");
                            TextView btn = (TextView) empty
                                    .findViewById(R.id.txt_error_loading);

                            btn.setVisibility(View.GONE);
                            ImageView im = (ImageView) empty.findViewById(R.id.errorImage);
                            im.setVisibility(View.GONE);
                            containerLayout.addView(empty);
                            lv.setVisibility(View.GONE);

                        }
                        // progressLayout.setVisibility(View.GONE);

                        // populateViews(cruecoFriends);
                        // listview.setAdapter(new FriendsAdapter(getActivity(),
                        // 0, cruecoFriends));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                    isEventList = false;
                }
            }
        }.header("Content-Type", "application/json");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void postEventForMap() {
        AQuery aquery = new AQuery(this);
        if (GeneralUtil.isNetworkAvailable(this)) {
            mProgressDialog.setMessage("getting events..");
            mProgressDialog.show();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();

                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("Feel", Feel);
                mainJson.put("Time", Time);
                mainJson.put("Distance", Distance);
                mainJson.put("Period", Period);
                mainJson.put("Reset", reset);
                mainJson.put("lat", mVivaApplicatiion.getUserData(DataHolder.CURRENT_LOCATION_LATITUDE));
                mainJson.put("lng", mVivaApplicatiion.getUserData(DataHolder.CURRENT_LOCATION_LONGITUDE));
                if (isRandomSelected) {
                    mainJson.put("Random", "1");
                } else {
                    mainJson.put("Random", "0");
                }
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);
                Log.d("checkmainjson", mainJson.toString());
                System.out.println(DataHolder.FILTER_SEARCH_EVENTS + params.toString());
                aquery.ajax(DataHolder.FILTER_SEARCH_EVENTS, params, JSONObject.class, fetchDataCallback());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println(DataHolder.FILTER_SEARCH_EVENTS + "no internet");
            showToast("No internet connection available.");
        }
    }


    private AjaxCallback<JSONObject> fetchDataCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.e("checkresult", "" + result);
                mProgressDialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject object = result.getJSONObject("output");
                        int finalstatus = object.optInt("status");
                        String message = object.optString("message");
                        showToast(message);
                        Log.v("check",isFromHomeActivity+""+finalstatus);
                        if (finalstatus == 1) {
                            Log.v("check1",""+finalstatus);
                            mVivaApplicatiion.setUserData(DataHolder.EVENTS, result.toString());
                            if (isFromHomeActivity) {
                                Log.v("check1",""+finalstatus);
                                Intent i = new Intent(FilterSearchActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Intent i = new Intent(FilterSearchActivity.this, MapActivity.class);
                                i.putExtra("eventData", mVivaApplicatiion.getUserData(DataHolder.EVENTS));
                                startActivity(i);
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        }.header("Content-Type", "application/json");
    }


    private AjaxCallback<JSONObject> fetchWheelDataCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.e("checkresult", "" + result);
                hideLoadingDialog();
                if (result != null) {
                    try {
                        JSONObject object = result.getJSONObject("output");
                        int finalstatus = object.optInt("status");
                        String message = object.optString("message");
                        showToast(message);
                        if (finalstatus == 1) {
                            JSONObject response = object.getJSONObject("response");
//                            String eventId=response.optString("event_id");
//                            Intent i = new Intent(CreateEvent3.this, EventDetailActivity.class);
//                            i.putExtra("eventId", eventId);
//                            startActivity(i);
//                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        }.header("Content-Type", "application/json");
    }


    @Override
    public void onBackPressed() {
        if (isFromHomeActivity) {
            Intent i = new Intent(FilterSearchActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(FilterSearchActivity.this, MapActivity.class);
            i.putExtra("eventData", mVivaApplicatiion.getUserData(DataHolder.EVENTS));
            startActivity(i);
            finish();
        }
        super.onBackPressed();
    }


    Dialog loadingDialog;

    private void showLoadingDialog() {
        loadingDialog = new Dialog(this,
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(R.layout.loading_layout,
                null);
        loadingDialog.addContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

    }

    private void hideLoadingDialog() {
        loadingDialog.dismiss();
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getWindow().getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    public void addDoneinKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


}
