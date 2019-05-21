package viva.oneplatinum.com.viva.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import java.util.ArrayList;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.models.MemeberList;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.RobotoLightTextView;

/**
 * Created by D-MAX on 4/7/2016.
 */
public class MemberAdapter extends ArrayAdapter<MemeberList> {
    private Context context;
    ArrayList<MemeberList> friend;
    private Bitmap userImageBitmap;
    AQuery aq;
    View view;

    public MemberAdapter(Context context, int textViewResourceId, ArrayList<MemeberList> list) {
        super(context, textViewResourceId, list);
        friend = list;
        this.friend = list;
        this.context = context;
        aq=new AQuery(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        // TODO Auto-generated method stub
        view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            view = inflater.inflate(R.layout.invite_friends_grid_item, null);
            viewHolder = new ViewHolder();

            viewHolder.friendImage = (CircularImageView) view.findViewById(R.id.friendImage);
            viewHolder.firstName = (RobotoLightTextView) view.findViewById(R.id.firstName);
            viewHolder.lastName = (RobotoLightTextView) view.findViewById(R.id.lastName);

            view.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }
        MemeberList mFriend = friend.get(position);
        viewHolder.firstName.setText(mFriend.firstName);
        viewHolder.lastName.setText(mFriend.lastName);
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
    public static class ViewHolder {
        public RobotoLightTextView firstName;
        public RobotoLightTextView lastName;
        public CircularImageView friendImage;
    }
}
