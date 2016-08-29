package android.xwpeng.testipc;

import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by xwpeng on 16-8-29.
 */
public class PushReceiverActivity extends AppCompatActivity {
    private final static String TAG = PushReceiverActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_receiver);
        Log.e(TAG, "processname: " + ProcessUtil.getProcessName() + " pid: " + Process.myPid());
    }
}
