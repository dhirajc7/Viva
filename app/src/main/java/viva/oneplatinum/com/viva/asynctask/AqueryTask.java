package viva.oneplatinum.com.viva.asynctask;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.util.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AqueryTask {

    private AQuery mAquery;

    public AqueryTask(Context context) {
        mAquery = new AQuery(context);
    }

    public void sendRequest(String requestUrl,
                            HashMap<String, String> mRequestParams, Class<JSONObject> object,
                            AjaxCallback<JSONObject> callback) {
        mAquery.ajax(requestUrl, mRequestParams, object, callback);
    }

    public void sendPostRequest(String requestUrl,
                                HashMap<String, Object> mRequestParams, Class<JSONObject> object,
                                AjaxCallback<JSONObject> callback) {
        mAquery.ajax(requestUrl, mRequestParams, object, callback);
    }
    public void sendPostObjectRequest(String requestUrl,
                                Map<String, Object> mRequestParams, Class<JSONObject> object,
                                AjaxCallback<JSONObject> callback) {
        mAquery.ajax(requestUrl, mRequestParams, object, callback);
    }


    public void sendRequest(String requestUrl, Class<JSONObject> object,
                            AjaxCallback<JSONObject> callback) {
        mAquery.ajax(requestUrl, object, callback);
    }
    public void setImage(String url, ImageView npic,ProgressBar progress) {
        // TODO Auto-generated method stub
        Log.d("image url", url + "");
        mAquery.id(npic).progress(progress).image(url, true, true, 0, 0, null, Constants.FADE_IN);
    }

}
