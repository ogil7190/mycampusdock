package com.swalla.campusdock.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.swalla.campusdock.Classes.Bulletin;
import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.Fragments.BulletinFragment;
import com.swalla.campusdock.Fragments.HomeFragment;
import com.swalla.campusdock.Fragments.ProfileFragment;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.Config;
import com.swalla.campusdock.Utils.NotiUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.swalla.campusdock.Utils.Config.SHOW_NEW_BULLETIN;
import static com.swalla.campusdock.Utils.Config.SHOW_NEW_EVENT;
import static com.swalla.campusdock.Utils.Config.TYPE_BULLETIN;
import static com.swalla.campusdock.Utils.Config.TYPE_EVENT;

public class HomeActivity extends AppCompatActivity {
    private Fragment currentFragment;
    private static AHBottomNavigation navigation;
    private FragmentTransaction transaction;
    private static String currentFragmentTag = HomeFragment.ID;
    private boolean isOK = false;
    private static int notifyPosition = -1;
    private static int currentPos = 0;

    private AHBottomNavigation.OnTabSelectedListener listener = new AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {
            currentPos = position;
            switch(position){
                case 0:
                    currentFragment = HomeFragment.newInstance();
                    currentFragmentTag = HomeFragment.ID;
                    break;
                case 1 :
                    currentFragment = BulletinFragment.newInstance();
                    currentFragmentTag = BulletinFragment.ID;
                    break;
                case 4:
                    currentFragment = ProfileFragment.newInstance();
                    currentFragmentTag = ProfileFragment.ID;
                    break;
            }
            if(notifyPosition==position){
                navigation.setNotification("",notifyPosition);
            }
            replaceFragment();
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int count = getSupportFragmentManager().getBackStackEntryCount() -1; //top fragment
        if(count < 0) {
            setNavigationTab(HomeFragment.ID);
        } else
            setNavigationTab(getSupportFragmentManager().getBackStackEntryAt(count).getName());
    }

    private void setNavigationTab(String Id){
        switch (Id){
            case HomeFragment.ID :
                navigation.setCurrentItem(0, false);
                break;
            case BulletinFragment.ID:
                navigation.setCurrentItem(1, false);
                break;
            case ProfileFragment.ID:
                navigation.setCurrentItem(4, false);
                break;
        }
    }

    private void replaceFragment(){
        try {
            if (isOK) {
                int count = getSupportFragmentManager().getBackStackEntryCount();
                boolean legacyFragmentExists = false;
                transaction = getSupportFragmentManager().beginTransaction();
                for(int i=0; i<count; i++){
                    if(currentFragmentTag.equals(getSupportFragmentManager().getBackStackEntryAt(i).getName())){
                        legacyFragmentExists = true;
                    }
                }
                if(legacyFragmentExists){
                    getSupportFragmentManager().popBackStack(currentFragmentTag, 0);
                }
                else {
                    transaction.replace(R.id.container, currentFragment, currentFragmentTag).addToBackStack(currentFragmentTag).commit();
                }
                hideKeyboard(HomeActivity.this);
            } else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, currentFragment).commit();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        isOK = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        currentFragment = HomeFragment.newInstance();
        navigation = findViewById(R.id.navigation);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("UpComing", R.drawable.ic_upcoming);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Bulletin", R.drawable.ic_school_black_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Discover", R.drawable.ic_hotdeals);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("History", R.drawable.ic_change_history_black_24dp);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Profile", R.drawable.ic_profile);
        navigation.addItem(item1);
        navigation.addItem(item2);
        navigation.addItem(item3);
        navigation.addItem(item4);
        navigation.addItem(item5);
        navigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        navigation.setDefaultBackgroundColor(getResources().getColor(R.color.colorPrimary));
        navigation.setAccentColor(getResources().getColor(R.color.pureWhite));
        navigation.setInactiveColor(getResources().getColor(R.color.colorPrimaryDark));
        navigation.setOnTabSelectedListener(listener);
        replaceFragment();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, new IntentFilter(Config.NEW_UPDATE));
        NotiUtil.clearNotifications(getApplicationContext());
    }

    public static void showNotification(int position){
        if(currentPos==position){
            navigation.setNotificationAnimationDuration(2000);
        }
        navigation.setNotification("*", position);
        notifyPosition = position;
    }

    private void handleStart(){
        if (getIntent().getAction() != null) {
            switch (getIntent().getAction()) {
                case SHOW_NEW_EVENT :
                    try {
                        JSONObject obj = new JSONObject(getIntent().getExtras().getString(TYPE_EVENT));
                        Event event = Event.parseFromJSON(obj);
                        HomeFragment fragment = HomeFragment.newInstance();
                        fragment.setStartingEvent(event);
                        currentFragment = fragment;
                        setNavigationTab(HomeFragment.ID);
                        getIntent().setAction(null);
                        replaceFragment();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SHOW_NEW_BULLETIN:
                    try {
                        JSONObject obj = new JSONObject(getIntent().getExtras().getString(TYPE_BULLETIN));
                        Bulletin bulletin = Bulletin.parseFromJSON(obj);
                        BulletinFragment fragment = BulletinFragment.newInstance();
                        fragment.setStartingBulletin(bulletin);
                        currentFragment = fragment;
                        setNavigationTab(BulletinFragment.ID);
                        getIntent().setAction(null);
                        replaceFragment();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent()!=null)
            handleStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("App","Action:"+getIntent().getAction());
        handleStart();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(Config.NEW_UPDATE)){
                case Config.SHOW_NEW_EVENT:
                    showNotification(0);
                    break;
                case Config.SHOW_NEW_BULLETIN:
                    showNotification(1);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }
}
