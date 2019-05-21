package viva.oneplatinum.com.viva.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.gcm.GcmManager;
import viva.oneplatinum.com.viva.utils.DataHolder;

/**
 * Created by Dell on 3/15/2016.
 */
public class GoogleLoginActivity extends ParentActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener, GoogleApiClient.ConnectionCallbacks {
    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    GcmManager gcmManager;
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private int request_code=0;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress=false;
    private boolean is_gglSignInBtn_clicked=true;
    ProgressDialog progress_dialog;
    String imageUrl, familyName, givenName, gender, displayName, email, googleId ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.google_login_activity);
        gcmManager = new GcmManager(this);
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setMessage("Signing in....");
        buildNewGoogleApiClient();
        progress_dialog.show();
    }




    private void buildNewGoogleApiClient() {
        google_api_client =  new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }


    @Override
    protected void onStart() {
        super.onStart();
        google_api_client.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (google_api_client.isConnected()) {
            google_api_client.connect();
        }
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
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {

        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (responseCode != RESULT_OK) {
                is_gglSignInBtn_clicked = false;
                progress_dialog.dismiss();

            }

            is_intent_inprogress = false;

            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        }

    }
    @Override
    public void onConnected(Bundle arg0) {
        is_gglSignInBtn_clicked = false;

        getProfileInfo();

    }

    @Override
    public void onConnectionSuspended(int i) {
        google_api_client.connect();
    }







    private void gPlusSignIn() {
        if (!google_api_client.isConnecting()) {

            //progress_dialog.show();
                is_gglSignInBtn_clicked = true;
                resolveSignInError();


        }
    }

    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();
            }
        }
    }

    private void getProfileInfo() {
        try {

            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);

                    Log.i("googleEmail", "" + (Plus.AccountApi.getAccountName(google_api_client)));
                        email=""+(Plus.AccountApi.getAccountName(google_api_client));
                setPersonalInfo(currentPerson);
            } else {
                Toast.makeText(getApplicationContext(),
                        "No Personal info mention", Toast.LENGTH_LONG).show();
                progress_dialog.dismiss();
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setPersonalInfo(Person currentPerson) {


        Log.i("googleLoginData", "" + currentPerson);

        try {
            JSONObject googleJsonObject=new JSONObject(currentPerson+"");
            imageUrl=googleJsonObject.getJSONObject("image").optString("url");
            familyName=googleJsonObject.getJSONObject("name").optString("familyName");
            givenName=googleJsonObject.getJSONObject("name").optString("givenName");
            gender=googleJsonObject.optString("gender");
            displayName=googleJsonObject.optString("displayName");
            googleId=googleJsonObject.optString("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //String firstName=currentPerson.getName();


        if (gcmManager.getRegistrationId(GoogleLoginActivity.this).isEmpty()) {
            gcmManager.initGcmRegister();
            gcmManager.setListner(new GcmManager.onCompleteFetchingGcmIdListner() {

                @Override
                public void onCompleteFetchingGcmId(String gcmId) {
                   gPlusLogin(DataHolder.GOOGLE_LOGIN,gcmId);

                }
            });
        } else {
            gPlusLogin(DataHolder.GOOGLE_LOGIN, gcmManager.getRegistrationId(GoogleLoginActivity.this));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginWithGoogle:
                gPlusSignIn();
                break;
        }
    }

    public void gPlusLogin(final String url,String gcmId){
        if(isNetworkAvailable()){
                        HashMap<String, Object> params=new HashMap<String, Object>();
                        //check this if it must be googleid or id
                        params.put("googleid",googleId);
                        params.put("email",email);
                        params.put("device_type","A");
                        params.put("device_token",gcmId);
                        Log.i("checkURL","checkURL"+url+params);
                        mAqueryTask.sendPostRequest(url, params, JSONObject.class, dataCallBack());

                    }
    }
    private AjaxCallback<JSONObject> dataCallBack() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.i("checkURL",""+result);
                // {"output":{"response":"","message":"Invalid username or password, try again","action":"login","status":0}}
                // {"output":{"response":{"data":"Sharmila Khadka","token":"x21v1ft5ms1cvuol5510px9lgmebd92a"},"message":"You are authorized user.","action":"login","status":1}}
                progress_dialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject object=result.getJSONObject("output");
                        int userStatus=object.optInt("status");
                        String message = object.optString("message");
                        if (userStatus==2){
                            Intent intent=new Intent(GoogleLoginActivity.this,SignupActivity.class);
                            intent.putExtra("image",imageUrl);
                            Log.i("imageUrl",imageUrl);
                            intent.putExtra("loginType","google");
                            intent.putExtra("id",googleId);
                            intent.putExtra("familyName",familyName);
                            intent.putExtra("givenName",givenName);
                            intent.putExtra("displayName",displayName);
                            intent.putExtra("email",email);
                            intent.putExtra("gender",gender);
                            startActivity(intent);
                            finish();
                        } if(userStatus==1){
                            JSONObject userObject = object.getJSONObject("response");
                            String token = userObject.optString("token");
                            String email = userObject.optString("email");
                            String user_id = userObject.optString("user_id");
                            String name = userObject.optString("name");
                            mVivaApplicatiion.setUserData(DataHolder.TOKEN, token);
                            mVivaApplicatiion.setUserData(DataHolder.USEREMAIL, email);
                            mVivaApplicatiion.setUserData(DataHolder.USERNAME, name);
                            mVivaApplicatiion.setUserData(DataHolder.USERID, user_id);
                            Log.i("callBack", "googleCheck");
                            showToast(message);
                            Intent intent=new Intent(GoogleLoginActivity.this,HomeActivity.class);
                            startActivity(intent);

                            finish();
                            } else if (userStatus == 0) {
                                showToast(message);
                            }
                    //check.....

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{

//                    Intent intent=new Intent(GoogleLoginActivity.this,SignupActivity.class);
//                    intent.putExtra("image",imageUrl);
//                    Log.i("imageUrl",imageUrl);
//                    intent.putExtra("familyName",familyName);
//                    intent.putExtra("givenName",givenName);
//                    intent.putExtra("familyName",familyName);
//                    intent.putExtra("displayName",displayName);
//                    intent.putExtra("email",email);
//                    intent.putExtra("gender",gender);
//                    Log.i("callBack","googleCheck");
//                    startActivity(intent);
//                    finish();
                }
            }
        };

    }

}
