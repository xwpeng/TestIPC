package android.xwpeng.testipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.xwpeng.testipc.entity.Book;
import android.xwpeng.testipc.entity.User2;
import android.xwpeng.testipc.util.ProcessUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xwpeng on 16-11-23.
 */

public class PushService extends Service {
    private MyBinder mBinder = new MyBinder();
    private final static String TAG = PushService.class.getSimpleName();
    private List<Book> mBooks;
    private List<User2> mUsers;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        Log.e(TAG, "processname: " + ProcessUtil.getProcessName() + " pid: " + Process.myPid());
        if (mBooks == null) {
            mBooks = new ArrayList<>();
            Book book = new Book();
            book.id = 1;
            book.name = "pushCreateBook";
            book.price = 99.00;
            mBooks.add(book);
        }
        if (mUsers == null) {
            mUsers = new ArrayList<>();
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
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    class MyBinder extends IBookManager.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public List<Book> getBookList() throws RemoteException {
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


    }
}
