package org.zpcat.ble.ui.main;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.zpcat.ble.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
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
}
