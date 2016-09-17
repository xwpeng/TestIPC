package android.xwpeng.testipc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by xwpeng on 16-8-29.
 */
public class ProcessUtil {
     /* spend too much time !! 60+ms
    private String getProcessName(){
		ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		int pid = android.os.Process.myPid();
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (int i = 0; i < infos.size(); i++) {
			RunningAppProcessInfo info = infos.get(i);
			if(pid == info.pid){
				return info.processName;
			}
		}
		return null;
	}
	*/

    public static String getProcessName() {
        String processNmae = "";
        BufferedReader reader = null;
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            reader = new BufferedReader(new FileReader(file));
            processNmae = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseIO(reader);
        }
        return processNmae;
    }

    private static void releaseIO(BufferedReader reader) {
        if (reader != null) try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
