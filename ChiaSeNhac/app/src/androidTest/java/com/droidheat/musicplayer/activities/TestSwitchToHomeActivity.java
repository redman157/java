package com.droidheat.musicplayer.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

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

import static androidx.test.espresso.intent.Intents.intended;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertTrue;

import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;



import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.droidheat.musicplayer.R;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestSwitchToHomeActivity {
    @Rule
    public ActivityTestRule<SplashActivity> activityTestRule =
            new ActivityTestRule<SplashActivity>(SplashActivity.class, true, true){
                @Override
                protected Intent getActivityIntent() {
                    /*added predefined intent data*/
                    Intent intent = new Intent();
                    intent.putExtra("key","value");
                    return intent;
                }
            };

    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule
                    .grant(Manifest.permission.READ_CONTACTS)
                    .grant(Manifest.permission.WRITE_CONTACTS);

    @Rule
    public IntentsTestRule<SplashActivity> mActivityRule =
            new IntentsTestRule<>(SplashActivity.class);

    @Before
    public void grantPhonePermission() {
        // In M+, trying to call a number will trigger a runtime dialog. Make sure
        // the permission is granted before running this test.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.READ_EXTERNAL_STORAGE");
        }
    }
    @Test
    public void waitSwitchingToHomeActivity() {
		// onView(withId(R.id.mTextSync)).check(matches(withText(containsString("3"))));
		// onView(withId(R.id.mTextSync)).perform(waitFor(1000)).check(matches(withText(containsString("2"))));
		// onView(withId(R.id.mTextSync)).perform(waitFor(1000)).check(matches(withText(containsString("1"))));
		// onView(withId(R.id.mTextSync)).perform(waitFor(1000)).check(matches(withText(containsString("1"))));

    	/* @NOTE: chờ 3s để chắc chắn sẽ chuyển qua HomeActivity */

        String name = getText(withId(R.id.textView10));
        if(name.equals("Initiating..")){
            onView(isRoot()).perform(waitFor(1000));

            assertEquals(HomeActivity.class.getSimpleName(), getActivityInstance().getClass().getSimpleName());
            assertTrue(isRunning(getActivityInstance()));


        }else if (name.equals("Syncing..")){

        }

    }
	@After
	public void afterSwitchToHome(){

	}
    public static ViewAction setTextInTextView(final String value){
        return new ViewAction() {
            @SuppressWarnings("unchecked")
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TextView.class));
                // To check that the found view is TextView or it's subclass like EditText
                // so it will work for TextView and it's descendants
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((TextView) view).setText(value);
            }

            @Override
            public String getDescription() {
                return "replace text";
            }
        };
    }
	public static String getText(final Matcher<View> matcher) {
        final String[] stringHolder = { null };
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView)view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }
    @Test
    // test intent data 
    public void triggerIntentTest() {
        onView(withId(R.id.button)).perform(click());
        intended(allOf(
                hasAction(Intent.ACTION_CALL),
                hasData(INTENT_DATA_PHONE_NUMBER),
                toPackage(PACKAGE_ANDROID_DIALER)));
    }

    @Test
    public void ensureListViewIsPresent() throws Exception {
        onData(hasToString(containsString("Frodo")))
                .perform(click());
        onView(withText(startsWith("Clicked:"))).
                inRoot(withDecorView(
                        not(is(activityTestRule.getActivity().
                                getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

	public static Activity getActivityInstance(){
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
    public static boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

	private static ViewAction waitFor(final long delay) {
    	return new ViewAction() {
        	@Override public Matcher<View> getConstraints() {
            	return isRoot();
        	}

        	@Override public String getDescription() {
            	return "wait for " + delay + "milliseconds";
        	}

        	@Override public void perform(UiController uiController, View view) {
            	uiController.loopMainThreadForAtLeast(delay);
        	}
    	};
    }
}