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

    @OnClick(R.id.host)
    public void startHostMode() {
        startActivity(new Intent(this, HostActivity.class));
    }

    @OnClick(R.id.peripheral)
    public void startPeripheralMode() {
        startActivity(new Intent(this, PeripheralActivity.class));
    }
}
