package viva.oneplatinum.com.viva.models;

import java.io.Serializable;

public class ImageItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String imagePath = "", imageId = "", imageBucket = "",
			imageBucketId = "", postDate = "", posted = "false", caption = "",
			reminder = "false";
	public long timeMil = 0;

	public String toString() {
		return imageBucket + "," + caption + "," + imagePath + "," + postDate
				+ "," + imageId + "," + posted + "," + timeMil + "," + reminder;
	}
}
