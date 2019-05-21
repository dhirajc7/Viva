package viva.oneplatinum.com.viva.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.NotificationAdapter;
import viva.oneplatinum.com.viva.models.Event;
import viva.oneplatinum.com.viva.models.EventDetail;
import viva.oneplatinum.com.viva.models.Notifications;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by Dell on 3/29/2016.
 */
public class NotificationActivity extends ParentActivity {
    ListView lv;
   NotificationAdapter mNotificationAdapter;
    Context context;
    ArrayList<Notifications> notification;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    LinearLayout  search_bar;
    private int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
    int totalpageCount, CurrentPageCount = 1;
    Boolean isFetching = false, isFirstFetch = true;
    ArrayList<Notifications> notificationsArrayList;
    EventDetail mEventDetail;
    String currentUrl, currentTitle;
    ArrayList<Event> eventArrayList;
    ArrayList<Notifications> arrayList;
    LayoutInflater mInflater;
    private View listViewFooter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        initToolBar();
        super.initViews(findViewById(R.id.main));
        notificationsArrayList = new ArrayList<Notifications>();

        eventArrayList = new ArrayList<Event>();
        getNotificationList(DataHolder.NOTIFICATION_LIST+"1.json");
        initViews();
        initData();
        mNotificationAdapter = new NotificationAdapter(NotificationActivity.this, 0, notificationsArrayList,mVivaApplicatiion.getUserData(DataHolder.TOKEN));

        lv.setAdapter(mNotificationAdapter);
        context = this;
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

        titleToolbar.setText("NOTIFICATIONS");
        nextBtnToolbar.setText("i");
        nextBtnToolbar.setVisibility(View.VISIBLE);
        nextBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NotificationActivity.this, NewPostActivity.class);
                startActivity(i);
            }
        });

        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        backBtnToolbar.setText("o");
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
        mInflater = getLayoutInflater();
        lv = (ListView) findViewById(R.id.notification_list);
        listViewFooter = mInflater.inflate(R.layout.listview_footer_layout, lv, false);



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
        mNotificationAdapter = new NotificationAdapter(getApplicationContext(), 0, notificationsArrayList,mVivaApplicatiion.getUserData(DataHolder.TOKEN));
    }

    public void isScrollCompleted() {
        if (!isFetching) {
            if (CurrentPageCount < totalpageCount) {
                int pageCount = (CurrentPageCount + 1);
                getNotificationList(DataHolder.NOTIFICATION_LIST+pageCount+".json");
                isFetching = true;
                Log.d("getCategoriesresponse", "getCategoriesresponse");
            }
        }
    }

    public void getNotificationList(String url) {
        if (isNetworkAvailable()) {
            isFetching=true;
            if (isFirstFetch) {
                showLoading();
            }
            Log.e("getCategoriesresponse", "getCategoriesresponse");
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            Log.d("params",params.toString());
            mAqueryTask.sendRequest(url, params, JSONObject.class, getNotificationListCallback());

        }
    }
    private AjaxCallback<JSONObject> getNotificationListCallback() {
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
                            CurrentPageCount=outpuJsonObject.optInt("currentPage");
                            totalpageCount=outpuJsonObject.optInt("pagecount");
                            arrayList=new ArrayList<Notifications>();
                            arrayList = ParseUtility.parseNotificationList(result);
                            notificationsArrayList.addAll(arrayList);
                            isFetching=false;
                            isFirstFetch=false;
                            setData();
                            showDataView();
                        }
                        else {
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
        if (arrayList != null) {
            if (CurrentPageCount < totalpageCount) {
                Log.v("addFooter", "addFooter");
                if (!isFirstFetch) {
                    lv.removeFooterView(listViewFooter);
                }
                lv.addFooterView(listViewFooter);
            } else {
                Log.v("removeFooter", "removeFooter");
                lv.removeFooterView(listViewFooter);
            }
        }
       mNotificationAdapter.notifyDataSetChanged();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub

            }
        });
//        mNotificationAdapter.notifyDataSetChanged();
    }


    }

