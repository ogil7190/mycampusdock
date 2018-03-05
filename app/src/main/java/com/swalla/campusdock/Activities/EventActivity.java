package com.swalla.campusdock.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
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
import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.swalla.campusdock.Utils.Config.PREF_NAME;
import static com.swalla.campusdock.Utils.Config.PREF_USER_API_KEY;
import static com.swalla.campusdock.Utils.Config.PREF_USER_ROLL;

public class EventActivity extends AppCompatActivity  {
    private ImageView cardImage;
    private TextView cardTitle, cardDescription, cardDate;
    private SharedPreferences pref;
    private Button enroll;
    private Event event;
    private TextView chipText;
    private Snackbar snackbar;

    private AlertDialog prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.HolyBlack);
        setContentView(R.layout.activity_event);
        event = (Event) getIntent().getSerializableExtra("event");
        event = DockDB.getIntsance(this).getEventDao().getEvent(event.getId());
        enroll = findViewById(R.id.enroll);
        if(event.isEnrolled()){
            enroll.setText("Successfully Enrolled");
            enroll.setBackground(getDrawable(R.drawable.input_round_disable));
        }
        if(event.getId().equals("@ogil")){
            enroll.setEnabled(false);
        }

        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String text = "Do you want to get enrolled in this event?";
            String text2 = "Already enrolled! Wants to withdraw yourself?";
            String action = "Enroll";
            String action2 = "WithDraw";
            snackbar = Snackbar.make(view, event.isEnrolled()?text2:text ,5000);
            snackbar.setAction(event.isEnrolled()?action2:action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleSubscription(event.getId());
                }
            }).setActionTextColor(getResources().getColor(R.color.colorAccent)).show();
            }
        });
        chipText = findViewById(R.id.chipText);
        cardImage = findViewById(R.id.card_image);
        cardTitle = findViewById(R.id.card_title);
        cardDescription = findViewById(R.id.card_desc);
        cardDate = findViewById(R.id.card_date);

        cardTitle.setText(event.getEventName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cardDescription.setText(Html.fromHtml(event.getDescription(), Html.FROM_HTML_MODE_LEGACY) );
        }
        else{
            cardDescription.setText(Html.fromHtml(event.getDescription()));
        }
        cardDescription.setMovementMethod(new ScrollingMovementMethod());
        chipText.setText(event.getCreated_by());
        cardDate.setText(event.getDate()+" - "+event.getEndDate());
        if(event.getUrl()!=null) {
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "CampusDock");
            File f = new File(folder, event.getUrl());
            if(f.exists())
                Glide.with(this).load(f).into(cardImage);
            else {
                cardImage.setImageResource(R.drawable.test_poster);
                NotiUtil.getBitmapFromURL(event.getUrl());
            }
        }
        else {
            cardImage.setImageResource(R.drawable.test_poster);
        }
        cardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent preview = new Intent(getApplicationContext(), PreviewImage.class);
                LocalStore.putObject("previewImage", cardImage.getDrawable());
                startActivity(preview);
            }
        });
        init();
    }

    private void init(){
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_prompt, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.PinDialog);
        prompt = dialogBuilder.setCancelable(false).setView(dialogView).create();
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);
        title.setText("Please Wait...");
        message.setText("Updating changes on server");
    }

    @Override
    public void onBackPressed() {
        enroll.setVisibility(View.GONE);
        super.onBackPressed();
    }

    private void handleSubscription(final String eventId){
        prompt.show();
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String url = "https://mycampusdock.herokuapp.com/enroll";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("App", "response:"+response);
                        try {
                            if (!new JSONObject(response).getBoolean("error")) {
                                FirebaseMessaging.getInstance().subscribeToTopic(event.getId());
                                if(event.isEnrolled())
                                    event.setEnrolled(false);
                                else
                                    event.setEnrolled(true);
                                enroll.setText(event.isEnrolled()?"Successfully Enrolled":"Click to Enroll");
                                if(event.isEnrolled())
                                    enroll.setBackground(getDrawable(R.drawable.input_round_disable));
                                else
                                    enroll.setBackground(getDrawable(R.drawable.input_round_color));

                                DockDB.getIntsance(getApplicationContext()).getEventDao().update(event);
                                prompt.dismiss();
                                Toasty.normal(getApplicationContext(),"Success!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            prompt.dismiss();
                            Toasty.normal(getApplicationContext(),"Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("App", error.toString());
                prompt.dismiss();
                Toasty.normal(getApplicationContext(),"Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("roll", pref.getString(PREF_USER_ROLL, ""));
                params.put("api", pref.getString(PREF_USER_API_KEY, ""));
                params.put("event_id", eventId);
                params.put("flag", event.isEnrolled() ? ""+107 : ""+101);
                return params;
            }
        };
        LocalStore.getNetworkqueue(this).add(stringRequest);
    }

}
