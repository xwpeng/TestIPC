package android.xwpeng.testipc.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.xwpeng.testipc.IBinderPool;
import android.xwpeng.testipc.realize.ComputeImpl;
import android.xwpeng.testipc.realize.SecuriCenterImpl;

import static android.xwpeng.testipc.realize.PoolManager.BINDER_COMPUTE;
import static android.xwpeng.testipc.realize.PoolManager.BINDER_SECURITY_CENTER;

/**
 * Created by xwpeng on 16-12-15.
 */

public class BinderPoolService extends Service {
//private final static String TAG = "BinderPoolService";
    private Binder mBinder = new BinderPoolImpl();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static class BinderPoolImpl extends IBinderPool.Stub {
        public BinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_SECURITY_CENTER:
                    binder = new SecuriCenterImpl();
                    break;
                case BINDER_COMPUTE:
                    binder = new ComputeImpl();
                    break;
            }
            return binder;
        }
    }
}
