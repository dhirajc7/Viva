package viva.oneplatinum.com.viva.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.SharedPreferencesHelper.SharedPreferencesHelper;

public class GCMIntentService extends IntentService {
	String type = "", message = "", user = "", id = "", messageType = "", gender, token, user_id;
	int d;
	private NotificationManager mNotificationManager;
	public GCMIntentService() {
		super("GcmIntentService");
	}
	SharedPreferencesHelper shelper;
	@Override
	protected void onHandleIntent(Intent intent) {
		shelper = new SharedPreferencesHelper(this);
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		Log.v("GCMRESPONSE", extras.toString() + "::" + messageType);
		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				if (shelper.getNotifications()) {
					sendNotification(extras);
				}

			}
		}
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(Bundle bundle) {
		if (bundle.getString("info") == null) {
			return;
		}



		Log.e("Message fromserver", bundle.getString("info") + "  " + bundle.toString());
		try {
			JSONObject obj = new JSONObject(bundle.getString("info"));
			if (obj.has("type")) {
				type = obj.optString("type");
			}
			if (obj.has("messageType")) {
				messageType = obj.optString("messageType");
			}
			if (obj.has("message")) {
				message = obj.optString("message");
			}
			if (obj.has("id")) {
				id = obj.optString("id");
			}
			mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent intent = new Intent();
			NotificationCompat.Builder mBuilder;
			mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(bundle.getString("title"));
			// Detail the notification here
			// Detail the notification here

			if (shelper.getSound()) {
				Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				mBuilder.setSound(uri);
			}
			if (shelper.getVibrate()) {
				mBuilder.setVibrate(new long[] { 700, 700 });
			}

			if (type.equalsIgnoreCase("group")) {
				// check message type
				if (messageType.equalsIgnoreCase("invite")) {
					// go to invite page
//					intent = new Intent(getApplicationContext(), InviteActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);

					intent.putExtra("selected", "group");
//					intent.putExtra("isFromPush", isFromPush);
					// no need of id here
					// intent.putExtra("id", id);
				} else if (messageType.equalsIgnoreCase("comment")) {
					// go to group detail page
//					intent = new Intent(getApplicationContext(), GroupDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("isPost", true);
					intent.putExtra("selected", "group");
					intent.putExtra("groupId", id);
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("reply")) {
					// go to group detail page
//					intent = new Intent(getApplicationContext(), GroupDetailActivity.class);

					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("selected", "group");
					intent.putExtra("isPost", true);
					intent.putExtra("groupId", id);
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("delete")) {
					// go to main activity
//					intent = new Intent(getApplicationContext(), LoginActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("groupId", id);
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("join")) {
//					intent = new Intent(getApplicationContext(), GroupDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("selected", "group");
					intent.putExtra("groupId", id);
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("request")) {
//					intent = new Intent(getApplicationContext(), PendingActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("sectionExtra", "group");
					intent.putExtra("groupId", id);
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("approve")) {
//					intent = new Intent(getApplicationContext(), GroupDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("groupId", id);
					intent.putExtra("selected", "group");
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("accept")) {
//					intent = new Intent(getApplicationContext(), GroupDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("groupId", id);
					intent.putExtra("selected", "group");
//					intent.putExtra("isFromPush", isFromPush);
				}
			} else if (type.equalsIgnoreCase("friend")) {
				// goto invite page
				if (messageType.equalsIgnoreCase("request")) {
//					intent = new Intent(getApplicationContext(), InviteActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("selected", "friend");
//					intent.putExtra("isFromPush", isFromPush);
					// no need of id here
					// intent.putExtra("id", id);
				} else if (messageType.equalsIgnoreCase("accept")) {
					// goto friend detail page
//					intent = new Intent(getApplicationContext(), FriendDetail.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("friendId", id);
//					intent.putExtra("isFromPush", isFromPush);
				}
			} else if (type.equalsIgnoreCase("event")) {
				// For Events
				if (messageType.equalsIgnoreCase("request")) {
//					intent = new Intent(getApplicationContext(), PendingActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("selected", "event");
//					intent.putExtra("isFromPush", isFromPush);
					if (messageType.equalsIgnoreCase("accept")) {
						intent.putExtra("selected", "event");
						intent.putExtra("eventId", id);
//						intent.putExtra("isFromPush", isFromPush);
					}
				} else if (messageType.equalsIgnoreCase("approve")) {
//					intent = new Intent(getApplicationContext(), EventDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("selected", "event");
					intent.putExtra("eventId", id);
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("invite")) {
//					intent = new Intent(getApplicationContext(), InviteActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("selected", "event");
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("delete")) {
//					intent = new Intent(getApplicationContext(), LoginActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("join")) {
//					intent = new Intent(getApplicationContext(), EventDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("selected", "joined");
					intent.putExtra("eventId", id);
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("post")) {
//					intent = new Intent(getApplicationContext(), EventDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("isPost", true);
					intent.putExtra("selected", "discussion");
					intent.putExtra("eventId", id);
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("comment")) {
//					intent = new Intent(getApplicationContext(), EventDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("isPost", true);
					intent.putExtra("selected", "discussion");
					intent.putExtra("eventId", id);
//					intent.putExtra("isFromPush", isFromPush);
				} else if (messageType.equalsIgnoreCase("reply")) {
//					intent = new Intent(getApplicationContext(), EventDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
					// contentIntent = PendingIntent.getActivity(this, 0,
					// intent, PendingIntent.FLAG_CANCEL_CURRENT);
					intent.putExtra("selected", "discussion");
					intent.putExtra("isPost", true);
					intent.putExtra("eventId", id);
//					intent.putExtra("isFromPush", isFromPush);
				}
			}
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			mBuilder.setContentText(message);
			mBuilder.setContentIntent(contentIntent);
			Notification notification = mBuilder.build();
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			// mNotificationManager.notify(Integer.parseInt(id), notification);
			int idd = 0;
			if (id.length() == 0) {
				// random code for the notification so that the notifications do
				// not replace previous ones
				Random r = new Random();
				idd = r.nextInt();
			} else {
				idd = Integer.parseInt(id);
			}
			mNotificationManager.notify(idd, notification);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
