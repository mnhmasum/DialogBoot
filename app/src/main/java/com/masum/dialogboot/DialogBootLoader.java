package com.masum.dialogboot;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by mac on 3/29/18.
 */

public class DialogBootLoader {
    public static void bind(Activity activity) {
        try {

            Class bindingClass = Class.forName("com.masum.dialogboot.DialogBoot");
            //noinspection unchecked
            Constructor constructor = bindingClass.getConstructor(activity.getClass());
            constructor.newInstance(activity);

        } catch (ClassNotFoundException e) {
            Log.e("TAG", "Meaningful Message1", e);
        } catch (NoSuchMethodException e) {
            Log.e("TAG", "Meaningful Message2", e);
        } catch (IllegalAccessException e) {
            Log.e("TAG", "Meaningful Message3", e);
        } catch (InstantiationException e) {
            Log.e("TAG", "Meaningful Message4", e);
        } catch (InvocationTargetException e) {
            Log.e("TAG", "Meaningful Message5", e);
        }
    }

    public static void bind(Activity activity, View view) {
        try {

            Class bindingClass = Class.forName("com.masum.dialogboot.DialogBoot");
            //Class bindingClass = Class.forName(activity.getClass().getCanonicalName());
            //noinspection unchecked
            Constructor constructor = bindingClass.getConstructor(activity.getClass() , View.class);
            constructor.newInstance(activity, view);

           /* Constructor constructor = bindingClass.getConstructor(new Class[] { Activity.class, View.class});
            constructor.newInstance(activity, view});*/


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
