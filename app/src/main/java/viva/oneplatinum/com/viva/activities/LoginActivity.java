package viva.oneplatinum.com.viva.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.fb.FacebookLogin;
import viva.oneplatinum.com.viva.fb.SessionStore;
import viva.oneplatinum.com.viva.gcm.GcmManager;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;

/**
 * Created by Dell on 3/16/2016.
 */
public class LoginActivity extends ParentActivity {
    public static final int RQUEST_FACEBOOK_LOGIN = 1001;
    AQuery aQuery;
    SharedPreferences userInfo;
    LinearLayout googleLoginView, facebookLoginView;
    GcmManager gcmManager;
    EditText Au_email, password;
    TextView sign_register, forgotPassword;
    ProgressDialog progress_dialog;
    Dialog addNewPostDialog, verificationDialog;
    EditText forgotPasswordEmail, verifCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viva_login);
        gcmManager = new GcmManager(this);

        Log.i("checkFb", "check01");
        aQuery = new AQuery(this);

        initView();


    }

    private void initView() {
        googleLoginView = (LinearLayout) findViewById(R.id.loginWithGoogle);
        facebookLoginView = (LinearLayout) findViewById(R.id.loginWithFb);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);

        Au_email = (EditText) findViewById(R.id.Au_email);
        password = (EditText) findViewById(R.id.password);
        sign_register = (TextView) findViewById(R.id.sign_register);
        Log.i("checkFb", "check01");
        googleLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(LoginActivity.this, GoogleLoginActivity.class);

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "No network connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        facebookLoginView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("checkFb", "check01");
                if (isNetworkAvailable()) {
                    initFbLogin();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "No network connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewPostDialog();
            }
        });

        sign_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Au_email.getText().length() == 0 || password.getText().length() == 0) {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    intent.putExtra("loginType", "emailReg");
                    startActivity(intent);
                    finish();
                    Log.i("checkblankFieldOk", "blankField");
                } else if (Au_email.getText().length() > 0 && GeneralUtil.isEmailValid(Au_email.getText().toString().trim()) && password.getText().length() == 0) {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    intent.putExtra("loginType", "emailReg");
                    intent.putExtra("email", Au_email.getText().toString().trim());
                    startActivity(intent);
                    Log.i("checkLengthButNoPasswd", "lengBtNoPswd");
                } else {
                    if (gcmManager.getRegistrationId(LoginActivity.this).isEmpty()) {
                        gcmManager.initGcmRegister();
                        gcmManager.setListner(new GcmManager.onCompleteFetchingGcmIdListner() {

                            @Override
                            public void onCompleteFetchingGcmId(String gcmId) {
                                //processLogin(gcmId)
                                processLogin(gcmId);
                                Log.i("checkPLogStart", "checkPLogStart");
                            }
                        });

                    } else {
                        processLogin(gcmManager.getRegistrationId(LoginActivity.this));
                        Log.i("checkP2LogStart", "checkPLogStart");
                    }
                }
            }
        });

    }

    private void showAddNewPostDialog() {
        addNewPostDialog = new Dialog(this,
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(
                R.layout.add_new_post_dialog_layout, null);
        forgotPasswordEmail = (EditText) view.findViewById(R.id.post);
        TextView addpost = (TextView) view.findViewById(R.id.addPost);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Forgot password ?");
        forgotPasswordEmail.setHint("Enter your email");
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                addNewPostDialog.dismiss();
            }
        });

        addpost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                hideSoftKeyboardAuto();
                String strPost = forgotPasswordEmail.getText().toString();
                if (strPost.length() == 0) {
                    forgotPasswordEmail.setError("Enter email");
                }
                else {
                    addNewPostDialog.dismiss();
                    forgotPasswordPost(strPost);
                    hideSoftKeyboardAuto();
                }

            }
        });
        addNewPostDialog.setContentView(view);
        addNewPostDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        addNewPostDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addNewPostDialog.show();

    }

    private void forgotPasswordPost(String strPost) {
        if (isNetworkAvailable()) {
            HashMap<String, String> params = new HashMap<String, String>();
            Log.d("getCategories", "check");
            params.put("email", strPost);
            mAqueryTask.sendRequest(DataHolder.FORGOT_PASSWORD, params, JSONObject.class, postDataCallback());
            // TODO Auto-generated catch block
        }

    }


    private AjaxCallback<JSONObject> postDataCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.i("checkpostCall", "" + result);

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            showVerificationDialog();
                        } else {
                            showToast(outpuJsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    private void showVerificationDialog() {
        verificationDialog = new Dialog(this,
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(
                R.layout.verification_dialog, null);
        verifCode = (EditText) view.findViewById(R.id.post);
        TextView verifpost = (TextView) view.findViewById(R.id.addPost);
        final TextView resendCode = (TextView) view.findViewById(R.id.resendCode);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Enter your code");
        //forgotPasswordEmail.setHint("Enter your email");

        resendCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                resendCodeMethod(DataHolder.RESEND_CODE);
            }
        });
        verifpost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String strPost = verifCode.getText().toString();
                if (strPost.length() == 0) {
                    verifCode.setError("Enter code");
                } else {
                    verificationDialog.dismiss();
                    verificationPost(strPost);
                }

            }
        });
        verificationDialog.setContentView(view);
        verificationDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        verificationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        verificationDialog.show();

    }

    public void resendCodeMethod(String url) {
        if (isNetworkAvailable()) {
            showLoading();
            Log.e("getCategoriesresponse", "getCategoriesresponse");
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("email", forgotPasswordEmail.getText().toString().trim());
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
                            showToast(outpuJsonObject.optString("message"));
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

    private void verificationPost(String str2Post) {
        HashMap<String, String> params = new HashMap<String, String>();
        Log.d("checkCodes", "check");
        params.put("code", str2Post);
        mAqueryTask.sendRequest(DataHolder.VERIFY_CODE, params, JSONObject.class, postCodeCallback());
        // TODO Auto-generated catch block

    }

    private AjaxCallback<JSONObject> postCodeCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.i("check00", "check" + result);

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        JSONObject obj = outpuJsonObject.getJSONObject("response");
                        String user_id = obj.optString("user_id");
                        String code = obj.optString("code");
                        Log.i("checkResponse", "check" + result);
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
//

//
                            showToast(outpuJsonObject.optString("message"));
                            Intent i = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                            i.putExtra("code", code);
                            i.putExtra("user_id", user_id);
                            startActivity(i);


                        } else {
                            showToast(outpuJsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    private void processLogin(String gcmId) {
        Log.i("checkLoginStarted", "check");
        boolean validation = true;

        if (Au_email.getText().toString().length() == 0) {
            Au_email.setError("Enter username");
            validation = false;

        } else if (!GeneralUtil.isEmailValid(Au_email.getText().toString())) {
            validation = false;
            Au_email.requestFocus();
            Au_email.setError("Please enter valid email address.");
        }
        if (password.getText().toString().length() == 0) {
            password.setError("Enter password");
            validation = false;
        }
        if (validation) {
            Log.i("checkValidDone", "check");
            loginRequest(DataHolder.LOGIN, gcmId);
            Log.i("check2ValidDone", "check");
        }
    }

    private void loginRequest(String url, String gcmId) {
        Log.v("checkLRRequest", "chek");
        if (isNetworkAvailable()) {
            Map<String, Object> params = null;
            try {
                JSONObject mainJson = new JSONObject();
                mProgressDialog.show();
                mainJson.put("email", Au_email.getText().toString());
                mainJson.put("password", password.getText().toString());
                mainJson.put("device_token", gcmId);
                mainJson.put("device_type", "A");
                StringEntity s = new StringEntity(mainJson.toString(),
                        HTTP.UTF_8);
                params = new HashMap<String, Object>();
                params.put(AQuery.POST_ENTITY, s);

                System.out.println(url + mainJson);

                aQuery.ajax(url, params,// joined_events.json,
                        // public_events.json, detail::
                        // event_view.json (token, event_id)
                        JSONObject.class, loginRequestCallback());

            } catch (Exception e) {

            }

        } else {
            showToast("Please check your internet connection.");
        }

    }

    private AjaxCallback<JSONObject> loginRequestCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.i("checkResp", "" + result);
                mProgressDialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject object = result.getJSONObject("output");
                        int userStatus = object.optInt("status");
                        String message = object.optString("message");
                        if (userStatus == 1) {
                            JSONObject userObject = object.getJSONObject("response");
                            Log.d("checkresponse", "resp" + userObject);
                            String token = userObject.optString("token");
                            String email = userObject.optString("email");
                            String user_id = userObject.optString("user_id");
                            String name = userObject.optString("name");
                            int redirect = userObject.optInt("redirect");
                            Log.d("checkUserId", "check" + user_id);
                            mVivaApplicatiion.setUserData(DataHolder.TOKEN, token);
                            mVivaApplicatiion.setUserData(DataHolder.USEREMAIL, email);
                            mVivaApplicatiion.setUserData(DataHolder.USERNAME, name);
                            mVivaApplicatiion.setUserData(DataHolder.USERID, user_id);
                            mVivaApplicatiion.setUserData(DataHolder.ISFROMREGISTER, true);
                            showToast(message);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            if (redirect == 1) {
                                intent.putExtra("redirect", true);
                            }
                            startActivity(intent);
                            showToast(message);
                            finish();
                        } else if (userStatus == 0) {
                            showToast(message);
                        }
                        if (userStatus == 2) {
                            JSONObject response = object
                                    .getJSONObject("response");
                            showToast(message);
                            Log.d("response", response + "");
                            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                            //Under construction.........
                            intent.putExtra("loginType", "emailReg");

                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }

            }
        }.header("Content-Type", "application/json");

    }

    private void initFbLogin() {
        Intent fbLoginIntent = new Intent(this, FacebookLogin.class);
        Bundle params = new Bundle();
        params.putInt(FacebookLogin.LOGTYPE, FacebookLogin.LOGIN);
        fbLoginIntent.putExtras(params);
        startActivityForResult(fbLoginIntent, RQUEST_FACEBOOK_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RQUEST_FACEBOOK_LOGIN) {
            if (gcmManager.getRegistrationId(LoginActivity.this).isEmpty()) {
                gcmManager.initGcmRegister();
                gcmManager.setListner(new GcmManager.onCompleteFetchingGcmIdListner() {
                    @Override
                    public void onCompleteFetchingGcmId(String gcmId) {
                        checkUserLogin(gcmId);
                    }
                });

            } else {
                checkUserLogin(gcmManager.getRegistrationId(LoginActivity.this));
            }
        }
    }

    private void checkUserLogin(String gcmId) {
        HashMap<String, String> map = (HashMap<String, String>) SessionStore.fetchFacebookInformation(this);
        Log.v("facebookInformation", map.toString());
        if (isNetworkAvailable()) {
            mProgressDialog.setMessage("Signing in....");
            mProgressDialog.show();
            HashMap<String, String> mainJson = new HashMap<String, String>();
            if (map.get(SessionStore.FB_EMAIL) != null) {
                mainJson.put("email", map.get(SessionStore.FB_EMAIL));
            } else {
                mainJson.put("email", "");
            }

            mainJson.put("fbid", map.get(SessionStore.USER_ID));
            //required or not
            mainJson.put("email", map.get(SessionStore.FB_EMAIL));
            mainJson.put("device_token", gcmId);
            mainJson.put("device_type", "A");
            mAqueryTask.sendRequest(DataHolder.FACEBOOK_LOGIN, mainJson, JSONObject.class, dataCallBack());
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection available.", Toast.LENGTH_LONG).show();
        }
    }

    Dialog loadingDialog;

    private void showLoadingDialog() {
        loadingDialog = new Dialog(this,
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_loading,
                null);
        loadingDialog.addContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    private AjaxCallback<JSONObject> dataCallBack() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                // {"output":{"response":"","message":"Invalid username or password, try again","action":"login","status":0}}
                // {"output":{"response":{"data":"Sharmila Khadka","token":"x21v1ft5ms1cvuol5510px9lgmebd92a"},"message":"You are authorized user.","action":"login","status":1}}
                mProgressDialog.dismiss();
                if (result != null) {
                    try {
                        JSONObject object = result.getJSONObject("output");
                        int userStatus = object.optInt("status");
                        String message = object.optString("message");
                        if (userStatus == 2) {
                            HashMap<String, String> map = (HashMap<String, String>) SessionStore.fetchFacebookInformation(LoginActivity.this);
                            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                            intent.putExtra("loginType", "facebook");
                            intent.putExtra("isFromFacebook", true);
                            intent.putExtra("image", map.get(SessionStore.USER_IMAGE));
                            intent.putExtra("familyName", map.get(SessionStore.LNAME));
                            intent.putExtra("givenName", map.get(SessionStore.FNAME));
                            intent.putExtra("displayName", map.get(SessionStore.FACEBOOK_UNAME));
                            intent.putExtra("email", map.get(SessionStore.FB_EMAIL));
                            intent.putExtra("gender", map.get(SessionStore.GENDER));
                            intent.putExtra("birthday", map.get(SessionStore.BDAY));
                            intent.putExtra("id", map.get(SessionStore.FACEBOOK_ID));
                            startActivity(intent);
                            finish();
                        }
                        //check
                        else if (userStatus == 1) {
                            JSONObject userObject = object.getJSONObject("response");
                            String token = userObject.optString("token");
                            String email = userObject.optString("email");
                            String user_id = userObject.optString("userId");
                            String name = userObject.optString("name");
                            String prf_image = userObject.optString("prf_image");
                            mVivaApplicatiion.setUserData(DataHolder.TOKEN, token);
                            mVivaApplicatiion.setUserData(DataHolder.PRF_IMAGE, prf_image);
                            mVivaApplicatiion.setUserData(DataHolder.USEREMAIL, email);
                            mVivaApplicatiion.setUserData(DataHolder.USERNAME, name);
                            mVivaApplicatiion.setUserData(DataHolder.USERID, user_id);
                            mVivaApplicatiion.setUserData(DataHolder.ISFROMFACEBOOK, true);
                            showToast(message);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (userStatus == 0) {
                            showToast(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    HashMap<String, String> map = (HashMap<String, String>) SessionStore.fetchFacebookInformation(LoginActivity.this);
                    if (isNetworkAvailable()) {
//                        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
//                        intent.putExtra("loginType", "facebook");
//                        intent.putExtra("image", map.get(SessionStore.USER_IMAGE));
//                        intent.putExtra("familyName", map.get(SessionStore.LNAME));
//                        intent.putExtra("givenName", map.get(SessionStore.FNAME));
//                        intent.putExtra("displayName", map.get(SessionStore.FACEBOOK_UNAME));
//                        intent.putExtra("email", map.get(SessionStore.FB_EMAIL));
//                        intent.putExtra("gender", map.get(SessionStore.GENDER));
//                        intent.putExtra("birthday",map.get(SessionStore.BDAY));
//                        intent.putExtra("id",map.get(SessionStore.FACEBOOK_ID));
//                        startActivity(intent);
//                        finish();
                    }
                }
            }
        };

    }

    private void hideLoadingDialog() {
        loadingDialog.dismiss();
        //finish();
    }
    public void hideSoftKeyboardAuto() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(forgotPasswordEmail.getApplicationWindowToken(), 0);

    }
}





