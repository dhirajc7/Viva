package viva.oneplatinum.com.viva.fb;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.internal.SessionAuthorizationType;
import com.facebook.internal.Utility;
import com.facebook.model.GraphUser;

import java.util.Arrays;
import java.util.List;

public class FacebookLogin extends Activity {

    private LoginProperties properties = new LoginProperties();
    private static String TAG = "FacebookLogin";
    private String applicationId = null;

    public static final int LOGIN = 0x1;
    public static final int LOGOUT = 0x2;
    public static final String LOGTYPE = "logType";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("checkFb", "check08");
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            this.finish();
            return;
        }

        initializeActiveSessionWithCachedToken(getApplicationContext());
        if (bundle.getInt(LOGTYPE) == LOGIN)
            loginFacebook();
        else
            logoutFacebook();
    }

    private void loginFacebook() {
        Log.i("checkFb", "check09");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(progressDialogCancelListener);

        Session currentSession = createSession();
        Session.OpenRequest openRequest = new Session.OpenRequest(
                this);
        if (openRequest != null) {
            openRequest.setDefaultAudience(properties.defaultAudience);
            openRequest.setPermissions(properties.permissions);
            openRequest.setLoginBehavior(properties.loginBehavior);
            if (SessionAuthorizationType.PUBLISH
                    .equals(properties.authorizationType)) {
                currentSession.openForPublish(openRequest);
                Log.i("session open for", "PUBLISH");
            } else {
                Log.i("session open for", "READ");
                currentSession.openForRead(openRequest);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session session = Session.getActiveSession();
        if (session != null && session.getAccessToken() != null) {
            session.onActivityResult(this, requestCode, resultCode, data);

            // DataHolder.Log("facebook auth token --->"+session.getAccessToken());
            SessionStore.storeAuthTOken(FacebookLogin.this,
                    session.getAccessToken());
            fetchUserInfo();
        } else {
            clearSession();
        }
    }

    private void fetchUserInfo() {
        final Session currentSession = Session.getActiveSession();
        Log.v("TAG : Fetch user Name",
                "Fetching userName" + Session.getActiveSession() == null ? "null"
                        : "active");
        if (currentSession != null && currentSession.isOpened()) {
//			showProgressDialog("Fetching User Info");

            Request request = Request.newMeRequest(currentSession,
                    new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser me, Response response) {
                            if (me != null) {
                                Log.d("response", response.toString());
                                Intent i = new Intent();
                                setResult(Activity.RESULT_OK, i);
                                FacebookLogin.this.finish();
                                SessionStore.storeFacebookInformation(
                                        FacebookLogin.this, me,response);
                                //showProgressDialog(false);

                            } else {
                                showProgressDialog(false);
                                Toast.makeText(FacebookLogin.this, "null",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email,name,birthday,gender,first_name,last_name,location,friends");
            request.setParameters(parameters);
            Request.executeBatchAsync(request);

        } else {
            Log.v("TAG ", "Something else");
            this.finish();
        }

    }

    private Session createSession() {
        clearSession();
        // Session session = Session.getActiveSession();
        // if (session == null || session.isClosed()
        // || session.getAccessToken() == null) {
        Session session = new Session.Builder(this).setApplicationId(
                applicationId).build();
        Session.setActiveSession(session);
        // }
        return session;
    }

    private void logoutFacebook() {
        clearSession();
//		Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
        this.finish();

    }

    private void clearSession() {
        Session session = Session.getActiveSession();
        if (session != null)
            session.closeAndClearTokenInformation();
        Session.setActiveSession(null);
    }

    private boolean initializeActiveSessionWithCachedToken(Context context) {
        if (context == null)
            return false;
        Session session = Session.getActiveSession();
        if (session != null)
            return session.isOpened();

        String applicationId = Utility.getMetadataApplicationId(context);
        if (applicationId == null)
            return false;
        this.applicationId = applicationId;
        return Session.openActiveSessionFromCache(context) != null;
    }

    @Override
    public void onBackPressed() {
        haltLoginActivity();
    }

    private OnCancelListener progressDialogCancelListener = new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
            haltLoginActivity();
        }
    };

    private void haltLoginActivity() {
        clearSession();
        this.finish();
    }

    private void showProgressDialog(boolean show) {
        if (show) {
            if (!progressDialog.isShowing())
                progressDialog.show();
        } else if (progressDialog.isShowing())
            progressDialog.dismiss();

    }

    private void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        showProgressDialog(true);
    }

    /**
     * Sets an OnErrorListener for this instance of LoginButton to call into
     * when certain exceptions occur.
     *
     * @param onErrorListener The listener object to set
     */
    public void setOnErrorListener(OnErrorListener onErrorListener) {
        properties.setOnErrorListener(onErrorListener);
    }

    /**
     * Returns the current OnErrorListener for this instance of LoginButton.
     *
     * @return The OnErrorListener
     */
    public OnErrorListener getOnErrorListener() {
        return properties.getOnErrorListener();
    }

    /**
     * Sets the default audience to use when the session is opened. This value
     * is only useful when specifying write permissions for the native login
     * dialog.
     *
     * @param defaultAudience the default audience value to use
     */
    public void setDefaultAudience(SessionDefaultAudience defaultAudience) {
        properties.setDefaultAudience(defaultAudience);
    }

    /**
     * Gets the default audience to use when the session is opened. This value
     * is only useful when specifying write permissions for the native login
     * dialog.
     *
     * @return the default audience value to use
     */
    public SessionDefaultAudience getDefaultAudience() {
        return properties.getDefaultAudience();
    }

    /**
     * Specifies a callback interface that will be called when the button's
     * notion of the current user changes (if the fetch_user_info attribute is
     * true for this control).
     */
    public interface UserInfoChangedCallback {
        /**
         * Called when the current user changes.
         *
         * @param user the current user, or null if there is no user
         */
        void onUserInfoFetched(GraphUser user);
    }

    /**
     * Callback interface that will be called when a network or other error is
     * encountered while logging in.
     */
    public interface OnErrorListener {
        /**
         * Called when a network or other error is encountered.
         *
         * @param error a FacebookException representing the error that was
         *              encountered.
         */
        void onError(FacebookException error);
    }

    static class LoginProperties {
        private SessionDefaultAudience defaultAudience = SessionDefaultAudience.FRIENDS;
        private List<String> permissions = Arrays.asList(new String[]{
                "user_photos", "email", "user_birthday", "public_profile", "user_location", "user_friends"});

        private SessionAuthorizationType authorizationType = null;
        private OnErrorListener onErrorListener;
        private SessionLoginBehavior loginBehavior = SessionLoginBehavior.SSO_WITH_FALLBACK;
        private Session.StatusCallback sessionStatusCallback;

        public void setOnErrorListener(OnErrorListener onErrorListener) {
            this.onErrorListener = onErrorListener;
        }

        public OnErrorListener getOnErrorListener() {
            return this.onErrorListener;
        }

        public void setDefaultAudience(SessionDefaultAudience defaultAudience) {
            this.defaultAudience = defaultAudience;
        }

        public SessionDefaultAudience getDefaultAudience() {
            return this.defaultAudience;
        }

        public void setReadPermissions(List<String> permissions, Session session) {
            if (SessionAuthorizationType.PUBLISH.equals(authorizationType)) {
                throw new UnsupportedOperationException(
                        "Cannot call setReadPermissions after setPublishPermissions has been called");
            }
            if (validatePermissions(permissions, SessionAuthorizationType.READ,
                    session)) {
                this.permissions = permissions;
                authorizationType = SessionAuthorizationType.READ;
            }
        }

        public void setPublishPermissions(List<String> permissions,
                                          Session session) {
            if (SessionAuthorizationType.READ.equals(authorizationType))
                throw new UnsupportedOperationException(
                        "Cannot call setPublishPermissions after setReadPermissions has been called");
            if (validatePermissions(permissions,
                    SessionAuthorizationType.PUBLISH, session)) {
                this.permissions = permissions;
                authorizationType = SessionAuthorizationType.PUBLISH;
            }
        }

        private boolean validatePermissions(List<String> permissions,
                                            SessionAuthorizationType authType, Session currentSession) {
            if (SessionAuthorizationType.PUBLISH.equals(authType)) {
                if (Utility.isNullOrEmpty(permissions))
                    throw new IllegalArgumentException(
                            "Permissions for publish actions cannot be null or empty");
            }

            if (currentSession != null && currentSession.isOpened()) {
                if (!Utility.isSubset(permissions,
                        currentSession.getPermissions())) {
                    Log.e(TAG,
                            "Cannot set additional permissions when session is already open");
                    return false;
                }
            }
            return true;
        }

        List<String> getPermissions() {
            return this.permissions;
        }

        public void clearPermissions() {
            permissions = null;
            authorizationType = null;
        }

        public void setLoginBehavior(SessionLoginBehavior loginBehavior) {
            this.loginBehavior = loginBehavior;
        }

        public void setSessionStatusCallback(Session.StatusCallback callback) {
            this.sessionStatusCallback = callback;
        }

        public Session.StatusCallback getSessionStatusCallback() {
            return this.sessionStatusCallback;
        }
    }

}
