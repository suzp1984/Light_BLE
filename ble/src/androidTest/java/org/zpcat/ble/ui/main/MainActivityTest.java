package org.zpcat.ble.ui.main;

import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.mikepenz.aboutlibraries.ui.LibsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.zpcat.ble.R;
import org.zpcat.ble.ui.central.CentralActivity;
import org.zpcat.ble.ui.peripheral.PeripheralActivity;
import org.zpcat.ble.utils.BLEIntents;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by suzhenxi on 9/19/2016.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Test
    public void testDisplayed() {
        onView(withId(R.id.central))
                .check(matches(isDisplayed()));

        onView(withId(R.id.peripheral))
                .check(matches(isDisplayed()));

        onView(withId(R.id.about))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAboutIntent() {
        Intents.init();

        onView(withId(R.id.about))
                .check(matches(isDisplayed()))
                .perform(click());

        Intents.intended(hasComponent(LibsActivity.class.getName()));

        Intents.release();
    }

    @Test
    public void testCentralIntent() {
        Intents.init();

        onView(withId(R.id.central))
                .check(matches(isDisplayed()))
                .perform(click());

        Intents.intended(hasAction(BLEIntents.ACTION_CENTRAL_MODE));
        // Intents.intended(hasComponent(CentralActivity.class.getName()));

        Intents.release();
    }

    @Test
    public void testPeripheralIntent() {
        Intents.init();

        onView(withId(R.id.peripheral))
                .check(matches(isDisplayed()))
                .perform(click());

        Intents.intended(hasAction(BLEIntents.ACTION_PERIPHERAL_MODE));
        // Intents.intended(hasComponent(PeripheralActivity.class.getName()));

        Intents.release();
    }
}
