package viva.oneplatinum.com.viva.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.gcm.GcmManager;
import viva.oneplatinum.com.viva.widgets.FontIcon;

/**
 * Created by Dell on 3/23/2016.
 */
public class ForgetPassword extends ParentActivity {
    String imageUrl, familyName, givenName, gender, dob, email;
    TextView textViewfname, textViewlname, textViewMale, textViewFemale, textViewOther;
    EditText editTextEmail, editYear;
    TextView textLocation, textWebsite;
    Toolbar mToolBar;
    FontIcon backBtnToolbar;
    Button save;
    EditText password, confirmPassword;
    String location, website;
    GcmManager gcmManager;

    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        gcmManager = new GcmManager(this);
        initView();


    }

    public void initView() {
        imageUrl = getIntent().getStringExtra("image");
        familyName = getIntent().getStringExtra("familyName");
        givenName = getIntent().getStringExtra("givenName");
        gender = getIntent().getStringExtra("gender");
        email = getIntent().getStringExtra("email");
        dob = getIntent().getStringExtra("dob");
        location = getIntent().getStringExtra("location");
        website = getIntent().getStringExtra("website");


        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        save = (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    register();
                    Intent intent=new Intent(ForgetPassword.this, HomeActivity.class);
                    startActivity(intent);

              }
            }
        });
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

    private void register() {
        if (isNetworkAvailable()) {
            HashMap<String, String>
                    mainJson = new HashMap<String, String>();
            mainJson.put("image", imageUrl);
            mainJson.put("familyName", familyName);
            mainJson.put("givenName", givenName);
            mainJson.put("email", email);
            mainJson.put("gender", gender);
            mainJson.put("dob", dob);
            mainJson.put("location", location);
            mainJson.put("website", website);
            mainJson.put("password", password.getText().toString().trim());

            mainJson.put("device_token", gcmManager.getRegistrationId(ForgetPassword.this));
            mainJson.put("device_type", "A");
            mAqueryTask.sendRequest("", mainJson, JSONObject.class, registerCallBack());

        }
    }
    protected AjaxCallback<JSONObject> registerCallBack() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);

                if (result != null) {
                    try {
                        Toast.makeText(
                                ForgetPassword.this,
                                result.getJSONObject("output").getString(
                                        "message"), Toast.LENGTH_LONG).show();
                        hideLoadingDialog();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        loadingDialog.setCancelable(false);
        loadingDialog.show();

    }

    private void hideLoadingDialog() {
        loadingDialog.dismiss();
    }


}
