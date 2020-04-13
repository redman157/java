package com.droidheat.musicplayer.activities;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.droidheat.musicplayer.R;

import java.util.Collection;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestSwitchToHomeActivity {
	@Rule
    public ActivityScenarioRule<SplashActivity> scenarioRule
    	= new ActivityScenarioRule<>(SplashActivity.class);

    @Test
    public void waitSwitchingToHomeActivity() {
		// onView(withId(R.id.mTextSync)).check(matches(withText(containsString("3"))));
		// onView(withId(R.id.mTextSync)).perform(waitFor(1000)).check(matches(withText(containsString("2"))));
		// onView(withId(R.id.mTextSync)).perform(waitFor(1000)).check(matches(withText(containsString("1"))));
		// onView(withId(R.id.mTextSync)).perform(waitFor(1000)).check(matches(withText(containsString("1"))));

    	/* @NOTE: chờ 3s để chắc chắn sẽ chuyển qua HomeActivity */

		onView(isRoot()).perform(waitFor(1000));
		onView(withId(R.id.vp_Home)).check(matches(isCompletelyDisplayed()));
		onView(withId(R.id.icon_play)).check(matches(isCompletelyDisplayed()));
		onView(withId(R.id.icon_play)).perform(click());
    }

	private Activity getActivityInstance(){
		final Activity[] currentActivity = new Activity[1];

    	getInstrumentation().runOnMainSync(new Runnable() {
        	public void run() {
            	Collection<Activity> resumedActivities =
            	ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);

            	for (Activity activity: resumedActivities){
                	Log.d("Your current activity: ", activity.getClass().getName());
                	currentActivity[0] = activity;
                	break;
            	}
     	   	}
    	});

    	return currentActivity[0];
	}

    private static ViewAction waitFor(final long delay) {
    	return new ViewAction() {
        	@Override public Matcher<View> getConstraints() {
            	return isRoot();
        	}

        	@Override public String getDescription() {
            	return "wait for " + (long) delay + "milliseconds";
        	}

        	@Override public void perform(UiController uiController, View view) {
            	uiController.loopMainThreadForAtLeast((long) delay);
        	}
    	};
    }
}