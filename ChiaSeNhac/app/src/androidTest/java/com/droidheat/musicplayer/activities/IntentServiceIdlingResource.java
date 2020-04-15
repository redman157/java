package com.droidheat.musicplayer.activities;

import android.app.ActivityManager;
import android.content.Context;

import androidx.test.espresso.IdlingResource;

import java.util.List;

public class IntentServiceIdlingResource implements IdlingResource {

    ResourceCallback resourceCallback;
    private Context context;

    public IntentServiceIdlingResource(Context context) {
        this.context = context;
    }

    @Override
    public String getName() {
        return IntentServiceIdlingResource.class.getName();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = !isIntentServiceRunning();
        if (idle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
        return idle;
    }

    private boolean isIntentServiceRunning() {
        ActivityManager manager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // Get all running services
        List<ActivityManager.RunningServiceInfo> runningServices =
                manager.getRunningServices(Integer.MAX_VALUE);
        // check if our is running
        for (ActivityManager.RunningServiceInfo info : runningServices) {
            if (SplashActivity.class.getName().equals(
                    info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}