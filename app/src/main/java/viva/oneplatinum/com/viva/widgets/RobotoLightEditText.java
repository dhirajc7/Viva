package viva.oneplatinum.com.viva.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import viva.oneplatinum.com.viva.app.VivaApplication;


public class RobotoLightEditText extends EditText {

	public RobotoLightEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomTextStyle();

	}

	public RobotoLightEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomTextStyle();
	}

	public RobotoLightEditText(Context context) {
		super(context);
		setCustomTextStyle();
	}

	private void setCustomTextStyle() {
		this.setTypeface(VivaApplication.Fonts.ROBOT0_LIGHT);
		// setClickable(true);
	}
}
