package android.xwpeng.testipc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.xwpeng.testipc.entity.Book;
import android.xwpeng.testipc.entity.User;
import android.xwpeng.testipc.entity.User2;
import android.xwpeng.testipc.service.PushService;
import android.xwpeng.testipc.ui.NewBookReceiverActivity;
import android.xwpeng.testipc.ui.PublicActivity;
import android.xwpeng.testipc.ui.TBinderPoolActivity;
import android.xwpeng.testipc.ui.TcpClientActivity;
import android.xwpeng.testipc.util.ProcessUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * main activity,launch activity,some buttons access functions
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private ServiceConnection mConn;
    private IBookManager mIBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "processname: " + ProcessUtil.getProcessName() + " pid: " + Process.myPid());
        findViewById(R.id.main_start_public).setOnClickListener(this);
        findViewById(R.id.main_bind_push_service).setOnClickListener(this);
        findViewById(R.id.main_add_book_in).setOnClickListener(this);
        findViewById(R.id.main_add_book_out).setOnClickListener(this);
        findViewById(R.id.main_add_book_inout).setOnClickListener(this);
        findViewById(R.id.main_get_books).setOnClickListener(this);
        findViewById(R.id.main_add_user).setOnClickListener(this);
        findViewById(R.id.main_get_users).setOnClickListener(this);
        findViewById(R.id.main_serial_user).setOnClickListener(this);
        findViewById(R.id.main_unserial_user).setOnClickListener(this);
        findViewById(R.id.main_to_newbookrecevice).setOnClickListener(this);
        findViewById(R.id.main_test_tcp).setOnClickListener(this);
        findViewById(R.id.main_test_binderpool).setOnClickListener(this);
    }

    private void initConn() {
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mIBookManager = IBookManager.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_start_public:
                startActivity(new Intent(MainActivity.this, PublicActivity.class));
                break;
            case R.id.main_bind_push_service:
                initConn();
                bindService(new Intent(MainActivity.this, PushService.class), mConn, BIND_AUTO_CREATE);
                break;
            case R.id.main_add_book_in:
                addBook(1);
                break;
            case R.id.main_add_book_out:
                addBook(2);
                break;
            case R.id.main_add_book_inout:
                addBook(3);
                break;
            case R.id.main_get_books:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getBooks();
                    }
                }).start();
                break;
            case R.id.main_add_user:
                addUser();
                break;
            case R.id.main_get_users:
                getUsers();
            case R.id.main_serial_user:
                serivalUser();
                break;
            case R.id.main_unserial_user:
                unSerialUser();
                break;
            case R.id.main_to_newbookrecevice:
                startActivity(new Intent(MainActivity.this, NewBookReceiverActivity.class));
                break;
            case R.id.main_test_tcp:
                startActivity(new Intent(MainActivity.this, TcpClientActivity.class));
                break;
            case R.id.main_test_binderpool:
                startActivity(new Intent(MainActivity.this, TBinderPoolActivity.class));
                break;
        }
    }

    private void addBook(int i) {
        if (mIBookManager == null) {
            initConn();
            bindService(new Intent(MainActivity.this, PushService.class), mConn, BIND_AUTO_CREATE);
        }
        if (mIBookManager == null) return;
        Book b = new Book();
        b.id = 2;
        b.price = 999.00;
        Book returnB;
        try {
            switch (i) {
                case 1:
                    Log.e(TAG, "in->origin hash:" + b.hashCode());
                    b.name = "mainBookIn";
                    returnB = mIBookManager.addBookIn(b);
                    Log.e(TAG, "in->origin added hash:" + b.hashCode());
                    Log.e(TAG, "in->origin added:" + b.toString());
                    Log.e(TAG, "in->return hash:" + returnB.hashCode());
                    Log.e(TAG, "in->return:" + returnB.toString());
                    break;
                case 2:
                    Log.e(TAG, "out->origin hash:" + b.hashCode());
                    b.name = "mainBookOut";
                    returnB = mIBookManager.addBookOut(b);
                    Log.e(TAG, "out->origin added hash:" + b.hashCode());
                    Log.e(TAG, "out->origin added:" + b.toString());
                    Log.e(TAG, "out->return hash:" + returnB.hashCode());
                    Log.e(TAG, "out->return:" + returnB.toString());
                    break;
                case 3:
                    Log.e(TAG, "inout->origin hash:" + b.hashCode());
                    b.name = "mainBookInOut";
                    returnB = mIBookManager.addBookInOut(b);
                    Log.e(TAG, "inout->origin added hash:" + b.hashCode());
                    Log.e(TAG, "inout->origin added:" + b.toString());
                    Log.e(TAG, "inout->return hash:" + returnB.hashCode());
                    Log.e(TAG, "inout->return:" + returnB.toString());
                    break;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "onclick", e);
        }
    }

    private void getBooks() {
        if (mIBookManager == null) {
            initConn();
            bindService(new Intent(MainActivity.this, PushService.class), mConn, BIND_AUTO_CREATE);
        }
        if (mIBookManager == null) return;
        try {
            List<Book> books = mIBookManager.getBookList();
            if (books != null)
                for (Book b : books) {
                    Log.d(TAG, b.toString());
                }
        } catch (RemoteException e) {
            Log.e(TAG, "onclick", e);
        }
    }

    private void addUser() {
        if (mIBookManager == null) {
            initConn();
            bindService(new Intent(MainActivity.this, PushService.class), mConn, BIND_AUTO_CREATE);
        }
        if (mIBookManager == null) return;
        User2 u = new User2();
        Book b = new Book();
        b.id = 2;
        b.name = "mainBook";
        b.price = 999.00;
        u.book = b;
        u.userId = 2;
        u.userName = "mainUser";
        u.gender = "female";
        try {
            mIBookManager.addUser(u);
        } catch (RemoteException e) {
            Log.e(TAG, "onclick", e);
        }
    }

    private void getUsers() {
        if (mIBookManager == null) {
            initConn();
            bindService(new Intent(MainActivity.this, PushService.class), mConn, BIND_AUTO_CREATE);
        }
        if (mIBookManager == null) return;
        try {
            List<User2> users = mIBookManager.getUserList();
            if (users != null)
                for (User2 u : users) {
                    Log.d(TAG, u.toString());
                }
        } catch (RemoteException e) {
            Log.e(TAG, "onclick", e);
        }
    }

    private void serivalUser() {
        User user = new User();
        user.userName = "xwpeng";
        user.userId = "0101001";
        user.gender = "male";
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(getUserFile()));
            out.writeObject(user);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unSerialUser() {
        try {
            ObjectInputStream ins = new ObjectInputStream(new FileInputStream(getUserFile()));
            User user = (User) ins.readObject();
            Log.e(TAG, user.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private File getUserFile() {
        String path = getExternalCacheDir().getAbsolutePath() + "/user.tex";
        File file = new File(path);
        if (!file.exists()) try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


}
