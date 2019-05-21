package viva.oneplatinum.com.viva.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import java.util.ArrayList;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.models.Friend;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.RobotoLightTextView;

/**
 * Created by Dell on 4/4/2016.
 */
public class FriendAdapter extends ArrayAdapter<Friend> {
    static FriendAdapter mGrid_Adapter;
    private Context context;
    ArrayList<Friend> friend;
    private Bitmap userImageBitmap;
    AQuery aq;
    View view;

    public FriendAdapter(Context context, int textViewResourceId, ArrayList<Friend> list) {
        super(context, textViewResourceId, list);
       friend = list;
       this.friend = friend;
       this.context = context;
        aq=new AQuery(context);
    }
//    @Override
//    public int getCount() {
//        return 10;
//    }
//
//    @Override
//    public Friend getItem(int position) {
//        return friend.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        // TODO Auto-generated method stub
        view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            view = inflater.inflate(R.layout.invite_friends_grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.gridItemLayout=(LinearLayout)view.findViewById(R.id.gridItemView);
            viewHolder.friendImage = (CircularImageView) view.findViewById(R.id.friendImage);
            viewHolder.firstName = (RobotoLightTextView) view.findViewById(R.id.firstName);
            viewHolder.lastName = (RobotoLightTextView) view.findViewById(R.id.lastName);

            view.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }
        final Friend mFriend = friend.get(position);
        viewHolder.firstName.setText(mFriend.first_name);
        viewHolder.lastName.setText(mFriend.last_name);
        viewHolder.mFriend=mFriend;

        AQuery aq1 = aq.recycle(view);
        aq1.recycle(view);
        final Bitmap defaultImage = BitmapFactory.decodeResource(context
                .getResources(), R.drawable.ic_launcher);
        userImageBitmap = GeneralUtil.getRoundedBitmap(defaultImage);
        // aq.id(userImage).image(profileInfo.myPhoto);
        aq.id(viewHolder.friendImage).image(mFriend.profile_picture, true, true, 400, 0,
                new BitmapAjaxCallback() {
                    protected void callback(String url, ImageView iv,
                                            Bitmap bm, AjaxStatus status) {
                        if (bm != null) {
                            userImageBitmap = GeneralUtil.getRoundedBitmap(bm);

                        } else {
                            userImageBitmap = GeneralUtil
                                    .getRoundedBitmap(defaultImage);
                        }
                        viewHolder.friendImage.setImageBitmap(userImageBitmap);
                    }

                    ;
                });


        return view;

    }
    public void showToast(String message) {
        LayoutInflater myInflater = LayoutInflater.from(context);
        View view = myInflater.inflate(R.layout.toast_layout, null);
        Toast mytoast = new Toast(context);
        mytoast.setView(view);
        TextView txtToast = (TextView) view.findViewById(R.id.txtToast);
        txtToast.setText(message);
        mytoast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        mytoast.setDuration(Toast.LENGTH_LONG);
        mytoast.show();
    }
    public  class ViewHolder {
        public RobotoLightTextView firstName;
        public RobotoLightTextView lastName;
        public CircularImageView friendImage;
        public Friend mFriend;
        public LinearLayout gridItemLayout;
    }
}
