package android.xwpeng.testipc.realize;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.xwpeng.testipc.IBinderPool;
import android.xwpeng.testipc.service.BinderPoolService;

import java.util.concurrent.CountDownLatch;

/**
 * Created by xwpeng on 16-12-15.
 */

public class PoolManager {
    private final static String TAG = "PoolManager";
    public final static int BINDER_NONE = -1;
    public final static int BINDER_COMPUTE = 0;
    public final static int BINDER_SECURITY_CENTER = 1;

    private Context mContext;
    private IBinderPool mBinderPool;
    /**
     * 在多线程并发编程中synchronized和Volatile都扮演着重要的角色，Volatile是轻量级的synchronized，它在多处理器开发中保证了共享变量的“可见性”。
     * 可见性的意思是当一个线程修改一个共享变量时，另外一个线程能读到这个修改的值。
     * 它在某些情况下比synchronized的开销更小，本文将深入分析在硬件层面上Inter处理器是如何实现Volatile的，通过深入分析能帮助我们正确的使用Volatile变量。
     */
    private static volatile PoolManager sInstance;
    /**
     * CountDownLatch 计数未完,线程等待
     */
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    private PoolManager(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static PoolManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PoolManager.class) {
                if (sInstance == null) {
                    sInstance = new PoolManager(context);
                }
            }
        }
        return sInstance;
    }

    private synchronized void connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent i = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(i, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            Log.d(TAG, "binder died.");
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();
            try {
                mConnectBinderPoolCountDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        if (mBinderPool != null) {
            try {
                binder = mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }

}
