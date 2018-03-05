package com.swalla.campusdock.Fragments;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.swalla.campusdock.Activities.EventActivity;
import com.swalla.campusdock.Activities.HomeActivity;
import com.swalla.campusdock.Adapters.EventAdapter;
import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.Classes.RecyclerItemClickListener;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.Config;
import com.swalla.campusdock.Databases.DockDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by ogil on 14/01/18.
 */

public class HomeFragment extends Fragment {
    public static final String ID = "HomeFragment";
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,null);

        recyclerView = v.findViewById(R.id.recycler_view);
        eventList = new ArrayList<>();

        eventList = DockDB.getIntsance(getContext()).getEventDao().getAllEvents();

        Collections.reverse(eventList);

        Event demo = new Event("@ogil", "CS: GO LAN Gaming","Show your <b>Gaming skills</b> in popular CS Go game!<br> Compete with other players <br><br><u>Follow us on Instagram</u> <br> <b>#OGIL7190</b> ","18 Jan","19 Jan", null, "@Dock");
        demo.setEnrolled(true);
        eventList.add(demo);
        adapter = new EventAdapter(getContext(), eventList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                       gotoEvent(eventList.get(position), view);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationZ", 8f);
                        animation.setDuration(300);
                        animation.start();
                    }
                })
        );
        if(startingEvent!=null){
            gotoEvent(startingEvent, recyclerView);
        }
        return v;
    }

    private Event startingEvent;
    public void setStartingEvent(Event event){
        startingEvent = event;
    }

    private void gotoEvent(Event event, View view){
        if(event.getEventName().contains(" ● ")) {
            Event e = DockDB.getIntsance(getContext()).getEventDao().getEvent(event.getId());
            e.setEventName(e.getEventName().replace(" ● ", ""));
            DockDB.getIntsance(getContext()).getEventDao().update(e);
        }
        event.getEventName().replace(" ● ", "");
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra(Config.TYPE_EVENT, event);
        if(startingEvent == null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, ViewCompat.getTransitionName(view));
            startActivity(intent, options.toBundle());
        } else {
            startingEvent = null;
            startActivity(intent);
        }
    }
}