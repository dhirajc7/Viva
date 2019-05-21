package viva.oneplatinum.com.viva.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import viva.oneplatinum.com.viva.app.VivaApplication;


public class RobotoRegularEditText extends EditText {

	public RobotoRegularEditText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setCustomTextStyle();

	}

	public RobotoRegularEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomTextStyle();
	}

	public RobotoRegularEditText(Context context) {
		super(context);
		setCustomTextStyle();
	}

	private void setCustomTextStyle() {
		this.setTypeface(VivaApplication.Fonts.ROBOTO_REGULAR);
		// setClickable(true);
	}
}
