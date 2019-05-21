package viva.oneplatinum.com.viva.dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.app.VivaApplication;
import viva.oneplatinum.com.viva.asynctask.AqueryTask;

/**
 * Created by Dell on 4/8/2016.
 */
public class ParentDialog extends DialogFragment {
    public AqueryTask mAqueryTask;

    public static ProgressDialog mProgressDialog;

    public VivaApplication mVivaApplicatiion;

    public String mUserToken, mUserId;

    public AQuery mAquery;

    public View mLayoutLoading, mLayoutErrorLoading, mLayoutDataView,
            mLayoutReloadData;
    public TextView mTxtErrorMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mAquery = new AQuery(getActivity());
        mAqueryTask = new AqueryTask(getActivity());
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCanceledOnTouchOutside(false);
        mVivaApplicatiion = (VivaApplication) getActivity().getApplication();
        // mUserToken =
        // mPocketButlerApplication.getUserData(DataHolder.USER_TOKEN);
        // mUserId = mPocketButlerApplication.getUserData("id");

    }


    public void initViews(View view) {
        mLayoutLoading = view.findViewById(R.id.layoutLoading);
        mLayoutErrorLoading = view.findViewById(R.id.layoutErrorLoading);
        //mLayoutDataView = view.findViewById(R.id.layoutDataView);
        mTxtErrorMessage = (TextView) view
                .findViewById(R.id.txt_error_loading_message);
        mLayoutReloadData = view.findViewById(R.id.txt_error_loading);
    }

    public void showLoading() {
        if (mLayoutErrorLoading != null) {
            mLayoutErrorLoading.setVisibility(View.GONE);
        }
        if (mLayoutDataView != null) {
            mLayoutDataView.setVisibility(View.GONE);
        }
        if (mLayoutLoading != null) {
            mLayoutLoading.setVisibility(View.VISIBLE);

        }
    }

    public void showErrorLoading(String message) {
        if (mLayoutLoading != null) {
            mLayoutLoading.setVisibility(View.GONE);
        }
        if (mLayoutErrorLoading != null) {
            mLayoutErrorLoading.setVisibility(View.VISIBLE);
            mTxtErrorMessage.setText(message);

        }
        if (mLayoutDataView != null) {
            mLayoutDataView.setVisibility(View.GONE);
        }
    }

    public void showDataView() {
        if (mLayoutLoading != null) {
            mLayoutLoading.setVisibility(View.GONE);
        }
        if (mLayoutErrorLoading != null) {
            mLayoutErrorLoading.setVisibility(View.GONE);
        }
        if (mLayoutDataView != null) {
            mLayoutDataView.setVisibility(View.VISIBLE);
        }
    }

    public void showToast(String message) {
        LayoutInflater myInflater = LayoutInflater.from(getActivity());
        View view = myInflater.inflate(R.layout.toast_layout, null);
        Toast mytoast = new Toast(getActivity());
        mytoast.setView(view);
        TextView txtToast = (TextView) view.findViewById(R.id.txtToast);
        txtToast.setText(message);
        mytoast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        mytoast.setDuration(Toast.LENGTH_LONG);
        mytoast.show();
    }
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && (networkInfo.isConnected())) {
            return true;
        }
        return false;
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getWindow().getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    public void addDoneinKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    public interface OnDismissListner {
        public void onDismissDialog();
    }
}
