package org.zpcat.ble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);

        super.onDestroy();
    }

    @OnClick(R.id.central)
    public void startCentralMode() {
        startActivity(new Intent(this, CentralActivity.class));
    }

    @OnClick(R.id.peripheral)
    public void startPeripheralMode() {
        startActivity(new Intent(this, PeripheralActivity.class));
    }
}
