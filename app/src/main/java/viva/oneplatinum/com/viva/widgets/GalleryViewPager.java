package viva.oneplatinum.com.viva.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class GalleryViewPager extends ViewPager {

	private boolean isPagingEnabled;

	public GalleryViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		isPagingEnabled = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (this.isPagingEnabled) {
			return super.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (this.isPagingEnabled) {
			return super.onInterceptTouchEvent(event);
		}
		return false;
	}

	public void setPagingEnabled(boolean isPagingEnabled) {
		this.isPagingEnabled = isPagingEnabled;
	}

}
