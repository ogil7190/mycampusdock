package com.swalla.campusdock.Activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.Config;
import com.swalla.campusdock.Utils.LocalStore;

import org.json.JSONArray;
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

public class Registration extends AppCompatActivity {

    private EditText editText_rollNumber;
    private ImageButton btn_submit;
    private ViewGroup transitionsContainer;
    private TextView text;
    private boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if(pref.getBoolean(PREF_USER_IS_LOGGED_IN, false)){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
        setContentView(R.layout.activity_registration);
        editText_rollNumber = findViewById(R.id.editText_rollNumber);
        btn_submit = findViewById(R.id.btn_submit);
        transitionsContainer = findViewById(R.id.transitions_container);
        text = transitionsContainer.findViewById(R.id.textView_showLoadingText);

        editText_rollNumber.requestFocus();

        editText_rollNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    ObjectAnimator animation = ObjectAnimator.ofFloat(v, "translationZ", 8f);
                    animation.setDuration(300);
                    animation.start();
                } else {
                    ObjectAnimator animation = ObjectAnimator.ofFloat(v, "translationZ", 2f);
                    animation.setDuration(300);
                    animation.start();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        editText_rollNumber.addTextChangedListener(new TextWatcher() {
            int prevL = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                prevL = editText_rollNumber.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if (prevL < length) {
                    switch (length){
                        case 1 : editable.append("/");break;
                        case 4 : editable.append("/");break;
                        case 8 : editable.append("/");break;
                        case 12 : editable.append("/");break;
                        case 14 : editable.append("/"); isValid = true; break;
                    }
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                String roll = editText_rollNumber.getText().toString().toLowerCase();

                if(validate(roll) && isValid){
                    fetchData(roll);
                    text.setVisibility(View.VISIBLE);
                } else {
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    editText_rollNumber.startAnimation(shake);
                }
            }
        });
        getStoragePermission();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean validate(String roll){
        if(roll.isEmpty() || roll.length()==0){
            return false;
        }
        if(!isNetworkAvailable()){
            Toasty.error(getApplicationContext(), "No Network Available!").show();
            return false;
        }
        else return true;
    }

    private SharedPreferences pref;

    private void fetchData(final String roll){
        String url ="https://mycampusdock.herokuapp.com/Register";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("App", "Response:"+response);
                        if(response.equals("{}")){
                            Toasty.error(getApplicationContext(), "Invalid Roll No.", Toast.LENGTH_SHORT).show();
                            text.setVisibility(View.GONE);
                            return;
                        }
                        try {
                            JSONObject obj = new JSONObject(response);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(PREF_USER_NAME, obj.getString("name"));
                            editor.putString(PREF_USER_ROLL, roll);
                            editor.putString(PREF_USER_PHONE, obj.getString("phone"));
                            editor.putString(PREF_USER_SUBSCRIPTIONS, obj.get("subscriptions").toString());
                            editor.putBoolean(PREF_USER_IS_LOGGED_IN, true);
                            editor.putString(PREF_USER_API_KEY, obj.getString("api"));
                            editor.apply();
                            subscribe(obj.getJSONObject("subscriptions"));
                            callOnDoneLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("App",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("roll", roll);
                return params;
            }
        };
        LocalStore.getNetworkqueue(this).add(stringRequest);
    }

    private void subscribe(JSONObject obj) throws JSONException{
        JSONArray a = obj.getJSONArray(Config.TYPE_IMPLICIT);
        for(int i=0; i<a.length(); i++){
            String s = a.getString(i);
            FirebaseMessaging.getInstance().subscribeToTopic(s);
        }

        a = obj.getJSONArray(Config.TYPE_EXPLICIT);
        for(int i=0; i<a.length(); i++){
            String s = a.getString(i);
            FirebaseMessaging.getInstance().subscribeToTopic(s);
        }
        FirebaseMessaging.getInstance().subscribeToTopic(pref.getString(PREF_USER_ROLL, "global").replace('/','-'));
    }

    public void callOnDoneLoading() {
        circle(transitionsContainer);
    }

    public void getStoragePermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7190);
        }
    }

    public void circle(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(HomeActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(HomeActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }
}
