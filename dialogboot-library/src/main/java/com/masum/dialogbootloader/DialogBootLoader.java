package com.masum.dialogbootloader;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by mac on 3/29/18.
 */

@SuppressWarnings("unchecked")
public class DialogBootLoader {

    @SuppressWarnings("unchecked")
    public static void bind(Activity activity) {
        try {

            Class bindingClass = Class.forName("com.masum.dialogboot.DialogBoot");
            //noinspection unchecked
            Constructor constructor = bindingClass.getConstructor(activity.getClass());
            constructor.newInstance(activity);

        } catch (Exception e) {
            Log.e("TAG", "Meaningful Message1", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void bind(Activity activity, View view) {
        try {

            Class bindingClass = Class.forName("com.masum.dialogboot.DialogBoot");
            //Class bindingClass = Class.forName(activity.getClass().getCanonicalName());
            //noinspection unchecked
            Constructor constructor = bindingClass.getConstructor(activity.getClass() , View.class);
            constructor.newInstance(activity, view);

        } catch (ClassNotFoundException e) {
            Log.e("TAG", "Meaningful Message", e);
        } catch (NoSuchMethodException e) {
            Log.e("TAG", "Meaningful Message", e);
        } catch (IllegalAccessException e) {
            Log.e("TAG", "Meaningful Message", e);
        } catch (InstantiationException e) {
            Log.e("TAG", "Meaningful Message", e);
        } catch (InvocationTargetException e) {
            Log.e("TAG", "Meaningful Message", e);
        }
    }
}
