package viva.oneplatinum.com.viva.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import viva.oneplatinum.com.viva.fragments.ImageGalleryFragment;


public class ImageGalleryPagerAdapter extends FragmentStatePagerAdapter {
	ArrayList<String> list;

	public ImageGalleryPagerAdapter(FragmentManager fm,
			ArrayList<String> list) {
		super(fm);
		this.list = list;

	}

	@Override
	public Fragment getItem(int position) {
		String galleryItem = list.get(position);
		return ImageGalleryFragment.newInstance(galleryItem);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

}
