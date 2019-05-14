package org.zpcat.ble.ui.main;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.zpcat.ble.ui.central.CentralActivity;
import org.zpcat.ble.ui.peripheral.PeripheralActivity;
import org.zpcat.ble.R;
import org.zpcat.ble.fragment.PermissionAgreeFragment;
import org.zpcat.ble.ui.base.BaseActivity;
import org.zpcat.ble.utils.BLEIntents;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainMvpView {

    private static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_LOCATION_CODE = 10;

    @Inject BluetoothManager mBluetoothManager;
    @Inject MainPresenter mMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();

        // Checks if Bluetooth is enabled
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();

        //checkLocationPermission();
        mMainPresenter.attachView(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        mMainPresenter.detachView();
    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0) {
                    //great;
                }
        }
    }

    @Override
    @OnClick(R.id.central)
    public void startCentralMode() {
        if (checkLocationPermission()) {
            startActivity(new Intent(BLEIntents.ACTION_CENTRAL_MODE));
        }
    }

    @Override
    @OnClick(R.id.peripheral)
    public void startPeripheralMode() {
        startActivity(new Intent(BLEIntents.ACTION_PERIPHERAL_MODE));
    }

    @Override
    @OnClick(R.id.about)
    public void startAboutLibrary() {
        new LibsBuilder().withActivityStyle(Libs.ActivityStyle.DARK)
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription("Good people create those good stuff.")
                .start(this);
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                PermissionAgreeFragment dialog = new PermissionAgreeFragment();
                dialog.show(getSupportFragmentManager(), "Location Permission");
            } else {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_CODE);
            }

            return false;
        } else {
            return true;
        }
    }

}
