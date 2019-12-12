package a.miracle.lib_utils;


import androidx.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * Created by A Miracle on 2016/9/19.
 */
public class LogUtils {
    public static String LOG_TAG = "Android Lib";
    public static boolean LOG_DEBUG = BuildConfig.DEBUG;

    /**
     * 初始化Log
     */
    static{
        // 日志输出配置
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder().tag(LOG_TAG).build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return LOG_DEBUG;
            }
        });
    }

    public static void v(String message) {
        Logger.v(message);
    }

    public static void d(String message) {
        Logger.d(message);
    }

    public static void d(Object object) {
        Logger.d(object);
    }

    public static void i(String message) {
        Logger.i(message);
    }

    public static void w(String message) {
        Logger.w(message);
    }

    public static void wtf(String message) {
        Logger.wtf(message);
    }

    public static void e(String message) {
        Logger.e(message);
    }

    public static void e(Throwable throwable) {
        Logger.e(throwable, "");
    }

    public static void e(String message, Throwable throwable) {
        Logger.e(throwable, message);
    }

    public static void v(String tag, String message) {
        Logger.t(tag).v(message);
    }

    public static void d(String tag, String message) {
        Logger.t(tag).d(message);
    }

    public static void d(String tag, Object object) {
        Logger.t(tag).d(object);
    }

    public static void i(String tag, String message) {
        Logger.t(tag).i(message);
    }

    public static void w(String tag, String message) {
        Logger.t(tag).w(message);
    }

    public static void wtf(String tag, String message) {
        Logger.t(tag).wtf(message);
    }

    public static void e(String tag, String message) {
        Logger.t(tag).e(message);
    }

    public static void e(String tag, String message, Throwable throwable) {
        Logger.t(tag).e(throwable, message);
    }

    public static void printOut(String message){
        if(LOG_DEBUG){
            System.out.println(">>> "+message);
        }
    }
    public static void printErr(String message){
        if(LOG_DEBUG){
            System.err.println(">>> "+message);
        }
    }

    public static void printOut(String tag, String message) {
        printOut(tag + " : " + message);
    }

    public static void printErr(String tag, String message) {
        printErr(tag + " : " + message);
    }

}
