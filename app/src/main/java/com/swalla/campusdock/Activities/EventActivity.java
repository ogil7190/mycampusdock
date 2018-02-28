package com.swalla.campusdock.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.Databases.DockDB;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.LocalStore;
import com.swalla.campusdock.Utils.NotiUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.swalla.campusdock.Utils.Config.PREF_NAME;
import static com.swalla.campusdock.Utils.Config.PREF_USER_API_KEY;
import static com.swalla.campusdock.Utils.Config.PREF_USER_ROLL;

public class EventActivity extends AppCompatActivity  {
    private ImageView cardImage;
    private TextView cardTitle, cardDescription, cardDate, cardCategory, cardOrganizer;
    private SharedPreferences pref;
    private Button enroll;
    private Event event;
    private TextView chipText;
    private Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.HolyBlack);
        setContentView(R.layout.activity_event);
        event = (Event) getIntent().getSerializableExtra("event");
        enroll = findViewById(R.id.enroll);
        if(event.isEnrolled()){
            enroll.setText("Successfully Enrolled");
            enroll.setEnabled(false);
            enroll.setBackgroundColor(Color.parseColor("#666666"));
        }
        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            snackbar = Snackbar.make(view,"Do you really want to get enrolled in this event?",Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Enroll", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscribe(event.getId());
                }
            }).setActionTextColor(getResources().getColor(R.color.colorAccent)).show();
            }
        });
        chipText = findViewById(R.id.chipText);
        cardImage = findViewById(R.id.card_image);
        cardTitle = findViewById(R.id.card_title);
        cardDescription = findViewById(R.id.card_desc);
        cardDate = findViewById(R.id.card_date);
        cardCategory = findViewById(R.id.card_category);
        cardOrganizer = findViewById(R.id.card_organizer);

        cardTitle.setText(event.getEventName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cardDescription.setText(Html.fromHtml(event.getDescription(), Html.FROM_HTML_MODE_LEGACY) );
        }
        else{
            cardDescription.setText(Html.fromHtml(event.getDescription()));
        }

        chipText.setText(event.getCreated_by());
        cardDate.setText(event.getDate());
        cardOrganizer.setText(event.getOrganizer());
        cardCategory.setText(event.getCategory());
        if(event.getUrl()!=null) {
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "CampusDock");
            File f = new File(folder, event.getUrl());
            if(f.exists())
                Glide.with(this).load(f).into(cardImage);
            else {
                cardImage.setImageResource(event.getBanner());
                NotiUtil.getBitmapFromURL(event.getUrl());
            }
        }
        else {
            cardImage.setImageResource(event.getBanner());
        }
        cardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent preview = new Intent(getApplicationContext(), PreviewImage.class);
                LocalStore.putObject("previewImage", cardImage.getDrawable());
                startActivity(preview);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        enroll.setVisibility(View.GONE);
        super.onBackPressed();
    }

    private void subscribe(final String eventId){
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String url = "https://mycampusdock.herokuapp.com/enroll";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("App", "response:"+response);
                        try {
                            if (new JSONObject(response).getBoolean("error")) {
                                FirebaseMessaging.getInstance().subscribeToTopic(event.getId());
                                event.setEnrolled(true);
                                enroll.setText("Successfully Enrolled!");
                                enroll.setBackgroundColor(Color.parseColor("#666666"));
                                DockDB.getIntsance(getApplicationContext()).getEventDao().update(event);
                                enroll.setEnabled(false);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
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
                params.put("flag", "subscribe");
                return params;
            }
        };
        LocalStore.getNetworkqueue(this).add(stringRequest);
    }

}
