package com.droidheat.musicplayer.services;

import android.content.Intent;
import android.os.IBinder;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.ServiceTestRule;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.activities.HomeActivity;
import com.droidheat.musicplayer.activities.SplashActivity;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeoutException;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
public class MediaPlayerServiceTest {

    @Rule
    public ServiceTestRule serviceRule
            = new ServiceTestRule();


    @Test
    public void onCreate() {

    }

    @Test
    public void onStartCommand() {

    }

    @Test
    public void onDestroy() {
    }

    @Test
    public void onAudioFocusChange() {
    }

    @Test
    public void onInfo() {
    }

    @Test
    public void onPrepared() {
    }

    @Test
    public void onSeekComplete() {
    }

    @Test
    public void onCompletion() {
    }

    @Test
    public void onError() {
    }

    @Test
    public void onTaskRemoved() {
    }

    @Test
    public void addVoteToTrack() {
    }
}