package viva.oneplatinum.com.viva.adapters;

import android.content.Context;
import android.util.AttributeSet;

import viva.oneplatinum.com.viva.circleview.CircleLayout;
import viva.oneplatinum.com.viva.circleview.CircularLayoutItem;
import viva.oneplatinum.com.viva.interfaces.EventListner;


public class MyCircleLayoutItem extends CircularLayoutItem {

    EventListner mEventListner;

    public MyCircleLayoutItem(Context context) {
        super(context);initialize(context);
    }

    public MyCircleLayoutItem(Context context, CircleLayout cl) {
        super(context, cl);
        initialize(context);
    }

    public MyCircleLayoutItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public MyCircleLayoutItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    public void initialize(Context mContext)
    {
        mEventListner=(EventListner)mContext;

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick() {
                mEventListner.eventposition(false,getIndex());
//                Toast.makeText(getContext(),"Item number: "+getIndex(),Toast.LENGTH_SHORT).show();
            }
        });

        this.setOnFocusListener(new OnFocusListener()
        {
            @Override
            public void onFocus() {
                mEventListner.eventposition(true,getIndex());
//                Toast.makeText(getContext(),"Item number: "+getIndex()+ " is now on focus ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnFocus() {

            }
        });
    }



}
