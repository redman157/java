package com.droidheat.musicplayer.activities;

import android.content.Intent;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertTrue;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.droidheat.musicplayer.R;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class TestHomeActivity {
    @Rule
    public ActivityTestRule<SplashActivity> activityTestRule =
            new ActivityTestRule<SplashActivity>(SplashActivity.class,true,true /*lazy launch
            activity*/){
                @Override
                protected Intent getActivityIntent() {
                    /*added predefined intent data*/
                    Intent intent = new Intent();
                    intent.putExtra("key","value");
                    return intent;
                }
            };

    @Test
    public void displayView(){
        onView(withId(R.id.layout_play_media)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.text_title_media)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.text_artists_media)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.imbt_Play_media)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.img_albumArt_media)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.btn_title_media)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.menu_item)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.rc_OptionMenu)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.nav_view)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.vp_Home)).check(matches(isCompletelyDisplayed()));
    }

}