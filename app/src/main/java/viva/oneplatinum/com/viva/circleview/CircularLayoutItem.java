package viva.oneplatinum.com.viva.circleview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.models.Event;
import viva.oneplatinum.com.viva.widgets.CircularImageView;

/**
 * Developer: Abed Almoradi
 * Email: Abd.Almoradi@gmail.com
 * Date 11/11/2014
 */


public abstract class CircularLayoutItem extends LinearLayout {
    private int rotation_angle = 0;
    private float onMotionDown_X, onMotionDown_Y;// coordinates when motion down event
    private boolean isPressed = false;
    private int index;
    protected CircleLayout parent;
    View view;
    private TextView eventTitle, eventSponsored;
    CircularImageView eventImage;
    private OnClickListener clickListener;
    private OnFocusListener focusListener;

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setRotationParameters(int angle)// set the rotation angle of the object
    {
        rotation_angle = angle;
        rotate();
        this.invalidate();
    }


    public void setParent(CircleLayout parent) {
        this.parent = parent;
    }

    public CircularLayoutItem(Context context) {
        super(context);
        init();
    }

    public CircularLayoutItem(Context context, CircleLayout cl) {
        super(context);
        parent = cl;
        init();
    }

    public CircularLayoutItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularLayoutItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)  // to pass the touch event to the parent
    {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onMotionDown_X = ev.getRawX();
            onMotionDown_Y = ev.getRawY();
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            //if move event occurs
            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis() + 100;
            float x = ev.getRawX();
            float y = ev.getRawY();
            if ((Math.abs(onMotionDown_X - x) > 10) || (Math.abs(onMotionDown_Y - y) > 10)) {
                int metaState = 0;
                //create new motion event
                MotionEvent motionEvent = MotionEvent.obtain(
                        downTime,
                        eventTime,
                        MotionEvent.ACTION_DOWN,
                        x,
                        y,
                        metaState
                );
                ((CircleLayout) this.getParent()).dispatchTouchEvent(motionEvent); //send event to listview
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void onClick() {
        clickListener.onClick();
    }

    public void onFocus() {
        focusListener.onFocus();
    }

    public void onUnFocus() {
        focusListener.onUnFocus();
    }


    public void init() //Initialize the object
    {
        setWillNotDraw(false);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.event_view_item_layout, this);
        eventTitle = (TextView) findViewById(R.id.eventName);
        eventSponsored = (TextView) findViewById(R.id.eventSponser);
        eventImage = (CircularImageView) findViewById(R.id.eventImage);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN)
                    isPressed = true;
                else if (action == MotionEvent.ACTION_UP) {
                    onClick();
                    if (isPressed) {
                        isPressed = false;
                        return false;
                    }
                }
                return true;
            }
        });
    }

    public void balance() {
        Animation an = new RotateAnimation(0, -1 * parent.get_pinnded_childs_rotation_angle(), this.getHeight() / 2, this.getWidth() / 2);
        an.setDuration(300);               // duration in ms
        an.setRepeatCount(0);                // -1 = infinite repeated
        an.setRepeatMode(Animation.REVERSE); // reverses each repeat
        an.setFillAfter(true);
        this.setAnimation(an);
    }


    private void rotate() {
        if (view != null)
            if (!parent.get_is_pinned_childs()) {

                view.setRotation(rotation_angle % 360);

            }else {
                view.setRotation(parent.get_pinnded_childs_rotation_angle());
            }
//        if(forground!=null)
//            if(!parent.get_is_pinned_childs())
//                forground.setRotation(rotation_angle%360);
//
//            else
//                forground.setRotation(parent.get_pinnded_childs_rotation_angle());

    }

    public void setBackground(Drawable image, Event event, Context mContext) {
        AQuery aq = new AQuery(mContext);
        int sdk = android.os.Build.VERSION.SDK_INT;
        eventSponsored.setText(event.eventName.toUpperCase());
        if (event.mainImage.length() > 0) {

            aq.id(eventImage).image(event.mainImage);
        } else {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                eventImage.setBackgroundDrawable(image);
            } else {
                eventImage.setBackground(image);
            }
        }
//        Log.v("chekEvent", "" + event.eventName);
        eventTitle.setText(event.eventName.toUpperCase());
//        eventSponsored.setText(event.eventName);
    }


    public interface OnClickListener {
        public void onClick();
    }

    public interface OnFocusListener {
        public void onFocus();

        public void onUnFocus();
    }

    public void setOnClickListener(OnClickListener o) {
        clickListener = o;
    }

    public void setOnFocusListener(OnFocusListener o) {
        focusListener = o;
    }

}
