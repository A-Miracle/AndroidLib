package a.miracle.androidlib;

import android.app.Application;
import android.os.Handler;

/**
 * Created by c.tao on 2019/11/8
 */
public class App extends Application {
    private static App sApp;
    private static int mMainThreadId = -1;
    private static Handler mMainThreadHandler;
    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        mMainThreadId = android.os.Process.myTid();
        mMainThreadHandler = new Handler();
    }

    public static App get() {
        return sApp;
    }

    public static Handler getHandler() {
        return mMainThreadHandler;
    }

    public static int getThreadId() {
        return mMainThreadId;
    }

}
