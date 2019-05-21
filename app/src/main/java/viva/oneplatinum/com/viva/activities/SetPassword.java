package viva.oneplatinum.com.viva.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.gcm.GcmManager;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;

/**
 * Created by Dell on 3/23/2016.
 */
public class SetPassword extends ParentActivity {
    String imageUrl, familyName, givenName, gender, dob, email;
    TextView textViewfname, textViewlname, textViewMale, textViewFemale, textViewOther;
    EditText editTextEmail, editYear;
    TextView textLocation, textWebsite;
//    Toolbar mToolBar;
//    FontIcon backBtnToolbar;
    Button save;
    EditText password, confirmPassword;
    String location, website;
    GcmManager gcmManager;
    int is_private=0;
    Toolbar mToolBar;
    FontIcon backBtnToolbar, nextBtnToolbar;
    RobotoBoldTextView titleToolbar;
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        gcmManager = new GcmManager(this);
        initView();
//        initToolbar();

    }

    public void initView() {
        is_private = getIntent().getIntExtra("is_private",0);
        Log.d("is_private", String.valueOf(is_private));
        imageUrl = getIntent().getStringExtra("picture");
        familyName = getIntent().getStringExtra("last_name");
        givenName = getIntent().getStringExtra("first_name");
        gender = getIntent().getStringExtra("gender");
        email = getIntent().getStringExtra("email");
        dob = getIntent().getStringExtra("dob");
        location = getIntent().getStringExtra("address");
        website = getIntent().getStringExtra("website");
        Log.i("checkIncomingData","checkData"+imageUrl+familyName+givenName+gender+email+dob+location+website);

        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        save = (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (gcmManager.getRegistrationId(SetPassword.this).isEmpty()) {
                        gcmManager.initGcmRegister();
                        gcmManager.setListner(new GcmManager.onCompleteFetchingGcmIdListner() {
                            @Override
                            public void onCompleteFetchingGcmId(String gcmId) {
                                register(gcmId);
                            }
                        });

                    } else {
                        register(gcmManager.getRegistrationId(SetPassword.this));
                    }

              }
            }
        });
    }
    private void initToolbar() {
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

        titleToolbar.setVisibility(View.GONE);
        nextBtnToolbar.setText("s");
        nextBtnToolbar.setTextColor(getResources().getColor(R.color.Blue));
        nextBtnToolbar.setVisibility(View.GONE);

//        titleToolbar.setTextColor(getResources().getColor(R.color.txt_color_default));
        backBtnToolbar.setText("o");
        backBtnToolbar.setTextColor(getResources().getColor(R.color.red));
        backBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(SetPassword.this,SignupActivity.class);
//                startActivity(i);
                finish();
            }
        });


        return customView;
    }

    private boolean validation() {
        Boolean validation = true;

        if (password.getText().toString().length() == 0) {
            password.setError("Enter password");
            validation = false;
        } else if (password.getText().toString().length() < 4) {
            validation = false;
            password.setError("please have a minimum of 4 characters.");
        }
        if (confirmPassword.getText().toString().length() == 0) {
            confirmPassword.setError("Enter confirm password");
            validation = false;
        }
        if (!password.getText().toString()
                .equals(confirmPassword.getText().toString())) {
            validation = false;
            confirmPassword.setError("Password and confirm password not same");
        }
        return validation;
    }

    private void register(String gcmId) {
        if (isNetworkAvailable()) {
            showLoadingDialog();
            HashMap<String, Object>
                    mainJson = new HashMap<String, Object>();
            mainJson.put("picture", imageUrl);
            mainJson.put("last_name", familyName);
            mainJson.put("first_name", givenName);
            mainJson.put("email", email);
            mainJson.put("gender", gender);
            mainJson.put("dob", dob);
            mainJson.put("address", location);
            mainJson.put("website", website);
            mainJson.put("password", password.getText().toString().trim());

            mainJson.put("is_private", is_private);
            Log.d("mainJsonPrivate", String.valueOf(is_private));

            mainJson.put("device_token",gcmId);
            mainJson.put("device_type", "A");
            Log.i("checkParams", "params" + mainJson);
            mAqueryTask.sendPostObjectRequest(DataHolder.REGISTER, mainJson, JSONObject.class, registerCallBack());

        }
    }
    protected AjaxCallback<JSONObject> registerCallBack() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.i("checkResponse", "responseCheck"+result);
                hideLoadingDialog();
                mProgressDialog.dismiss();
                if (result != null) {

                        try {
                            JSONObject object = result.getJSONObject("output");
                            int userStatus = object.optInt("status");
                            Log.i("checkStatus","checkstatus"+userStatus);
                            String message = object.optString("message");
                            if (userStatus == 1) {
                                JSONObject userObject = object.getJSONObject("response");
                                String token = userObject.optString("token");
                                String email = userObject.optString("email");
                                String user_id = userObject.optString("user_id");
                                String name = userObject.optString("name");
                                mVivaApplicatiion.setUserData(DataHolder.TOKEN, token);
                                mVivaApplicatiion.setUserData(DataHolder.USEREMAIL, email);
                                mVivaApplicatiion.setUserData(DataHolder.USERNAME, name);
                                mVivaApplicatiion.setUserData(DataHolder.USERID, user_id);
                                showToast(message);
                                Intent intent = new Intent(SetPassword.this, HomeActivity.class);

                                startActivity(intent);
                                finish();
                            } else if (userStatus == 0) {
                                showToast(message);
                            } else if (userStatus == 2) {
                                showToast(message);
                                editTextEmail.setError(message);
                                editTextEmail.setFocusable(true);
                            } else if (userStatus == 3) {
                                new AlertDialog.Builder(SetPassword.this).setTitle("Viva")
                                        .setMessage(message)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // continue with delete
                                            }
                                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                }).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        hideLoadingDialog();
                        finish();

                } else {

                }

            }
        };

    }
    Dialog loadingDialog;

    private void showLoadingDialog() {
        loadingDialog = new Dialog(this,
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(R.layout.loading_layout,
                null);
        loadingDialog.addContentView(view, new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(true);
        loadingDialog.show();

    }

    private void hideLoadingDialog() {
        loadingDialog.dismiss();
    }


}
