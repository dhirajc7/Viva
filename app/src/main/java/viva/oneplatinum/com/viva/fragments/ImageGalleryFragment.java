package viva.oneplatinum.com.viva.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.polites.android.GestureImageView;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.interfaces.ImageGalleryResponder;

public class ImageGalleryFragment extends Fragment {

	private View view;
	private GestureImageView gestureImageView;
	AQuery mAQuery;

	private ImageGalleryResponder mImageGalleryResponder;

	public static ImageGalleryFragment newInstance(String galleryItem) {
		ImageGalleryFragment imageViewFragment = new ImageGalleryFragment();
		Bundle bundle = new Bundle();
		bundle.putString("gallery", galleryItem);

		imageViewFragment.setArguments(bundle);
		return imageViewFragment;
	}

	public ImageGalleryFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mImageGalleryResponder = (ImageGalleryResponder) getActivity();
		mAQuery=new AQuery(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater
				.inflate(R.layout.image_gallery_layout, container, false);
		gestureImageView = (GestureImageView) view
				.findViewById(R.id.gallery_image);
		
		gestureImageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mImageGalleryResponder
						.setGalleryPageChangeEnabled(!gestureImageView
								.isZoomed());
				if (event.getAction() == MotionEvent.ACTION_UP) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							mImageGalleryResponder
									.setGalleryPageChangeEnabled(!gestureImageView
											.isZoomed());
						}
					}, 500);
				}
				return false;
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String galleryItem = (String) getArguments().get("gallery");
		ProgressBar pbImageLoading=(ProgressBar)view.findViewById(R.id.progressBar);
		mAQuery.recycle(view);
		mAQuery.id(gestureImageView).progress(pbImageLoading)
		 .image(galleryItem, true, true, 700, 0);
	}
}
