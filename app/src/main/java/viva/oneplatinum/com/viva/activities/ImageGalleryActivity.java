package viva.oneplatinum.com.viva.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.adapters.ImageGalleryPagerAdapter;
import viva.oneplatinum.com.viva.interfaces.ImageGalleryResponder;
import viva.oneplatinum.com.viva.widgets.GalleryViewPager;

public class ImageGalleryActivity extends FragmentActivity implements
		ImageGalleryResponder {

	private ArrayList<String> mImagesList;
	int position;
	ImageGalleryPagerAdapter mImageGalleryPagerAdapter;
	private GalleryViewPager mGalleryViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getImageData();
		setContentView(R.layout.image_gallery_pager_layout);
		mGalleryViewPager = (GalleryViewPager) findViewById(R.id.image_gallery_viewpager);

		loadImageData();
	}

	private void getImageData() {
		mImagesList = getIntent().getStringArrayListExtra("IMAGES");
		position = getIntent().getIntExtra("position", 0);

	}

	private void loadImageData() {
		if (mImagesList != null && mImagesList.size() > 0) {
			mImageGalleryPagerAdapter = new ImageGalleryPagerAdapter(
					getSupportFragmentManager(), mImagesList);
			mGalleryViewPager.setAdapter(mImageGalleryPagerAdapter);
			mGalleryViewPager.setCurrentItem(position);
		} else {
			this.finish();
		}
	}

	public void setGalleryPageChangeEnabled(boolean isPagingEnabled) {
		mGalleryViewPager.setPagingEnabled(isPagingEnabled);
	}

	@Override
	public String getGalleryItemAtPosition(int position) {
		String galleryItem = null;

		if (mImagesList.size() > 0) {
			galleryItem = mImagesList.get(position);
		}
		return galleryItem;
	}
}
