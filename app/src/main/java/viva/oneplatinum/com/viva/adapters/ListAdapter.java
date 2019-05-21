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
import viva.oneplatinum.com.viva.models.Event;
import viva.oneplatinum.com.viva.utils.GeneralUtil;
import viva.oneplatinum.com.viva.widgets.CircularImageView;
import viva.oneplatinum.com.viva.widgets.RobotoBoldTextView;
import viva.oneplatinum.com.viva.widgets.RobotoRegularTextView;

public class ListAdapter extends ArrayAdapter<Event> {
    private Context context;
    ArrayList<Event> event;
    private Bitmap userImageBitmap;
    AQuery aq;
    View view;
    String token;
    Boolean isFromMyEvent;

    public ListAdapter(Context context, int textViewResourceId, ArrayList<Event> list,String token,Boolean isFromMyEvent) {
        super(context, textViewResourceId, list);
        // TODO Auto-generated constructor stub
        event = list;
        this.event = event;
        this.context = context;
        this.token = token;
        this.isFromMyEvent = isFromMyEvent;
        aq = new AQuery(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        // TODO Auto-generated method stub
        view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            view = inflater.inflate(R.layout.filter_list_active_search_item, null);
            viewHolder = new ViewHolder();

            viewHolder.event_image = (CircularImageView) view.findViewById(R.id.eventImage);
            viewHolder.event_Name = (RobotoBoldTextView) view.findViewById(R.id.event_name);
            viewHolder.event_date = (RobotoBoldTextView) view.findViewById(R.id.Date_From);
            viewHolder.event_time = (RobotoBoldTextView) view.findViewById(R.id.time_From);
            viewHolder.event_location = (RobotoRegularTextView) view.findViewById(R.id.eventLocation);

            view.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }
        Event mEvent = event.get(position);
        viewHolder.event_Name.setText(mEvent.eventTitle);
        viewHolder.event_location.setText(mEvent.eventLocation);
        if(isFromMyEvent)
        {
            viewHolder.event_date.setText(GeneralUtil.convertDate(mEvent.eventStartdate));
        }
//        viewHolder.event_time.setText(GeneralUtil.convertDate(mNotification.eventStartdate));
//        viewHolder.event_date.setVisibility(View.GONE);
        AQuery aq1 = aq.recycle(view);
        aq1.recycle(view);
        final Bitmap defaultImage = BitmapFactory.decodeResource(context
                .getResources(), R.drawable.ic_launcher);
        userImageBitmap = GeneralUtil.getRoundedBitmap(defaultImage);
        // aq.id(userImage).image(profileInfo.myPhoto);
        if(mEvent.eventImages.size()>0) {
            aq1.id(viewHolder.event_image).image(mEvent.eventImages.get(0), true, true, 400, 0,
                    new BitmapAjaxCallback() {
                        protected void callback(String url, ImageView iv,
                                                Bitmap bm, AjaxStatus status) {
                            if (bm != null) {
                                userImageBitmap = GeneralUtil.getRoundedBitmap(bm);

                            } else {
                                userImageBitmap = GeneralUtil
                                        .getRoundedBitmap(defaultImage);
                            }
                            viewHolder.event_image.setImageBitmap(userImageBitmap);
                        }

                        ;
                    });
        }
        aq.id(viewHolder.event_image).image(mEvent.image, true, true, 400, 0,
                new BitmapAjaxCallback() {
                    protected void callback(String url, ImageView iv,
                                            Bitmap bm, AjaxStatus status) {
                        if (bm != null) {
                            userImageBitmap = GeneralUtil.getRoundedBitmap(bm);

                        } else {
                            userImageBitmap = GeneralUtil
                                    .getRoundedBitmap(defaultImage);
                        }
                        viewHolder.event_image.setImageBitmap(userImageBitmap);
                    }

                    ;
                });
        return view;

    }

    public static class ViewHolder {
        public RobotoBoldTextView event_date;
        public RobotoBoldTextView event_time;
        public RobotoBoldTextView event_Name;
        public CircularImageView event_image;
        public RobotoRegularTextView event_location;
    }
}

