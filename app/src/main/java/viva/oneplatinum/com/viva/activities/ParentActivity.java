package viva.oneplatinum.com.viva.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import java.util.Calendar;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.app.VivaApplication;
import viva.oneplatinum.com.viva.asynctask.AqueryTask;

public class ParentActivity extends AppCompatActivity {

	public AqueryTask mAqueryTask;

	public static ProgressDialog mProgressDialog;

	public VivaApplication mVivaApplicatiion;

	public String mUserToken, mUserId;

	public AQuery mAquery;

	public View mLayoutLoading, mLayoutErrorLoading, mLayoutDataView,
			mLayoutReloadData;
	public TextView mTxtErrorMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mAquery = new AQuery(this);
		mAqueryTask = new AqueryTask(this);
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mVivaApplicatiion = (VivaApplication) getApplication();
		// mUserToken =
		// mPocketButlerApplication.getUserData(DataHolder.USER_TOKEN);
		// mUserId = mPocketButlerApplication.getUserData("id");

	}

	public void initViews(View view) {
		mLayoutLoading = view.findViewById(R.id.layoutLoading);
		mLayoutErrorLoading = view.findViewById(R.id.layoutErrorLoading);
		mLayoutDataView = view.findViewById(R.id.layoutDataView);
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

	public void showErrorLoading(String message,int drawable) {
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
        LayoutInflater myInflater = LayoutInflater.from(this);
        View view = myInflater.inflate(R.layout.toast_layout, null);
        Toast mytoast = new Toast(this);
        mytoast.setView(view);
        TextView txtToast = (TextView) view.findViewById(R.id.txtToast);
        txtToast.setText(message);
        mytoast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        mytoast.setDuration(Toast.LENGTH_SHORT);
        mytoast.show();
    }
	public boolean isNetworkAvailable() {

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && (networkInfo.isConnected())) {
			return true;
		}
		return false;
	}

	public void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		View view = getWindow().getCurrentFocus();
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

		}
	}

	public void addDoneinKeyboard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			super.onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("check", "The onPause() event");
		Calendar mCalendar=Calendar.getInstance();
//		mVivaApplicatiion.setUserData(Da);
	}

	/** Called when the activity is no longer visible. */
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("check", "The onStop() event");
	}

	/** Called just before the activity is destroyed. */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("check", "The onDestroy() event");
	}
}
