package viva.oneplatinum.com.viva.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import viva.oneplatinum.com.viva.R;
import viva.oneplatinum.com.viva.interfaces.ImagePostListener;
import viva.oneplatinum.com.viva.utils.DataHolder;

public class ImagePagerFragment extends Fragment {
	ImageView im;
	int height, width;
	String image_path, image_id;
	ImagePostListener mListener;
	DataHolder.Dataholder dataholder = DataHolder.Dataholder.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		image_path = bundle.getString("image");
		image_id = bundle.getString("id");

		mListener = (ImagePostListener) getActivity();

		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		height = metrics.heightPixels;
		width = metrics.widthPixels;
		mListener.currentPos();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mListener.currentPos();
		Log.d("newimageFragmetn", "newFragment " + image_id);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					double aspect;
					File file = new File(image_path);
					Bitmap yourbitmap = BitmapFactory.decodeFile(image_path), bitmap;
					Log.d("imageOriginal", yourbitmap.getWidth() + " "
							+ yourbitmap.getHeight());
					// check orientation of the image and rotate accordingly
					ExifInterface exif;
					Matrix matrix = new Matrix();
					int rotationAngle = 0;
					try {
						exif = new ExifInterface(image_path);
						String orientString = exif
								.getAttribute(ExifInterface.TAG_ORIENTATION);
						int orientation = orientString != null ? Integer
								.parseInt(orientString)
								: ExifInterface.ORIENTATION_NORMAL;

						if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
							rotationAngle = 90;
						if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
							rotationAngle = 180;
						if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
							rotationAngle = 270;
						// Rotate Bitmap

						Log.d("thumbnailRotationA", "id " + image_id
								+ " rotationAngle " + rotationAngle
								+ " imagePth" + image_path);
						matrix.setRotate(rotationAngle,
								(float) yourbitmap.getWidth() / 2,
								(float) yourbitmap.getHeight() / 2);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.e("IOEinImageRotation", e.toString());
						e.printStackTrace();
					}

					if (yourbitmap.getWidth() > yourbitmap.getHeight()) {
						// landscape image
						aspect = (double) ((double) yourbitmap.getWidth() / (double) yourbitmap
								.getHeight());
						Bitmap scaled = Bitmap.createScaledBitmap(yourbitmap,
								(int) (width * aspect), width, false);
						int spacing = (scaled.getWidth() - width) / 2;
						Bitmap s = Bitmap.createBitmap(scaled, spacing, 0,
								width, width, matrix, true);
						Log.d("imageScaledLandscape",
								s.getWidth() + " " + s.getHeight() + " "
										+ spacing);
						bitmap = s;
					} else {
						// portrait
						aspect = (double) ((double) yourbitmap.getHeight() / (double) yourbitmap
								.getWidth());
						Bitmap scaled = Bitmap.createScaledBitmap(yourbitmap,
								width, (int) (width * aspect), false);
						int spacing = (scaled.getHeight() - width) / 2;
						Log.d("portraitSpacing", spacing + "");
						Bitmap s = Bitmap.createBitmap(scaled, 0, spacing,
								width, width, matrix, true);
						Log.d("imageScaledport", "height" + scaled.getHeight()
								+ " width " + scaled.getWidth() + " " + aspect);
						bitmap = s;
					}

					im.setImageBitmap(bitmap);
					// im.setScaleType(ScaleType.CENTER);
				} catch (NullPointerException e1) {
					// TODO Auto-generated catch block
					Log.e("nullPnterAtSingleIm", e1.toString());
					dataholder.noItemsList.add(image_id);
					getActivity().onBackPressed();
					Toast.makeText(getActivity(), "Image has been deleted!",
							Toast.LENGTH_LONG).show();
					e1.printStackTrace();
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.image_pager_item, null);
		im = (ImageView) v.findViewById(R.id.imageview_pager);

		return v;
	}

}
