package viva.oneplatinum.com.viva.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.MemberAdapter;
import viva.oneplatinum.com.viva.models.MemeberList;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by D-MAX on 4/7/2016.
 */
public class ViewAllMemberAvtivity extends ParentActivity {
    LinearLayout search_bar;
    GridView gv;
    MemberAdapter gridAdapter;
    Context context;
    ArrayList<MemeberList> eventMemberList;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    private int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
    int totalpageCount, CurrentPageCount = 1;
    Boolean isFetching = false,isFirstFetch=true;
    String eventId;
    RobotoBoldTextView deselectAll,selectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_friends);
        eventId = getIntent().getStringExtra("event_id");
        eventMemberList=new ArrayList<MemeberList>();
        initViews();
        super.initViews(findViewById(R.id.main));
        initData();
        context = this;
      //  initToolBar();
        getMyFriends(DataHolder.VIEW_MEMBERS+"1.json");
        gridAdapter = new MemberAdapter(getApplicationContext(),0, eventMemberList);
        gv.setAdapter(gridAdapter);
    }

    private void initViews() {
        gv = (GridView) findViewById(R.id.event_list);
        selectAll = (RobotoBoldTextView) findViewById(R.id.selectAll);
        deselectAll = (RobotoBoldTextView) findViewById(R.id.deselectAll);
        selectAll.setVisibility(View.GONE);
        deselectAll.setVisibility(View.GONE);
        search_bar = (LinearLayout) findViewById(R.id.search_bar);
        search_bar.setVisibility(View.GONE);

    }
    private void initData(){
        gv.setOnScrollListener(new AbsListView.OnScrollListener() {

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
        gridAdapter = new MemberAdapter(getApplicationContext(),0, eventMemberList);
    }
    public void isScrollCompleted() {
        if (!isFetching){
            if (CurrentPageCount < totalpageCount) {
                int pageCount = (CurrentPageCount + 1);
                getMyFriends(DataHolder.VIEW_MEMBERS+pageCount+".json");
                isFetching = true;
                Log.d("getCategoriesresponse", "getCategoriesresponse");
            }
        }
    }
    public void getMyFriends(String url) {
        if (isNetworkAvailable()) {
            if(isFirstFetch) {
                showLoading();
            }
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token",  mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            params.put("event_id", eventId);
            Log.e("getCategoriesresponse", url + "," + params);
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
                            CurrentPageCount=outpuJsonObject.optInt("currentPage");
                            totalpageCount=outpuJsonObject.optInt("pagecount");
                            ArrayList<MemeberList> arrayList=new ArrayList<MemeberList>();
                            arrayList = ParseUtility.parseMemberList(result);
                            eventMemberList.addAll(arrayList);
                            isFetching=false;
                            isFirstFetch=false;
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
        gridAdapter.notifyDataSetChanged();
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ViewAllMemberAvtivity.this, FriendDetailActivity.class);
                intent.putExtra("friendId", eventMemberList.get(position).user_id);
                startActivity(intent);
            }
        });
        gridAdapter.notifyDataSetChanged();
    }
}
