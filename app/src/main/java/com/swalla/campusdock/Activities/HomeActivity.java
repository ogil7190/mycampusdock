package com.swalla.campusdock.Activities;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.Fragments.BulletinFragment;
import com.swalla.campusdock.Fragments.HomeFragment;
import com.swalla.campusdock.Fragments.ProfileFragment;
import com.swalla.campusdock.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.swalla.campusdock.Utils.Config.PUSH_NOTI;
import static com.swalla.campusdock.Utils.Config.TYPE_EVENT;

public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private Fragment currentFragment;
    View rootLayout;

    private int revealX;
    private int revealY;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_upcoming:
                    currentFragment = HomeFragment.newInstance(HomeActivity.this);
                    break;

                case R.id.navigation_history:
                    break;

                case R.id.navigation_interests:
                    break;

                case R.id.navigation_classroom:
                    currentFragment = BulletinFragment.newInstance(HomeActivity.this);
                    break;

                case R.id.navigation_profile:
                    currentFragment = ProfileFragment.newInstance();
                    break;
            }
            replaceFragment();
            return true;
        }
    };

    private void replaceFragment(){
        try {
            if (isOK) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, currentFragment).commit();
                hideKeyboard(HomeActivity.this);
            } else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, currentFragment).commit();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity) {
        final InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean isOK = false;
    @Override
    protected void onPostResume() {
        super.onPostResume();
        isOK = true;
    }

    public void setBadge(){

    }

    private BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        currentFragment = HomeFragment.newInstance(this);
        final Intent intent=getIntent();
        rootLayout=findViewById(R.id.activity_home);
        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);


            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        replaceFragment();
        FirebaseMessaging.getInstance().subscribeToTopic("global");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getAction() != null
                ) {
            Log.d("App",getIntent().getAction());
            switch (getIntent().getAction()) {
                case PUSH_NOTI :
                    try {
                        JSONObject obj = new JSONObject(getIntent().getExtras().getString(TYPE_EVENT));
                        Event event = new Event(obj.getString("event_id"), obj.getString("name"), obj.getString("description").replace("\r\n", "<br>"), obj.getString("date"), obj.getString("organizer"), obj.getString("category"), obj.getString("url"), obj.getString("created_by"));
                        HomeFragment fragment = HomeFragment.newInstance(HomeActivity.this);
                        fragment.setStartingEvent(event);
                        currentFragment = fragment;
                        getIntent().setAction(null);
                        replaceFragment();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) );

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 35, finalRadius);
            circularReveal.setDuration(600);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        } else {
            finish();
        }
    }


}
