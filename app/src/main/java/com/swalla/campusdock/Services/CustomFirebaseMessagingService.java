package com.swalla.campusdock.Services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.swalla.campusdock.Activities.HomeActivity;
import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.Utils.Config;
import com.swalla.campusdock.Databases.DockDB;
import com.swalla.campusdock.Utils.LocalStore;
import com.swalla.campusdock.Utils.NotiUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.swalla.campusdock.Utils.Config.PREF_NAME;
import static com.swalla.campusdock.Utils.Config.PREF_USER_API_KEY;
import static com.swalla.campusdock.Utils.Config.PREF_USER_IS_LOGGED_IN;
import static com.swalla.campusdock.Utils.Config.PREF_USER_NAME;
import static com.swalla.campusdock.Utils.Config.PREF_USER_PHONE;
import static com.swalla.campusdock.Utils.Config.PREF_USER_ROLL;
import static com.swalla.campusdock.Utils.Config.PREF_USER_SUBSCRIPTIONS;
import static com.swalla.campusdock.Utils.Config.TYPE_EVENT;


/**
 * Created by ogil on 14/01/18.
 */

public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = CustomFirebaseInstanceService.class.getSimpleName();

    private NotiUtil notificationUtils;
    private SharedPreferences pref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null) return;

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotiUtil.isAppIsInBackground(getApplicationContext())) {

            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTI);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotiUtil notificationUtils = new NotiUtil(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
    }

    private void handleDataMessage(JSONObject obj) throws JSONException{
        Log.e(TAG, "pushed json: " + obj.toString());

        JSONObject data = obj.getJSONObject("content");

        final String title = data.getString("title");
        final String message = data.getString("description");
        final String imageUrl = data.getString("url");
        final String timestamp = data.getString("timestamp");
        final JSONObject payload = new JSONObject(data.getString("payload"));

        Log.e(TAG, "title: " + title);
        Log.e(TAG, "message: " + message);
        Log.e(TAG, "payload: " + payload.toString());
        Log.e(TAG, "imageUrl: " + imageUrl);
        Log.e(TAG, "timestamp: " + timestamp);

        //payload is a json which can contain JSON data about event.
        // {"type":"event", "name":"Demo Event", "description":"This is the test description", "date":"JAN 21", "organizer":"FCS", "category":"Demo" }

        switch (payload.getString("type")){
            case TYPE_EVENT :
                accountReach(payload.getString("event_id"));
                DockDB.getIntsance(getApplicationContext()).getEventDao().insert(new Event(payload.getString("event_id"), payload.getString("name"), payload.getString("description").replace("\r\n", "<br>"), payload.getString("date"), payload.getString("organizer"), payload.getString("category"), payload.getString("url"), payload.getString("created_by")));
                if (!NotiUtil.isAppIsInBackground(getApplicationContext())) {
                    NotiUtil notificationUtils = new NotiUtil(getApplicationContext());
                    notificationUtils.getBitmapFromURL(imageUrl);

                    Intent pushNotification = new Intent(Config.PUSH_NOTI);
                    pushNotification.putExtra(TYPE_EVENT, payload.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                }
                else {
                    Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    resultIntent.setAction(Config.PUSH_NOTI);
                    resultIntent.putExtra(TYPE_EVENT, payload.toString());
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    }
                    else {
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    }
                }
                break;
        }
    }

    private void accountReach(final String eventId){
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String url = "https://mycampusdock.herokuapp.com/enroll";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("App", "Response:" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("App", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("roll", pref.getString(PREF_USER_ROLL, ""));
                params.put("api", pref.getString(PREF_USER_API_KEY, ""));
                params.put("event_id", eventId);
                params.put("flag", "reach");
                return params;
            }
        };
        LocalStore.getNetworkqueue(this).add(stringRequest);
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotiUtil(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotiUtil(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
