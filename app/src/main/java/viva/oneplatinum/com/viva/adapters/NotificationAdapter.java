package viva.oneplatinum.com.viva.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.activities.EventDetailActivity;
import viva.oneplatinum.com.viva.activities.FriendDetailActivity;
import viva.oneplatinum.com.viva.activities.NotificationDetailActivity;
import viva.oneplatinum.com.viva.asynctask.AqueryTask;
import viva.oneplatinum.com.viva.models.Notifications;
import viva.oneplatinum.com.viva.utils.DataHolder;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.FontIcon;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;
import viva.oneplatinum.com.viva.widgets.RobotoLightTextView;
import viva.oneplatinum.com.viva.widgets.RobotoRegularTextView;

/**
 * Created by D-MAX on 4/24/2016.
 */
public class NotificationAdapter extends ArrayAdapter<Notifications> {
    Context context;
    ArrayList<Notifications> notification;
    //    NotificationDetail mNotificationDetail;
    private Bitmap userImageBitmap;
    AQuery aq;
    View view;
    LayoutInflater inflater;
    RobotoRegularTextView block_permit, reportUs;
    RobotoBoldTextView allow, disallow;
    RelativeLayout cancel, report, block;
    LinearLayout notificationItems;
    AqueryTask mAqueryTask;
    String token;
    ProgressDialog mProgressDialog;

    public NotificationAdapter(Context context, int textViewResourceId, ArrayList<Notifications> list, String token) {
        super(context, textViewResourceId, list);
        // TODO Auto-generated constructor stub
        notification = list;
        this.context = context;
        aq = new AQuery(context);
        mAqueryTask = new AqueryTask(context);
        this.token = token;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }


    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;

        // TODO Auto-generated method stub
        view = convertView;
        if (view == null) {
            inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.notification_list_item, null);
            viewHolder = new ViewHolder();

            viewHolder.notification_image = (CircularImageView) view.findViewById(R.id.notificationImage);
            viewHolder.notification_description = (RobotoLightTextView) view.findViewById(R.id.notificationDescription);
            viewHolder.actionButton = (FontIcon) view.findViewById(R.id.actionButton);
            viewHolder.notificationHasImage = (ImageView) view.findViewById(R.id.notificationHasImage);
            viewHolder.notificationItems = (LinearLayout) view.findViewById(R.id.notificationItems);
            viewHolder.allowDisallow = (LinearLayout) view.findViewById(R.id.allowDisallow);
            viewHolder.allow = (RobotoBoldTextView) view.findViewById(R.id.allow);
            viewHolder.disallow = (RobotoBoldTextView) view.findViewById(R.id.disallow);
            view.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.allowDisallow.setVisibility(View.GONE);
        viewHolder.allowDisallow.animate().alpha(1.0f).setDuration(500);
        final Notifications mNotification = notification.get(position);
        AQuery aq1 = aq.recycle(view);
        aq1.recycle(view);
        viewHolder.notification_description.setText(mNotification.message);
        final Bitmap defaultImage = BitmapFactory.decodeResource(context
                .getResources(), R.drawable.ic_launcher);
        userImageBitmap = GeneralUtil.getRoundedBitmap(defaultImage);

        aq1.id(viewHolder.notification_image).image(mNotification.sender_picture, true, true, 400, 0,
                new BitmapAjaxCallback() {
                    protected void callback(String url, ImageView iv,
                                            Bitmap bm, AjaxStatus status) {
                        if (bm != null) {
                            userImageBitmap = GeneralUtil.getRoundedBitmap(bm);

                        } else {
                            userImageBitmap = GeneralUtil
                                    .getRoundedBitmap(defaultImage);
                        }
                        viewHolder.notification_image.setImageBitmap(userImageBitmap);
                    }

                    ;
                });
        if (mNotification.hasDisplayImageOnNotification == 1) {

            viewHolder.notificationHasImage.setVisibility(View.VISIBLE);
            aq.id(viewHolder.notificationHasImage).image(mNotification.eventImage);
            viewHolder.actionButton.setVisibility(View.GONE);
        } else {
            viewHolder.actionButton.setVisibility(View.VISIBLE);
            viewHolder.notificationHasImage.setVisibility(View.GONE);
            viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNotification.action == 1) {
                        if (mNotification.link_type.equals("followRequest")) {
                            if (viewHolder.allowDisallow.getVisibility() == View.VISIBLE) {
                                viewHolder.allowDisallow.setVisibility(View.GONE);
                                viewHolder.allowDisallow.animate().alpha(0.0f).setDuration(500);

                            } else {
                                viewHolder.allowDisallow.setVisibility(View.VISIBLE);
                                viewHolder.allowDisallow.animate().alpha(1.0f).setDuration(500);


                                viewHolder.allow.setText("permit");
                                viewHolder.disallow.setText("refuse");

                                viewHolder.allow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //codes to block user
                                        mProgressDialog.setMessage("Loading...");
                                        mProgressDialog.show();
                                        HashMap<String, Object> params = new HashMap<String, Object>();
                                        params.put("token", token);
                                        params.put("sender_id", mNotification.senderId);
                                        params.put("receiver_id", mNotification.receiverId);
                                        params.put("notification_id", mNotification.notificationId.toString());
                                        params.put("response", 1);
                                        Log.d("params", params.toString());
                                        mAqueryTask.sendPostObjectRequest(DataHolder.ALLOW_FRIEND, params, JSONObject.class, allowCallback(viewHolder));


                                    }


                                });
                                viewHolder.disallow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mProgressDialog.setMessage("Loading...");
                                        mProgressDialog.show();
                                        HashMap<String, Object> params = new HashMap<String, Object>();
                                        params.put("token", token);
                                        params.put("sender_id", mNotification.senderId);
                                        params.put("receiver_id", mNotification.receiverId);
                                        params.put("notification_id", mNotification.notificationId.toString());
                                        params.put("response", 0);
                                        Log.d("params", params.toString());
                                        mAqueryTask.sendPostObjectRequest(DataHolder.ALLOW_FRIEND, params, JSONObject.class, disallowCallback(viewHolder));

                                    }

                                });
                            }
                        }

//                        if (mNotification.link_type.equals("friendRequest")) {
//                            if (viewHolder.allowDisallow.getVisibility() == View.VISIBLE) {
//                                viewHolder.allowDisallow.setVisibility(View.GONE);
//                            } else {
//                                viewHolder.allowDisallow.setVisibility(View.VISIBLE);
//                                viewHolder.allow.setText("allow");
//                                viewHolder.disallow.setText("disallow");
//
//                                viewHolder.allow.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                        //codes to block user
//                                    }
//                                });
//                                viewHolder.disallow.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        //codes to report user
//                                    }
//                                });
//                            }
//                        }

                        if (mNotification.link_type.equals("eventSuggestion")) {
                            if (viewHolder.allowDisallow.getVisibility() == View.VISIBLE) {
                                viewHolder.allowDisallow.setVisibility(View.GONE);
                                viewHolder.allowDisallow.animate().alpha(0.0f).setDuration(500);

                            } else {
                                viewHolder.allowDisallow.setVisibility(View.VISIBLE);
                                viewHolder.allowDisallow.animate().alpha(1.0f).setDuration(500);


                                viewHolder.allow.setText("allow");
                                viewHolder.disallow.setText("disallow");

                                viewHolder.allow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mProgressDialog.setMessage("Loading...");
                                        mProgressDialog.show();
                                        HashMap<String, Object> params = new HashMap<String, Object>();
                                        params.put("token", token);
                                        params.put("sender_id", mNotification.senderId.toString());
                                        params.put("suggested_user_id", mNotification.suggestedUserId.toString());
                                        params.put("notification_id", mNotification.notificationId.toString());
                                        params.put("event_id", mNotification.id.toString());
                                        params.put("response", 1);
                                        Log.d("params", params.toString());
                                        mAqueryTask.sendPostObjectRequest(DataHolder.EVENT_SUGGESTION, params, JSONObject.class, allowEventSuggestCallback(viewHolder));


                                    }


                                });
                                viewHolder.disallow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mProgressDialog.setMessage("Loading...");
                                        mProgressDialog.show();
                                        HashMap<String, Object> params = new HashMap<String, Object>();
                                        params.put("token", token);
                                        params.put("sender_id", mNotification.senderId.toString());
                                        params.put("suggested_user_id", mNotification.suggestedUserId.toString());
                                        params.put("notification_id", mNotification.notificationId.toString());
                                        params.put("event_id", mNotification.id.toString());
                                        params.put("response", 0);
                                        Log.d("params", params.toString());
                                        mAqueryTask.sendPostObjectRequest(DataHolder.EVENT_SUGGESTION, params, JSONObject.class, disallowEventSuggestCallback( viewHolder));
                                        //codes to report user
                                    }
                                });
                            }
                        }
                        if (mNotification.link_type.equals("eventInvitation")) {
                            if (viewHolder.allowDisallow.getVisibility() == View.VISIBLE) {
                                viewHolder.allowDisallow.setVisibility(View.GONE);
                                viewHolder.allowDisallow.animate().alpha(0.0f).setDuration(500);

                            } else {
                                viewHolder.allowDisallow.setVisibility(View.VISIBLE);
                                viewHolder.allowDisallow.animate().alpha(1.0f).setDuration(500);

                                viewHolder.allow.setText("Going");
                                viewHolder.disallow.setText("Interested");
                                viewHolder.allow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mProgressDialog.setMessage("Loading...");
                                        mProgressDialog.show();
                                        HashMap<String, Object> params = new HashMap<String, Object>();
                                        params.put("token", token);
                                        params.put("event_id", mNotification.id);
                                        params.put("notification_id", mNotification.notificationId);
                                        Log.d("params", params.toString());
                                        mAqueryTask.sendPostObjectRequest(DataHolder.UPDATE_USER_GOING, params, JSONObject.class, goingCallback(viewHolder));
                                        //codes to block user
                                    }
                                });
                                viewHolder.disallow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //codes to report user
                                        mProgressDialog.setMessage("Loading...");
                                        mProgressDialog.show();
                                        HashMap<String, Object> params = new HashMap<String, Object>();
                                        params.put("token", token);
                                        params.put("event_id", mNotification.id);
                                        params.put("notification_id", mNotification.notificationId);
                                        Log.d("params", params.toString());
                                        mAqueryTask.sendPostObjectRequest(DataHolder.UPDATE_USER_INTEREST, params, JSONObject.class, interestedCallback(viewHolder));
                                    }
                                });
                            }
                        }

                    }
                    if (mNotification.action == 0) {
                        if (mNotification.link_type.equals("eventSuggestion")) {
                            Intent i = new Intent(context, EventDetailActivity.class);
                            i.putExtra("isFromNotification", true);
                            i.putExtra("eventId", mNotification.id);
                            context.startActivity(i);
                        }
                        if (mNotification.link_type.equals("eventInvitation")) {
                            Intent i = new Intent(context, EventDetailActivity.class);
                            i.putExtra("isFromNotification", true);
                            i.putExtra("eventId", mNotification.id);
                            context.startActivity(i);
                        }
                        if (mNotification.link_type.equals("eventJoin")) {
                            Intent i = new Intent(context, EventDetailActivity.class);
                            i.putExtra("isFromNotification", true);
                            i.putExtra("eventId", mNotification.id);
                            context.startActivity(i);
                        }
                        if (mNotification.link_type.equals("eventPost")) {
                            Intent i = new Intent(context, NotificationDetailActivity.class);
                            i.putExtra("post_id", mNotification.id);
                            context.startActivity(i);


                        }
                        if (mNotification.link_type.equals("eventCreate")) {
                            Intent i = new Intent(context, EventDetailActivity.class);
                            i.putExtra("eventId", mNotification.id);
                            i.putExtra("isFromNotification", true);
                            context.startActivity(i);
                        }
                        if (mNotification.link_type.equals("FollowUser")) {
                            Intent i = new Intent(context, FriendDetailActivity.class);
                            i.putExtra("friendId", mNotification.id);
                            context.startActivity(i);
                        }
//                        if (mNotification.link_type.equals("followRequest")) {
//                            Intent i = new Intent(context, FriendDetailActivity.class);
//                            i.putExtra("friendId", mNotification.id);
//                            context.startActivity(i);
//                        }

                    }
                }

            });
        }

        viewHolder.notificationItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNotification.has_link == 1) {
                    if (mNotification.link_type.equals("eventCreate")) {
                        Intent i = new Intent(context, EventDetailActivity.class);
                        i.putExtra("isFromNotification", true);
                        i.putExtra("eventId", mNotification.id);
                        context.startActivity(i);
                    } else if (mNotification.link_type.equals("eventInvitation")) {
                        Intent i = new Intent(context, EventDetailActivity.class);
                        i.putExtra("isFromNotification", true);
                        i.putExtra("eventId", mNotification.id);
                        context.startActivity(i);
                    } else if (mNotification.link_type.equals("eventSuggestion")) {
                        Intent i = new Intent(context, EventDetailActivity.class);
                        i.putExtra("eventId", mNotification.id);
                        context.startActivity(i);
                    } else if (mNotification.link_type.equals("eventUpdate")) {
                        Intent i = new Intent(context, EventDetailActivity.class);
                        i.putExtra("isFromNotification", true);
                        i.putExtra("eventId", mNotification.id);
                        context.startActivity(i);
                    } else if (mNotification.link_type.equals("eventPost")) {
                        Intent i = new Intent(context, NotificationDetailActivity.class);
                        i.putExtra("post_id", mNotification.id);
                        context.startActivity(i);
                    }
//                    else if (mNotification.link_type.equals("followRequest")) {
//                        Intent i = new Intent(context, FriendDetailActivity.class);
//                        i.putExtra("friendId", mNotification.id);
//                        context.startActivity(i);
//                    }
                    else if (mNotification.link_type.equals("followUser")) {
                        Intent i = new Intent(context, FriendDetailActivity.class);
                        i.putExtra("friendId", mNotification.id);
                        context.startActivity(i);
                    } else if (mNotification.link_type.equals("eventJoin")) {
                        Intent i = new Intent(context, EventDetailActivity.class);
                        i.putExtra("eventId", mNotification.id);
                        context.startActivity(i);
                    }

                } else {

                }
            }
        });

        return view;

    }

    public class ViewHolder {
        public RobotoLightTextView notification_description;
        public CircularImageView notification_image;
        public FontIcon actionButton;
        public ImageView notificationHasImage;
        public LinearLayout notificationItems;
        public LinearLayout allowDisallow;
        RobotoBoldTextView allow, disallow;
    }

    private AjaxCallback<JSONObject> allowCallback(final ViewHolder viewHolder) {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {
                mProgressDialog.dismiss();
                System.out.println(result);
                Log.d("getCategoriesresponse", result.toString());
                if (result != null) {

                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        String notification = outpuJsonObject.optString("notificationMsg");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            viewHolder.notification_description.setText(notification);
                            viewHolder.actionButton.setVisibility(View.GONE);
                            viewHolder.allowDisallow.setVisibility(View.GONE);
//                            mNotification.message=notification;


                            notificationItems.notify();
                            notifyDataSetChanged();
//                            viewHolder.notification_description.setText(notification);
////                            viewHolder.allow.setText("allow user to follow");
////                            viewHolder.allowDisallow.setVisibility(View.GONE);
//                            if (viewHolder.allowDisallow.getVisibility() == View.VISIBLE) {
//                                viewHolder.allowDisallow.setVisibility(View.GONE);
//                            } else {
//                                viewHolder.allowDisallow.setVisibility(View.VISIBLE);
//                                viewHolder.allowDisallow.setVisibility(View.GONE);
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    private AjaxCallback<JSONObject> disallowCallback(final ViewHolder viewHolder) {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.d("getCategoriesresponse", result.toString());
                mProgressDialog.dismiss();
                if (result != null) {

                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        String notification = outpuJsonObject.optString("notificationMsg");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            viewHolder.notification_description.setText(notification);
                            viewHolder.actionButton.setVisibility(View.GONE);
                            viewHolder.allowDisallow.setVisibility(View.GONE);
//                            mNotification.message=notification;
                            notificationItems.notify();
                            notifyDataSetChanged();

//                            viewHolder.notification_description.setText(outpuJsonObject.optString("notificationMsg"));
////                            viewHolder.disallow.setText("disallow user to follow");
////                            viewHolder.allowDisallow.setVisibility(View.GONE);
//                            if (viewHolder.allowDisallow.getVisibility() == View.VISIBLE) {
//                                viewHolder.allowDisallow.setVisibility(View.GONE);
//                            } else {
//                                viewHolder.allowDisallow.setVisibility(View.VISIBLE);
//                                viewHolder.allowDisallow.setVisibility(View.GONE);
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    private AjaxCallback<JSONObject> allowEventSuggestCallback(final ViewHolder viewHolder) {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.d("getCategoriesresponse", result.toString());
                mProgressDialog.dismiss();
                if (result != null) {

                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        String notification = outpuJsonObject.optString("notificationMsg");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            viewHolder.notification_description.setText(notification);
                            viewHolder.actionButton.setVisibility(View.GONE);
                            viewHolder.allowDisallow.setVisibility(View.GONE);
//                            mNotification.message=notification;
                            notificationItems.notify();
                            notifyDataSetChanged();
//                            viewHolder.notification_description.setText(notification);
////                            viewHolder.allow.setText("follow event");
////                            viewHolder.allowDisallow.setVisibility(View.GONE);
//                            if (viewHolder.allowDisallow.getVisibility() == View.VISIBLE) {
//                                viewHolder.allowDisallow.setVisibility(View.GONE);
//                            } else {
//                                viewHolder.allowDisallow.setVisibility(View.VISIBLE);
//                                viewHolder.allowDisallow.setVisibility(View.GONE);
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    private AjaxCallback<JSONObject> disallowEventSuggestCallback(final ViewHolder viewHolder) {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result);
                Log.d("getCategoriesresponse", result.toString());
                mProgressDialog.dismiss();
                if (result != null) {

                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        String notification = outpuJsonObject.optString("notificationMsg");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            viewHolder.notification_description.setText(notification);
                            viewHolder.actionButton.setVisibility(View.GONE);
                            viewHolder.allowDisallow.setVisibility(View.GONE);
//                            mNotification.message=notification;
                            notificationItems.notify();
                            notifyDataSetChanged();
//                            viewHolder.disallow.setText("unfollow event");
//                            viewHolder.notification_description.setText(notification);
////                            viewHolder.allowDisallow.setVisibility(View.GONE);
//                            if (viewHolder.allowDisallow.getVisibility() == View.VISIBLE) {
//                                viewHolder.allowDisallow.setVisibility(View.GONE);
//                            } else {
//                                viewHolder.allowDisallow.setVisibility(View.VISIBLE);
//                                viewHolder.allowDisallow.setVisibility(View.GONE);
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    private AjaxCallback<JSONObject> goingCallback(final ViewHolder viewHolder) {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {
                mProgressDialog.dismiss();
                System.out.println(result);
                if (result != null) {

                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        String notification = outpuJsonObject.optString("notificationMsg");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            viewHolder.notification_description.setText(notification);
                            viewHolder.actionButton.setVisibility(View.GONE);
                            viewHolder.allowDisallow.setVisibility(View.GONE);
//                            mNotification.message=notification;
                            notificationItems.notify();
                            notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    private AjaxCallback<JSONObject> interestedCallback(final ViewHolder viewHolder) {
        return new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject result,
                                 AjaxStatus status) {

                System.out.println(result + "dasdasdada");
                Log.d("getCategoriesresponse", result.toString());
                mProgressDialog.dismiss();
                if (result != null) {

                    try {
                        JSONObject outpuJsonObject = result.getJSONObject("output");
                        int userStatus = outpuJsonObject.optInt("status");
                        String notification = outpuJsonObject.optString("notificationMsg");
                        if (userStatus == 1) {
                            showToast(outpuJsonObject.optString("message"));
                            viewHolder.notification_description.setText(notification);
                            viewHolder.actionButton.setVisibility(View.GONE);
                            viewHolder.allowDisallow.setVisibility(View.GONE);
//                            mNotification.message=notification;
                            notificationItems.notify();
                            notifyDataSetChanged();
//                            viewHolder.notification_description.setText(notification);
////                            viewHolder.disallow.setText("Interested in event");
//                            if (viewHolder.allowDisallow.getVisibility() == View.VISIBLE) {
//                                viewHolder.allowDisallow.setVisibility(View.GONE);
//                            } else {
//                                viewHolder.allowDisallow.setVisibility(View.VISIBLE);
//                                viewHolder.allowDisallow.setVisibility(View.GONE);
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ;
        };
    }

    public void showToast(String message) {
        LayoutInflater myInflater = LayoutInflater.from(context);
        View view = myInflater.inflate(R.layout.toast_layout, null);
        Toast mytoast = new Toast(context);
        mytoast.setView(view);
        TextView txtToast = (TextView) view.findViewById(R.id.txtToast);
        txtToast.setText(message);
        mytoast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        mytoast.setDuration(Toast.LENGTH_SHORT);
        mytoast.show();
    }
}