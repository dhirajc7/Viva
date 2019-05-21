
package viva.oneplatinum.com.viva.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.PersonBuffer;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.data.Contact;
import viva.oneplatinum.com.viva.data.ContactAdapter;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;


public class FriendsList extends ParentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {
    private static final String TAG = "FriendsList";
    ContactAdapter adapter;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    private boolean is_gglSignInBtn_clicked=true;
    private boolean is_intent_inprogress=false;
    // Google client object used to interact with Google+ APIs
    private GoogleApiClient mGoogleApiClient = null;
    ArrayList<Contact> arrayListContacts=new ArrayList<Contact>();
    JSONArray Friends;
    ProgressDialog progress_dialog;
    private static final int SIGN_IN_CODE = 0;
    private int request_code=0;
    RobotoBoldTextView selectAll, deselectAll;
    GoogleApiAvailability google_api_availability;
    private ConnectionResult connection_result;
    EditText searchEvent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addfriend_listview_google);
        mGoogleApiClient =  new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        mProgressDialog.show();
        mProgressDialog.setMessage("Retrieiving Google Plus circle..");
        final ListView mListViewContacts = (ListView) findViewById(R.id.event_list);
        mListViewContacts.setAdapter(new ContactAdapter(this, arrayListContacts));
        initData();
        initToolBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
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

        nextBtnToolbar.setText("s");
        nextBtnToolbar.setTextColor(getResources().getColor(R.color.Blue));
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
                for (int i = 0; i < arrayListContacts.size(); i++) {
                    if (arrayListContacts.get(i).isSelected) {
                        Friends.put(arrayListContacts.get(i)._ID);
                    }
                }
                sendFriendRequest(DataHolder.SEND_FRIEND_REQUEST_GOOGLE);
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
                Log.i("check",""+mVivaApplicatiion.getUserData(DataHolder.TOKEN)+","+Friends);
                StringEntity s;

                s = new StringEntity(mainJson.toString(), HTTP.UTF_8);

                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);

                Log.i("checkParams", "" + mainJson);
                //params.put("friends",);

                mAqueryTask.sendPostObjectRequest(url, params, JSONObject.class, getFriendsCallback());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private AjaxCallback<JSONObject> getFriendsCallback() {
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
    private void initData() {
        selectAll= (RobotoBoldTextView) findViewById(R.id.selectAll);
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrayListContacts.size(); i++) {
                    arrayListContacts.get(i).isSelected=true;
                }
                adapter.notifyDataSetChanged();
            }
        });

        deselectAll= (RobotoBoldTextView) findViewById(R.id.deselectAll);
        deselectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrayListContacts.size(); i++) {
                    arrayListContacts.get(i).isSelected=false;
                }
                adapter.notifyDataSetChanged();
            }
        });

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
                //adapter.filter(String.valueOf(s));
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


    }

    @Override
    protected void onStart() {

        super.onStart();

        Log.d(TAG, "onStart called");
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {

        super.onStop();

        Log.d(TAG, "onStop called");

        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "onStop called - calling disconnect");
            mGoogleApiClient.disconnect();
        }

    }
    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {

        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (responseCode != RESULT_OK) {
                is_gglSignInBtn_clicked = false;
                progress_dialog.dismiss();

            }

            is_intent_inprogress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }

    }
    /**
     * As soon as user is given connected, it starts a request to query a list
     * of visible people in the user's circles.
     * <p/>
     * The result for this query will be available in the onResult callback
     * method and there, all contacts will be loaded into an adapter to be set
     * to the list view object { @see listView_contactsList }.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected called");
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback((ResultCallback<? super People.LoadPeopleResult>) this);
        getProfileInfo();
    }

    private void getProfileInfo() {
//        try {

//            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
//                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
//
//                Log.i("googleEmail", "" + (Plus.AccountApi.getAccountName(google_api_client)));
//                email=""+(Plus.AccountApi.getAccountName(google_api_client));
//                setPersonalInfo(currentPerson);
//            } else {
//                Toast.makeText(getApplicationContext(),
//                        "No Personal info mention", Toast.LENGTH_LONG).show();
//                progress_dialog.dismiss();
//                finish();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            google_api_availability.getErrorDialog(this, result.getErrorCode(), request_code).show();
            return;
        }
        if (!is_intent_inprogress) {

            connection_result = result;


            if (is_gglSignInBtn_clicked) {
                resolveSignInError();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Log.d(TAG, "onConnectionSuspended called");
    }

    /**
     * It a callback method that receives the result of querying all visible
     * people in the user's circles. It loads into an adapter all this data and
     * set the list view object { @see listView_contactsList }.
     */
    public void onResult(People.LoadPeopleResult peopleData) {

        Log.d(TAG, "onResult called - setting adapter");
        Contact contact;


        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            Log.d("count", ""+personBuffer);
            Log.e(TAG, "Error requesting visible circles : " + peopleData.getStatus());
            try {

                int count = personBuffer.getCount();
                Log.d("count", ""+personBuffer.getCount());
                for (int i = 0; i < count; i++) {

                    contact = new Contact(personBuffer.get(i).hasId() ? personBuffer.get(i).getId()
                            : null, personBuffer.get(i).hasDisplayName() ? personBuffer.get(i)
                            .getDisplayName() : null, personBuffer.get(i).hasUrl() ? personBuffer
                            .get(i).getUrl() : null, personBuffer.get(i).hasImage() ? personBuffer
                            .get(i).getImage().getUrl() : null, personBuffer.get(i).hasAboutMe()? personBuffer.get(i).getAboutMe() : null);

                    arrayListContacts.add(contact);
                    setPersonalInfo(contact);
                    Log.d("contacts", "" + arrayListContacts);
                    Log.i("checkimage",""+(personBuffer.get(i).hasImage() ? personBuffer.get(i).getImage().getUrl():null));
                    mProgressDialog.hide();
                }

            } finally {
                personBuffer.close();
            }
        } else {
            Log.e(TAG, "Error requesting visible circles : " + peopleData.getStatus());
        }

        // Setting the adapter already loaded with all contacts retrieved from
        // the connected user account



        selectAll = (RobotoBoldTextView) findViewById(R.id.selectAll);
        deselectAll = (RobotoBoldTextView) findViewById(R.id.deselectAll);

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrayListContacts.size(); i++) {
                    arrayListContacts.get(i).isSelected = true;
                }
                final ListView mListViewContacts = (ListView) findViewById(R.id.event_list);
                adapter=new ContactAdapter(FriendsList.this, arrayListContacts);
                mListViewContacts.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        });

        deselectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrayListContacts.size(); i++) {
                    arrayListContacts.get(i).isSelected = false;
                }
                adapter.notifyDataSetChanged();
            }

        });

        final ListView mListViewContacts = (ListView) findViewById(R.id.event_list);
        mListViewContacts.setAdapter(new ContactAdapter(this, arrayListContacts));

        // Setting a listener to monitor each line in the list view. Clicking it
        // will raise an intent to open the contact's profile in the Google+


//
//        final ListView mListViewContacts = (ListView) findViewById(R.id.event_list);
//        mListViewContacts.setAdapter(new ContactAdapter(this, arrayListContacts));

    }

    private void setPersonalInfo(Contact contact) {
//        final ListView mListViewContacts = (ListView) findViewById(R.id.event_list);
//      mListViewContacts.setAdapter(new ContactAdapter(this, arrayListContacts));
    }


}