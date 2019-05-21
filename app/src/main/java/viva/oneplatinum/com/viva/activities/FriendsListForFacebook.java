package viva.oneplatinum.com.viva.activities;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.FacebookFriendAdapter;
import viva.oneplatinum.com.viva.models.FbFriends;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

public class FriendsListForFacebook extends ParentActivity {
    ArrayList<FbFriends> fbFriendsArrayList;
    FacebookFriendAdapter mFacebookFriendAdapter;
    FontIcon backBtnToolbar, nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    Toolbar mToolBar;
    JSONArray Friends;
    ArrayList<FbFriends> friends;
    RobotoBoldTextView selectAll, deselectAll;
    EditText searchEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addfriend_listview_google);
        initToolBar();

            Intent intent = getIntent();
            String jsondata = intent.getStringExtra("jsondata");

            JSONArray friendslist;
        friends = new ArrayList<FbFriends>();

            try {
                friendslist = new JSONArray(jsondata);
                for (int l = 0; l < friendslist.length(); l++) {
                    FbFriends fbFriends = new FbFriends();
                    fbFriends.name = friendslist.getJSONObject(l).getString("name");
                    fbFriends.id = friendslist.getJSONObject(l).getString("id");
                    friends.add(fbFriends);


                    Log.d("friends",friends.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        searchEvent = (EditText) findViewById(R.id.searchEvents);
        searchEvent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent key) {
                // TODO Auto-generated method stub
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
                    addDoneinKeyboard();
                }

                return false;
            }
        });
        searchEvent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent key) {
                // TODO Auto-generated method stub
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
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
                mFacebookFriendAdapter.filter(String.valueOf(s));
//                if (s.length() == 0) {
//                    if (!isEventList) {
//                        loadContact.execute();
//                        listView.setVisibility(View.VISIBLE);
//                        hideSoftKeyboard();
//                    }
//                } else if (s.length() > 0) {
//                    //do your searches here
//                    adapter.filter((String) s);
//                    listView.setVisibility(View.VISIBLE);
//                }
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

//        search = (SearchView) findViewById(R.id.searchView);
//
//        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // TODO Auto-generated method stub
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // TODO Auto-generated method stub
//                adapter.filter(newText);
//                return false;
//            }
//        });

        selectAll = (RobotoBoldTextView) findViewById(R.id.selectAll);
        deselectAll = (RobotoBoldTextView) findViewById(R.id.deselectAll);

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < friends.size(); i++) {
                    friends.get(i).isSelected = true;
                }
                mFacebookFriendAdapter.notifyDataSetChanged();
            }

        });

        deselectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < friends.size(); i++) {
                    friends.get(i).isSelected = false;
                }
                mFacebookFriendAdapter.notifyDataSetChanged();
            }

        });


             // simple textview for list item
        ListView listView = (ListView) findViewById(R.id.event_list);
        mFacebookFriendAdapter = new FacebookFriendAdapter(getApplicationContext(),0,friends);
        listView.setAdapter(mFacebookFriendAdapter);
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

        titleToolbar.setText("ADD FRIENDS");
        nextBtnToolbar.setVisibility(View.VISIBLE);


        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        backBtnToolbar.setText("o");
        backBtnToolbar.setTextColor(getResources().getColor(R.color.red));
        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nextBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Friends = new JSONArray();
                for (int i = 0; i < friends.size(); i++) {
                    if (friends.get(i).isSelected) {
                        Friends.put(friends.get(i).id);
                    }
                }
                sendFriendRequest(DataHolder.SEND_FRIEND_REQUEST_FB);
            }
        });

        return customView;
    }
    public void sendFriendRequest(String url) {
        if (isNetworkAvailable()) {
            mProgressDialog.setMessage("Sending request..");
            mProgressDialog.show();
            Map<String, Object> params;
            try {
                JSONObject mainJson = new JSONObject();
                mainJson.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
                mainJson.put("friends", Friends);
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);

                Log.i("checkParams", "" + mainJson);
                //params.put("friends",);

                mAqueryTask.sendPostObjectRequest(url, params, JSONObject.class, getInvitationCallback());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private AjaxCallback<JSONObject> getInvitationCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {
                Log.i("checkResult", "" + result);
                mProgressDialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        showToast(outpuJsonObject.optString("message"));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        }.header("Content-Type", "application/json");
    }

}

