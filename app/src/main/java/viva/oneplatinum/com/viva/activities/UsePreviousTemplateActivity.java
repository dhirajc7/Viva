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
import android.widget.ListView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.ListAdapter;
import viva.oneplatinum.com.viva.models.Event;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by D-MAX on 4/6/2016.
 */
public class UsePreviousTemplateActivity extends ParentActivity {
    ListView lv;
    ListAdapter listAdapter;
    Context context;
    ArrayList<Event> event;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    ArrayList<Event> eventArrayList;
    private int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
    int totalpageCount, CurrentPageCount = 1;
    Boolean isFetching = false,isFirstFetch=true;
    JSONObject eventDetailResult;
    Boolean isEventList = true;
    ArrayList<Event> arrayList;
    LayoutInflater mInflater;
    private View listViewFooter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events);
        eventArrayList=new ArrayList<Event>();
        initViews();
        super.initViews(findViewById(R.id.main));
        initData();
        context = this;
        initToolBar();

        listAdapter = new ListAdapter(getApplicationContext(),0, eventArrayList,mVivaApplicatiion.getUserData(DataHolder.TOKEN),true);
        lv.setAdapter(listAdapter);
        getMyEvents(DataHolder.MY_EVENTS + "1.json");

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

        titleToolbar.setText("SELECT EVENT");
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
        mInflater = getLayoutInflater();
        lv = (ListView) findViewById(R.id.event_list);
        listViewFooter = mInflater.inflate(R.layout.listview_footer_layout, lv, false);
    }
    private void initData(){
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
        listAdapter = new ListAdapter(getApplicationContext(),0, eventArrayList,mVivaApplicatiion.getUserData(DataHolder.TOKEN),true);
    }
    public void isScrollCompleted() {
        if (!isFetching){
            if (CurrentPageCount < totalpageCount) {
                int pageCount = (CurrentPageCount + 1);
                getMyEvents(DataHolder.MY_EVENTS + pageCount + ".json");
                isFetching = true;
                Log.d("getCategoriesresponse", "getCategoriesresponse");
            }
        } else {
            if (!isFetching) {
                if (!isEventList) {
                    if (CurrentPageCount < totalpageCount) {
                        int pageCount = (CurrentPageCount + 1);
                        getMyEvents(DataHolder.MY_EVENTS + pageCount + ".json");
                        isFetching = true;
                    }
                }
            }
        }
    }
    public void getMyEvents(String url) {
        if (isNetworkAvailable()) {
            if(isFirstFetch) {
                showLoading();
            }
            Log.e("getCategoriesresponse", "getCategoriesresponse");
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token",  mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            mAqueryTask.sendRequest(url, params, JSONObject.class, getMyEventsCallback());

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
                        JSONObject outpuJsonObject=result.getJSONObject("output");
                        int userStatus=outpuJsonObject.optInt("status");
                        if(userStatus==1){
                            CurrentPageCount = outpuJsonObject.optInt("currentPage");
                            totalpageCount = outpuJsonObject.optInt("pagecount");
                             arrayList = new ArrayList<Event>();
                            arrayList = ParseUtility.parseEventsList(result);
                            eventArrayList.addAll(arrayList);
                            isFetching = false;
                            isFirstFetch = false;
                            setData();
                            showDataView();
                        }else{
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
    public void setData(){
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
        listAdapter.notifyDataSetChanged();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(UsePreviousTemplateActivity.this, UsingPreviousTemlateActivityStep2.class);
                intent.putExtra("eventId", eventArrayList.get(position).eventId);

                startActivity(intent);
                finish();
            }
        });

    }
}
