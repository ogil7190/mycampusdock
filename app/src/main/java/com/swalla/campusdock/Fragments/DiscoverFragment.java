package com.swalla.campusdock.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.swalla.campusdock.R;

/**
 * Created by ogil on 14/01/18.
 */

public class DiscoverFragment extends android.support.v4.app.Fragment {

    public static final String ID = "DiscoverFragment";
    public static DiscoverFragment newInstance() {
        return new DiscoverFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discover,null);
        return v;
    }

    public DiscoverFragment() {}
}