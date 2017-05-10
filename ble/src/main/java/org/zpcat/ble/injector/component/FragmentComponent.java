package org.zpcat.ble.injector.component;

import org.zpcat.ble.injector.PerFragment;
import org.zpcat.ble.injector.module.FragmentModule;
import org.zpcat.ble.ui.central.CentralScanFragment;

import dagger.Component;

/**
 * Created by jacobsu on 4/27/16.
 */
@PerFragment
@Component(dependencies = {ApplicationComponent.class}, modules = FragmentModule.class)
public interface FragmentComponent {
    void inject(CentralScanFragment fragment);
}
