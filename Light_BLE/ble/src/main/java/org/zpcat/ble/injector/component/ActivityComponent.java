package org.zpcat.ble.injector.component;

import org.zpcat.ble.injector.PerActivity;
import org.zpcat.ble.injector.module.ActivityModule;
import org.zpcat.ble.ui.central.CentralActivity;
import org.zpcat.ble.ui.central.DeviceScanFragment;
import org.zpcat.ble.ui.main.MainActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(CentralActivity activity);
}
