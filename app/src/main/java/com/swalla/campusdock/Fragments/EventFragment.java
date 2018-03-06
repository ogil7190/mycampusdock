package com.swalla.campusdock.Fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swalla.campusdock.Activities.EventActivity;
import com.swalla.campusdock.Adapters.EventAdapter;
import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.listeners.RecyclerItemClickListener;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.Config;
import com.swalla.campusdock.Databases.DockDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ogil on 14/01/18.
 */

public class EventFragment extends Fragment {
    public static final String ID = "EventFragment";
    private RecyclerView recyclerView;
    private static EventAdapter adapter;
    private List<Event> eventList;

    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();
        return fragment;
    }

    public EventFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,null);

        recyclerView = v.findViewById(R.id.recycler_view);
        eventList = new ArrayList<>();

        eventList = DockDB.getIntsance(getContext()).getEventDao().getAllEvents();

        Collections.reverse(eventList);

        Event demo = new Event("@ogil", "Career Expo 2018","The <b>Careers Expo</b> is your chance to discuss employment opportunities with organisations interested in UNSW graduates.<br><br><u>Follow us on Instagram</u> <br> <b>#EXPO2018</b> ","2018-03-25T00:00:00.000Z","2018-03-31T00:00:00.000Z", null, "fun","@Dock");
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
                        Event event = eventList.get(position);
                        if(event.isUpdated()){
                            event.setUpdated(false);
                            view.findViewById(R.id.updatedFlag).setVisibility(View.GONE);
                        }
                        DockDB.getIntsance(getContext()).getEventDao().update(event);
                        adapterDataUpdated();
                        gotoEvent(event, view);
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

    public static void adapterDataUpdated(){
        adapter.notifyDataSetChanged();
    }
}