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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

import static com.swalla.campusdock.Utils.Config.Prefs.*;
import static com.swalla.campusdock.Utils.Config.Types.TYPE_EXPLICIT;
import static com.swalla.campusdock.Utils.Config.Types.TYPE_IMPLICIT;
import static com.swalla.campusdock.Utils.Config.Types.TYPE_VERIFICATION;

public class Registration extends AppCompatActivity {

    private EditText editText_rollNumber;
    private ImageButton btn_submit;
    private ViewGroup transitionsContainer;
    private TextView text;
    private boolean isValid;
    private ProgressBar progressBar;
    private ImageView logo;
    private JSONObject res;
    private int wrongAttempt = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
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
                if (hasFocus) {
                    ObjectAnimator animation = ObjectAnimator.ofFloat(v, "translationZ", 8f);
                    animation.setDuration(300);
                    animation.start();
                } else {
                    ObjectAnimator animation = ObjectAnimator.ofFloat(v, "translationZ", 2f);
                    animation.setDuration(300);
                    animation.start();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    switch (length) {
                        case 1:
                            editable.append("/");
                            break;
                        case 4:
                            editable.append("/");
                            break;
                        case 8:
                            editable.append("/");
                            break;
                        case 12:
                            editable.append("/");
                            break;
                        case 14:
                            editable.append("/");
                            isValid = true;
                            break;
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

                if (validate(roll) && isValid) {
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

    private boolean validate(String roll) {
        if (roll.isEmpty() || roll.length() == 0) {
            return false;
        }
        if (!isNetworkAvailable()) {
            Toasty.error(getApplicationContext(), "No Network Available!").show();
            return false;
        } else return true;
    }

    private SharedPreferences pref;

    private void register(final String roll) {
        String url = "https://mycampusdock.herokuapp.com/Register";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        logo.setVisibility(View.VISIBLE);
                        Log.d("App", "Response:" + response);
                        if (response.equals("{}")) {
                            Toasty.error(getApplicationContext(), "Invalid Roll No.", Toast.LENGTH_SHORT).show();
                            text.setVisibility(View.GONE);
                            return;
                        }
                        try {
                            res = new JSONObject(response);
                            pref.edit().putString(PREF_USER_ROLL, roll).apply();
                            verifyPin(res.getString("email"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("App", error.toString());
                Toasty.error(getApplicationContext(), "Try Again!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                logo.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("roll", roll);
                params.put("token", FirebaseInstanceId.getInstance().getToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        LocalStore.getNetworkqueue(this).add(stringRequest);
    }

    private void saveUser(JSONObject obj) throws JSONException {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_USER_NAME, obj.getString("name"));
        editor.putString(PREF_USER_PHONE, obj.getString("phone"));
        editor.putString(PREF_USER_SUBSCRIPTIONS, obj.get("subscriptions").toString());
        editor.putBoolean(PREF_USER_IS_LOGGED_IN, true);
        editor.putString(PREF_USER_API_KEY, obj.getString("api"));
        editor.putString(PREF_USER_EMAIL, obj.getString("email"));
        editor.putString(PREF_USER_CLASS, obj.getString("class"));
        editor.apply();
        subscribe(obj.getJSONObject("subscriptions"));
        registrationDone();
    }

    private void subscribe(JSONObject obj) throws JSONException {
        JSONArray a = obj.getJSONArray(TYPE_IMPLICIT);
        for (int i = 0; i < a.length(); i++) {
            String s = a.getString(i);
            FirebaseMessaging.getInstance().subscribeToTopic(s);
        }

        a = obj.getJSONArray(TYPE_EXPLICIT);
        for (int i = 0; i < a.length(); i++) {
            String s = a.getString(i);
            FirebaseMessaging.getInstance().subscribeToTopic(s);
        }
        FirebaseMessaging.getInstance().subscribeToTopic("global");
        editText_rollNumber.setInputType(InputType.TYPE_NULL);
    }

    private void verifyPin(String email) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.verify_pin, null);
        wrongAttempt = 5;
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
        final TextView errorHelp = dialogView.findViewById(R.id.textHelp);
        errorHelp.setText("An e-mail is sent to " + email + ". Please verify PIN");
        p1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 == 1) {
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
                if (i2 == 1) {
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
                if (i2 == 1) {
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
                if (p4.getText().toString().length() > 0) {
                    error.setVisibility(View.GONE);
                    disable.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    String pin = p1.getText().toString() + p2.getText().toString() + p3.getText().toString() + p4.getText().toString();
                    try {
                        if (isPinVerified(pin) == 1) {
                            alertDialog.dismiss();
                            saveUser(res);
                        } else {
                            wrongAttempt--;
                            error.setVisibility(View.VISIBLE);
                        }
                        if(wrongAttempt<0){
                            alertDialog.dismiss();
                            Toasty.error(getApplicationContext(), "Maximum Attempts Crossed!", Toast.LENGTH_LONG).show();
                        }else {
                            disable.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    error.setVisibility(View.GONE);
                    disable.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private int isPinVerified(String pinEntered) {
        String originalPin = (String) LocalStore.getObject(TYPE_VERIFICATION);
        if (pinEntered.equals(originalPin)) {
            return 1;
        }
        return 0;
    }

    private void registrationDone() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void getStoragePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7190);
        }
    }
}
