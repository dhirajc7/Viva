package viva.oneplatinum.com.viva.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import viva.oneplatinum.com.viva.models.ImageItem;
import viva.oneplatinum.com.viva.utils.DataHolder;


public class VivaApplication extends MultiDexApplication {

    private SharedPreferences mUserPreference;
    SharedPreferences.Editor edit;
    DataHolder.Dataholder dataholder = DataHolder.Dataholder.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();

        mUserPreference = getSharedPreferences(DataHolder.USER_PERFERENCE,
                Context.MODE_PRIVATE);
        initializeFonts();
    }
    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public void setUserData(String key, Object value) {
        Editor editor = mUserPreference.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else {
            editor.putString(key, value + "");
        }
        editor.commit();
    }

    public void setUserInfo(String value) {
        Editor editor = mUserPreference.edit();
        editor.putString("userinfos", value.toString());
        editor.commit();
    }


    public String getUserData(String key) {
        return mUserPreference.getString(key, "").trim();
    }


    public boolean isUserLoggedIn() {
        return mUserPreference.getString(DataHolder.USERNAME, "").length() > 0;
    }


    public void logOutUser() {
        Editor editor = mUserPreference.edit();
        editor.clear();
        editor.commit();

    }

    private void initializeFonts() {
        Fonts.ROBOTO_REGULAR = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        Fonts.ROBOT0_LIGHT = Typeface.createFromAsset(getAssets(),"OpenSans-Light.ttf");
        Fonts.ROBOTO_BOLD = Typeface.createFromAsset(getAssets(), "Brandon-Grotesque.ttf");
        Fonts.FONT_ICON = Typeface.createFromAsset(getAssets(),"viva.ttf");

    }

    public static final class Fonts {
        public static Typeface FONT_ICON;
        public static Typeface ROBOTO_REGULAR;
        public static Typeface ROBOT0_LIGHT;
        public static Typeface ROBOTO_BOLD;

    }
    public String getFeedtTitle() {
        return mUserPreference.getString("feedTitle", "");
    }
    public String getHashtags() {
        return mUserPreference.getString("hashtags", "");
    }
    public void putHashTags(String string) {
        edit.putString("hashtags", string);
        edit.apply();
    }
    public void saveAllToShp() {
        String path, keys, items;
        StringBuilder str = new StringBuilder(), stkey = new StringBuilder(), stii = new StringBuilder();
        if (dataholder.ids.size() != 0) {
            for (int i = 0; i < dataholder.ids.size(); i++) {
                str.append(dataholder.images.get(dataholder.ids.get(i)) + ";");
                stkey.append(dataholder.ids.get(i) + ";");
                stii.append(dataholder.tempImageItemList.get(
                        dataholder.ids.get(i)).toString()
                        + ";");
                System.out.println("SettingToSHP____keys "
                        + dataholder.ids.get(i)
                        + " path "
                        + dataholder.images.get(dataholder.ids.get(i))
                        + " imageItem "
                        + dataholder.tempImageItemList.get(
                        dataholder.ids.get(i)).toString());
            }
            path = str.toString();
            keys = stkey.toString();
            items = stii.toString();
            edit.putString("dataholderImages", path);
            edit.putString("dataholderKeys", keys);
            edit.putString("dataholderItems", items);
            edit.apply();
            System.out.println("Save___" + items);
        } else {
            clearShp();

        }
    }

    public void getAllFromShp() {
        dataholder.ids.clear();
        dataholder.images.clear();
        dataholder.tempImageItemList.clear();

        String path, keys, items;
        String[] patharray, keysarray, itemsarray;
        path = mUserPreference.getString("dataholderImages", "");
        keys = mUserPreference.getString("dataholderKeys", "");
        items = mUserPreference.getString("dataholderItems", "");
        patharray = path.split(";");
        keysarray = keys.split(";");
        itemsarray = items.split(";");
        System.out.println("items______" + items);
        if (keys.length() > 0) {
            for (int i = 0; i < keysarray.length; i++) {
                Log.d("AppGetFromShp", keysarray[i] + " " + patharray[i]);
                dataholder.ids.add(keysarray[i]);
                dataholder.images.put(keysarray[i], patharray[i]);
                dataholder.tempImageItemList.put(keysarray[i],
                        getImageItemFromString(itemsarray[i]));

                System.out.println("GettingFromSHP____keys " + keysarray[i]
                        + " path " + patharray[i] + " imageItem "
                        + itemsarray[i].toString());
            }
        }

    }
    public void clearShp() {
        edit.clear();
        edit.apply();
    }
    public ImageItem getImageItemFromString(String string) {
        ImageItem i = new ImageItem();
        Log.d("imageitemFromString", "imageItem" + string);
        String[] data = string.split(",");

        if (data.length == 1) {
            i.imageBucket = "";
            i.imageId = "";
            i.imagePath = "";
            i.postDate = "";
            i.caption = "";
            // i.posted = "";
            i.timeMil = 0;
        } else {
            for (int ii = 0; ii < data.length; ii++) {
                if (ii == 0) {
                    i.imageBucket = data[0];
                }
                if (ii == 1) {
                    i.caption = data[1];
                }
                if (ii == 2) {
                    i.imagePath = data[2];
                }
                if (ii == 3) {
                    i.postDate = data[3];
                }

                if (ii == 4) {
                    i.imageId = data[4];
                }
                if (ii == 5) {
                    i.posted = data[5];
                }
                if (ii == 6) {
                    try {
                        i.timeMil = Long.parseLong(data[6]);
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        i.timeMil = 0;
                        Log.e("NumberFormatError", "SetToZero");
                        e.printStackTrace();
                    }
                }
                if (ii == 7) {
                    i.reminder = data[7];
                }
            }
        }

        Log.d("imageitemgetdata", data.toString());
        i.imageBucket = data[0];

        return i;
    }
    public void setIsAlbumCreated(Boolean isCreated) {
        edit.putBoolean("isAlbumCreated", isCreated);
        edit.apply();
    }

    public Boolean getIsAlbumCreated() {

        return mUserPreference.getBoolean("isAlbumCreated", false);
    }
}
