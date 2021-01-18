package com.android.music_player.activities;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;

import static android.support.test.internal.util.Checks.checkNotNull;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Checks.checkArgument;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertTrue;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.android.music_player.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    IntentServiceIdlingResource idlingResource;

    @Before
    public void before() {
        Instrumentation instrumentation
                = InstrumentationRegistry.getInstrumentation();
        Context ctx = instrumentation.getTargetContext();
        idlingResource = new IntentServiceIdlingResource(ctx);
        Espresso.registerIdlingResources(idlingResource);
    }
    @After
    public void after() {
        Espresso.unregisterIdlingResources(idlingResource);

    }
    @Test
    public void runSequence() {
        // this triggers our intent service, as we registered
        // Espresso for it, Espresso wait for it to finish

        onView(withText("Broadcast")).check(matches(notNullValue()));
    }

    @Test
    public void displayView(){
        onView(withId(R.id.layout_play_media)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.text_title_panel)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.text_artists_panel)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.imbt_play_media)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.img_albumArt_panel)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.btn_title_media)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.menu_item)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.rc_OptionMenu)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.nav_view)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.vp_Home)).check(matches(isCompletelyDisplayed()));
    }
    // custom Matcher so sánh text
    public static Matcher<Object> withItemText(String itemText) {
        // use preconditions to fail fast when a test is creating an invalid matcher.
        checkArgument(itemText != null);
        return withItemText(equalTo(itemText));
    }
    public static Matcher<Object> withItemText(final Matcher<String> matcherText) {
        // use preconditions to fail fast when a test is creating an invalid matcher.
        checkNotNull(matcherText);
        return new TypeSafeMatcher<Object>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("expected text: " + matcherText);
            }

            @Override
            public void describeMismatchSafely(
                    Object item,
                    Description mismatchDescription) {
                mismatchDescription.appendText("actual text: " + item.toString());
            }

            @Override
            public boolean matchesSafely(Object item) {
                return matcherText.equals(item);
            }
        };
    }


}