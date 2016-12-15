package android.xwpeng.testipc.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by xwpeng on 16-12-11.
 */

public class TCPServerService extends Service {
    private final static String TAG = "TCPServerService";
    private boolean mIsServiceDestoryed;
    private String[] mDefinedMeaasges = new String[]{
            "Hello World!",
            "how are you",
            "what is your name",
            " 1111",
            "22222",
            "3333333"
    };

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new TcpServer()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed = true;
        super.onDestroy();
    }

    private class TcpServer implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                Log.e(TAG, "establish tcp server failed, port:8868");
                e.printStackTrace();
                return;
            }
                try {
                    final Socket client = serverSocket.accept();
                    Log.d(TAG, "accpet");
                    responseClient(client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void responseClient(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
        out.println("welcome to chat room");
        while (!mIsServiceDestoryed) {
            String str = in.readLine();
            Log.d(TAG, "message from client: " + str);
            if (str == null) {
                // client disconnent
                Log.d(TAG, "client quit");
                break;
            }
            int i = new Random().nextInt(mDefinedMeaasges.length);
            String msg = mDefinedMeaasges[i];
            out.println(msg);
            Log.d(TAG, "send : " + msg);
        }
        if (out != null) {
            out.close();
        }
        if (in != null) {
            in.close();
        }
        client.close();
    }
}
