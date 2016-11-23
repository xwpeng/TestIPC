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
import android.xwpeng.testipc.entity.User2;

import java.util.List;
import android.xwpeng.testipc.entity.User;
import android.xwpeng.testipc.util.ProcessUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private ServiceConnection mConn;
    private IBookManager mIBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_start_email).setOnClickListener(this);
        findViewById(R.id.main_start_push_receiver).setOnClickListener(this);
        findViewById(R.id.main_add_book).setOnClickListener(this);
        findViewById(R.id.main_get_books).setOnClickListener(this);
        findViewById(R.id.main_add_user).setOnClickListener(this);
        findViewById(R.id.main_get_users).setOnClickListener(this);
        findViewById(R.id.main_serial_user).setOnClickListener(this);
        findViewById(R.id.main_unserial_user).setOnClickListener(this);
        Log.e(TAG, "processname: " + ProcessUtil.getProcessName() + " pid: " + Process.myPid());
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mIBookManager = IBookManager.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(MainActivity.this, PushService.class), mConn, BIND_AUTO_CREATE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_start_email:
                startActivity(new Intent(MainActivity.this, EmailActivity.class));
                break;
            case R.id.main_start_push_receiver:
                startActivity(new Intent(MainActivity.this, PushReceiverActivity.class));
                break;
            case R.id.main_add_book:
                if (mIBookManager != null) {
                    Book b = new Book();
                    b.id = 2;
                    b.name = "mainBook";
                    b.price = 999.00;
                    try {
                        mIBookManager.addBook(b);
                    } catch (RemoteException e) {
                        Log.e(TAG, "onclick", e);
                    }
                }
                break;
            case R.id.main_get_books:
                if (mIBookManager == null) return;
                try {
                    List<Book> books = mIBookManager.getBookList();
                    if (books != null)
                        for (Book b : books) {
                            Log.d(TAG,b.toString());
                        }
                } catch (RemoteException e) {
                    Log.e(TAG, "onclick", e);
                }
                break;
            case R.id.main_add_user:
                if (mIBookManager != null) {
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
                break;
            case R.id.main_get_users:
                if (mIBookManager == null) return;
                try {
                    List<User2> users = mIBookManager.getUserList();
                    if (users != null)
                        for (User2 u : users) {
                            Log.d(TAG,u.toString());
                        }
                } catch (RemoteException e) {
                    Log.e(TAG, "onclick", e);
                }
            case R.id.main_serial_user:
                serivalUser();
                break;
            case R.id.main_unserial_user:
                unSerialUser();
                break;
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
            User user = (User)ins.readObject();
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
