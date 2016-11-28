package android.xwpeng.testipc;

import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.xwpeng.testipc.util.ProcessUtil;

/**
 * Created by xwpeng on 16-8-29.
 */
public class PublicActivity extends AppCompatActivity {
    private final static String TAG = PublicActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public);
        Log.e(TAG, "processname: " + ProcessUtil.getProcessName() + " pid: " + Process.myPid());
    }
}
