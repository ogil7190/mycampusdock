package com.swalla.campusdock.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.swalla.campusdock.Activities.Registration;
import com.swalla.campusdock.Databases.DockDB;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Services.CustomFirebaseMessagingService;
import com.swalla.campusdock.Utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_NAME;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_USER_EMAIL;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_USER_LIKES_US;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_USER_NAME;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_USER_PHONE;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_USER_ROLL;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_USER_SUBSCRIPTIONS;
import static com.swalla.campusdock.Utils.Config.Requests.REQ_LIKE;
import static com.swalla.campusdock.Utils.Config.Types.TYPE_EXPLICIT;
import static com.swalla.campusdock.Utils.Config.Types.TYPE_IMPLICIT;

/**
 * Created by ogil on 14/01/18.
 */

public class ProfileFragment extends android.support.v4.app.Fragment {

    public static final String ID = "ProfileFragment";
    private TextView name, roll, phone, email;
    private Button logout, like;
    private SharedPreferences pref;
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile,null);
        pref = getContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        roll = v.findViewById(R.id.rollHint);
        roll.setText(pref.getString(PREF_USER_ROLL, ""));

        name = v.findViewById(R.id.nameHint);
        name.setText(pref.getString(PREF_USER_NAME, ""));

        phone = v.findViewById(R.id.phoneHint);
        phone.setText(pref.getString(PREF_USER_PHONE, ""));

        email = v.findViewById(R.id.emailHint);
        email.setText(pref.getString(PREF_USER_EMAIL, ""));

        logout = v.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.event_backup_prompt, null);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.PinDialog);
                final AlertDialog alertDialog = dialogBuilder.setCancelable(true).setView(dialogView).create();
                alertDialog.show();
                final TextView title = dialogView.findViewById(R.id.title);
                final TextView message = dialogView.findViewById(R.id.message);
                final Button yes = dialogView.findViewById(R.id.yes);
                final Button no = dialogView.findViewById(R.id.no);

                yes.setText("Logout");
                no.setText("Cancel");

                title.setText("Logout");
                message.setText("Are you sure you want to logout ?");
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        logout();
                    }
                });
            }
        });

        like = v.findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pref.getBoolean(PREF_USER_LIKES_US, false)) {
                    CustomFirebaseMessagingService.accountReach(getContext(), "OGIL", "" + REQ_LIKE);
                    pref.edit().putBoolean(PREF_USER_LIKES_US, true).apply();
                }
                Toasty.success(getContext(), "Thanks For your Support!", Toast.LENGTH_LONG).show();
            }
        });
        return v;
    }

    private void logout(){
        try {
            JSONObject obj = new JSONObject(pref.getString(PREF_USER_SUBSCRIPTIONS, ""));
            JSONArray a = obj.getJSONArray(TYPE_IMPLICIT);
            for (int i = 0; i < a.length(); i++) {
                String s = a.getString(i);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(s);
            }

            a = obj.getJSONArray(TYPE_EXPLICIT);
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
        startActivity(new Intent(getActivity(), Registration.class));
        getActivity().finish();
    }

    public ProfileFragment() {}
}