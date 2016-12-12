package android.xwpeng.testipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.xwpeng.testipc.entity.Book;

/**
 * test receive remote service new book add notify
 * Created by xwpeng on 16-12-10.
 */

public class NewBookReceiverActivity extends AppCompatActivity {
    private final static String TAG = NewBookReceiverActivity.class.getSimpleName();
    private final static int MESSAGE_NEW_BOOK_ARRIVED = 1;
    private IBookManager mIBookManager;

    private Handler mHandler = new MyHandle();

    private static class MyHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_NEW_BOOK_ARRIVED) {
                Log.d(TAG, "receive new book : " + msg.obj);
            }
            super.handleMessage(msg);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, PushService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {

        if (mIBookManager != null && mIBookManager.asBinder().isBinderAlive()) {
            try {
                mIBookManager.unregisterListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mServiceConnection);
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBookManager = IBookManager.Stub.asInterface(service);
            try {
//                service.linkToDeath(mDeathRecipient, 0);
                mIBookManager.registerListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mIBookManager != null) {
                mIBookManager = null;
                Intent intent = new Intent(NewBookReceiverActivity.this, PushService.class);
                bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
                Thread t = Thread.currentThread();
                Log.d(TAG, "onServiceDisconnected thread name: " + t.getName());
            }
            }

    };

    private IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {

        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mIBookManager != null) {
                mIBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
                mIBookManager = null;
                Intent intent = new Intent(NewBookReceiverActivity.this, PushService.class);
                bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
                Thread t = Thread.currentThread();
                Log.d(TAG, "binderDied thread name: " + t.getName());
            }
        }
    };
}
