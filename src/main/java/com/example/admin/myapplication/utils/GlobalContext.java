package com.example.admin.myapplication.utils;

import android.content.Context;



import java.util.HashMap;
import java.util.Map;

/**
 * Global Context holds class objects which can be retrieved from any Activity statically
 *
 * @author inder
 */
public final class GlobalContext {
    public static final TypedKey<Context> APP_CONTEXT = new TypedKey<>("app_context");



    @SuppressWarnings("rawtypes")
    private static final Map<TypedKey, Object> instances = new HashMap<>();

    public static <T> void bind(TypedKey<T> key, T value) {
        instances.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(TypedKey<T> key) {
        return (T) instances.get(key);
    }

    public static boolean getBoolean(TypedKey<Boolean> key) {
        if (instances.get(key) == null) {
            instances.put(key, false);
        }
        return get(key);
    }

    public static Context getContext() {
        return get(APP_CONTEXT);
    }


    public static void onExit() {
        instances.clear();
    }

    public static <T> void remove(TypedKey<T> key) {
        if (instances.containsKey(key)) {
            instances.remove(key);

        }
    }
}
