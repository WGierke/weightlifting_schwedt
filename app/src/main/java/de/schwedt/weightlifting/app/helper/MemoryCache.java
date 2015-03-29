package de.schwedt.weightlifting.app.helper;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MemoryCache {
    private Map<String, SoftReference<Bitmap>> cache = Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());
    private Map<String, SoftReference<String>> cache_string = Collections.synchronizedMap(new HashMap<String, SoftReference<String>>());

    public Bitmap get(String id) {
        if (!cache.containsKey(id))
            return null;
        SoftReference<Bitmap> ref = cache.get(id);
        return ref.get();
    }

    public String getString(String id) {
        if (!cache_string.containsKey(id))
            return null;
        SoftReference<String> ref = cache_string.get(id);
        return ref.get();
    }

    public void put(String id, Bitmap bitmap) {
        cache.put(id, new SoftReference<Bitmap>(bitmap));
    }

    public void put(String id, String value) {
        cache_string.put(id, new SoftReference<String>(value));
    }

    public void clear() {
        cache.clear();
    }
}