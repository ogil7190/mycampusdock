package com.swalla.campusdock.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.LocalStore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.swalla.campusdock.Utils.Config.PREF_NAME;
import static com.swalla.campusdock.Utils.Config.PREF_USER_API_KEY;
import static com.swalla.campusdock.Utils.Config.PREF_USER_ROLL;

public class EventActivity extends AppCompatActivity {
    private ImageView cardImage;
    private TextView cardTitle, cardDescription, cardDate, cardCategory, cardOrganizer;
    private SharedPreferences pref;
    private Button enroll;
    private Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.HolyBlack);
        setContentView(R.layout.activity_event);
        event = (Event) getIntent().getSerializableExtra("event");
        enroll = findViewById(R.id.enroll);
        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribe(event.getId());
            }
        });
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

        cardDate.setText(event.getDate());
        cardOrganizer.setText(event.getOrganizer());
        cardCategory.setText(event.getCategory());
        if(event.getUrl()!=null) {
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "CampusDock");
            File f = new File(folder, event.getUrl());
        }
        else {

        }

        cardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent preview = new Intent(getApplicationContext(), PreviewImage.class);
                LocalStore.putObjectInCache("previewImage", cardImage.getDrawable());
                startActivity(preview);
            }
        });
    }

    private void subscribe(final String eventId){
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
                params.put("flag", "subscribe");
                return params;
            }
        };
        LocalStore.getNetworkqueue(this).add(stringRequest);
    }
}
