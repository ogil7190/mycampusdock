package com.swalla.campusdock.Activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
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
import static com.swalla.campusdock.Utils.Config.PREF_REG_ID_KEY;
import static com.swalla.campusdock.Utils.Config.PREF_USER_API_KEY;
import static com.swalla.campusdock.Utils.Config.PREF_USER_IS_LOGGED_IN;
import static com.swalla.campusdock.Utils.Config.PREF_USER_NAME;
import static com.swalla.campusdock.Utils.Config.PREF_USER_PHONE;
import static com.swalla.campusdock.Utils.Config.PREF_USER_ROLL;
import static com.swalla.campusdock.Utils.Config.PREF_USER_SUBSCRIPTIONS;
import static com.swalla.campusdock.Utils.Config.TYPE_VERFIFICATION;

public class Registration extends AppCompatActivity {

    private EditText editText_rollNumber;
    private ImageButton btn_submit;
    private ViewGroup transitionsContainer;
    private TextView text;
    private boolean isValid;
    private ProgressBar progressBar;
    private ImageView logo;
    private int isSuccesfullyLoggedIn = -1;
    private String res;
    private String tempRoll = "OGIL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if(pref.getBoolean(PREF_USER_IS_LOGGED_IN, false)){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
        setContentView(R.layout.activity_registration);
        logo = findViewById(R.id.logo);

        progressBar = findViewById(R.id.progressBar);
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
                progressBar.setVisibility(View.VISIBLE);
                logo.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(transitionsContainer);
                String roll = editText_rollNumber.getText().toString().toLowerCase();

                if(validate(roll) && isValid){
                    register(roll);
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

    private void register(final String roll){
        isSuccesfullyLoggedIn = 0;
        tempRoll = roll.replace('/','-');
        FirebaseMessaging.getInstance().subscribeToTopic(tempRoll);
        String url ="https://mycampusdock.herokuapp.com/Register";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        logo.setVisibility(View.VISIBLE);
                        Log.d("App", "Response:"+response);
                        if(response.equals("{}")){
                            Toasty.error(getApplicationContext(), "Invalid Roll No.", Toast.LENGTH_SHORT).show();
                            text.setVisibility(View.GONE);
                            return;
                        }
                        res = response;
                        pref.edit().putString(PREF_USER_ROLL, roll).apply();
                        verifyPin();
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
                params.put("token", FirebaseInstanceId.getInstance().getToken());
                return params;
            }
        };
        LocalStore.getNetworkqueue(this).add(stringRequest);
    }

    private void saveUser(String response) throws JSONException{
        JSONObject obj = new JSONObject(response);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_USER_NAME, obj.getString("name"));
        editor.putString(PREF_USER_PHONE, obj.getString("phone"));
        editor.putString(PREF_USER_SUBSCRIPTIONS, obj.get("subscriptions").toString());
        editor.putBoolean(PREF_USER_IS_LOGGED_IN, true);
        editor.putString(PREF_USER_API_KEY, obj.getString("api"));
        editor.apply();
        subscribe(obj.getJSONObject("subscriptions"));
        isSuccesfullyLoggedIn = 1;
        registrationDone();
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
        FirebaseMessaging.getInstance().subscribeToTopic("global");
        editText_rollNumber.setInputType(InputType.TYPE_NULL);
    }

    private void verifyPin(){
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.verify_pin, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.PinDialog);
        final AlertDialog alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create();
        alertDialog.show();
        final ProgressBar progressBar = dialogView.findViewById(R.id.pinProgress);
        progressBar.setVisibility(View.GONE);
        final EditText p1 = dialogView.findViewById(R.id.p1);
        final EditText p2 = dialogView.findViewById(R.id.p2);
        final EditText p3 = dialogView.findViewById(R.id.p3);
        final EditText p4 = dialogView.findViewById(R.id.p4);
        final TextView error = dialogView.findViewById(R.id.textError);
        final TextView disable = dialogView.findViewById(R.id.disableView);
        p1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2==1)
                {
                    p2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        p2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2==1)
                {
                    p3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        p3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2==1)
                {
                    p4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        p4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(p4.getText().toString().length()>0) {
                    error.setVisibility(View.GONE);
                    disable.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    String pin = p1.getText().toString() + p2.getText().toString() + p3.getText().toString() + p4.getText().toString();
                    try {
                        if (isPinVerified(pin) == 1) {
                            alertDialog.dismiss();
                            saveUser(res);
                        } else {
                            error.setVisibility(View.VISIBLE);
                            disable.setVisibility(View.GONE);
                        }
                        disable.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    error.setVisibility(View.GONE);
                    disable.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private int isPinVerified(String pinEntered){
        Log.d("App", "Pin :"+pinEntered);
        String originalPin = (String)LocalStore.getObject(TYPE_VERFIFICATION);
        Log.d("App", "ORIGINAL PIN:"+originalPin);
        if(pinEntered.equals(originalPin)){
            return 1;
        }
        return 0;
    }

    private void registrationDone(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void getStoragePermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7190);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(isSuccesfullyLoggedIn == 0){
            Log.d("App","Registration not done!");
            FirebaseMessaging.getInstance().unsubscribeFromTopic(tempRoll.replace('/','-'));
            pref.edit().clear().commit();
        } else if(isSuccesfullyLoggedIn == 1){
            Log.d("App","Registration Done!");
        }
        super.onDestroy();
    }
}
