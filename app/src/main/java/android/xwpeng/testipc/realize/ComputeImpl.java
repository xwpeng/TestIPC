package android.xwpeng.testipc.realize;

import android.os.RemoteException;
import android.xwpeng.testipc.ICompute;

/**
 * Created by xwpeng on 16-12-15.
 */

public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
