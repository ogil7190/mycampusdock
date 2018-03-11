package com.swalla.campusdock.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.swalla.campusdock.Classes.Bulletin;
import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.Databases.DockDB;
import com.swalla.campusdock.Fragments.BulletinFragment;
import com.swalla.campusdock.Fragments.DiscoverFragment;
import com.swalla.campusdock.Fragments.EventFragment;
import com.swalla.campusdock.Fragments.HistoryFragment;
import com.swalla.campusdock.Fragments.ProfileFragment;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Services.CustomFirebaseMessagingService;
import com.swalla.campusdock.Utils.BusHolder;
import com.swalla.campusdock.Utils.Config;
import com.swalla.campusdock.Utils.LocalStore;
import com.swalla.campusdock.Utils.NotiUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.swalla.campusdock.Utils.Config.Flags.FLAG_DATA_FETCH;
import static com.swalla.campusdock.Utils.Config.Flags.FLAG_NEW_UPDATE;
import static com.swalla.campusdock.Utils.Config.Flags.FLAG_SHOW_NEW_BULLETIN;
import static com.swalla.campusdock.Utils.Config.Flags.FLAG_SHOW_NEW_EVENT;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_BACKUP_ARRAY_BULLETIN;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_BACKUP_ARRAY_EVENT;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_BACKUP_BULLETIN_FETCH_COUNT;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_BACKUP_BULLETIN_FETCH_COUNT_LIMIT;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_BACKUP_EVENT_FETCH_COUNT;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_BACKUP_EVENT_FETCH_COUNT_LIMIT;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_NAME;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_USER_API_KEY;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_USER_CLASS;
import static com.swalla.campusdock.Utils.Config.Prefs.PREF_USER_ROLL;
import static com.swalla.campusdock.Utils.Config.Requests.REQ_FETCH_BULLETIN;
import static com.swalla.campusdock.Utils.Config.Requests.REQ_FETCH_EVENT;
import static com.swalla.campusdock.Utils.Config.Requests.REQ_REACH_BULLETIN;
import static com.swalla.campusdock.Utils.Config.Requests.REQ_REACH_EVENT;
import static com.swalla.campusdock.Utils.Config.Types.TYPE_BULLETIN;
import static com.swalla.campusdock.Utils.Config.Types.TYPE_EVENT;

public class HomeActivity extends AppCompatActivity {
    private Fragment currentFragment;
    private static AHBottomNavigation navigation;
    private FragmentTransaction transaction;
    private static String currentFragmentTag = EventFragment.ID;
    private boolean isOK = false;
    private static int notifyPosition = -1;
    private static int currentPos = 0;
    private SharedPreferences pref;

    private AHBottomNavigation.OnTabSelectedListener listener = new AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {
            currentPos = position;
            switch(position){
                case 0:
                    currentFragment = EventFragment.newInstance();
                    currentFragmentTag = EventFragment.ID;
                    break;
                case 1 :
                    currentFragment = BulletinFragment.newInstance();
                    currentFragmentTag = BulletinFragment.ID;
                    break;
                case 2 :
                    currentFragment = DiscoverFragment.newInstance();
                    currentFragmentTag = DiscoverFragment.ID;
                    break;
                case 3 :
                    currentFragment = HistoryFragment.newInstance();
                    currentFragmentTag = HistoryFragment.ID;
                    break;
                case 4:
                    currentFragment = ProfileFragment.newInstance();
                    currentFragmentTag = ProfileFragment.ID;
                    break;
            }
            if(notifyPosition == position){
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
            setNavigationTab(EventFragment.ID);
        } else
            setNavigationTab(getSupportFragmentManager().getBackStackEntryAt(count).getName());
    }

    private void setNavigationTab(String Id){
        switch (Id){
            case EventFragment.ID :
                navigation.setCurrentItem(0, false);
                break;
            case BulletinFragment.ID:
                navigation.setCurrentItem(1, false);
                break;
            case DiscoverFragment.ID:
                navigation.setCurrentItem(2, false);
                break;
            case HistoryFragment.ID:
                navigation.setCurrentItem(3, false);
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
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentFragment = EventFragment.newInstance();
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
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, new IntentFilter(FLAG_NEW_UPDATE));
        NotiUtil.clearNotifications(getApplicationContext());

        if(pref.getInt(FLAG_DATA_FETCH, -1) == -1){
            fetchUserData();
        } else{
            if(pref.getInt(PREF_BACKUP_EVENT_FETCH_COUNT, 0)<pref.getInt(PREF_BACKUP_EVENT_FETCH_COUNT_LIMIT, 0)){
                try {
                    fetchEvents(new JSONArray(pref.getString(PREF_BACKUP_ARRAY_EVENT, "[]")), pref.getInt(PREF_BACKUP_EVENT_FETCH_COUNT, 0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(pref.getInt(PREF_BACKUP_BULLETIN_FETCH_COUNT, 0)<pref.getInt(PREF_BACKUP_BULLETIN_FETCH_COUNT_LIMIT, 0)){
                try {
                    fetchBulletins(new JSONArray(pref.getString(PREF_BACKUP_ARRAY_BULLETIN, "[]")), pref.getInt(PREF_BACKUP_BULLETIN_FETCH_COUNT, 0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int eventCount = 0;
    private void fetchUserData(){
        String url = "https://mycampusdock.herokuapp.com/mobile-app-interaction";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("App", "ResponseHome:" + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                JSONArray ar = obj.getJSONArray("data");
                                eventCount = ar.length();
                                fetchBulletinData(ar);
                            }
                            else{
                                Toasty.error(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                            Toasty.normal(getApplicationContext(),"Something went wrong :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("App", error.toString());
                Toasty.error(getApplicationContext(), "Try Again!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("roll", pref.getString(PREF_USER_ROLL, ""));
                params.put("api", pref.getString(PREF_USER_API_KEY, ""));
                params.put("class", pref.getString(PREF_USER_CLASS, ""));
                params.put("type", "" + REQ_FETCH_EVENT);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        LocalStore.getNetworkqueue(this).add(stringRequest);
    }

    private int bulletinCount = 0;
    private void fetchBulletinData(final JSONArray eventArray){
        String url = "https://mycampusdock.herokuapp.com/mobile-app-interaction";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("App", "ResponseHome:" + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                JSONArray ar = obj.getJSONArray("data");
                                bulletinCount = ar.length();
                                showPrompt(eventArray, ar);
                            }
                            else{
                                Toasty.error(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                            Toasty.normal(getApplicationContext(),"Something went wrong :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("App", error.toString());
                Toasty.error(getApplicationContext(), "Try Again!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("roll", pref.getString(PREF_USER_ROLL, ""));
                params.put("api", pref.getString(PREF_USER_API_KEY, ""));
                params.put("class", pref.getString(PREF_USER_CLASS, ""));
                params.put("type", "" + REQ_FETCH_BULLETIN);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        LocalStore.getNetworkqueue(this).add(stringRequest);
    }

    private void showPrompt(final JSONArray eventArray, final JSONArray bulletinArray){
        if(eventCount>0) {
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.event_backup_prompt, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.PinDialog);
            final AlertDialog alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create();
            alertDialog.show();
            final TextView title = dialogView.findViewById(R.id.title);
            final TextView message = dialogView.findViewById(R.id.message);
            final Button yes = dialogView.findViewById(R.id.yes);
            final Button no = dialogView.findViewById(R.id.no);

            title.setText("Backup");
            message.setText("Found " + eventCount + " active events & "+bulletinCount+" active bulletins!");
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    Toasty.normal(getApplicationContext(), "Fetching events in background", Toast.LENGTH_SHORT).show();
                    try {
                        pref.edit().putInt(FLAG_DATA_FETCH, 1).apply(); //starting to fetch
                        Log.d("App","Event Size:"+eventArray.length()+" Bulletin Size:"+bulletinArray.length());
                        pref.edit().putInt(PREF_BACKUP_EVENT_FETCH_COUNT_LIMIT, eventArray.length()-1).apply();
                        pref.edit().putInt(PREF_BACKUP_BULLETIN_FETCH_COUNT_LIMIT, bulletinArray.length()-1).apply();

                        JSONArray newEventArray = new JSONArray();
                        for (int i = eventArray.length()-1; i>=0; i--) {
                            newEventArray.put(eventArray.get(i));
                        }

                        JSONArray newBulletinArray = new JSONArray();
                        for (int i = bulletinArray.length()-1; i>=0; i--) {
                            newBulletinArray.put(bulletinArray.get(i));
                        }

                        pref.edit().putString(PREF_BACKUP_ARRAY_EVENT, newEventArray.toString()).apply();
                        pref.edit().putString(PREF_BACKUP_ARRAY_BULLETIN, newBulletinArray.toString()).apply();
                        fetchEvents(newEventArray, 0);
                        fetchBulletins(newBulletinArray, 0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pref.edit().putInt(FLAG_DATA_FETCH, 0).commit(); //skipped backup restore
                    alertDialog.dismiss();
                }
            });
        }
        else {
            pref.edit().putInt(FLAG_DATA_FETCH, 2).commit(); // no backup found!
            Toasty.normal(getApplicationContext(), "No Backup Event Found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchEvents(final JSONArray array, final int index) throws JSONException{
        if(index<array.length()) {
            Log.d("APP", "Index:"+index+" LIMIT:"+pref.getInt(PREF_BACKUP_EVENT_FETCH_COUNT_LIMIT, 0));
            pref.edit().putInt(PREF_BACKUP_EVENT_FETCH_COUNT, index).apply();
            final String id = array.getString(index);
            String url = "https://mycampusdock.herokuapp.com/get-event?";
            url = url + "event_id=" + id + "&isMobile=true";
            Log.d("App", "URL:"+ url + " EVENT_ID:"+id);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("App", "ResponseFetch:" + response);
                            try {
                                Event event = Event.parseFromJSON(new JSONObject(response).getJSONObject("event_data"));
                                DockDB.getIntsance(getApplicationContext()).getEventDao().insert(event);
                                BusHolder.getInstnace().post(event);
                                CustomFirebaseMessagingService.accountReach(getApplicationContext(), event.getId(), "" + REQ_REACH_EVENT);
                                fetchEvents(array, index + 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("App", error.toString());
                    Toasty.error(getApplicationContext(), "Try Again!", Toast.LENGTH_LONG).show();
                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            LocalStore.getNetworkqueue(this).add(stringRequest);
        }
    }

    private void fetchBulletins(final JSONArray array, final int index) throws JSONException{
        if(index<array.length()) {
            pref.edit().putInt(PREF_BACKUP_BULLETIN_FETCH_COUNT, index).apply();
            final String id = array.getString(index);
            String url = "https://mycampusdock.herokuapp.com/get-bulletin?";
            url = url + "bulletin_id=" + id + "&isMobile=true";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("App", "ResponseFetch:" + response);
                            try {
                                Bulletin bulletin = Bulletin.parseFromJSON(new JSONObject(response).getJSONObject("bulletin_data"));
                                DockDB.getIntsance(getApplicationContext()).getBulletinDao().insert(bulletin);
                                CustomFirebaseMessagingService.accountReach(getApplicationContext(), bulletin.getId(), "" + REQ_REACH_BULLETIN);
                                fetchBulletins(array, index + 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("App", error.toString());
                    Toasty.error(getApplicationContext(), "Try Again!", Toast.LENGTH_LONG).show();
                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            LocalStore.getNetworkqueue(this).add(stringRequest);
        }
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
                case FLAG_SHOW_NEW_EVENT :
                    try {
                        JSONObject obj = new JSONObject(getIntent().getExtras().getString(TYPE_EVENT));
                        Event event = Event.parseFromJSON(obj);
                        EventFragment fragment = EventFragment.newInstance();
                        fragment.setStartingEvent(event);
                        currentFragment = fragment;
                        setNavigationTab(EventFragment.ID);
                        getIntent().setAction(null);
                        replaceFragment();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case FLAG_SHOW_NEW_BULLETIN:
                    try {
                        setHomeTemporary();
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

    private void setHomeTemporary(){
        currentFragment = EventFragment.newInstance();
        replaceFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent()!=null) {
            handleStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleStart();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra(FLAG_NEW_UPDATE)){
                case FLAG_SHOW_NEW_EVENT:
                    showNotification(0);
                    EventFragment.adapterDataUpdated();
                    break;
                case FLAG_SHOW_NEW_BULLETIN:
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
