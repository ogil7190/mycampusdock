package com.swalla.campusdock.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.swalla.campusdock.Activities.Registration;
import com.swalla.campusdock.Databases.DockDB;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;
import static com.swalla.campusdock.Utils.Config.PREF_USER_NAME;
import static com.swalla.campusdock.Utils.Config.PREF_USER_PHONE;
import static com.swalla.campusdock.Utils.Config.PREF_USER_ROLL;
import static com.swalla.campusdock.Utils.Config.PREF_USER_SUBSCRIPTIONS;

/**
 * Created by ogil on 14/01/18.
 */

public class ProfileFragment extends android.support.v4.app.Fragment {

    public static final String ID = "ProfileFragment";
    private TextView name, roll, phone;
    private Button logout;
    private SharedPreferences pref;
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile,null);
        pref = getContext().getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);

        roll = v.findViewById(R.id.roll);
        roll.setText(pref.getString(PREF_USER_ROLL, ""));

        name = v.findViewById(R.id.name);
        name.setText(pref.getString(PREF_USER_NAME, ""));

        phone = v.findViewById(R.id.phone);
        phone.setText(pref.getString(PREF_USER_PHONE, ""));

        logout = v.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj = new JSONObject(pref.getString(PREF_USER_SUBSCRIPTIONS, ""));
                    JSONArray a = obj.getJSONArray(Config.TYPE_IMPLICIT);
                    for (int i = 0; i < a.length(); i++) {
                        String s = a.getString(i);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(s);
                    }

                    a = obj.getJSONArray(Config.TYPE_EXPLICIT);
                    for (int i = 0; i < a.length(); i++) {
                        String s = a.getString(i);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(s);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
                DockDB db = DockDB.getIntsance(getContext());
                db.getEventDao().nukeTable();
                db.getBulletinDao().nukeTable();
                pref.edit().clear().apply();
                File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "CampusDock");
                folder.delete();
                startActivity(new Intent(getActivity(), Registration.class));
                getActivity().finish();
            }
        });
        return v;
    }

    public ProfileFragment() {}
}