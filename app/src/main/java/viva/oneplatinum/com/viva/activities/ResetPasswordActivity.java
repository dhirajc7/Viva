package viva.oneplatinum.com.viva.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.utils.DataHolder;

/**
 * Created by Dell on 4/6/2016.
 */
public class ResetPasswordActivity extends ParentActivity {
    String code, user_id, password;
    EditText resetEmail, resetPassword, resetConfirmPassword;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        initViews();
    }

    private void initViews() {
        code=getIntent().getStringExtra("code");
        Log.i("checkCode",""+code);
        user_id=getIntent().getStringExtra("user_id");

        save= (Button) findViewById(R.id.save);
        resetEmail= (EditText) findViewById(R.id.resetEmail);
        resetPassword= (EditText) findViewById(R.id.resetPassword);
        resetConfirmPassword= (EditText) findViewById(R.id.resetConfirmPassword);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    resetPassword(DataHolder.RESET_PASSWORD);
                }
            }
        });


    }
    private boolean validation() {
        Boolean validation = true;

        if (resetPassword.getText().toString().length() == 0) {
            resetPassword.setError("Enter password");
            validation = false;
        } else if (resetPassword.getText().toString().length() < 4) {
            validation = false;
            resetPassword.setError("please have a minimum of 4 characters.");
        }
        if (resetConfirmPassword.getText().toString().length() == 0) {
            resetConfirmPassword.setError("Enter confirm password");
            validation = false;
        }
        if (!resetPassword.getText().toString()
                .equals(resetConfirmPassword.getText().toString())) {
            validation = false;
            resetConfirmPassword.setError("Password and confirm password not same");
        }
        return validation;
    }
    private void resetPassword(String url) {
        if (isNetworkAvailable()) {
          showLoading();
            Log.e("getCategoriesresponse", "getCategoriesresponse");
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("code", code);
            params.put("user_id", user_id);
            params.put("password",resetConfirmPassword.getText().toString().trim());
            Log.i("checkReset",""+code+user_id+resetConfirmPassword);
            mAqueryTask.sendRequest(url, params, JSONObject.class, getMyEventsCallback());

        }
    }
    private AjaxCallback<JSONObject> getMyEventsCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.e("check", result.toString());
//                mProgressDialog.dismiss();

                if (result != null) {
                    try {
                        JSONObject outpuJsonObject=result.getJSONObject("output");
                        int userStatus=outpuJsonObject.optInt("status");
                        if(userStatus==1) {
                            showToast(outpuJsonObject.optString("message"));
                            Intent i=new Intent(ResetPasswordActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                            if(userStatus==2) {
                                showToast(outpuJsonObject.optString("message"));
                            }
                                if(userStatus==0){
                                    showToast(outpuJsonObject.optString("message"));
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
}
