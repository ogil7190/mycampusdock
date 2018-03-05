package com.swalla.campusdock.Fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.swalla.campusdock.Activities.BulletinActivity;
import com.swalla.campusdock.Adapters.BulletinAdapter;
import com.swalla.campusdock.Classes.Bulletin;
import com.swalla.campusdock.Databases.DockDB;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.Config;
import com.swalla.campusdock.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HistoryFragment extends Fragment {
    public static final String ID = "HistoryFragment";
    private RecyclerView recyclerView;
    private BulletinAdapter adapter;
    private List<Bulletin> bulletinList;
    private SharedPreferences pref;

    public HistoryFragment() {}

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_class, null);

        recyclerView = v.findViewById(R.id.recycler_view_class);
        pref = getContext().getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);
        bulletinList = new ArrayList<>();

        bulletinList = DockDB.getIntsance(getContext()).getBulletinDao().getAllClassEvents();

        Collections.reverse(bulletinList);

        bulletinList.add(new Bulletin("@ogil","Demo Bulletin For Classes","This is my <b>demo Description </b><br><br>#OGIL7190","", "@dock", "21 JAN 2018", "21 JAN 2018"));
        adapter = new BulletinAdapter(getContext(), bulletinList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        gotoBulletin(bulletinList.get(position), view);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationZ", 8f);
                        animation.setDuration(300);
                        animation.start();

                    }
                })
        );
        if (startingBulletin != null) {
            gotoBulletin(startingBulletin, recyclerView);
        }
        return v;
    }

    private Bulletin startingBulletin;

    public void setStartingBulletin(Bulletin bulletin) {
        startingBulletin = bulletin;
    }

    private void gotoBulletin(Bulletin bulletin, View view) {
        Intent intent = new Intent(getActivity(), BulletinActivity.class);
        intent.putExtra(Config.TYPE_BULLETIN, bulletin);
        if (startingBulletin == null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), view, ViewCompat.getTransitionName(view));
            startActivity(intent, options.toBundle());
        } else {
            startingBulletin = null;
            startActivity(intent);
        }
    }
}
