package org.zpcat.ble.ui.base;

import androidx.fragment.app.Fragment;

import org.zpcat.ble.BLEApplication;
import org.zpcat.ble.injector.component.DaggerFragmentComponent;
import org.zpcat.ble.injector.component.FragmentComponent;
import org.zpcat.ble.injector.module.FragmentModule;

/**
 * Created by jacobsu on 4/27/16.
 */
public class BaseFragment extends Fragment {
    private FragmentComponent mFragmentComponent;

    public FragmentComponent getFragmentComponent() {
        if (mFragmentComponent == null) {
            mFragmentComponent = DaggerFragmentComponent.builder()
                    .applicationComponent(((BLEApplication)getActivity().getApplication()).getApplicationComponent())
                    .fragmentModule(new FragmentModule(this))
                    .build();
        }

        return mFragmentComponent;
    }
}
