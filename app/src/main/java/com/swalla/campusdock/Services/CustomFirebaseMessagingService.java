package com.swalla.campusdock.Services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.swalla.campusdock.Activities.HomeActivity;
import com.swalla.campusdock.Classes.Bulletin;
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
import static com.swalla.campusdock.Utils.Config.TYPE_BULLETIN;
import static com.swalla.campusdock.Utils.Config.TYPE_EVENT;
import static com.swalla.campusdock.Utils.Config.TYPE_EVENT_UPDATE;
import static com.swalla.campusdock.Utils.Config.TYPE_VERFIFICATION;


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
        pref = getApplicationContext().getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);
        if (remoteMessage == null) return;

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

    private void handleDataMessage(JSONObject obj) throws JSONException{
        Log.e(TAG, "pushed json: " + obj.toString());

        JSONObject data = obj.getJSONObject("content");
        final JSONObject payload = new JSONObject(data.getString("payload"));

        switch (payload.getString("type")){
            case TYPE_EVENT :
                final String title = data.getString("title");
                final String message = data.getString("description");
                final String imageUrl = data.getString("url");
                final String timestamp = data.getString("timestamp");

                Log.e(TAG, "title: " + title);
                Log.e(TAG, "message: " + message);
                Log.e(TAG, "payload: " + payload.toString());
                Log.e(TAG, "imageUrl: " + imageUrl);
                Log.e(TAG, "timestamp: " + timestamp);

                if(pref.getBoolean(PREF_USER_IS_LOGGED_IN, false)) {
                    accountReach(payload.getString("event_id"), ""+Config.REQ_REACH_EVENT);
                    DockDB.getIntsance(getApplicationContext()).getEventDao().insert(Event.parseFromJSON(payload));
                    if (!NotiUtil.isAppIsInBackground(getApplicationContext())) {
                        NotiUtil notificationUtils = new NotiUtil(getApplicationContext());
                        notificationUtils.getBitmapFromURL(imageUrl);
                        Intent notify = new Intent(Config.NEW_UPDATE);
                        notify.putExtra(Config.NEW_UPDATE, Config.SHOW_NEW_EVENT);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(notify);
                    } else {
                        Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        resultIntent.setAction(Config.SHOW_NEW_EVENT);
                        resultIntent.putExtra(TYPE_EVENT, payload.toString());
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        if (TextUtils.isEmpty(imageUrl)) {
                            showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                        } else {
                            showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                        }
                    }
                }
                break;
            case TYPE_EVENT_UPDATE :
                JSONObject updatedData = payload.getJSONObject("updatedData");
                try {
                    Log.d("App", "ID:"+updatedData.getString("event_id"));
                    Event e = DockDB.getIntsance(this).getEventDao().getEvent(updatedData.getString("event_id"));
                    Log.d("app","Event:"+e.getEventName()+e.getId());
                    e = e.updateEvent(updatedData);
                    DockDB.getIntsance(this).getEventDao().update(e);
                } catch (JSONException e){
                    e.printStackTrace();
                }
                break;
            case TYPE_BULLETIN :
                final String noti_bullet_title = data.getString("title");
                final String noti_bullet_message = data.getString("description");
                final String noti_bullet_timestamp = data.getString("timestamp");

                Log.e(TAG, "title: " + noti_bullet_title);
                Log.e(TAG, "message: " + noti_bullet_message);
                Log.e(TAG, "payload: " + payload.toString());
                Log.e(TAG, "timestamp: " + noti_bullet_timestamp);

                if(pref.getBoolean(PREF_USER_IS_LOGGED_IN, false)) {
                    accountReach(payload.getString("bulletin_id"), ""+Config.REQ_REACH_BULLETIN);
                    DockDB.getIntsance(getApplicationContext()).getBulletinDao().insert(Bulletin.parseFromJSON(payload));
                    if (!NotiUtil.isAppIsInBackground(getApplicationContext())) {
                        Intent notify = new Intent(Config.NEW_UPDATE);
                        notify.putExtra(Config.NEW_UPDATE, Config.SHOW_NEW_BULLETIN);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(notify);
                    } else {
                        Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        resultIntent.setAction(Config.SHOW_NEW_BULLETIN);
                        resultIntent.putExtra(TYPE_BULLETIN, payload.toString());
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        showNotificationMessage(getApplicationContext(), noti_bullet_title, noti_bullet_message, noti_bullet_timestamp, resultIntent);
                    }
                }
                break;
            case TYPE_VERFIFICATION :
                LocalStore.putObject(TYPE_VERFIFICATION, payload.getString("pin"));
                break;
        }
    }

    private void accountReach(final String eventId,final String reach){
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
                params.put("flag", reach);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
