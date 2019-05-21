package viva.oneplatinum.com.viva.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("Push notification", "Hello cruizco PUSH" + "::"
				+ intent.getAction().toString());
		System.out.println("Push notification: Hello cruizco PUSH" + "::"
				+ intent.getAction().toString());
		if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
			ComponentName comp = new ComponentName(context.getPackageName(),
					GCMIntentService.class.getName());
			startWakefulService(context, (intent.setComponent(comp)));
			setResultCode(Activity.RESULT_OK);
		}
	}
}
