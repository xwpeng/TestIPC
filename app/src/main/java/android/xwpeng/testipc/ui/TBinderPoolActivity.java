package android.xwpeng.testipc.ui;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.xwpeng.testipc.ICompute;
import android.xwpeng.testipc.ISecurityCenter;
import android.xwpeng.testipc.realize.PoolManager;
import android.xwpeng.testipc.realize.ComputeImpl;
import android.xwpeng.testipc.realize.SecuriCenterImpl;

/**
 * Created by xwpeng on 16-12-15.
 */

public class TBinderPoolActivity extends AppCompatActivity {
    private final static String TAG = "TBinderPoolActivity";
    private ISecurityCenter mSecurityCenter;
    private ICompute mCompute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread() {
            @Override
            public void run() {
                doWork();
            }
        }.start();
    }

    private void doWork() {
        PoolManager binderPool = PoolManager.getInstance(TBinderPoolActivity.this);
        IBinder securityCenterBinder = binderPool.queryBinder(PoolManager.BINDER_SECURITY_CENTER);
        mSecurityCenter = SecuriCenterImpl.asInterface(securityCenterBinder);
        Log.d(TAG, "visit ISecuriCenter");
        String msg = "helloworld-安卓";
        Log.d(TAG, "content:" + msg);
        try {
            String password = mSecurityCenter.encrypt(msg);
            Log.d(TAG, "encrypt:" + password);
            String decrypt = mSecurityCenter.decrypt(password);
            Log.d(TAG, "decrypt:" + decrypt);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        IBinder computeBinder = binderPool.queryBinder(PoolManager.BINDER_COMPUTE);
        mCompute = ComputeImpl.asInterface(computeBinder);
        Log.d(TAG, "visit ICompute");

        try {
            int count = mCompute.add(1, 2);
            Log.d(TAG, "doWork: compute 1 add 2 count " + count);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
