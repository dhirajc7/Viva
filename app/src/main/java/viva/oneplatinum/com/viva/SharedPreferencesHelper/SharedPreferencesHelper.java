package viva.oneplatinum.com.viva.SharedPreferencesHelper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

	static SharedPreferences shp;
	static SharedPreferences.Editor editor;

	public SharedPreferencesHelper(Context context) {
		shp = context.getSharedPreferences("Viva_user_config_" , Context.MODE_PRIVATE);
		editor = shp.edit();
	}

	public void setNotifications(Boolean stat) {
		editor.putBoolean("notifs", stat).commit();
	}

	public void setSound(Boolean stat) {
		editor.putBoolean("sound", stat).commit();
	}

	public void setVibrate(Boolean stat) {
		editor.putBoolean("vibrate", stat).commit();
	}

	public Boolean getSound() {
		return shp.getBoolean("sound", true);
	}

	public Boolean getNotifications() {
		return shp.getBoolean("notifs", true);
	}

	public Boolean getVibrate() {
		return shp.getBoolean("vibrate", true);
	}

}
