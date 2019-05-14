package org.zpcat.ble.ui.base;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
