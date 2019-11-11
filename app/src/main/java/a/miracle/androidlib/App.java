package a.miracle.androidlib;

import android.app.Application;

/**
 * Created by c.tao on 2019/11/8
 */
public class App extends Application {
    private static App sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }

    public static App get() {
        return sApp;
    }
}
