package android.xwpeng.testipc.realize;

import android.os.RemoteException;
import android.xwpeng.testipc.ISecurityCenter;

/**
 * Created by xwpeng on 16-12-15.
 */

public class SecuriCenterImpl extends ISecurityCenter.Stub {
    private final static char SECRET_CODE = '^';
    @Override
    public String encrypt(String content) throws RemoteException {
     char [] chars = content.toCharArray();
        int size = chars.length;
        for (int i = 0; i < size; i++) {
            chars[i] ^= SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
