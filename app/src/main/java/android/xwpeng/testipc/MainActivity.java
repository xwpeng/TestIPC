package android.xwpeng.testipc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_start_email).setOnClickListener(this);
        findViewById(R.id.main_start_push_receiver).setOnClickListener(this);
        findViewById(R.id.main_serial_user).setOnClickListener(this);
        findViewById(R.id.main_unserial_user).setOnClickListener(this);
        Log.e(TAG, "processname: " + ProcessUtil.getProcessName() + " pid: " + Process.myPid());
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
