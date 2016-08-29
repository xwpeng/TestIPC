package android.xwpeng.testipc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_start_email).setOnClickListener(this);
        findViewById(R.id.main_start_push_receiver).setOnClickListener(this);
        Log.e(TAG, "processname: " + ProcessUtil.getProcessName() + " pid: " + Process.myPid());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_start_email:
                startActivity(new Intent(MainActivity.this, EmailActivity.class));
                break;
            case R.id.main_start_push_receiver:
                startActivity(new Intent(MainActivity.this, PushReceiverActivity.class));
                break;
        }

    }
}
