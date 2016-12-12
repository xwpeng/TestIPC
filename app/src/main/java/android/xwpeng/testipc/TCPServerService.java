package android.xwpeng.testipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xwpeng on 16-12-11.
 */

public class TCPServerService extends Service {
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);
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
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();
    }

    private class TcpServer implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                System.err.println("establish tcp server failed, port:8868");
                e.printStackTrace();
                return;
            }
            while (!mIsServiceDestoryed.get()) {
                try {
                    Socket client = serverSocket.accept();
                    System.out.println("accept");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //todo responseClient
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
        out.println("welcome to chat room");
        while (!mIsServiceDestoryed.get()) {
            String str = in.readLine();
            System.out.println("message from client: " + str);
            if (str == null) {
                // client id disconnent
                break;
            }
        }
    }
}
