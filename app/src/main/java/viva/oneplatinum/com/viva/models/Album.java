package viva.oneplatinum.com.viva.models;

import java.util.ArrayList;

public class Album {
	public long coverId;
	public String name;
	public String id;
	public ArrayList<ImageData> imagesList = new ArrayList<ImageData>();
	public String toString(){
		return name+" "+id;
	}
}
