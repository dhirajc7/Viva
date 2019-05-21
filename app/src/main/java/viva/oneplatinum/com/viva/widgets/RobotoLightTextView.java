package viva.oneplatinum.com.viva.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import viva.oneplatinum.com.viva.app.VivaApplication;


public class RobotoLightTextView extends TextView {

    public RobotoLightTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomTextStyle();

    }

    public RobotoLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomTextStyle();
    }

    public RobotoLightTextView(Context context) {
        super(context);
        setCustomTextStyle();
    }

    private void setCustomTextStyle() {
        this.setTypeface(VivaApplication.Fonts.ROBOT0_LIGHT);
        // setClickable(true);
    }
}
