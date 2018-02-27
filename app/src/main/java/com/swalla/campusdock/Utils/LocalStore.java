package com.swalla.campusdock.Utils;

import android.content.Context;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ogil7190 on 19/02/18.
 */

public class LocalStore {
    private static int cacheSize = 5 * 1024 * 1024;
    private static LruCache<String, Object> cache = new LruCache<String, Object>(cacheSize) {};

    private static RequestQueue networkqueue;

    public static RequestQueue getNetworkqueue(Context context){
        if(networkqueue==null)
            networkqueue = Volley.newRequestQueue(context);
        return networkqueue;
    }

    public static void putObjectInCache(String key, Object value) {
        if (getObjectFromCache(key) == null) {
            cache.put(key, value);
        }
    }

    public static Object getObjectFromCache(String key) {
        return cache.get(key);
    }
}
