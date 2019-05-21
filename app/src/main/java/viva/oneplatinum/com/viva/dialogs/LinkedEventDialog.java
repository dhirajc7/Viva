package viva.oneplatinum.com.viva.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import viva.oneplatinum.com.viva.adapters.LinkEventAdapter;
import viva.oneplatinum.com.viva.app.VivaApplication;
import viva.oneplatinum.com.viva.interfaces.LinkedEventSelectorListener;
import viva.oneplatinum.com.viva.models.Event;
import viva.oneplatinum.com.viva.parser.ParseUtility;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;

/**
 * Created by Dell on 4/7/2016.
 */
public class LinkedEventDialog extends ParentDialog {
    LinkedEventSelectorListener mLinkedEventSelectorListener;
    VivaApplication mVivaApplication;
    Dialog customDailog;
    Context mContext;
    private OnDismissListner listner;
    LinearLayout searchBar;
    ListView eventList;
//    ListAdapter adapter;
    LinkEventAdapter adapter;
    ArrayList<Event> eventArrayList;
    private int currentFirstVisibleItem, currentVisibleItemCount, currentScrollState;
    int totalpageCount, CurrentPageCount = 1;
    Boolean isFetching = false, isFirstFetch = true;
    ArrayList<Event> event;
    ArrayList<Event> arrayList;
    LayoutInflater mInflater;
    private View listViewFooter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventArrayList = new ArrayList<Event>();
        mLinkedEventSelectorListener = (LinkedEventSelectorListener) getActivity();


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mVivaApplication = (VivaApplication) mContext.getApplicationContext();
    }

    public void setDismissListner(OnDismissListner listner) {
        this.listner = listner;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        customDailog = new Dialog(mContext);
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.my_events, null);
        searchBar = (LinearLayout) view.findViewById(R.id.search_bar);
        searchBar.setVisibility(View.GONE);
        mInflater = LayoutInflater.from(getContext());
        eventList = (ListView) view.findViewById(R.id.event_list);
        listViewFooter = mInflater.inflate(R.layout.listview_footer_layout, eventList, false);
        super.initViews(view);
        getMyEvents(DataHolder.LINKED_EVENTS + "1.json");
        customDailog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        customDailog.setContentView(view);
        customDailog.getWindow().setBackgroundDrawableResource(R.color.Semitransparent);
        customDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        customDailog.show();
//        Window window = customDailog.getWindow();
//        WindowManager.LayoutParams wlp = window.getAttributes();
//        wlp.gravity = Gravity.BOTTOM;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        window.setAttributes(wlp);
        customDailog.show();
        initData();
        adapter = new LinkEventAdapter(getActivity(), 0, eventArrayList, mVivaApplicatiion.getUserData(DataHolder.TOKEN),false);
        eventList.setAdapter(adapter);

        return customDailog;

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public void getMyEvents(String url) {
        if (GeneralUtil.isNetworkAvailable(mContext)) {
            if (isFirstFetch) {
                showLoading();
            }
            Log.e("getCategoriesresponse", "getCategoriesresponse");
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplication.getUserData(DataHolder.TOKEN));
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
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            CurrentPageCount = outpuJsonObject.optInt("currentPage");
                            totalpageCount = outpuJsonObject.optInt("pagecount");
                            arrayList = new ArrayList<Event>();
                            arrayList = ParseUtility.parseLinkedEventsList(result);
                            eventArrayList.addAll(arrayList);
                            isFetching = false;
                            isFirstFetch = false;
                            setData();
                            showDataView();
                        } else {
                            showErrorLoading(outpuJsonObject.optString("message"));
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
                    eventList.removeFooterView(listViewFooter);
                }
                eventList.addFooterView(listViewFooter);
            } else {
                Log.v("removeFooter", "removeFooter");
                eventList.removeFooterView(listViewFooter);
            }
        }
        adapter.notifyDataSetChanged();
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                if (eventArrayList.get(position).eventImages.size() > 0) {
                    mLinkedEventSelectorListener.event(eventArrayList.get(position).eventTitle.trim(), eventArrayList.get(position).eventLocation.trim(), eventArrayList.get(position).eventImages.get(0).trim(), eventArrayList.get(position).eventId);
                    Log.d("eventImage",mLinkedEventSelectorListener.toString());
                } else {
                    mLinkedEventSelectorListener.event(eventArrayList.get(position).eventTitle, eventArrayList.get(position).eventLocation, "", eventArrayList.get(position).eventId);
                }
                dismiss();
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        eventList.setOnScrollListener(new AbsListView.OnScrollListener() {

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
        adapter = new LinkEventAdapter(getActivity(), 0, eventArrayList, mVivaApplicatiion.getUserData(DataHolder.TOKEN),false);
    }

    public void isScrollCompleted() {
        if (!isFetching) {
            if (CurrentPageCount < totalpageCount) {
                int pageCount = (CurrentPageCount + 1);
                getMyEvents(DataHolder.LINKED_EVENTS + pageCount + ".json");
                isFetching = true;
                Log.d("getCategoriesresponse", "getCategoriesresponse");
            }
        }
    }
}
