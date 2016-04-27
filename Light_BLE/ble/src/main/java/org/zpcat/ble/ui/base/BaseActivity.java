package org.zpcat.ble.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.zpcat.ble.BLEApplication;
import org.zpcat.ble.injector.component.ActivityComponent;
import org.zpcat.ble.injector.component.DaggerActivityComponent;
import org.zpcat.ble.injector.module.ActivityModule;

/**
 * Created by jacobsu on 4/27/16.
 */
public class BaseActivity extends AppCompatActivity {

    private ActivityComponent mComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ActivityComponent getActivityComponent() {
        if (mComponent == null) {
            mComponent = DaggerActivityComponent.builder()
                    .applicationComponent(((BLEApplication)getApplication()).getApplicationComponent())
                    .activityModule(new ActivityModule(this))
                    .build();
        }

        return mComponent;
    }
}
