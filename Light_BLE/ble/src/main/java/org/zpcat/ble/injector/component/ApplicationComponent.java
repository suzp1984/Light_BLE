package org.zpcat.ble.injector.component;

import org.zpcat.ble.MainActivity;
import org.zpcat.ble.injector.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jacobsu on 4/20/16.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(MainActivity activity);

}
