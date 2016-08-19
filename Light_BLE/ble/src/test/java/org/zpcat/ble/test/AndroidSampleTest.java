package org.zpcat.ble.test;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zpcat.ble.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;

/**
 * Created by jacobsu on 8/19/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class AndroidSampleTest {

    private static final String FAKE_STRING = "HELLO WORLD";

    @Mock
    Context mMockContext;

    @Test
    public void testContextString() {
        when(mMockContext.getString(R.string.app_name)).thenReturn(FAKE_STRING);

        assertThat(mMockContext.getString(R.string.app_name), is(FAKE_STRING));
    }
}
