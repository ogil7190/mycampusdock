package com.swalla.campusdock.Fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swalla.campusdock.Activities.EventActivity;
import com.swalla.campusdock.Adapters.EventAdapter;
import com.swalla.campusdock.Adapters.EventEnrollAdapter;
import com.swalla.campusdock.Classes.Event;
import com.swalla.campusdock.Databases.DockDB;
import com.swalla.campusdock.R;
import com.swalla.campusdock.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.swalla.campusdock.Utils.Config.Types.TYPE_EVENT;

/**
 * Created by ogil on 14/01/18.
 */

public class HistoryFragment extends android.support.v4.app.Fragment {
    public static final String ID = "HistoryFragment";
    private RecyclerView recyclerView;
    private List<Event> eventList, enrolled;
    private EventEnrollAdapter adapter;
    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history,null);

        recyclerView = v.findViewById(R.id.recycler_view);
        eventList = new ArrayList<>();
        enrolled = new ArrayList<>();

        eventList = DockDB.getIntsance(getContext()).getEventDao().getAllEvents();
        for(Event e : eventList){
            if(e.isEnrolled()){
                enrolled.add(e);
            }
        }
        Collections.reverse(enrolled);
        adapter = new EventEnrollAdapter(getContext(), enrolled);

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
                        Event event = enrolled.get(position);
                        gotoEvent(event, view);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationZ", 8f);
                        animation.setDuration(300);
                        animation.start();
                    }
                })
        );

        return v;
    }

    private void gotoEvent(Event event, View view){
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra(TYPE_EVENT, event);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), view, ViewCompat.getTransitionName(view));
        startActivity(intent, options.toBundle());
    }

    public HistoryFragment() {}
}