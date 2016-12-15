package android.xwpeng.testipc.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.xwpeng.testipc.IBookManager;
import android.xwpeng.testipc.IOnNewBookArrivedListener;
import android.xwpeng.testipc.entity.Book;
import android.xwpeng.testipc.entity.User2;
import android.xwpeng.testipc.util.ProcessUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * test private process and remote Service
 * Created by xwpeng on 16-11-23.
 */

public class PushService extends Service {
    private final static String TAG = PushService.class.getSimpleName();
    private MyBinder mBinder = new MyBinder();
    private CopyOnWriteArrayList<Book> mBooks;
    private CopyOnWriteArrayList<User2> mUsers;
    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);
    private RemoteCallbackList<IOnNewBookArrivedListener> mListeners = new RemoteCallbackList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
//        int check = checkCallingOrSelfPermission("android.xwpeng.permission.ACCESS_PUSH_SERVICE");
//        if (check == PackageManager.PERMISSION_DENIED) {
//            return null;
//        }
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        Log.e(TAG, "processname: " + ProcessUtil.getProcessName() + " pid: " + Process.myPid());
        if (mBooks == null) {
            mBooks = new CopyOnWriteArrayList<>();
            Book book = new Book();
            book.id = 1;
            book.name = "pushCreateBook";
            book.price = 99.00;
            mBooks.add(book);
        }
        if (mUsers == null) {
            mUsers = new CopyOnWriteArrayList<>();
            User2 u = new User2();
            Book book = new Book();
            book.id = 1;
            book.name = "pushCreateBook";
            book.price = 99.00;
            u.book = book;
            u.userId = 1;
            u.gender = "male";
            u.userName = "pushUser";
            mUsers.add(u);
        }
        new Thread(new AddBookWorker()).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        mBooks.add(book);
        //notify all listeners
//        for (IOnNewBookArrivedListener l : mListeners) {
//            l.onNewBookArrived(book);
//        }
        int size = mListeners.beginBroadcast();
        for (int i = 0; i< size; i++) {
            mListeners.getBroadcastItem(i).onNewBookArrived(book);
        }
        mListeners.finishBroadcast();
    }

    private class MyBinder extends IBookManager.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public List<Book> getBookList() throws RemoteException {
//            SystemClock.sleep(5000);
            return mBooks;
        }

        @Override
        public Book addBookIn(Book b) throws RemoteException {
            Log.e(TAG, "in->receive:" + b.toString());
            Log.e(TAG, "in->receive book's hash:" + b.hashCode());
            mBooks.add(b);
            b.name = "pushAddIn";
            return b;
        }

        @Override
        public Book addBookOut(Book b) throws RemoteException {
            Log.e(TAG, "out->receive:" + b.toString());
            Log.e(TAG, "out->receive book's hash:" + b.hashCode());
            mBooks.add(b);
            b.name = "pushAddOut";
            return b;
        }

        @Override
        public Book addBookInOut(Book b) throws RemoteException {
            Log.e(TAG, "inout->receive:" + b.toString());
            Log.e(TAG, "inout->receive book's hash:" + b.hashCode());
            mBooks.add(b);
            b.name = "pushAddInOut";
            return b;
        }

        @Override
        public List<User2> getUserList() throws RemoteException {
            return mUsers;
        }

        @Override
        public void addUser(User2 u) throws RemoteException {
            mUsers.add(u);
        }

        @Override
        public void registerListener(android.xwpeng.testipc.IOnNewBookArrivedListener listener) throws RemoteException {
         mListeners.register(listener);
        }

        @Override
        public void unregisterListener(android.xwpeng.testipc.IOnNewBookArrivedListener listener) throws RemoteException {
          mListeners.unregister(listener);
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int check = checkCallingOrSelfPermission("android.xwpeng.permission.ACCESS_PUSH_SERVICE");
            if (check == PackageManager.PERMISSION_DENIED) {
                return false;
            }

            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
            }
            if (!packageName.startsWith("android.xwpeng")) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    }

    private class AddBookWorker implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBooks.size() + 1;
                Book b = new Book(bookId, "Love Android " + bookId, bookId * 100);
                try {
                    onNewBookArrived(b);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
