package com.swalla.campusdock.Services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.swalla.campusdock.Utils.Config.Prefs.PREF_NAME;
import static com.swalla.campusdock.Utils.Config.Flags.*;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_REG_ID_KEY;

/**
 * Created by ogil on 14/01/18.
 */

public class CustomFirebaseInstanceService extends FirebaseInstanceIdService {
    private static final String TAG = CustomFirebaseInstanceService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storeRegIdInPref(refreshedToken);
        sendRegistrationToServer(refreshedToken);
        Intent registrationComplete = new Intent(FLAG_REG_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_REG_ID_KEY, token);
        editor.apply();
    }
}
