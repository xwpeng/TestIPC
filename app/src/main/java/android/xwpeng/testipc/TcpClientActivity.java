package android.xwpeng.testipc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xwpeng on 16-12-14.
 */

public class TcpClientActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "TcpClientActivity";
    private final static int MESSAGE_RECEIVE_NEW_MSG = 1;
    private final static int MESSAGE_SOCKET_CONNECTED = 2;
    private Button mSendButton;
    private TextView mMessageTextView;
    private EditText mMessageEditText;
    private PrintWriter mPrintWeiter;
    private Socket mClientSocket;
    private Handler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpclient);
        initView();
        Intent i = new Intent(this, TCPServerService.class);
        startService(i);
        new Thread() {
            @Override
            public void run() {
                connect();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void initView() {
        mMessageTextView = (TextView) findViewById(R.id.tcp_client_msg_textview);
        mMessageEditText = (EditText) findViewById(R.id.tcp_client_msg_edittext);
        mSendButton = (Button) findViewById(R.id.tcp_client_send_button);
        mSendButton.setOnClickListener(this);
    }


    private void connect() {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket("localhost", 8688);
                mClientSocket = socket;
                mPrintWeiter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                Log.d(TAG, "connect success");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "connect server tcp failed");
                SystemClock.sleep(1000);
            }
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!TcpClientActivity.this.isFinishing()) {
                String msg = br.readLine();
//                Log.d(TAG, "receive :" + msg);
                if (msg != null) {
                    String time = formatDateTime(System.currentTimeMillis());
                    final String showedMsg = "server " + time + ":" + msg + "\n";
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, showedMsg).sendToTarget();
                }
            }
            Log.d(TAG, "quit...");
            mPrintWeiter.close();
            br.close();
            socket.close();
        } catch (IOException e) {
        e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tcp_client_send_button:
                final String msg = mMessageEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(msg) && mPrintWeiter != null) {
                    mPrintWeiter.println(msg);
                    mMessageEditText.setText("");
                    mMessageTextView.append("self " + formatDateTime(System.currentTimeMillis()) + " : " + msg + "\n");
                }
                break;
        }
    }

    private String formatDateTime(long time) {
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }


    private static class MyHandler extends Handler {
        private TcpClientActivity tcpClientActivity;

        public MyHandler(TcpClientActivity activity) {
            WeakReference<TcpClientActivity> w = new WeakReference(activity);
            this.tcpClientActivity = w.get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG:
                    tcpClientActivity.mMessageTextView.append((String) msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    tcpClientActivity.mSendButton.setEnabled(true);
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
