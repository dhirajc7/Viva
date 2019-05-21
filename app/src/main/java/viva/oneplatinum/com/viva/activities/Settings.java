package viva.oneplatinum.com.viva.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.SharedPreferencesHelper.SharedPreferencesHelper;
import viva.oneplatinum.com.viva.utils.DataHolder;

/**
 * Created by D-MAX on 3/18/2016.
 */
public class Settings extends  ParentActivity {
    SharedPreferencesHelper shelper;
    Switch mes, vib, voi;
    LinearLayout changepass, delaccnt,logOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        shelper = new SharedPreferencesHelper(Settings.this);
        mes = (Switch) findViewById(R.id.mes_notif_switch);
        voi = (Switch) findViewById(R.id.voice_switch);
        vib = (Switch) findViewById(R.id.vib_switch);
        delaccnt = (LinearLayout) findViewById(R.id.del_account);
        logOut = (LinearLayout) findViewById(R.id.logOut);

        mes.setChecked(shelper.getNotifications());
        vib.setChecked(shelper.getVibrate());
        voi.setChecked(shelper.getSound());


        // save the toggle settings to sharedpreferences
        mes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                shelper.setNotifications(arg1);
            }
        });
        vib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                shelper.setVibrate(arg1);
            }
        });
        voi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                shelper.setSound(arg1);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut(DataHolder.LOGOUT);
            }
        });

        delaccnt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d("delete", "delete account");
                AlertDialog.Builder dialog = new AlertDialog.Builder(Settings.this);
                dialog.setMessage(R.string.account_del_terms);
                dialog.setPositiveButton("Delete Account", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // account deleted
                        deleteAccount();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // account not deleted
                    }
                });

                // show the dialog
                AlertDialog dialogshow = dialog.create();
                dialogshow.show();
            }
        });

    }

    private void logOut(String url) {
        if (isNetworkAvailable()) {
            showLoadingDialog();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", mVivaApplicatiion.getUserData(DataHolder.TOKEN));
            mAqueryTask.sendRequest(url, params, JSONObject.class, LogoutCallback());

        } else {
           showToast("connect to internet");
        }
    }

    private AjaxCallback<JSONObject> LogoutCallback() {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.e("getCategoriesresponse", result.toString());

                if (result != null) {
                    hideLoadingDialog();
                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                           mVivaApplicatiion.logOutUser();
                            finish();
                            startActivity(new Intent(Settings.this,
                                    LoginActivity.class));



                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    public void deleteAccount() {
        if (isNetworkAvailable()) {
            mProgressDialog.setMessage("Deleting your Account..");
            mProgressDialog.show();
            HashMap<String, Object> params;
//            String url = DataHolder.DELETE_ACCOUNT;
            params = new HashMap<String, Object>();
//            params.put("token", userInfo.getString(DataHolder.TOKEN, ""));

//            mAqueryTask.sendRequest(url, params, JSONObject.class, DeleteAccountCallback());

        } else {
//			showErrorLoading("Error loading data");
//            Toast.makeText(getActivity(), "No internet connection available.", 1).show();
        }
    }


//    private AjaxCallback<JSONObject> DeleteAccountCallback() {
//        return new AjaxCallback<JSONObject>() {
//            @Override
//            public void callback(String url, JSONObject object, AjaxStatus status) {
//                super.callback(url, object, status);
//                Log.d("at ajaxcallback deleteaccount", object + "");
//                mProgressDialog.dismiss();
//                if (object != null) {
//                    int astatus = ParseUtility.deleteProfile(object);
//                    if (astatus == 1) {
//                        // userprofile successfully deleted
//                        SharedPreferences userInfo = getActivity().getSharedPreferences(DataHolder.USERINFO, 0);
//                        userInfo.edit().clear().commit();
//                        Toast.makeText(getActivity(), "Your Account Has Been successfully Deleted ", Toast.LENGTH_SHORT)
//                                .show();
//                        Intent intent = new Intent(getActivity(), LoginActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        // mCruizcoApplication.logOutUser();
//                        startActivity(intent);
//
//                        getActivity().finish();
//
//                    } else {
//
//                        Toast.makeText(getActivity(), "user profile not deleted", Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    Toast.makeText(getActivity(), "No response from server", Toast.LENGTH_LONG).show();
//                }
//            }
//        };
//    };

    Dialog loadingDialog;

    private void showLoadingDialog() {
        loadingDialog = new Dialog(Settings.this,
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View view = LayoutInflater.from(Settings.this).inflate(R.layout.loading_layout,
                null);
        loadingDialog.addContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

    }

    private void hideLoadingDialog() {
        loadingDialog.dismiss();
    }
}
