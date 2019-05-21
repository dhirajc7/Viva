package viva.oneplatinum.com.viva.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.AllEventsAdapter;
import viva.oneplatinum.com.viva.models.Event;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by D-MAX on 4/28/2016.
 */
public class EventListingActivity extends ParentActivity implements AdapterView.OnItemClickListener {
    ListView lv;
    AllEventsAdapter listAdapter;
    Context context;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar, searchIcon;
    RobotoBoldTextView titleToolbar;
    ArrayList<Event> eventArrayList;
    private int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
    int totalpageCount, CurrentPageCount = 1;
    Boolean isFetching = false, isFirstFetch = true;
    EditText searchEvent;
    LinearLayout containerLayout, search_bar;
    Boolean isEventList = true;
    AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events);
        eventArrayList = new ArrayList<Event>();
        initViews();
        super.initViews(findViewById(R.id.main));
        initData();
        context = this;
        initToolBar();
        getMyEvents(DataHolder.EVENT_LISTING);
        aq = new AQuery(this);
//        fetchEventList();
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

        titleToolbar.setText("EVENTS");
        nextBtnToolbar.setVisibility(View.GONE);

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
        lv = (ListView) findViewById(R.id.event_list);
        search_bar = (LinearLayout) findViewById(R.id.search_bar);
        search_bar.setVisibility(View.GONE);


    }

    private void initData() {
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentScrollState = scrollState;
                if (currentVisibleItemCount > 0 && currentScrollState == SCROLL_STATE_IDLE) {
                    isScrollCompleted();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
            }

        });
        listAdapter = new AllEventsAdapter(getApplicationContext(), 0, eventArrayList);
    }

    public void isScrollCompleted() {
        if (!isFetching) {
            if (CurrentPageCount < totalpageCount) {
                int pageCount = (CurrentPageCount + 1);
                getMyEvents(DataHolder.EVENT_LISTING);
                isFetching = true;
                Log.d("getCategoriesresponse", "getCategoriesresponse");
            }
        }
    }

    public void getMyEvents(String url) {
        if (isNetworkAvailable()) {
            if (isFirstFetch) {
                showLoading();
            }
            Log.e("getCategoriesresponse", "getCategoriesresponse");

            mAqueryTask.sendRequest(url, JSONObject.class, getMyEventsCallback());

        }
    }

    private AjaxCallback<JSONObject> getMyEventsCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.e("getCategoriesresponse", result.toString());
//                mProgressDialog.dismiss();

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            eventArrayList = ParseUtility.parseAllEventsList(result);
                            setData();
                            showDataView();
                        } else {
//                            showErrorLoading(outpuJsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    public void setData() {
        listAdapter = new AllEventsAdapter(getApplicationContext(), 0, eventArrayList);
        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(EventListingActivity.this, EventDetailActivity.class);
                intent.putExtra("eventId",eventArrayList.get(position).eventId);
                Log.d("eventid",eventArrayList.get(position).eventId);
                startActivity(intent);
            }
        });
        listAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
