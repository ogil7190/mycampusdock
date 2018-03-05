package com.swalla.campusdock.Utils;

import android.content.Context;
import android.util.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

/**
 * Created by ogil7190 on 19/02/18.
 */

public class LocalStore {
    private static HashMap<String, Object> store = new HashMap<>();
    private static RequestQueue networkQueue;

    public static RequestQueue getNetworkqueue(Context context){
        if(networkQueue==null) {
            networkQueue = Volley.newRequestQueue(context);
        }
        return networkQueue;
    }

    public static void putObject(String key, Object value){
        store.put(key, value);
    }

    public static Object getObject(String key){
        return store.get(key);
    }
}
